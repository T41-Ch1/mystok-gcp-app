package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
 * Servlet implementation class TabetaPageServlet
 */
@WebServlet("/TabetaPageServlet")
public class TabetaPageServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final int DATA_PER_PAGE = 25;
		final String JSP_PATH = "tabeta.jsp";
		int pageNum; //検索結果ページのページ番号
		if (Objects.equals(request.getParameter("pageNum"), null)) {
			pageNum = 1; //pageNumのパラメータがnullなら1ページ目を表示
		} else {
			pageNum = Integer.parseInt(request.getParameter("pageNum")); //そうでないなら送信されたパラメータpageNumを格納
		}
		Date now = new Date(); //現在時刻
		String year; //表示するデータの年
		String month; //表示するデータの月
		SimpleDateFormat fYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat fMonth = new SimpleDateFormat("MM");
		if (Objects.equals(request.getParameter("year"), null)) {
			//yearのパラメータがnullなら現在の月を表示
			year = fYear.format(now);
			month = fMonth.format(now);
		} else {
			//そうでないなら送信されたパラメータを格納
			year = request.getParameter("year");
			month = request.getParameter("month");
		}
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1); //該当月の1日目をセットする
		int youbi = cal.get(Calendar.DAY_OF_WEEK); //該当月の1日目の曜日(日曜が1、土曜が7)
		ArrayList<String> ryourimei = new ArrayList<>();
		ArrayList<Integer> ryouriID = new ArrayList<>();
		ArrayList<Integer> tabetaDayList = new ArrayList<>();
		ArrayList<Boolean> tabetaList = new ArrayList<>();
		ArrayList<Boolean> favoList = new ArrayList<>();
		ArrayList<String> tabetaMonthList = new ArrayList<>();
		ArrayList<Integer> tabetaCountList = new ArrayList<>();

		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		String sql1 = "select date_format(TabetaTime, '%Y%m') as Time, count(*) as cnt from TabetaTB where UserID = ? group by Time order by Time desc";
		String sql2 = "select RyouriTB.Ryourimei, TabetaTB.RyouriID, date_format(TabetaTB.TabetaTime, '%d') as TabetaDay from TabetaTB inner join RyouriTB on RyouriTB.RyouriID = TabetaTB.RyouriID "
				+ "where TabetaTB.UserID = ? and date_format(TabetaTB.TabetaTime, '%Y%m') = '" + year + month + "' "
				+ "order by TabetaTB.TabetaTime desc limit " + DATA_PER_PAGE + " offset " + DATA_PER_PAGE * (pageNum - 1);

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
			System.out.println("食べた月検索SQL: " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					tabetaMonthList.add(rs.getString("Time"));
					tabetaCountList.add(rs.getInt("cnt"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("食べた月検索SQL完了");
		System.out.println("食べた月: " + Arrays.toString(tabetaMonthList.toArray()) + " 食べた回数: " + Arrays.toString(tabetaCountList.toArray()));

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setInt(1, userID);
			System.out.println("食べたレシピ検索SQL: " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryourimei.add(rs.getString("RyouriTB.Ryourimei"));
					ryouriID.add(rs.getInt("TabetaTB.RyouriID"));
					tabetaDayList.add(rs.getInt("TabetaDay"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("食べたレシピ検索SQL完了");
		System.out.println(year + "年" + month + "月に食べた料理名: " + Arrays.toString(ryourimei.toArray()) +
				" 料理ID: " + Arrays.toString(ryouriID.toArray()) + " 日付: " + Arrays.toString(tabetaDayList.toArray()));

		//Tabeta確認SQLを表示レシピに対して実行
		tabetaList = Util.tabetaTodayInfo(ryouriID, userID);
		//Favo確認SQLを食べたレシピに対して実行
		favoList = Util.favoInfo(ryouriID, userID);

		request.setAttribute("pageNum", pageNum);
		request.setAttribute("year", year);
		request.setAttribute("month", month);
		request.setAttribute("youbi", youbi);
		request.setAttribute("tabetaMonthList", tabetaMonthList);
		request.setAttribute("tabetaCountList", tabetaCountList);
		request.setAttribute("ryourimei", ryourimei);
		request.setAttribute("ryouriID", ryouriID);
		request.setAttribute("tabetaDayList", tabetaDayList);
		request.setAttribute("tabetaList", tabetaList);
		request.setAttribute("favoList", favoList);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
		rd_result.forward(request, response);
	}

}
