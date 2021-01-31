package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pac1.func.Util;

/**
 * Servlet implementation class MypageServlet
 */
@WebServlet("/MypageServlet")
public class MypageServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final int DATA_PER_PAGE = 5;
		final String JSP_PATH = "mypage.jsp";
		ArrayList<Integer> ryouriIDFavo = new ArrayList<>();
		ArrayList<String> ryourimeiFavo = new ArrayList<>();
		ArrayList<String> imageNameFavo = new ArrayList<>();
		ArrayList<Boolean> tabetaListFavo = new ArrayList<>();
		ArrayList<Integer> ryouriIDTabeta = new ArrayList<>();
		ArrayList<String> tabetaTimeList = new ArrayList<>();
		ArrayList<String> ryourimeiTabeta = new ArrayList<>();
		ArrayList<String> imageNameTabeta = new ArrayList<>();
		ArrayList<Boolean> tabetaListTabeta = new ArrayList<>();
		ArrayList<Boolean> favoListTabeta = new ArrayList<>();
		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String sql1 = "select RyouriID from FavoTB where UserID = ? order by FavoTime desc limit " + DATA_PER_PAGE;
		String sql3 = "select RyouriID, TabetaTime from TabetaTB where UserID = ? order by TabetaTime desc limit " + DATA_PER_PAGE;

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
			System.out.println("Favoレシピ検索SQL(マイページ): " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryouriIDFavo.add(rs.getInt("RyouriID"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Favoレシピ検索SQL(マイページ)完了");
		System.out.println("料理ID: " + Arrays.toString(ryouriIDFavo.toArray()));

		if (ryouriIDFavo.size() > 0) {
			String sql2 = "select Ryourimei, ImageName from RyouriTB where RyouriID in (?";
			for (int i = 1; i < ryouriIDFavo.size(); i++) sql2 += ", ?";
			sql2 += ") order by field(RyouriID, ?";
			for (int i = 1; i < ryouriIDFavo.size(); i++) sql2 += ", ?";
			sql2 += ")";

			try (
					Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
					PreparedStatement prestmt = conn.prepareStatement(sql2)) {
				for (int i = 0; i < ryouriIDFavo.size(); i++) {
					prestmt.setInt(i + 1, ryouriIDFavo.get(i));
					prestmt.setInt(i + 1 + ryouriIDFavo.size(), ryouriIDFavo.get(i));
				}
				System.out.println("Favoレシピ名検索SQL(マイページ): " + prestmt.toString());
				try (ResultSet rs = prestmt.executeQuery()) {
					while (rs.next()) {
						ryourimeiFavo.add(rs.getString("Ryourimei"));
						if (rs.getString("ImageName") == null) imageNameFavo.add("noimage.jpg"); //条件を満たす画像名を格納
						else imageNameFavo.add(rs.getString("ImageName")); //条件を満たす画像名を格納
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Favoレシピ検索SQL(マイページ)完了");
			System.out.println("料理名: " + Arrays.toString(ryourimeiFavo.toArray()) + "画像名: " + Arrays.toString(imageNameFavo.toArray()));
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			prestmt.setInt(1, userID);
			System.out.println("Tabetaレシピ検索SQL(マイページ): " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryouriIDTabeta.add(rs.getInt("RyouriID"));
					tabetaTimeList.add(f.format(rs.getTimestamp("TabetaTime")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Tabetaレシピ検索SQL(マイページ)完了");
		System.out.println("料理ID: " + Arrays.toString(ryouriIDTabeta.toArray()) + " TabetaTime: " + Arrays.toString(tabetaTimeList.toArray()));

		if (ryouriIDTabeta.size() > 0) {
			String sql4 = "select Ryourimei, ImageName from RyouriTB inner join TabetaTB on RyouriTB.RyouriID = TabetaTB.RyouriID where TabetaTime in (?";
			for (int i = 1; i < tabetaTimeList.size(); i++) sql4 += ", ?";
			sql4 += ") order by field(TabetaTime, ?";
			for (int i = 1; i < tabetaTimeList.size(); i++) sql4 += ", ?";
			sql4 += "), TabetaID desc";

			try (
					Connection conn = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
					PreparedStatement prestmt = conn.prepareStatement(sql4)) {
				for (int i = 0; i < tabetaTimeList.size(); i++) {
					prestmt.setString(i + 1, tabetaTimeList.get(i));
					prestmt.setString(i + 1 + tabetaTimeList.size(), tabetaTimeList.get(i));
				}
				System.out.println("Tabetaレシピ名検索SQL(マイページ): " + prestmt.toString());
				try (ResultSet rs = prestmt.executeQuery()) {
					while (rs.next()) {
						ryourimeiTabeta.add(rs.getString("Ryourimei"));
						if (rs.getString("ImageName") == null) imageNameTabeta.add("noimage.jpg"); //条件を満たす画像名を格納
						else imageNameTabeta.add(rs.getString("ImageName")); //条件を満たす画像名を格納
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Tabetaレシピ検索SQL(マイページ)完了");
			System.out.println("料理名: " + Arrays.toString(ryourimeiTabeta.toArray()) + "画像名: " + Arrays.toString(imageNameTabeta.toArray()));
		}

		//Tabeta確認SQLをお気に入りレシピに対して実行
		tabetaListFavo = Util.tabetaTodayInfo(ryouriIDFavo, userID);
		//Favo確認SQLを食べたレシピに対して実行
		favoListTabeta = Util.favoInfo(ryouriIDTabeta, userID);
		//Tabeta確認SQLを食べたレシピに対して実行
		tabetaListTabeta = Util.tabetaTodayInfo(ryouriIDTabeta, userID);

		request.setAttribute("ryouriIDFavo", ryouriIDFavo);
		request.setAttribute("ryourimeiFavo", ryourimeiFavo);
		request.setAttribute("imageNameFavo", imageNameFavo);
		request.setAttribute("tabetaListFavo", tabetaListFavo);
		request.setAttribute("ryouriIDTabeta", ryouriIDTabeta);
		request.setAttribute("tabetaTimeList", tabetaTimeList);
		request.setAttribute("ryourimeiTabeta", ryourimeiTabeta);
		request.setAttribute("imageNameTabeta", imageNameTabeta);
		request.setAttribute("tabetaListTabeta", tabetaListTabeta);
		request.setAttribute("favoListTabeta", favoListTabeta);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
		rd_result.forward(request, response);
	}

	@Override
	public void doPost (HttpServletRequest request, HttpServletResponse response)	throws IOException, ServletException {
		this.doGet(request, response);
	}

}
