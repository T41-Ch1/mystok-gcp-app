package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pac1.func.Util;

/**
 * Servlet implementation class FavoPageServlet
 */
@WebServlet("/FavoPageServlet")
public class FavoPageServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		ArrayList<Integer> recipeID = new ArrayList<>();
		ArrayList<String> ryourimei = new ArrayList<>();
		ArrayList<String> imageName = new ArrayList<>();
		ArrayList<Boolean> tabetaList = new ArrayList<>();
		int pageNum; //検索結果ページのページ番号
		if (Objects.equals(request.getParameter("pageNum"), null)) {
			pageNum = 1; //pageNumのパラメータがnullなら1ページ目を表示
		} else {
			pageNum = Integer.parseInt(request.getParameter("pageNum")); //そうでないなら送信されたパラメータpageNumを格納
		}
		final int DATA_PER_PAGE = 25; //1ページごとに表示する最大件数
		int recipeNum = 0;
		String JSP_PATH = "okiniiri.jsp";
		//Favoレシピ検索SQLの組み立て
		String sql1 = "select count(RyouriTB.RyouriID) as cnt from FavoTB inner join RyouriTB on "
				  + "FavoTB.RyouriID = RyouriTB.RyouriID where FavoTB.UserID = ?";
		String sql2;

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

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
			System.out.println("Favoレシピ件数検索SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					recipeNum = rs.getInt("cnt");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Favoレシピ件数検索SQL完了 " + recipeNum + "件");

		if (recipeNum == 0) {
			//表示するレシピが0件なら遷移
			request.setAttribute("recipeID", recipeID);
			request.setAttribute("ryourimei", ryourimei);
			request.setAttribute("imageName", imageName);
			request.setAttribute("recipeNum", recipeNum);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("tabetaList", tabetaList);
			RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
			rd_result.forward(request, response);
			return;
		} else 	if (recipeNum <= DATA_PER_PAGE * (pageNum - 1)) {
			//pageTotalを超えるpageNumを送信されたらpageNumを上書き
			pageNum = (recipeNum - 1) / DATA_PER_PAGE + 1;
		}

		sql2 = "select RyouriTB.RyouriID, RyouriTB.Ryourimei, RyouriTB.ImageName from FavoTB inner join RyouriTB on "
				  + "FavoTB.RyouriID = RyouriTB.RyouriID where FavoTB.UserID = ? "
				  + "order by FavoTB.FavoTime desc "
				  + "limit " + DATA_PER_PAGE + " offset " + DATA_PER_PAGE * (pageNum - 1);

		//Favoレシピ検索SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {

			prestmt.setInt(1, userID);
			System.out.println("Favoレシピ検索SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					recipeID.add(rs.getInt("RyouriID"));
					ryourimei.add(rs.getString("Ryourimei"));
					if (rs.getString("ImageName") == null) imageName.add("noimage.jpg"); //noimageの画像名を格納
					else imageName.add(rs.getString("ImageName")); //条件を満たす画像名を格納
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Favoレシピ検索SQL完了");
		System.out.println("料理ID:" + Arrays.toString(recipeID.toArray()));
		System.out.println("料理名:" + Arrays.toString(ryourimei.toArray()));

		//Tabeta確認SQLを表示レシピに対して実行
		tabetaList = Util.tabetaTodayInfo(recipeID, userID);

		request.setAttribute("recipeID", recipeID);
		request.setAttribute("ryourimei", ryourimei);
		request.setAttribute("imageName", imageName);
		request.setAttribute("recipeNum", recipeNum);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("tabetaList", tabetaList);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
		rd_result.forward(request, response);

	}

}
