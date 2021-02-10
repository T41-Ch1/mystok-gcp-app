package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
 * Servlet implementation class RecipeRegisterPageServlet
 */
@WebServlet("/RecipeRegisterPageServlet")
public class RecipeRegisterPageServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String recipe_kana = ""; //ふりがな
		String recipe_name = ""; //料理名
		String tukurikata = ""; //作り方
		String syoukai = ""; //紹介文
		String imageName = ""; //画像名 20210210追加
		String[] str = new String[3]; //順に食材名、分量、単位が格納される
		ArrayList<String[]> recipe_bunryou = new ArrayList<>(); //strの情報を順に格納する
		int recipeID = 0;//マイレシピ一覧画面からパラメータrecipeIDをgetして変数recipeIDに代入する
		String sql0 = "select * from RyouriTB where RyouriID = ? and userID = ?";

		ArrayList<String> syokuzaikanalist = new ArrayList<>();
		ArrayList<String> tannilist = new ArrayList<>();
		final String JSP_PATH = "recipeRegister.jsp";

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		String userName; //マイレシピページに表示したユーザ名
		if (Objects.equals(request.getParameter("userName"), null)) {
			userName = ""; //新規レシピ登録の際はnullを格納
		} else {
			userName = request.getRemoteUser(); //レシピ修正の際はユーザ名を格納
		}

		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		//食材名取得SQL
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select SyokuzaiKana, Tanni from SyokuzaiTB")) {
			while (rs.next()) {
				syokuzaikanalist.add(rs.getString("SyokuzaiKana")); //条件を満たすSyokuzaiKanaを格納
				tannilist.add(rs.getString("Tanni")); //条件を満たすTanniを格納
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("食材名取得SQL:select SyokuzaiKana, Tanni from SyokuzaiTB");
		System.out.println("食材名取得SQL完了");
		System.out.println("食材件数:" + syokuzaikanalist.size());

		if (userName.equals("")) {
			//requestに属性を追加してJSPにフォワードする
			request.setAttribute("syokuzaikanalist", syokuzaikanalist);
			request.setAttribute("tannilist", tannilist);
			request.setAttribute("recipeID", 0);
			RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
			rd_result.forward(request, response);
			return;
		}

		recipeID = Integer.parseInt(request.getParameter("recipeID"));//マイレシピ一覧画面からパラメータrecipeIDをgetして変数recipeIDに代入する
		String sql1 = "select RyouriKana, Ryourimei, Tukurikata, Syoukai, ImageName from RyouriTB where RyouriID = " + recipeID; // 20210210 ", ImageName"追加
		String sql2 = "select SyokuzaiTB.SyokuzaiKana, BunryouTB.Bunryou, SyokuzaiTB.Tanni from BunryouTB inner join SyokuzaiTB on BunryouTB.SyokuzaiID = SyokuzaiTB.SyokuzaiID where BunryouTB.RyouriID = " + recipeID;

		boolean accessable = false;
		//他人のマイレシピでないか確認
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql0)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("レシピ表示確認SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					accessable = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ表示確認SQL完了:" + accessable);

		if (!accessable) {
			RequestDispatcher rd_result = request.getRequestDispatcher("top.jsp");
			rd_result.forward(request, response);
			return;
		}

		//レシピ名、ふりがな、作り方、紹介文を検索
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql1)) {

			while (rs.next()) {
				recipe_kana = rs.getString("RyouriKana");
				recipe_name = rs.getString("Ryourimei");
				tukurikata = rs.getString("Tukurikata");
				syoukai = rs.getString("Syoukai");
				if (rs.getString("ImageName") == null) imageName = "noimage.jpg"; // 20210210追加
                                else imageName = rs.getString("ImageName"); // 20210210追加
			}
			System.out.println("レシピ詳細検索SQL(レシピ名、作り方):" + sql1);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ詳細検索SQL(レシピ名、作り方)完了");
		System.out.println("レシピ名:" + recipe_name + "作り方:" + tukurikata);

		//食材名、分量、単位を検索
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST", "mystok", "mySqlStok");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql2)) {
			//必要な分量のデータが入ったArrayList recipe_bunryou1を作成する
			//レコードstrの例:[[じゃがいも,1,個],[にんじん,2,本],[牛肉,100,g]]
			while (rs.next()) {
				str[0] = rs.getString("syokuzaitb.SyokuzaiKana");
				str[1] = rs.getString("bunryoutb.Bunryou");
				str[2] = rs.getString("syokuzaitb.Tanni");
				recipe_bunryou.add(str);
				str = new String[3];
			}
			System.out.println("レシピ詳細検索SQL(食材名、分量、単位):" + sql2);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ詳細検索SQL(食材名、分量、単位)完了");
		for (int i = 0; i < recipe_bunryou.size(); i++) {
			String buff = (i + 1) + "番目の分量:";
			for (int j = 0; j < 3; j++) {
				buff += recipe_bunryou.get(i)[j];
			}
			System.out.println(buff);
		}

		//requestに属性を追加してJSPにフォワードする
		request.setAttribute("syokuzaikanalist", syokuzaikanalist);
		request.setAttribute("tannilist", tannilist);
		request.setAttribute("recipe_kana", recipe_kana);
		request.setAttribute("recipe_name", recipe_name);
		request.setAttribute("tukurikata", tukurikata);
		request.setAttribute("syoukai", syoukai);
		request.setAttribute("imageName", imageName); // 20210210追加
		request.setAttribute("recipeID", recipeID);
		request.setAttribute("recipe_bunryou", recipe_bunryou);
		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH);
		rd.forward(request, response);
	}

	@Override
	//PostメソッドでRecipeRegisterServletから遷移されるためdoGetを実行するようにする
	public void doPost(HttpServletRequest request, HttpServletResponse response)	throws IOException, ServletException {
		this.doGet(request, response);
	}

}
