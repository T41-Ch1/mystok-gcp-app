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

/**
 * Servlet implementation class TabetaDeleteServlet
 */
@WebServlet("/TabetaDeleteServlet")
public class TabetaDeleteServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		final String JSP_PATH = "tabeta_favo_switch.jsp"; //vesselに表示させるJSP名
		String userName = request.getRemoteUser(); //ユーザ名 ログイン中でなければnullが格納される
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		int recipeID = Integer.parseInt(request.getParameter("recipeID"));
		String sql = "delete from TabetaTB where UserID = ? and RyouriID = ?";

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
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {

			prestmt.setInt(1, userID);
			prestmt.setInt(2, recipeID);
			System.out.println("Tabeta削除SQL:" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Tabeta削除SQL完了");
		request.setAttribute("logMessage", "");
		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
		rd.forward(request, response);
	}

}
