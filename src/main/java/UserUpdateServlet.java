package pac1;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pac1.func.Util;

/**
 * Servlet implementation class UserUpdateServlet
 */
@WebServlet("/UserUpdateServlet")
public class UserUpdateServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		String nameOld = request.getRemoteUser();
		String nameNew = nameOld;
		if (request.getParameter("mode").equals("namechange")) nameNew = Util.sanitizing(request.getParameter("nameNew"));
		String mySqlStokOld = Util.sanitizing(request.getParameter("mySqlStokOld"));
		String mySqlStokNew = mySqlStokOld;
		if (request.getParameter("mode").equals("passchange")) mySqlStokNew = Util.sanitizing(request.getParameter("mySqlStokNew"));
		String saltOld = "";
		String saltNew = "";
		String passOldHashed = "";
		String passNewHashed = "";

		HttpSession session = request.getSession(false);

		//ソルト取得SQLの組み立て
		String sql1 = "select Salt from UserTB where UserName = ?";
		//パスワード確認SQLの組み立て
		String sql2 = "select count(*) as cnt from UserTB where UserName = ? and Password = ?";
		//会員情報更新SQLの組み立て
		String sql3 = "update UserTB set UserName = ?, Password = ?, Salt = ? where UserName = ? and Password = ?";

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//ソルト取得SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setString(1, nameOld);
			System.out.println("ソルト取得SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					saltOld = rs.getString("Salt");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("ソルト取得SQL完了");

		//ユーザ名がなかったらエラー画面に遷移する
		if (saltOld.equals("")) {
			System.out.println("結果:該当ユーザなし");
			session.invalidate();
			request.setAttribute("errorMessage", "既に変更されたユーザ名です。やり直してください。");
			RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
			rd.forward(request, response);
			return;
		}

		//passOldHashedの生成
		mySqlStokOld += saltOld;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
		    digest.update(mySqlStokOld.getBytes("utf8"));
		    passOldHashed = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//パスワード確認SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setString(1, nameOld);
			System.out.println("パスワード確認SQL:" + prestmt.toString());
			prestmt.setString(2, passOldHashed);
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					if (rs.getInt("cnt") == 0) {
						System.out.println("結果:パスワード間違い");
						request.setAttribute("errorMessage", "パスワードが違います。やり直してください。");
						RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
						rd.forward(request, response);
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("パスワード確認SQL完了");

		//64bitのソルトを生成しパスワードと結合
		for (int j = 0; j < 16; j++) {
			saltNew += String.format("%x", (int)(Math.random() * 16));
		}
		mySqlStokNew += saltNew;

		//パスワードをSHA-256でハッシュ化
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
		    digest.update(mySqlStokNew.getBytes("utf8"));
		    passNewHashed = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//会員情報更新SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			prestmt.setString(1, nameNew);
			prestmt.setString(4, nameOld);
			System.out.println("会員情報更新SQL:" + prestmt.toString());
			prestmt.setString(2, passNewHashed);
			prestmt.setString(3, saltNew);
			prestmt.setString(5, passOldHashed);
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("会員情報更新SQL完了");

		//セッションのユーザ名を更新する
		session = request.getSession();
		session.setAttribute("auth.user", nameNew);
		//マイページに遷移する
		RequestDispatcher rd = request.getRequestDispatcher("MypageServlet");
		rd.forward(request, response);
	}

}
