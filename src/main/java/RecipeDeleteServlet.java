package pac1;

import java.io.IOException;
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
 * Servlet implementation class RecipeDeleteServlet
 */
@WebServlet("/RecipeDeleteServlet")
public class RecipeDeleteServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		int recipeID = Integer.parseInt(request.getParameter("recipeID"));
		String sql0 = "select * from RyouriTB where RyouriID = ? and UserID = ?";
		String sql1 = "delete from FavoTB where RyouriID = ? and UserID = ?";
		String sql2 = "delete from TabetaTB where RyouriID = ? and UserID = ?";
		String sql3 = "delete from BunryouTB where RyouriID = ? and UserID = ?";
		String sql4 = "delete from RyouriTB where RyouriID = ? and UserID = ?";

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		if (userName.equals("admin")) { //共通レシピが削除されそうになったらトップページに遷移する
			System.out.println("共通レシピのレシピ削除ボタンが押されました");
			RequestDispatcher rd = request.getRequestDispatcher("top.jsp");
			rd.forward(request, response);
			return;
		}

		if (!userName.equals(request.getParameter("userName"))) { //ログイン中のユーザとページ表示時のユーザが異なったらトップページに遷移する
			System.out.println(request.getParameter("userName") + "としてレシピ削除ボタンが押されました、現在" + userName + "がログイン中です");
			RequestDispatcher rd = request.getRequestDispatcher("top.jsp");
			rd.forward(request, response);
			return;
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean existsData = false; //1件も該当レシピがなければ削除せずトップページに遷移する
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql0)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("削除確認SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					existsData = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!existsData) {
			System.out.println(request.getParameter("userName") + "のものでないレシピ削除ボタンが押されました");
			RequestDispatcher rd = request.getRequestDispatcher("top.jsp");
			rd.forward(request, response);
			return;
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("Favo削除SQL:" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("Tabeta削除SQL:" + prestmt.toString());
			prestmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("分量削除SQL:" + prestmt.toString());
			prestmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql4)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("レシピ削除SQL:" + prestmt.toString());
			prestmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("レシピ削除完了");

		RequestDispatcher rd_result = request.getRequestDispatcher("MyRecipePageServlet");
		rd_result.forward(request, response);
	}

}
