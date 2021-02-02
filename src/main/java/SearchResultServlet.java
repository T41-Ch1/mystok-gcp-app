package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
 * Servlet implementation class SearchResultServlet
 */
@WebServlet("/SearchResultServlet")
public class SearchResultServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userName = request.getRemoteUser(); //ユーザ名 ログイン中でなければnullが格納される
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		String[] inputData; //検索窓に入力された文字列を全角スペースで分割して順に格納する配列
		String searchMode; //検索モード 料理名検索ならryouri 食材名検索ならsyokuzaiが格納される
		boolean onlyFavo; //検索モード お気に入りのみを表示するが格納される
		String favoTerm; //お気に入りのみを表示するSQLの文字列
		boolean onlyMy; //検索モード マイレシピのみを表示するが格納される
		final String JSP_PATH0 = "top.jsp"; //メインページのJSP
		final String JSP_PATH1 = "searchResult.jsp"; //検索結果ページのJSP
		String sql = ""; //DBに送信するためのSQL文を格納する
		String[] recipeBunryouRecord = new String[3]; //レコードに登録する食材名、分量、単位を格納する配列
		ArrayList<Integer> recipeID = new ArrayList<Integer>(); //検索結果に対応するレシピのIDを格納するリスト
		ArrayList<String> recipeTitle = new ArrayList<String>(); //検索結果に対応するレシピの名前を格納するリスト
		ArrayList<String> recipeIntro = new ArrayList<String>(); //検索結果に対応するレシピの紹介文を格納するリスト
		ArrayList<String> imageName = new ArrayList<String>(); //検索結果に対応するレシピの画像名を格納するリスト
		ArrayList<ArrayList<String[]>> recipeBunryouList = new ArrayList<>(); //検索結果に対応するレシピの食材名、分量、単位を格納するリスト
		ArrayList<Boolean> isMyRecipe = new ArrayList<>(); //検索結果に対応するレシピのマイレシピかどうかを格納するリスト
		ArrayList<Boolean> tabetaList = new ArrayList<Boolean>(); //検索結果に対応するレシピの今日食べたかどうかを格納するリスト
		ArrayList<Boolean> favoList = new ArrayList<Boolean>(); //検索結果に対応するレシピのお気に入りかどうかを格納するリスト

		int pageNum; //検索結果ページのページ番号
		if (Objects.equals(request.getParameter("pageNum"), null)) {
			pageNum = 1; //pageNumのパラメータがnullなら1ページ目を表示
		} else {
			pageNum = Integer.parseInt(request.getParameter("pageNum")); //そうでないなら送信されたパラメータpageNumを格納
		}
		final int DATA_PER_PAGE = 10; //1ページごとに表示する最大件数
		int recipeNum = 0; //表示するデータの件数

		//inputDataとsearchModeとfavoTermとonlyFavoとonlyMyの準備
		if (Objects.equals(request.getParameter("searchMode"), null)) {
			 //searchModeのパラメータがnullなら食材名検索
			 //検索結果ページの一番消費したい食材を選びなおす機能から実行されたときはここ
			searchMode = "syokuzai";
		} else {
			searchMode = request.getParameter("searchMode"); //そうでないなら送信されたパラメータsearchModeを格納
		}
		String input; //入力されたデータを格納
		if (Objects.equals(request.getParameter("input"), null)) {
			input = ""; //inputのパラメータがnullなら空白
		} else {
			input = Util.sanitizing(request.getParameter("input")); //そうでないなら送信されたパラメータpageNumをサニタイジングしてから格納
		}
		while (input.contains("　　")) input = input.replace("　　", "　"); //スペースが連続していたら1つに圧縮
		if (input.length() > 0){
			if (input.charAt(0) == '　') input = input.substring(1); //スペースから始まっていたら削る
			if (searchMode.equals("syokuzai")) inputData = input.split("　"); //inputが1文字以上で、食材名検索なら半角スペースで分割
			else {
				inputData = new String[1];
				inputData[0] = input.replaceAll("　", "|"); //inputが1文字以上で、料理名検索なら空白を|に置換
			}
		} else {
			inputData = new String[0]; //inputが0文字ならinputDataは要素数0 splitで生成すると要素数が1になる
		}
		if (!Objects.equals(request.getParameter("onlyfavo"), null) && request.getParameter("onlyfavo").equals("1") && userID > 1) {
			favoTerm = " and RyouriID in (select RyouriID from FavoTB where UserID = " + userID + ") "; //ログイン中のユーザがお気に入り登録したレシピのみ表示
			onlyFavo = true; //onlyfavoが"1"だったときのみtrue
		} else {
			favoTerm = "";
			onlyFavo = false; //そうでないならfalse
		}
		if (!Objects.equals(request.getParameter("onlymy"), null) && request.getParameter("onlymy").equals("1") && userID > 1) {
			onlyMy = true; //onlymyが"1"だったときのみtrue
		} else {
			onlyMy = false; //そうでないならfalse
		}

		//入力文字列がない場合の処理
		if (inputData.length == 0) {
			//何も入力されなかったらトップページに遷移する処理 URLを削って入力されたときに呼ばれうる
			RequestDispatcher rd_top = request.getRequestDispatcher(JSP_PATH0);
			rd_top.forward(request, response);
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

		//レシピ件数検索SQL
		if (searchMode.equals("ryouri")) {
			//レシピ件数検索SQL(料理名検索)の組み立て
			sql = "select count(RyouriID) from RyouriTB where RyouriKana regexp ?";
			if (onlyMy) sql += " and UserID = " + userID + favoTerm;
			else sql += " and UserID in (" + userID + ", 1)" + favoTerm;
		} else {
			//レシピ件数検索SQL(食材名検索)の組み立て
			int dataNum = 1; //入力された食材の個数を格納する(重複するものを除く)
			if (onlyMy) sql = "select count(RyouriID) from RyouriTB where UserID = " + userID + " and RyouriID in (select RyouriID from BunryouTB where RyouriID in (select RyouriID from BunryouTB where SyokuzaiID in (select SyokuzaiID from SyokuzaiTB where SyokuzaiKana in (?";
			else sql = "select count(RyouriID) from RyouriTB where UserID in (" + userID + ", 1) and RyouriID in (select RyouriID from BunryouTB where RyouriID in (select RyouriID from BunryouTB where SyokuzaiID in (select SyokuzaiID from SyokuzaiTB where SyokuzaiKana in (?";
			for (int i = 1; i < inputData.length; i++) {
				//すでに追加されたデータではないものを追加する
				boolean dataExists = false;
				for (int j = 0; j < i; j++) {
					if (inputData[i].equals(inputData[j])) {
						dataExists = true;
					}
				}
				if (!dataExists) {
					dataNum++;
					sql += ", ?";
				}
			}
			sql += ")) group by RyouriID having count(RyouriID) = " + dataNum + ") and SyokuzaiID = (select SyokuzaiID from SyokuzaiTB where SyokuzaiKana = ?) " + favoTerm + " order by Bunryou desc);";
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			if (searchMode.equals("ryouri")) {
				prestmt.setString(1, inputData[0]);
			} else {
				int dataNum = 1; //入力された食材の個数を格納する(重複するものを除く)
				prestmt.setString(1, inputData[0]);
				for (int i = 1; i < inputData.length; i++) {
					//すでに追加されたデータではないものを追加する
					boolean dataExists = false;
					for (int j = 0; j < i; j++) {
						if (inputData[i].equals(inputData[j])) {
							dataExists = true;
						}
					}
					if (!dataExists) {
						dataNum++;
						prestmt.setString(dataNum, inputData[i]);
					}
				}
				prestmt.setString(dataNum + 1, inputData[0]);
				System.out.println("レシピ件数検索SQL: "+ prestmt.toString());
			}
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					recipeNum = rs.getInt("count(RyouriID)"); //条件を満たすRyouriIDの件数を格納
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ件数検索SQL完了");
		System.out.println("レシピ件数:" + recipeNum);

		if (recipeNum == 0) {
			//requestに属性を追加してJSPにフォワードする
			request.setAttribute("tabetaList", tabetaList);
			request.setAttribute("favoList", favoList);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("searchMode", searchMode);
			request.setAttribute("inputData", inputData);
			request.setAttribute("onlyFavo", onlyFavo);
			request.setAttribute("onlyMy", onlyMy);
			request.setAttribute("recipeID", recipeID);
			request.setAttribute("recipeNum", recipeNum);
			request.setAttribute("recipeTitle", recipeTitle);
			request.setAttribute("recipeIntro", recipeIntro);
			request.setAttribute("imageName", imageName);
			request.setAttribute("recipeBunryouList", recipeBunryouList);
			RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH1);
			rd_result.forward(request, response);
			return;
		} else 	if (recipeNum <= DATA_PER_PAGE * (pageNum - 1)) {
			//pageTotalを超えるpageNumを送信されたらpageNumを上書き
			pageNum = (recipeNum - 1) / DATA_PER_PAGE + 1;
		}

		if (searchMode.equals("ryouri")) {
			//表示レシピ検索SQL(料理名検索)の組み立て
			sql = "select RyouriID from RyouriTB where RyouriKana regexp ?";
			if (onlyMy) sql += " and UserID = " + userID + favoTerm + " order by UpdateTime desc limit " + DATA_PER_PAGE + " offset " + DATA_PER_PAGE * (pageNum - 1);
			else sql += " and UserID in (" + userID + ", 1)" + favoTerm + " order by UpdateTime desc limit " + DATA_PER_PAGE + " offset " + DATA_PER_PAGE * (pageNum - 1);
		} else {
			//表示レシピ検索SQL(食材名検索)の組み立て
			int dataNum = 1; //入力された食材の個数を格納する(重複するものを除く)
			if (onlyMy) sql = "select RyouriID from BunryouTB where UserID = " + userID + " and RyouriID in (select RyouriID from BunryouTB where SyokuzaiID in (select SyokuzaiID from SyokuzaiTB where SyokuzaiKana in (?";
			else sql = "select RyouriID from BunryouTB where UserID in (" + userID + ", 1) and RyouriID in (select RyouriID from BunryouTB where SyokuzaiID in (select SyokuzaiID from SyokuzaiTB where SyokuzaiKana in (?";
			for (int i = 1; i < inputData.length; i++) {
				//すでに追加されたデータではないものを追加する
				boolean dataExists = false;
				for (int j = 0; j < i; j++) {
					if (inputData[i].equals(inputData[j])) {
						dataExists = true;
					}
				}
				if (!dataExists) {
					dataNum++;
					sql += ", ?";
				}
			}
			sql += ")) group by RyouriID having count(RyouriID) = " + dataNum + ") and SyokuzaiID = (select SyokuzaiID from SyokuzaiTB where SyokuzaiKana = ?)";
			sql += favoTerm + " order by Bunryou desc limit " + DATA_PER_PAGE + " offset " + DATA_PER_PAGE * (pageNum - 1);
		}

		//表示レシピ検索SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			if (searchMode.equals("ryouri")) {
				prestmt.setString(1, inputData[0]);
			} else {
				int dataNum = 1; //入力された食材の個数を格納する(重複するものを除く)
				prestmt.setString(1, inputData[0]);
				for (int i = 1; i < inputData.length; i++) {
					//すでに追加されたデータではないものを追加する
					boolean dataExists = false;
					for (int j = 0; j < i; j++) {
						if (inputData[i].equals(inputData[j])) {
							dataExists = true;
						}
					}
					if (!dataExists) {
						dataNum++;
						prestmt.setString(dataNum, inputData[i]);
					}
				}
				prestmt.setString(dataNum + 1, inputData[0]);
			}
			System.out.println("表示レシピ検索SQL: " + prestmt.toString());

			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					recipeID.add(rs.getInt("RyouriID")); //条件を満たすRyouriIDを格納
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("表示レシピ検索SQL完了");
		System.out.println("レシピID:" + Arrays.toString(recipeID.toArray()));

		//レシピ概要検索SQL(料理名、紹介文、画像名)の組み立て
		sql = "select Ryourimei, Syoukai, ImageName, UserID from RyouriTB where RyouriID in ('" + recipeID.get(0);
		for (int i = 1; i < recipeID.size(); i++) {
			sql += "', '" + recipeID.get(i);
		}
		sql += "') order by field(RyouriID, '" + recipeID.get(0);
		for (int i = 1; i < recipeID.size(); i++) {
			sql += "', '" + recipeID.get(i);
		}
		sql += "')";
		System.out.println(sql);

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				recipeTitle.add(rs.getString("Ryourimei")); //条件を満たすレシピ名を格納
				recipeIntro.add(rs.getString("Syoukai")); //条件を満たすレシピ紹介文を格納
				if (rs.getString("ImageName") == null) imageName.add("noimage.jpg"); //条件を満たす画像名を格納
				else imageName.add(rs.getString("ImageName")); //条件を満たす画像名を格納
				isMyRecipe.add(rs.getInt("UserID") == userID); //条件を満たすレシピがマイレシピかどうかを格納
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ概要検索SQL(料理名、紹介文)完了");
		System.out.println("レシピ名:" + Arrays.toString(recipeTitle.toArray()));
		System.out.println("レシピ紹介文:" + Arrays.toString(recipeIntro.toArray()));
		System.out.println("画像名:" + Arrays.toString(imageName.toArray()));

		//レシピ概要検索SQL(分量)の組み立て
		sql = "select BunryouTB.RyouriID, SyokuzaiTB.Syokuzaimei, BunryouTB.Bunryou, SyokuzaiTB.Tanni from BunryouTB inner join SyokuzaiTB on BunryouTB.SyokuzaiID = SyokuzaiTB.SyokuzaiID where BunryouTB.RyouriID in ('" + recipeID.get(0);
		for (int i = 1; i < recipeID.size(); i++) {
			sql += "', '" + recipeID.get(i);
		}
		sql += "') order by field(BunryouTB.RyouriID, '" + recipeID.get(0);
		for (int i = 1; i < recipeID.size(); i++) {
			sql += "', '" + recipeID.get(i);
		}
		sql += "'), field(SyokuzaiTB.SyokuzaiKana, '" + inputData[inputData.length - 1];
		for (int i = inputData.length - 2; i >= 0; i--) {
			sql += "', '" + inputData[i];
		}
		sql += "') desc";
		System.out.println(sql);

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			//レシピごとに必要な分量のデータが入ったArrayList<String[]> tempListを作成する
			//tempListの例:[[じゃがいも,1,個],[にんじん,2,本][牛肉,100,g]]
			ArrayList<String[]> tempList = new ArrayList<String[]>();
			boolean flag = false; //次のwhileの1回目のループの途中までfalse 残りはずっとtrue
			String pos = ""; //RyouriIDが格納される この値が次のレコードのRyouriIDと異なったらtempListをrecipeBunryouListに追加して新しいtempListを用意する
			while (rs.next()) {
				recipeBunryouRecord[0] = rs.getString("syokuzaitb.Syokuzaimei"); //条件を満たす食材名を格納
				recipeBunryouRecord[1] = rs.getString("bunryoutb.Bunryou"); //条件を満たす分量を格納
				recipeBunryouRecord[2] = rs.getString("syokuzaitb.Tanni"); //条件を満たす単位を格納
				if (flag && !pos.equals(rs.getString("BunryouTB.RyouriID"))) { //最初のレコードでなく、前回のレコードとRyouriIDが異なったら
					recipeBunryouList.add(tempList); //tempListをrecipeBunryouListに追加して
					tempList = new ArrayList<String[]>(); //新しいtempListを用意する
				}
				tempList.add(recipeBunryouRecord); //tempListにレコードを追加する
				pos = rs.getString("BunryouTB.RyouriID"); //RyouriIDを更新する
				recipeBunryouRecord = new String[3]; //新しいrecipeBunryouRecordを用意する
				flag = true; //2件目以降のwhile文はtrueで回す
			}
			recipeBunryouList.add(tempList); //最後のレコードをrecipeBunryouListに追加する
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("レシピ概要検索SQL(分量)完了");
		for (int i = 0; i < recipeBunryouList.size(); i++) {
			String buff = (i + 1) + "番目の分量:";
			for (int j = 0; j < recipeBunryouList.get(i).size(); j++) {
				buff += Arrays.toString(recipeBunryouList.get(i).get(j));
			}
			System.out.println(buff);
		}

		//Tabeta確認SQLを表示レシピに対して実行
		tabetaList = Util.tabetaTodayInfo(recipeID, userID);
		//Favo確認SQLを表示レシピに対して実行
		favoList = Util.favoInfo(recipeID, userID);

		//requestに属性を追加してJSPにフォワードする
		request.setAttribute("tabetaList", tabetaList);
		request.setAttribute("favoList", favoList);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("searchMode", searchMode);
		request.setAttribute("inputData", inputData);
		request.setAttribute("onlyFavo", onlyFavo);
		request.setAttribute("onlyMy", onlyMy);
		request.setAttribute("recipeID", recipeID);
		request.setAttribute("recipeNum", recipeNum);
		request.setAttribute("recipeTitle", recipeTitle);
		request.setAttribute("recipeIntro", recipeIntro);
		request.setAttribute("imageName", imageName);
		request.setAttribute("isMyRecipe", isMyRecipe);
		request.setAttribute("recipeBunryouList",recipeBunryouList);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH1);
		rd_result.forward(request, response);
	}

}
