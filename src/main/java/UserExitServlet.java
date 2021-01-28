package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pac1.func.Util;

/**
 * Servlet implementation class UserExitServlet
 */
@WebServlet("/UserExitServlet")
public class UserExitServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		HttpSession session = request.getSession(false);
		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		final String JSP_PATH1 = "error.jsp";
		final String JSP_PATH2 = "top.jsp";

		String sql1 = "delete from FavoTB where UserID = ?";
		String sql2 = "delete from TabetaTB where UserID = ?";
		String sql3 = "delete from BunryouTB where UserID = ?";
		String sql4 = "delete from RyouriTB where UserID = ?";
		String sql5 = "delete from UserRoleTB where UserID = ?";
		String sql6 = "delete from UserTB where UserID = ?";

		if (userID == 0) { //ログアウト中の時
			request.setAttribute("errorMessage", "現在ログアウト中です、トップページからやり直してください。");
			RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH1);
			rd_result.forward(request, response);
			return;
		}

		if (userID == 1) { //adminを退会させようとした時
			request.setAttribute("errorMessage", "adminは退会できません。");
			RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH1);
			rd_result.forward(request, response);
			return;
		}

		if (!userName.equals(request.getParameter("userName"))) { //別のユーザーに切り替えたとき
			request.setAttribute("errorMessage", "別のユーザーとしてログイン中です。");
			System.out.println(request.getParameter("userName") + "として退会ボタンが押されました、現在" + userName + "がログイン中です");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH1);
			rd.forward(request, response);
			return;
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
			prestmt.setInt(1, userID);
			System.out.println("退会SQL(Favo)SQL: " + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setInt(1, userID);
			System.out.println("退会SQL(Tabeta)SQL: " + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			prestmt.setInt(1, userID);
			System.out.println("退会SQL(分量)SQL: " + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql4)) {
			prestmt.setInt(1, userID);
			System.out.println("退会SQL(料理)SQL: " + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql5)) {
			prestmt.setInt(1, userID);
			System.out.println("退会SQL(ロール)SQL: " + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql6)) {
			prestmt.setInt(1, userID);
			System.out.println("退会SQL(ユーザ)SQL: " + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("退会SQL完了");

		session.invalidate();
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH2);
		rd_result.forward(request, response);
	}

}
