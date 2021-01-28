package pac1.func;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Util {

	public static String sanitizing(String str) {
		return str.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;")
				.replaceAll("\'", "&apos;");
	}

	public static boolean tabetaTodayInfo(int recipeID, int userID) {
		boolean existsData = false;
		String sql = "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String date = f.format(Calendar.getInstance().getTime()); //MySQLに記録される日付部分の形式で表された今日の日付
		if (userID == 0) {
			return existsData;
		}

		sql = "select * from TabetaTB where UserID = ? and RyouriID = ? and date(TabetaTime) = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST", "root", "password");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			prestmt.setInt(1, userID);
			prestmt.setInt(2, recipeID);
			prestmt.setString(3, date);
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					existsData = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Tabeta: " + existsData);
		return existsData;
	}

	public static ArrayList<Boolean> tabetaTodayInfo(ArrayList<Integer> recipeID, int userID) {
		ArrayList<Boolean> existsData = new ArrayList<>();
		String sql = "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		String date = f.format(Calendar.getInstance().getTime()); //MySQLに記録される日付部分の形式で表された今日の日付
		if (recipeID.size() == 0 || userID == 0) {
			for (int i = 0; i < recipeID.size(); i++) {
				existsData.add(false);
			}
			return existsData;
		}

		sql = "select RyouriID from TabetaTB where UserID = ? and RyouriID in (?";
		for (int i = 1; i <= recipeID.size() - 1; i++) {
			sql += ",?";
		}
		sql += ") and date(TabetaTime) = ?";

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
			prestmt.setString(2 + recipeID.size(), date);
			for (int i = 0; i < recipeID.size(); i++) {
				prestmt.setInt(i + 2, recipeID.get(i));
			}
			try (ResultSet rs = prestmt.executeQuery()) {
				ArrayList<Integer> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getInt("RyouriID"));
				}
				for (int i = 0; i < recipeID.size(); i++) {
					if (result.indexOf(recipeID.get(i)) == -1) {
						existsData.add(false);
					} else {
						existsData.add(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Tabeta: " + Arrays.toString(existsData.toArray()));
		return existsData;
	}

	public static boolean favoInfo(int recipeID, int userID) {
		boolean existsData = false;
		String sql = "";
		if (userID == 0) {
			return existsData;
		}

		sql = "select * from FavoTB where UserID = ? and RyouriID = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			prestmt.setInt(1,userID);
			prestmt.setInt(2,recipeID);
			try (ResultSet rs = prestmt.executeQuery()) {
				while (rs.next()) {
					existsData = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Favo: " + existsData);
		return existsData;
	}

	public static ArrayList<Boolean> favoInfo(ArrayList<Integer> recipeID, int userID) {
		ArrayList<Boolean> existsData = new ArrayList<>();
		String sql = "";
		if (recipeID.size() == 0 || userID == 0) {
			for (int i = 0; i < recipeID.size();i++) {
				existsData.add(false);
			}
			return existsData;
		}

		sql = "select RyouriID from FavoTB where UserID = ? and RyouriID in (?";
		for (int i = 1; i <= recipeID.size() - 1; i++) {
			sql += ",?";
		}
		sql += ")";

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
			for (int i = 0; i < recipeID.size(); i++) {
				prestmt.setInt(i + 2, recipeID.get(i));
			}
			try (ResultSet rs = prestmt.executeQuery()) {
				ArrayList<Integer> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getInt("RyouriID"));
				}
				for (int i = 0; i < recipeID.size(); i++) {
					if (result.indexOf(recipeID.get(i)) == -1) {
						existsData.add(false);
					} else {
						existsData.add(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Favo: " + Arrays.toString(existsData.toArray()));
		return existsData;
	}

	public static boolean existsUser(String userName) {

		String sql = "";
		sql = "select UserID from UserTB where UserName = ?";
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (
				Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/j2a1b?serverTimezone=JST","root","password");
				PreparedStatement prestmt = conn.prepareStatement(sql)) {
			prestmt.setString(1, userName);
			System.out.println("アカウント名重複チェックSQL: " + prestmt.toString());
			try (ResultSet rs = prestmt.executeQuery()) {
				if (rs.next()) {
					System.out.println("アカウント名重複チェック結果: 重複あり");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("アカウント名重複チェック結果: 重複なし");
        return false;
    }

	public static boolean checkAuth(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
		//認証チェック
		HttpSession session = request.getSession(false);
		if (session == null) {
			//セッションが切れている場合
			session = request.getSession(true);
			session.setAttribute("targetURI", request.getRequestURI());
			System.out.println("targetURI:" + request.getRequestURI());
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.forward(request, response);
			return false;
		} else if (request.getRemoteUser() == null) {
			//ログインしていない場合
			session.setAttribute("targetURI", request.getRequestURI());
			System.out.println("targetURI:" + request.getRequestURI());
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.forward(request, response);
			return false;
		}
		return true;
	}

}

