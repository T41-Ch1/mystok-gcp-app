package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
 * Servlet implementation class TabetaDayPageServlet
 */
@WebServlet("/TabetaDayPageServlet")
public class TabetaDayPageServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final String JSP_PATH = "tabetaday.jsp";
		String year; //表示するデータの年
		String month; //表示するデータの月
		String day; //表示するデータの日
		if (Objects.equals(request.getParameter("year"), null) || Objects.equals(request.getParameter("month"), null) || Objects.equals(request.getParameter("day"), null)) {
			//yearかmonthかdayのパラメータがnullならエラーページを表示
			System.out.println("不正な年/月/日が入力されました");
			request.setAttribute("errorMessage", "不正なパラメータが入力されました。");
			RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
			rd.forward(request, response);
			return;
		} else {
			//そうでないなら送信されたパラメータを格納
			year = request.getParameter("year");
			month = request.getParameter("month");
			day = request.getParameter("day");
		}
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
		int dayMax = c.getActualMaximum(Calendar.DATE); //月末が何日か(30日まであれば30が格納される)

		ArrayList<String> ryourimei = new ArrayList<>();
		ArrayList<Integer> ryouriID = new ArrayList<>();
		ArrayList<String> imageName = new ArrayList<>();
		ArrayList<Boolean> isMyRecipe = new ArrayList<>();
		ArrayList<Boolean> tabetaList = new ArrayList<>();
		ArrayList<Boolean> favoList = new ArrayList<>();

		try {
			if (Integer.parseInt(year) < 2000 || Integer.parseInt(year) > 2100 || year.length() != 4 || Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12 || month.length() != 2
					|| Integer.parseInt(day) < 1 || Integer.parseInt(day) > dayMax || month.length() != 2) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("不正な年/月/日が入力されました");
			request.setAttribute("errorMessage", "不正なパラメータが入力されました。");
			RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
			rd.forward(request, response);
			return;
		}

		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		String sql = "select RyouriTB.Ryourimei, TabetaTB.RyouriID, RyouriTB.ImageName, RyouriTB.UserID from TabetaTB inner join RyouriTB on RyouriTB.RyouriID = TabetaTB.RyouriID "
				+ "where TabetaTB.UserID = ? and date_format(TabetaTB.TabetaTime, '%Y%m%d') = '" + year + month + day + "' "
				+ "order by TabetaTB.TabetaTime";

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
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			prestmt.setInt(1, userID);
			System.out.println("日別食べた履歴検索SQL: " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryourimei.add(rs.getString("RyouriTB.Ryourimei"));
					ryouriID.add(rs.getInt("TabetaTB.RyouriID"));
					if (rs.getString("ImageName") == null) imageName.add("noimage.jpg"); //条件を満たす画像名を格納
					else imageName.add(rs.getString("ImageName")); //条件を満たす画像名を格納
					isMyRecipe.add(rs.getInt("RyouriTB.UserID") == userID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("日別食べた履歴検索SQL完了");
		System.out.println(year + "年" + month + "月" + day + "に食べた料理名: " + Arrays.toString(ryourimei.toArray()) +
				" 料理ID: " + Arrays.toString(ryouriID.toArray()) + " 画像名: " + Arrays.toString(imageName.toArray()));

		//Tabeta確認SQLを表示レシピに対して実行
		tabetaList = Util.tabetaTodayInfo(ryouriID, userID);
		//Favo確認SQLを食べたレシピに対して実行
		favoList = Util.favoInfo(ryouriID, userID);

		request.setAttribute("year", year);
		request.setAttribute("month", month);
		request.setAttribute("day", day);
		request.setAttribute("ryourimei", ryourimei);
		request.setAttribute("ryouriID", ryouriID);
		request.setAttribute("imageName", imageName);
		request.setAttribute("isMyRecipe", isMyRecipe);
		request.setAttribute("tabetaList", tabetaList);
		request.setAttribute("favoList", favoList);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
		rd_result.forward(request, response);
	}

	public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		this.doGet(request, response);
	}
}
