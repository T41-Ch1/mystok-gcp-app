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
 * Servlet implementation class RecipeServlet
 */
@WebServlet("/RecipeServlet")
public class RecipeServlet extends HttpServlet {
	int recipeID = 0; //表示するレシピのID
	String searchMode = ""; //検索窓のラジオボタンに最初からチェックを入れる方
	String input = ""; //検索窓に最初から表示させる文字列
	String recipe_name = ""; //料理名
	String tukurikata = ""; //作り方
	String imageName = ""; //画像名
	String[] str = new String[3]; //順に食材名、分量、単位が格納される
	final String JSP_PATH0 = "top.jsp"; //遷移先のJSP
	final String JSP_PATH1 = "recipe.jsp"; //遷移先のJSP

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<String[]> recipe_bunryou = new ArrayList<>(); //strの情報を順に格納する

		//recipeID, searchMode, inputの準備
		if (Objects.equals(request.getParameter("recipeID"), null)) {
			RequestDispatcher rd_result = request.getRequestDispatcher("JSP_PATH0");
			rd_result.forward(request, response);
			return;
		}
		recipeID = Integer.parseInt(request.getParameter("recipeID"));//検索結果画面からパラメータrecipeIDをgetして変数recipeIDに代入する
		System.out.println("レシピID:" + request.getParameter("recipeID"));
		if (Objects.equals(request.getParameter("searchMode"), null)) {
			searchMode = "syokuzai";
		} else {
			searchMode = request.getParameter("searchMode"); //検索窓のラジオボタンに最初からチェックを入れる方を取得する
		}
		if (Objects.equals(request.getParameter("input"), null)) {
			input = "";
		} else {
			input = request.getParameter("input"); //検索窓に最初から表示させる文字列を取得する
		}

		HttpSession session = request.getSession();
		String userName = request.getRemoteUser(); //ユーザ名 ログイン中でなければnullが格納される
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		boolean isMyRecipe = false; //ログイン中のユーザのマイレシピかどうか
		boolean tabeta = false; //ログイン中のユーザが今日食べた登録しているかどうか
		boolean favo = false; //ログイン中のユーザがお気に入り登録しているかどうか

		//DBに接続し、recipeIDに該当するレシピ名、作り方、食材を検索する
		//SQLの組み立て
		String sql1 = "select Ryourimei, Tukurikata, ImageName, UserID from RyouriTB where RyouriID = ? and userID in(?, 1)";
		String sql2 = "select SyokuzaiTB.Syokuzaimei, BunryouTB.Bunryou, SyokuzaiTB.Tanni from BunryouTB inner join SyokuzaiTB on BunryouTB.SyokuzaiID = SyokuzaiTB.SyokuzaiID where BunryouTB.RyouriID = ?";

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//レシピ名、作り方、画像名を検索
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST", "root", "password");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setInt(1, recipeID);
			prestmt.setInt(2, userID);
			System.out.println("レシピ詳細検索SQL(レシピ名、作り方、画像名): " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				if (rs.next()) {
					recipe_name = rs.getString("Ryourimei");
					tukurikata = rs.getString("Tukurikata");
					if (rs.getString("ImageName") == null) imageName = "noimage.jpg";
					else imageName = rs.getString("ImageName");
					isMyRecipe = rs.getInt("UserID") == userID;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("レシピ詳細検索SQL(レシピ名、作り方、画像名)完了");
		System.out.println("レシピ名:" + recipe_name + ", 作り方:" + tukurikata + ", 画像名" + imageName);

		if (recipe_name.equals("")) {
			RequestDispatcher rd_result = request.getRequestDispatcher("JSP_PATH0");
			rd_result.forward(request, response);
			return;
		}

		//食材名、分量、単位を検索
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST", "root", "password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setInt(1, recipeID);
			System.out.println("レシピ詳細検索SQL(食材名、分量、単位):" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				//必要な分量のデータが入ったArrayList recipe_bunryou1を作成する
				//レコードstrの例:[[じゃがいも,1,個],[にんじん,2,本],[牛肉,100,g]]
				while (rs.next()) {
					str[0] = rs.getString("syokuzaitb.Syokuzaimei");
					str[1] = rs.getString("bunryoutb.Bunryou");
					str[2] = rs.getString("syokuzaitb.Tanni");
					recipe_bunryou.add(str);
					str = new String[3];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("レシピ詳細検索SQL(食材名、分量、単位)完了");
		for (int i = 0; i < recipe_bunryou.size(); i++) {
			System.out.println((i + 1) + "番目の分量:" + Arrays.toString(recipe_bunryou.get(i)));
		}

		//Tabeta確認SQLを表示レシピに対して実行
		tabeta = Util.tabetaTodayInfo(recipeID, userID);
		//Favo確認SQLを表示レシピに対して実行
		favo = Util.favoInfo(recipeID, userID);

		//requestに属性を追加してJSPにフォワードする
		request.setAttribute("searchMode", searchMode);
		request.setAttribute("input", input);
		request.setAttribute("recipe_name", recipe_name);
		request.setAttribute("tukurikata", tukurikata);
		request.setAttribute("imageName", imageName);
		request.setAttribute("recipeID", recipeID);
		request.setAttribute("isMyRecipe", isMyRecipe);
		request.setAttribute("tabeta", tabeta);
		request.setAttribute("favo", favo);
		request.setAttribute("recipe_bunryou", recipe_bunryou);
		RequestDispatcher rd = request.getRequestDispatcher(JSP_PATH1);
		rd.forward(request, response);
	}
}