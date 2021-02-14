package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
//Image-CloudStorage(1)
import java.io.File;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import pac1.func.Util;

/**
 * Servlet implementation class RecipeUpdateServlet
 */
@WebServlet("/RecipeUpdateServlet")
@MultipartConfig(location = "/tmp", maxFileSize = 1024 * 1024 * 1)
public class RecipeUpdateServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String ryourimei = "";
		String ryourikana = "";
		String tukurikata = "";
		String syoukai = "";
		//Image-CloudStorage(3)
		String imageName = "";
		String currentTime = "";
		String name = "";
		String userName = request.getRemoteUser();
		List<String> syokuzaikanalist = new ArrayList<>();
		String[] recipeBunryouRecord = new String[2];
		ArrayList<String[]> recipeBunryouList = new ArrayList<>();
		int ryouriID = 0;
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		final String SERVLET_PATH = "RecipeServlet";

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		HttpSession session = request.getSession();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		Part part = request.getPart("pic");
		if (part.getSize() > 0) {
			//Image-CloudStorage(1)
			name = this.getFileName(part);
	        //C:\Users\197029\Documents\pleiades\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\mystok\WEB-INFにuploadedフォルダを手動で作ること
	        part.write(getServletContext().getRealPath("/WEB-INF/uploaded") + "/" + name);
		}

        //受け取った値を変数に格納する
        ryourimei = request.getParameter("ryourimei");
		ryourikana = request.getParameter("ryourikana");
		tukurikata = request.getParameter("tukurikataTotal");
		syoukai = request.getParameter("syoukai");
		ryouriID = Integer.parseInt(request.getParameter("recipeID"));
		String tempStr = request.getParameter("syokuzaikanalist");
		tempStr = tempStr.substring(1, tempStr.length() - 1); //[じゃがいも, にんじん, ぎゅうにく, たまねぎ] の[]を外す
		syokuzaikanalist = Arrays.asList(tempStr.split(", "));
		System.out.println("syokuzaikanalist: " + Arrays.toString(syokuzaikanalist.toArray()));
		for (int i = 1; !Objects.equals(request.getParameter("syokuzaikana" + i), null); i++) {
			recipeBunryouRecord[0] = "" + (syokuzaikanalist.indexOf(request.getParameter("syokuzaikana" + i)) + 1);
			recipeBunryouRecord[1] = request.getParameter("bunryou" + i);
			System.out.println("recipeBunryouList[" + (i - 1) + "]: " + Arrays.toString(recipeBunryouRecord));
			recipeBunryouList.add(recipeBunryouRecord);
			recipeBunryouRecord = new String[2];
		}

		//tukurikataの改行コードを全て「/」に置き換える
		tukurikata = tukurikata.replace("\r\n", "/");
		tukurikata = tukurikata.replace("\r", "/");
		tukurikata = tukurikata.replace("\n", "/");
		//サニタイジング
		ryourimei = Util.sanitizing(ryourimei);
		ryourikana = Util.sanitizing(ryourikana);
		tukurikata = Util.sanitizing(tukurikata);
		syoukai = Util.sanitizing(syoukai);
		for (int i = 0; i < recipeBunryouList.size(); i++) recipeBunryouList.get(i)[1] = Util.sanitizing(recipeBunryouList.get(i)[1]);

		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		//料理修正SQL(RyouriTB),分量削除SQL,料理登録SQL(分量)
		sql1 = "update RyouriTB set RyouriKana = ?, Ryourimei = ?, Tukurikata = ?, Syoukai = ?, UpdateTime = ? where RyouriID = ? and UserID = ?";
		sql2 = "delete from BunryouTB where RyouriID = ?";
		sql3 = "insert into BunryouTB values (?, ?, ?, ?)";
		for (int i = 1; i < recipeBunryouList.size(); i++) {
			sql3 += ", (?, ?, ?, ?)";
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

		boolean accessable = false;
		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql1)) {
			prestmt.setString(1, ryourikana);
			prestmt.setString(2, ryourimei);
			prestmt.setString(3, tukurikata);
			prestmt.setString(4, syoukai);
			prestmt.setString(5, f.format(new Date())); //P323参照
			prestmt.setInt(6, ryouriID);
			prestmt.setInt(7, userID);
			System.out.println("料理修正SQL(RyouriTB):" + prestmt.toString());
			int ret = prestmt.executeUpdate();
			if (ret > 0) accessable = true; //修正に成功
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("料理修正SQL(RyouriTB)完了");

		if (!accessable) {
			System.out.println("レシピ編集に失敗しました");
			request.setAttribute("errorMessage", "レシピ編集に失敗しました。やり直してください。");
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setInt(1, ryouriID);
			System.out.println("分量削除SQL:" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("分量削除SQL完了");

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql3)) {
			for (int i = 0; i < recipeBunryouList.size(); i++) {
				prestmt.setInt(1 + 4 * i, ryouriID);
				prestmt.setString(2 + 4 * i, recipeBunryouList.get(i)[0]);
				prestmt.setString(3 + 4 * i, recipeBunryouList.get(i)[1]);
				prestmt.setInt(4 + 4 * i, userID);
			}
			System.out.println("料理登録SQL(分量):" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		//Image-CloudStorage(?)
                String sql4 = "update RyouriTB set ImageName = ?  where RyouriID = " + ryouriID;
                //画像がアップロードされたか判定
                if (part.getSize() > 0) {

                        System.out.println("画像うｐ判定True");

                        //TimeStamp用に、CurrentTimeを取得(表示形式はmilli second)
                        currentTime = String.valueOf(System.currentTimeMillis());
                        //imageNameをryouriID、現在時刻から生成(拡張子は含まない)
                        imageName = ryouriID + "-" + currentTime;

                        //Image変換処理が必要かどうか判定=>変換処理
                        String imageFolderPath = "/usr/local/tomcat/webapps/mystok/WEB-INF/uploaded";
                        String imagePath = imageFolderPath + "/" +name;

                        if(!name.endsWith(".jpg")) {
                                System.out.println("The value of name is " + name);
                                System.out.println("画像変換処理の要不要判定True");

                                String imageOutputPath = imageFolderPath + "/" + imageName + ".jpg";
                                ImageConverter ic = new ImageConverter();
                                ic.imageConverter(imagePath,imageOutputPath);

                                //変換前の画像を削除
                                File UploadedImage = new File(imagePath);
                                UploadedImage.delete();

                                imagePath = imageOutputPath;
                        }


                        //ImageをCloudStorageへUploadする
                        //第一引数は"アップロード後の名前",第二引数は"アップロード対象ファイルへの絶対パス"
                        System.out.println("画像うｐ開始");
                        UploadObject uo = new UploadObject();
                        uo.uploadObject("images/RyouriPIC/" + imageName + ".jpg",imagePath);
                        System.out.println("画像うｐ完了");

                        //jpg形式の画像ファイルをCloudStorageにアップロード後、コンテナから削除
                        System.out.println("うｐ画像をlocalから削除開始");
                        File JpgImage = new File(imagePath);
                        JpgImage.delete();
                        System.out.println("うｐ画像をlocalから削除完了");

                        //DBにCloudStorageへアップロードした画像のImageNameをInsertする
                        System.out.println("画像名をDBに挿入開始");


                        try (
                                Connection conn = DriverManager.getConnection(
                                        "jdbc:mysql://127.0.0.1:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
                                PreparedStatement prestmt = conn.prepareStatement(sql4)) {
                                prestmt.setString(1, imageName + ".jpg");
                                System.out.println("料理登録SQL(料理画像名):" + prestmt.toString());
                                prestmt.executeUpdate();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        System.out.println("料理登録SQL(料理画像名)完了");
                }
		System.out.println("料理登録SQL(分量)完了");

		response.sendRedirect(SERVLET_PATH + "?recipeID=" + ryouriID);
	}

	//サーバの指定のファイルパスへアップロードしたファイルを保存
	private String getFileName(Part part) {
        String name = null;
        for (String dispotion : part.getHeader("Content-Disposition").split(";")) {
            if (dispotion.trim().startsWith("filename")) {
                name = dispotion.substring(dispotion.indexOf("=") + 1).replace("\"", "").trim();
                name = name.substring(name.lastIndexOf("\\") + 1);
                break;
            }
        }
        return name;
    }

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		System.out.println("GETメソッドでRecipeUpdateServletにアクセスされました");
		request.setAttribute("errorMessage", "不正なアクセスです。やり直してください。");
		RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
		rd_result.forward(request, response);
	}
}
