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
 * Servlet implementation class MyRecipePageServlet
 */
@WebServlet("/MyRecipePageServlet")
public class MyRecipePageServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		int pageNum = 1;
		if (!Objects.equals(request.getParameter("pageNum"), null)) {
			pageNum = Integer.parseInt(request.getParameter("pageNum"));
		}
		final int DATA_PER_PAGE = 10;
		final String JSP_PATH = "myrecipe.jsp";
		ArrayList<Integer> ryouriID = new ArrayList<>();
		ArrayList<String> ryourimei = new ArrayList<>();
		ArrayList<String> syoukai = new ArrayList<>();
		ArrayList<String> imageName = new ArrayList<>();
		String[] recipeBunryouRecord = new String[3]; //レコードに登録する食材名、分量、単位を格納する配列
		ArrayList<ArrayList<String[]>> recipeBunryouList = new ArrayList<>(); //検索結果に対応するレシピの食材名、分量、単位を格納するリスト
		ArrayList<Boolean> tabetaList = new ArrayList<>();
		ArrayList<Boolean> favoList = new ArrayList<>();
		int recipeNum = 0; //マイレシピ件数
		String userName = request.getRemoteUser();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");
		String sql1 = "select count(*) as cnt from RyouriTB where UserID = ?";
		String sql2;
		String sql3;

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//マイレシピ件数検索SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {

			prestmt.setInt(1,userID);
			System.out.println("マイレシピ件数検索SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				if (rs.next()) {
					recipeNum = rs.getInt("cnt");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("マイレシピ件数検索SQL完了");
		System.out.println("マイレシピ件数:" + recipeNum);

		if (recipeNum == 0) {
			//表示するマイレシピが0件なら遷移
			request.setAttribute("tabetaList", tabetaList);
			request.setAttribute("favoList", favoList);
			request.setAttribute("pageNum", pageNum);
			request.setAttribute("ryouriID", ryouriID);
			request.setAttribute("ryourimei", ryourimei);
			request.setAttribute("syoukai", syoukai);
			request.setAttribute("imageName", imageName);
			request.setAttribute("recipeBunryouList", recipeBunryouList);
			request.setAttribute("recipeNum", recipeNum);
			RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
			rd_result.forward(request, response);
			return;
		} else 	if (recipeNum <= DATA_PER_PAGE * (pageNum - 1)) {
			//pageTotalを超えるpageNumを送信されたらpageNumを上書き
			pageNum = (recipeNum - 1) / DATA_PER_PAGE + 1;
		}

		sql2 = "select RyouriID, Ryourimei, Syoukai, ImageName from RyouriTB where UserID = ? order by UpdateTime desc limit " + DATA_PER_PAGE;
		sql2 += " offset " + DATA_PER_PAGE * (pageNum - 1);

		//マイレシピ検索SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {

			prestmt.setInt(1,userID);
			System.out.println("マイレシピ検索SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryouriID.add(rs.getInt("RyouriID"));
					ryourimei.add(rs.getString("Ryourimei"));
					syoukai.add(rs.getString("Syoukai"));
					if (rs.getString("ImageName") == null) imageName.add("noimage.jpg"); //noimageの画像名を格納
					else imageName.add(rs.getString("ImageName")); //条件を満たす画像名を格納
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("マイレシピ検索SQL完了");
		System.out.println("マイレシピID:" + Arrays.toString(ryouriID.toArray()));

		sql3 = "select SyokuzaiTB.Syokuzaimei, BunryouTB.Bunryou, SyokuzaiTB.Tanni, BunryouTB.RyouriID from BunryouTB"
				+ " inner join SyokuzaiTB on BunryouTB.SyokuzaiID = SyokuzaiTB.SyokuzaiID inner join RyouriTB on BunryouTB.RyouriID = RyouriTB.RyouriID"
				+ " where BunryouTB.RyouriID in ('" + ryouriID.get(0);
		for (int i = 1; i < ryouriID.size(); i++) {
			sql3 += "', '" + ryouriID.get(i);
		}
		sql3 += "') order by field(BunryouTB.RyouriID, '" + ryouriID.get(0);
		for (int i = 1; i < ryouriID.size(); i++) {
			sql3 += "', '" + ryouriID.get(i);
		}
		sql3 += "')";

		//マイレシピ分量検索SQLの実行
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {

			System.out.println("マイレシピ分量検索SQL:" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				//レシピごとに必要な分量のデータが入ったArrayList<String[]> tempListを作成する
				//tempListの例:[[じゃがいも,1,個],[にんじん,2,本][牛肉,100,g]]
				ArrayList<String[]> tempList = new ArrayList<String[]>();
				boolean flag = false; //次のwhileの1回目のループの途中までfalse 残りはずっとtrue
				String pos = ""; //RyouriIDが格納される この値が次のレコードのRyouriIDと異なったらtempListをrecipeBunryouListに追加して新しいtempListを用意する
				while (rs.next()) {
					recipeBunryouRecord[0] = rs.getString("SyokuzaiTB.Syokuzaimei"); //条件を満たす食材名を格納
					recipeBunryouRecord[1] = rs.getString("BunryouTB.Bunryou"); //条件を満たす分量を格納
					recipeBunryouRecord[2] = rs.getString("SyokuzaiTB.Tanni"); //条件を満たす単位を格納
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("マイレシピ分量検索SQL完了");
		for (int i = 0; i < recipeBunryouList.size(); i++) {
			String buff = (i + 1) + "番目の分量:";
			for (int j = 0; j < recipeBunryouList.get(i).size(); j++) {
				buff += Arrays.toString(recipeBunryouList.get(i).get(j));
			}
			System.out.println(buff);
		}

		tabetaList = Util.tabetaTodayInfo(ryouriID, userID);
		favoList = Util.favoInfo(ryouriID, userID);

		request.setAttribute("tabetaList", tabetaList);
		request.setAttribute("favoList", favoList);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("ryouriID", ryouriID);
		request.setAttribute("ryourimei", ryourimei);
		request.setAttribute("syoukai", syoukai);
		request.setAttribute("imageName", imageName);
		request.setAttribute("recipeBunryouList", recipeBunryouList);
		request.setAttribute("recipeNum", recipeNum);
		RequestDispatcher rd_result = request.getRequestDispatcher(JSP_PATH);
		rd_result.forward(request, response);
	}

	@Override
	//PostメソッドでRecipeRegisterServletから遷移されるためdoGetを実行するようにする
	public void doPost(HttpServletRequest request, HttpServletResponse response)	throws IOException, ServletException {
		this.doGet(request, response);
	}
}
