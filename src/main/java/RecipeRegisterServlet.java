package pac1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
//Image-CloudStorage(1)
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 * Servlet implementation class RecipeRegisterServlet
 */
@WebServlet("/RecipeRegisterServlet")
@MultipartConfig(location = "/tmp", maxFileSize = 1024 * 1024 * 1)
public class RecipeRegisterServlet extends HttpServlet {

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
		//Image-CloudStorage(1)
		String sql4 = "insert into RyouriTB (ImageName) values (?) where ryouriID = " + ryouriID;
		final String SERVLET_PATH = "RecipeServlet";

		//認証チェック
		if (!Util.checkAuth(request, response)) return;

		HttpSession session = request.getSession();
		int userID; //ユーザID ログイン中でなければ0が格納される
		if (userName == null) userID = 0;
		else userID = (int)session.getAttribute("auth.userid");

		Part part = request.getPart("pic");
		if (part.getSize() > 0) {
			name = this.getFileName(part);
	        //C:\Users\197029\Documents\pleiades\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\mystok\WEB-INFにuploadedフォルダを手動で作ること
	        part.write(getServletContext().getRealPath("/WEB-INF/uploaded") + "/" + name);
		}

        //受け取った値を変数に格納する
        ryourimei = request.getParameter("ryourimei");
		ryourikana = request.getParameter("ryourikana");
		tukurikata = request.getParameter("tukurikataTotal");
		syoukai = request.getParameter("syoukai");
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

		//料理登録SQL(料理名)と料理登録SQL(料理ID検索)
		sql1 = "insert into RyouriTB (RyouriKana, Ryourimei, Tukurikata, Syoukai, UserID, UpdateTime) values (?, ?, ?, ?, ?, ?)";
		sql2 = "select RyouriID from RyouriTB where Ryourimei = ? order by RyouriID desc limit 1";

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
			prestmt.setString(1, ryourikana);
			prestmt.setString(2, ryourimei);
			prestmt.setString(3, tukurikata);
			prestmt.setString(4, syoukai);
			prestmt.setInt(5, userID);
			prestmt.setString(6, f.format(new Date())); //P323参照
			System.out.println("料理登録SQL(料理名):" + prestmt.toString());
			prestmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("料理登録SQL(料理名)完了");

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
				PreparedStatement prestmt = conn.prepareStatement(sql2)) {
			prestmt.setString(1, ryourimei);
			System.out.println("料理登録SQL(料理ID検索):" + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					ryouriID = rs.getInt("RyouriID"); //条件を満たすRyouriIDを格納
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errorMessage", e);
			RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
			rd_result.forward(request, response);
			return;
		}
		System.out.println("料理登録SQL(料理ID検索)完了");
		System.out.println("料理ID:" + ryouriID);

		//料理登録SQL(分量)
		sql3 = "insert into BunryouTB values (?, ?, ?, ?)";
		for (int i = 1; i < recipeBunryouList.size(); i++) {
			sql3 += ", (?, ?, ?, ?)";
		}

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
		System.out.println("料理登録SQL(分量)完了");

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

                        if(!(name.endsWith(".jpg"))) {

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
                        UploadObject uo = new UploadObject();
                        uo.uploadObject(imageName + ".jpg",imagePath);

                        //jpg形式の画像ファイルをCloudStorageにアップロード後、コンテナから削除
                        File JpgImage = new File(imagePath);
                        JpgImage.delete();

                        //DBにCloudStorageへアップロードした画像のImageNameをInsertする
			try (
                                Connection conn = DriverManager.getConnection(
                                        "jdbc:mysql://127.0.0.1:3306/mystok?serverTimezone=JST","root","password");
                                PreparedStatement prestmt = conn.prepareStatement(sql4)) {
                                prestmt.setString(1, imageName);
                                System.out.println("料理登録SQL(料理画像名):" + prestmt.toString());
                                prestmt.executeUpdate();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        System.out.println("料理登録SQL(料理画像名)完了");
                } else {
                        //DBにNoImage用のImageNameをInsertする
                        imageName = "noimage.jpg";

                        try (
                                Connection conn = DriverManager.getConnection(
                                        "jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","mystok","mySqlStok");
                                PreparedStatement prestmt = conn.prepareStatement(sql4)) {
                                prestmt.setString(1, imageName);
                                System.out.println("料理登録SQL(料理画像名):" + prestmt.toString());
                                prestmt.executeUpdate();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        System.out.println("料理登録SQL(料理画像名)完了");
                }               

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
		System.out.println("GETメソッドでRecipeRegisterServletにアクセスされました");
		request.setAttribute("errorMessage", "不正なアクセスです。やり直してください。");
		RequestDispatcher rd_result = request.getRequestDispatcher("error.jsp");
		rd_result.forward(request, response);
	}
}
