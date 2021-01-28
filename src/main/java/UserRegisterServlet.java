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

import pac1.func.Util;

/**
 * Servlet implementation class NewUserServlet
 */
@WebServlet("/UserRegisterServlet")
public class UserRegisterServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = Util.sanitizing(request.getParameter("name"));
		String password = Util.sanitizing(request.getParameter("password"));
		int userID = 0;
		String sql1 = "insert into UserTB (UserName, Password, Salt) values (?, ?, ?)";
		String sql2 = "select UserID from UserTB where UserName = ?";
		String sql3 = "insert into UserRoleTB values (?, 'user')";

		if (name.length() > 15) {
			System.out.println("結果:不正なユーザ名");
			request.setAttribute("errorMessage", "不正なユーザ名です。やり直してください。");
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		if (Util.existsUser(name)) {
			System.out.println("結果:ユーザ名重複");
			request.setAttribute("errorMessage", "そのユーザ名は既に登録されています。やり直してください。");
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//64bitのソルトを生成しパスワードと結合
		String salt = "";
		for (int j = 0; j < 16; j++) {
			salt += String.format("%x", (int)(Math.random() * 16));
		}
		password += salt;

		//パスワードをSHA-256でハッシュ化
		String passHashed = "";
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    digest.reset();
		    digest.update(password.getBytes("utf8"));
		    passHashed = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    e.printStackTrace();
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setString(1, name);
			System.out.println("会員登録SQL:" + prestmt.toString());
			prestmt.setString(2, passHashed);
			prestmt.setString(3, salt);
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("会員登録SQL完了");

		//ユーザID取得SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setString(1, name);
			System.out.println("ユーザID取得SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					userID = rs.getInt("UserID");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ユーザID取得SQL完了");

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			prestmt.setInt(1, userID);
			System.out.println("ロール登録SQL:" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ロール登録SQL完了");

		RequestDispatcher rd_result = request.getRequestDispatcher("MypageServlet");
		rd_result.forward(request, response);

	}

}
