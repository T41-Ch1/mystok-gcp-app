package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class TabetaDayDeleteServlet
 */
@WebServlet("/TabetaDayDeleteServlet")
public class TabetaDayDeleteServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final String JSP_PATH = "tabetadayvessel.jsp";
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
		int recipeID = Integer.parseInt(request.getParameter("recipeID"));

		String sql = "delete from TabetaTB where UserID = ? and RyouriID = ? and date_format(TabetaTB.TabetaTime, '%Y%m%d') = '" + year + month + day + "' ";

		request.setAttribute("recipeID", recipeID);
		request.setAttribute("logMessage", "");

		//認証チェック
		//if (!Util.checkAuth(request, response)) return; とするとログイン中ではない時ログインページに飛ぶ
		if (session == null || request.getRemoteUser() == null) {
			request.setAttribute("logMessage", "ログインしてから押してください");
			System.out.println("ログアウト状態でTabeta削除ボタンが押されました");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
		}

		//別のユーザーに切り替えたとき
		if (!userName.equals(request.getParameter("userName"))) {
			request.setAttribute("logMessage", "別のユーザーとしてログイン中です");
			System.out.println(request.getParameter("userName") + "としてTabeta削除ボタンが押されました、現在" + userName + "がログイン中です");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
		}

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
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			prestmt.setInt(1, userID);
			prestmt.setInt(2, recipeID);
			System.out.println("日別食べた履歴削除SQL: " + prestmt.toString());
			int ret = prestmt.executeUpdate();
			System.out.println("日別食べた履歴削除SQL完了");
			if (ret > 0) {
				request.setAttribute("mode", "Delete");
			} else {
				request.setAttribute("logMessage", "既に履歴が削除されています。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
		rd.forward(request, response);

	}

}
