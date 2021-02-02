package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class TabetaInsertServlet
 */
@WebServlet("/TabetaInsertServlet")
public class TabetaInsertServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final String JSP_PATH = "tabeta_favo_switch.jsp"; //vesselに表示させるJSP名
		final int TABETA_MAX_PER_DAY = 5;
		String userName = request.getRemoteUser(); //ユーザ名 ログイン中でなければnullが格納される
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		int recipeID = Integer.parseInt(request.getParameter("recipeID"));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat fDay = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();

		ArrayList<Integer> tabetaTodayList = new ArrayList<>();

		String sql1 = "select RyouriID from TabetaTB where UserID = ? and date_format(TabetaTB.TabetaTime, '%Y%m%d') = '" + fDay.format(date) + "' ";
		String sql2 = "insert into TabetaTB (UserID, RyouriID, TabetaTime) values (?, ?, ?)";

		request.setAttribute("recipeID", recipeID);
		request.setAttribute("buttonType", request.getParameter("buttonType"));
		request.setAttribute("buttonState", request.getParameter("buttonState"));
		request.setAttribute("buttonSize", request.getParameter("buttonSize"));

		//認証チェック
		//if (!Util.checkAuth(request, response)) return; とするとログイン中ではない時ログインページに飛ぶ
		if (session == null || request.getRemoteUser() == null) {
			request.setAttribute("logMessage", "ログインしてから押してください");
			System.out.println("ログアウト状態でTabetaされました");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
		}

		//別のユーザーに切り替えたとき
		if (!userName.equals(request.getParameter("userName"))) {
			request.setAttribute("logMessage", "別のユーザーとしてログイン中です");
			System.out.println(request.getParameter("userName") + "としてTabetaされました、現在" + userName + "がログイン中です");
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
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {

			prestmt.setInt(1, userID);
			System.out.println("Tabeta件数・重複チェックSQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					if (rs.getInt("RyouriID") == recipeID) {
						System.out.println("すでにTabetaされています");
						request.setAttribute("logMessage", "");
						RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
						rd.forward(request, response);
						return;
					}
					tabetaTodayList.add(rs.getInt("RyouriID"));
					if (tabetaTodayList.size() >= TABETA_MAX_PER_DAY) {
						System.out.println("今日のTabeta数の上限です");
						request.setAttribute("logMessage", "今日の食べた押下数の上限です");
						RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
						rd.forward(request, response);
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("Tabeta件数・重複チェックSQL完了");

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {

			prestmt.setInt(1, userID);
			prestmt.setInt(2, recipeID);
			prestmt.setString(3, f.format(date)); //P323参照
			System.out.println("Tabeta登録SQL:" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("Tabeta登録SQL完了");
		request.setAttribute("logMessage", "");
		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
		rd.forward(request, response);
	}

}
