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

/**
 * Servlet implementation class TabetaDayPreviewServlet
 */
@WebServlet("/TabetaDayPreviewServlet")
public class TabetaDayPreviewServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int recipeID = 0; //表示するレシピのID
		String ryourimei = ""; //料理名
		String imageName = "noimage.jpg"; //料理画像名
		recipeID = Integer.parseInt(request.getParameter("recipeID"));//検索結果画面からパラメータrecipeIDをgetして変数recipeIDに代入する
		final String JSP_PATH = "tabetadayvessel.jsp";

		HttpSession session = request.getSession();
		String userName = request.getRemoteUser(); //ユーザ名 ログイン中でなければnullが格納される
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		//DBに接続し、recipeIDに該当するレシピ名、作り方、食材を検索する
		//SQLの組み立て
		String sql = "select Ryourimei, ImageName from RyouriTB where RyouriID = ? and userID in(?, 1)";

		request.setAttribute("year", request.getParameter("year"));
		request.setAttribute("month", request.getParameter("month"));
		request.setAttribute("day", request.getParameter("day"));
		request.setAttribute("recipeID", recipeID);
		request.setAttribute("ryourimei", "");
		request.setAttribute("imageName", "noimage.jpg");
		request.setAttribute("logMessage", "");

		//認証チェック
		//if (!Util.checkAuth(request, response)) return; とするとログイン中ではない時ログインページに飛ぶ
		if (session == null || request.getRemoteUser() == null) {
			request.setAttribute("logMessage", "ログインしてから押してください");
			System.out.println("ログアウト状態でレシピプレビューボタンが押されました");
			RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
			rd.forward(request, response);
			return;
		}

		//別のユーザーに切り替えたとき
		if (!userName.equals(request.getParameter("userName"))) {
			request.setAttribute("logMessage", "別のユーザーとしてログイン中です");
			System.out.println(request.getParameter("userName") + "としてレシピプレビューボタンが押されました、現在" + userName + "がログイン中です");
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

		//レシピ名、作り方、画像名を検索
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST", "mystok", "mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("レシピプレビューSQL: " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				if (rs.next()) {
					ryourimei = rs.getString("Ryourimei");
					if (rs.getString("ImageName") == null) imageName = "noimage.jpg"; //条件を満たす画像名を格納
					else imageName = rs.getString("ImageName"); //条件を満たす画像名を格納
					System.out.println("レシピプレビューSQL完了");
					System.out.println("レシピ名:" + ryourimei + ", 画像名: " + imageName);
					request.setAttribute("ryourimei", ryourimei);
					request.setAttribute("imageName", imageName);
					request.setAttribute("mode", "Preview");
					RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
					rd.forward(request, response);
				} else {
					System.out.println("レシピプレビューSQL完了");
					System.out.println("レシピが見つかりませんでした。");
					request.setAttribute("mode", "NotFound");
					RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
					rd.forward(request, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
	}

}
