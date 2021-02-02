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
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//変数
		String userName; //フォームから入力されたユーザ名
		String mySqlStok; //フォームから入力されたPW
		String target; //フォームから受け取った元々のアクセス先URI
		String salt = ""; //userNameに対応するソルト
		String passHashed = ""; //ハッシュ化されたPW
		String sql1 = ""; //ソルト取得SQL
		String sql2 = ""; //ログイン判定SQL
		int userID = 0; //ログインしたユーザのID
		String role = ""; //ログインしたユーザのロール(user以外の値は取らない)
		String JSP_PATH = "error.jsp"; //エラーページの名前

		HttpSession session = request.getSession(false);
	    if (session == null){
	    	/* セッションが開始されずにここへ来た場合は無条件でエラー表示 */
	    	System.out.println("LoginServletへの不正アクセス");
	    	request.setAttribute("errorMessage", "不正なアクセスです、トップページからやり直してください。");
	    	RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
	    }

	    //入力されたデータを格納、URI以外はサニタイジングも同時に行う
		userName = Util.sanitizing(request.getParameter("username"));
		mySqlStok = Util.sanitizing(request.getParameter("mySqlStok"));
		target = (String)request.getParameter("targetURI");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//ソルト取得SQLの組み立て
		sql1 =  "select Salt from UserTB where UserName = ?";

		//ソルト取得SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setString(1, userName);
			System.out.println("ソルト取得SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					salt = rs.getString("Salt");
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
		if (salt.equals("")) {
			System.out.println("結果:ログイン失敗");
			request.setAttribute("errorMessage", "ユーザ名またはパスワードに誤りがあります。");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
		}

		//passHashedの生成
		mySqlStok += salt;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
		    digest.update(mySqlStok.getBytes("utf8"));
		    passHashed = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//ログイン判定SQLの組み立て
		sql2 = "select UserTB.UserID, UserRoleTB.Role from UserTB inner join UserRoleTB on UserTB.UserID = UserRoleTB.UserID "
				+ "where UserTB.UserName = ? and UserTB.Password = ?";

		//ログイン判定SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setString(1, userName);
			System.out.println("ログイン判定SQL:" + prestmt.toString());
			prestmt.setString(2, passHashed);
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					userID = rs.getInt("UserTB.UserID");
					role = rs.getString("UserRoleTB.Role"); //roleは1種類の前提
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("ログイン判定SQL完了");

		if (!role.equals("")) {
			System.out.println("結果:ログイン成功");
			//セッションにユーザ名、ユーザID、ロールを登録する
			session = request.getSession();
			session.setAttribute("auth.user", userName);
			session.setAttribute("auth.userid", userID);
			session.setAttribute("auth.role", role);
			//例えば"/mystok/LoginServlet"を"LoginServlet"に削る
			target = target.substring(target.lastIndexOf("/") + 1);
	    	response.sendRedirect(target);
		} else {
			System.out.println("結果:ログイン失敗");
			request.setAttribute("errorMessage", "ユーザ名またはパスワードに誤りがあります。");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
		}
	}

	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)	throws IOException, ServletException {

		String JSP_PATH = "error.jsp"; //エラーページの名前

		/* 飛び込みの場合は無条件でエラー表示 */
		System.out.println("LoginServletへの不正アクセス");
		request.setAttribute("errorMessage", "不正なアクセスです、トップページからやり直してください");
		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
		rd.forward(request, response);
		return;
	}

}
