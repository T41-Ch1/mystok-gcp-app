package pac1.func;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CreateRyouriTB {
	public static void main(String[] args) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String FILE_NAME_IN = "shokuzai_for_common_recipe.csv";
		final String FILE_NAME_OUT = "output" + sdf.format(new Date()) + ".txt";

		ArrayList<String> tanni = new ArrayList<>();
		ArrayList<String> syokuzaiID = new ArrayList<>();
		ArrayList<Integer> readyID = new ArrayList<>();
		readyID.add(2);
		readyID.add(3);
		readyID.add(4);
		readyID.add(5);
		readyID.add(6);
		readyID.add(7);
		readyID.add(22);
		readyID.add(25);
		readyID.add(34);
		readyID.add(46);

		try {
			File f1 = new File(FILE_NAME_IN);
			BufferedReader br = new BufferedReader(new FileReader(f1));

			boolean isTitle = true;
			System.out.println("単位と食材IDのリストを用意します");

			String line;
			while ((line = br.readLine()) != null) {
				if (!isTitle) {
					String[] data = line.split(",", 0);
					tanni.add(data[3]);
					syokuzaiID.add(data[1]);
				}
				isTitle = false;
			}
			br.close();
			System.out.println("単位と食材IDのリストを用意しました");
		} catch (IOException e) {
			System.out.println(e);
		}

		try {
			FileWriter f2 = new FileWriter(FILE_NAME_OUT, false);
			PrintWriter p = new PrintWriter(new BufferedWriter(f2));
			p.println("料理ID,食材ID,分量,ユーザID");
			for (int i = 0; i < 46; i++) {
				if (readyID.indexOf(i) == -1) {
					for (String id : syokuzaiID) {
						if (Math.random() < 0.25) {
							double bunryou = 0;
							if (id.equals("1")) {
								bunryou = 100 + (int)(Math.random() * 11) * 10;
							} else if (id.equals("10")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("18")) {
								bunryou = 3 + (int)(Math.random() * 18);
							} else if (id.equals("22")) {
								bunryou = 10 + (int)(Math.random() * 11);
							} else if (id.equals("55")) {
								bunryou = 0.1 + (int)(Math.random() * 9) * 0.05;
							} else if (id.equals("62")) {
								bunryou = 25 + (int)(Math.random() * 4) * 25;
							} else if (id.equals("63")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("72")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("73")) {
								bunryou = 1 + (int)(Math.random() * 4);
							} else if (id.equals("80")) {
								bunryou = 1 + (int)(Math.random() * 4);
							} else if (id.equals("101")) {
								bunryou = 10 + (int)(Math.random() * 9) * 5;
							} else if (id.equals("118")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("121")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("125")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("131")) {
								bunryou = 1 + (int)(Math.random() * 3);
							} else if (id.equals("170")) {
								bunryou = 50 + (int)(Math.random() * 4) * 50;
							} else if (id.equals("171")) {
								bunryou = 50 + (int)(Math.random() * 4) * 50;
							} else if (id.equals("172")) {
								bunryou = 50 + (int)(Math.random() * 4) * 50;
							} else if (id.equals("175")) {
								bunryou = 5 + (int)(Math.random() * 26);
							} else if (id.equals("176")) {
								bunryou = 0.2 + (int)(Math.random() * 14) * 0.1;
							} else if (id.equals("177")) {
								bunryou = 5 + (int)(Math.random() * 26);
							} else if (id.equals("179")) {
								bunryou = 5 + (int)(Math.random() * 26);
							} else if (id.equals("181")) {
								bunryou = 5 + (int)(Math.random() * 26);
							} else if (id.equals("182")) {
								bunryou = 0.5 + (int)(Math.random() * 16) * 0.1;
							} else if (id.equals("185")) {
								bunryou = 10 + (int)(Math.random() * 6) * 5;
							} else if (id.equals("190")) {
								bunryou = 5 + (int)(Math.random() * 2) * 5;
							} else if (id.equals("192")) {
								bunryou = 0.25 + (int)(Math.random() * 10) * 0.25;
							} else if (id.equals("193")) {
								bunryou = 5 + (int)(Math.random() * 30) * 5;
							} else if (id.equals("194")) {
								bunryou = 25 + (int)(Math.random() * 4) * 25;
							} else if (id.equals("195")) {
								bunryou = 10 + (int)(Math.random() * 20) * 10;
							} else if (id.equals("196")) {
								bunryou = 20 + (int)(Math.random() * 2) * 20;
							} else if (id.equals("197")) {
								bunryou = 5 + (int)(Math.random() * 10) * 5;
							} else if (id.equals("198")) {
								bunryou = 5 + (int)(Math.random() * 10) * 5;
							} else if (id.equals("199")) {
								bunryou = 5 + (int)(Math.random() * 10) * 5;
							} else if (id.equals("200")) {
								bunryou = 5 + (int)(Math.random() * 10) * 5;
							} else if (id.equals("201")) {
								bunryou = 50 + (int)(Math.random() * 3) * 50;
							}
							p.println(i + ", " + id + ", " + bunryou + ", 1");
						}
					}
				}
			}
			p.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		System.out.println("料理TB用の本番データを出力しました(ランダム生成分)");
		System.out.println("ファイル名: " + FILE_NAME_OUT);
	}
}
