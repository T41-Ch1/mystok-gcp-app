package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pac1.func.Util;

/**
 * Servlet implementation class RecipeSuggestServlet
 */
@WebServlet("/RecipeSuggestServlet")
public class RecipeSuggestServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final int DATA_PER_PAGE = 4;
		final String JSP_PATH = "suggest.jsp";
		ArrayList<Integer> ryouriID = new ArrayList<>();
		ArrayList<String> ryourimei = new ArrayList<>();
		ArrayList<String> imageName = new ArrayList<>();
		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		System.out.println("userid: " + userID);

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.DAY_OF_MONTH, -7); //1週間前の日付をセットする

		String sql1 = "select RyouriID from RyouriTB where UserID in (?, 1) and RyouriID not in"
				+ " (select distinct RyouriID from TabetaTB where UserID = ? and TabetaTime > ?)"
				+ " order by rand() limit " + DATA_PER_PAGE;
		String sql2 = "select RyouriID from RyouriTB order by rand() limit " + DATA_PER_PAGE * 2;
		String sql3 = "select Ryourimei, ImageName from RyouriTB where RyouriID in (?";
		for (int i = 1; i < DATA_PER_PAGE; i++) sql3 += ", ?";
		sql3 += ") order by field (RyouriID, ?";
		for (int i = 1; i < DATA_PER_PAGE; i++) sql3 += ", ?";
		sql3 += ")";

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setInt(1, userID);
			prestmt.setInt(2, userID);
			prestmt.setString(3, f.format(cl.getTime()));
			System.out.println("レシピ提案SQL(料理ID): " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryouriID.add(rs.getInt("RyouriTB.RyouriID"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ提案SQL(料理ID)完了 " + ryouriID.size() + "件");

		if (ryouriID.size() < 4) {
			try (
					Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
					PreparedStatement prestmt = conn.prepareStatement(sql2)) {
				System.out.println("レシピ提案SQL(料理ID補充): " + prestmt.toString());
				try (ResultSet rs = prestmt.executeQuery()) {
					while (rs.next()) {
						if (ryouriID.indexOf(rs.getInt("RyouriID")) == -1) ryouriID.add(rs.getInt("RyouriTB.RyouriID"));
						if (ryouriID.size() == 4) break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("errorMessage", e);
				RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
				rd_result.forward(request, response);
				return;
			}
			System.out.println("レシピ提案SQL(料理ID補充)完了 " + ryouriID.size() + "件");
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			for (int i = 0; i < ryouriID.size(); i++) {
				prestmt.setInt(i + 1, ryouriID.get(i));
				prestmt.setInt(i + 1 + ryouriID.size(), ryouriID.get(i));
			}
			System.out.println("レシピ提案SQL(料理名、画像名):" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryourimei.add(rs.getString("Ryourimei"));
					if (rs.getString("ImageName") == null) imageName.add("noimage.jpg"); //条件を満たす画像名を格納
					else imageName.add(rs.getString("ImageName")); //条件を満たす画像名を格納
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ提案SQL(料理名、画像名)完了 " + ryourimei.size() + "件");

		//Favo確認SQLを表示レシピに対して実行
		ArrayList<Boolean> tabetaList = Util.tabetaTodayInfo(ryouriID, userID);
		//Favo確認SQLを表示レシピに対して実行
		ArrayList<Boolean> favoList = Util.favoInfo(ryouriID, userID);

		request.setAttribute("ryouriID", ryouriID);
		request.setAttribute("ryourimei", ryourimei);
		request.setAttribute("imageName", imageName);
		request.setAttribute("tabetaList", tabetaList);
		request.setAttribute("favoList", favoList);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
		rd_result.forward(request, response);
	}

}
