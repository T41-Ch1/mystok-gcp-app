<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Objects" %>
<%@ page import="pac1.func.Util" %>
<!DOCTYPE html>
<html>
<head><!--headにはPCが参照する情報を記入する　例：タイトルや読み込む関連ファイル -->
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=11">
<title>レシピ検索サイト　レシピコンシェル｜レシピ登録</title>
<link href="CSS/KondateKanri.css" rel="stylesheet">
<link rel="icon" href="images/fav32.ico">
</head>
<body><!--bodyにはWEBブラウザに表示させる内容を記載する-->

<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->
<%
//認証チェック
if (!Util.checkAuth(request, response)) return;
%>
<%
ArrayList<String> syokuzaikanalist = (ArrayList<String>)request.getAttribute("syokuzaikanalist"); //検索結果の複数のSyokuzaiKanaを格納する配列。
ArrayList<String> tannilist = (ArrayList<String>)request.getAttribute("tannilist"); //検索結果の複数のTanniを格納する配列。
int recipeID = (int)(request.getAttribute("recipeID")); //表示するレシピのID、新規登録の際は0が格納される
//ここから下はレシピ修正の際に初期値として表示する情報
String recipe_kana = "";
String recipe_name = "";
String[] tukurikata;
String syoukai = "";
ArrayList<String[]> bunryouList = new ArrayList<>();
if (recipeID > 0) {
	recipe_kana = (String)request.getAttribute("recipe_kana"); //表示するふりがな
	recipe_name = (String)request.getAttribute("recipe_name"); //表示するレシピ名
	tukurikata = ((String)request.getAttribute("tukurikata")).split("/"); //表示するレシピの作り方
	syoukai = (String)request.getAttribute("syoukai"); //表示するレシピの紹介文
	bunryouList = (ArrayList<String[]>)request.getAttribute("recipe_bunryou"); //表示するレシピの分量
	for (int i = 0; i < bunryouList.size(); i++) {
		//右端のゼロと小数点を削る処理
		while (bunryouList.get(i)[1].length() > 0 && bunryouList.get(i)[1].substring(bunryouList.get(i)[1].length() - 1).equals("0")) {
			bunryouList.get(i)[1] = bunryouList.get(i)[1].substring(0, bunryouList.get(i)[1].length() - 1);
		}
		if (bunryouList.get(i)[1].substring(bunryouList.get(i)[1].length() - 1).equals(".")) {
			bunryouList.get(i)[1] = bunryouList.get(i)[1].substring(0, bunryouList.get(i)[1].length() - 1);
		}
	}
} else {
	tukurikata = new String[0];
}
%>
<br>
  <div id="parent">
    <div id="child1"><h1>　献立登録管理</h1></div>
    <div id="child2"><h2 style="text-align: center;"><a href="MypageServlet">マイページに戻る</a></h2></div>
  </div>
  <br>
<%
String servletName = "RecipeRegisterServlet";
if (recipeID > 0) servletName = "RecipeUpdateServlet";
%>
<form name="recipeRegisterForm" action="<%= servletName %>" method="post" enctype="multipart/form-data" onsubmit="return false;" id="recipeRegisterForm"><!-- 誤Enterに反応しないようにする -->
	<datalist id="syokuzaikanalist">
	<%
	//syokuzaikanalistの個数分だけプルダウンの中身を並べる
	for (int i = 0; i < syokuzaikanalist.size(); i++) out.println("<option value=\"" + syokuzaikanalist.get(i) + "\">");
	%>
	</datalist>
料理名：<input type="text" id="ryourimei" name="ryourimei" size="50" maxlength=30 required value="<%= recipe_name %>">
<br><br>
<!-- \u3041-\u3096は平仮名、\u3000は全角スペース、\u30fcは長音 これらの文字の組み合わせのみ許可する 正規表現で書いたのがpatternの所 -->
料理名のふりがな：<input type="text" id="ryourikana" name="ryourikana" size="50" maxlength=50 required value="<%= recipe_kana %>" pattern="[\u3041-\u3096|\u3000|\u30fc]*">

<br><br>
紹介文(100文字まで)：<br><textarea id="syoukai" name="syoukai" cols=30 rows=4 maxlength=100 required><%= syoukai %></textarea>
<br><br>

<div id="syokuzaicontainer">
<%
if (recipeID > 0) {
%>
	<input type="hidden" name="recipeID" value="<%= recipeID %>">
<%
	for (int i = 1; i <= bunryouList.size(); i++) {
%>
<div id="syokuzaifield<%= i %>">
食材<%= i %>(ひらがな)：<input id="syokuzai<%= i %>" name="syokuzaikana<%= i %>" type="text" list="syokuzaikanalist" placeholder="プルダウンメニュー" autocomplete="off" size=30 required value="<%= bunryouList.get(i - 1)[0] %>" onChange="getTanni(<%= i %>)">&emsp;分量：<input type="text" id="bunryou<%= i %>" name="bunryou<%= i %>" size=10 required value="<%= bunryouList.get(i - 1)[1] %>">&emsp;<span id="tanni<%= i %>"><%= bunryouList.get(i - 1)[2] %></span>
</div>
<!-- 食材<%= bunryouList.size() + 1 %>以降を追加する部分 -->
<div id="syokuzaifield<%= bunryouList.size() + 1 %>"></div>
<%
	}
} else {
%>
<div id="syokuzaifield1">
食材1(ひらがな)：<input id="syokuzai1" name="syokuzaikana1" type="text" list="syokuzaikanalist" placeholder="プルダウンメニュー" autocomplete="off" size=30 required onChange="getTanni(1)">&emsp;分量：<input type="text" id="bunryou1" name="bunryou1" size=10>&emsp;<span id="tanni1"></span>
</div>
<!-- 食材2以降を追加する部分 -->
<div id="syokuzaifield2"></div>
<%
}
%>
<input type="button" value="食材を追加する" onClick="ItemField.add();" />
<input type="button" value="食材を削除する" onClick="ItemField.remove();" />
</div>

<div id="tukurikatacontainer">
<%
if (recipeID > 0) {
	for (int i = 1; i <= tukurikata.length; i++) {
%>
<div id="tukurikatafield<%= i %>">
レシピ<%= i %>：<input id="tukurikata<%= i %>" type="text" size=50 required value="<%= tukurikata[i - 1] %>">
</div>
<!-- 作り方<%= tukurikata.length + 1 %>以降を追加する部分 -->
<div id="tukurikatafield<%= tukurikata.length + 1 %>"></div>
<%
	}
} else {
%>
<div id="tukurikatafield1">
レシピ1：<input id="tukurikata1" type="text" size=50 required>
</div>
<!-- 食材2以降を追加する部分 -->
<div id="tukurikatafield2"></div>
<%
}
%>
<input type="button" value="レシピを追加する" onClick="TukurikataField.add();" />
<input type="button" value="レシピを削除する" onClick="TukurikataField.remove();" />
</div>

<br><br>
画像ファイル(1MBまで、任意)：<input type="file" id="ryouripic" name="pic" accept="image/*">

<br><br>
<div style="margin-left: 30%; margin-right: 30%;">
<input type="button" style="width:100%;padding:10px;font-size:30px;" value="レシピ登録決定" onClick="completeCheck();"><!-- 送信ボタンをクリックしたらsubmitではなく判定を行う -->
</div>
<input type="hidden" name="syokuzaikanalist" value="<%= syokuzaikanalist %>">
<input type="hidden" name="tukurikataTotal" id="tukurikataTotal">
<input type="submit" value="不可視ボタン" style="display:none;" name=submitBtn><!-- formのエラーチェック用 -->

</form>

<br>

<script>
<%
//javascriptに食材リストと単位リストを生成
out.print("var syokuzaikanalist = ['" + syokuzaikanalist.get(0));
for (int i = 1; i < syokuzaikanalist.size(); i++) {
	out.print("','" + syokuzaikanalist.get(i));
}
out.println("']");
out.print("var tannilist = ['" + tannilist.get(0));
for (int i = 1; i < tannilist.size(); i++) {
	out.print("','" + tannilist.get(i));
}
out.println("']");
%>
//食材欄に入力の変化があったとき単位欄を変更
function getTanni(index) {
	//indexは食材入力欄の番号
	if (syokuzaikanalist.indexOf(document.getElementById('syokuzai' + index).value) == -1) {
		//入力された食材がリストになかったら単位欄を空にする
		document.getElementById('tanni' + index).innerHTML = '';
	} else {
		//入力された食材がリストにあったら該当する単位を単位欄に表示する
		document.getElementById('tanni' + index).innerHTML = tannilist[syokuzaikanalist.indexOf(document.getElementById('syokuzai' + index).value)];
	}
}
//食材指定テキストボックスの増減
var ItemField = {
    currentNumber : Math.max(1, <%= bunryouList.size() %>),
    itemTemplate : '食材__count__(ひらがな)：'
        + '<input id="syokuzai__count__" name="syokuzaikana__count__" type="text" list="syokuzaikanalist" placeholder="プルダウンメニュー" autocomplete="off" size=30 required  onChange="getTanni(__count__)">'
        + '&emsp;分量：<input type="text" id="bunryou__count__" name="bunryou__count__" size=10 required>'
        + '&emsp;<span id="tanni__count__"></span>',
    add : function () {
        this.currentNumber++;

        var field = document.getElementById('syokuzaifield' + this.currentNumber);
        var newItem = this.itemTemplate.replace(/__count__/mg, this.currentNumber); //mは複数行の入力文字列を複数行として扱う（^及び$が各行の先頭末尾にマッチする） gはグローバルサーチ。文字列全体に対してマッチングするか（無指定の場合は1度マッチングした時点で処理を終了）
        field.innerHTML = newItem;

        var nextNumber = this.currentNumber + 1;
        var new_area = document.createElement("div");
        new_area.setAttribute("id", "syokuzaifield" + nextNumber);
        field.appendChild(new_area);
    },
    remove : function () {
        if ( this.currentNumber == 1 ) { return; }

        var field = document.getElementById('syokuzaifield' + this.currentNumber);
        field.removeChild(field.lastChild);
        field.innerHTML = '';

        this.currentNumber--;
    }
}
//作り方テキストボックスの増減
var TukurikataField = {
    currentNumber : Math.max(1, <%= tukurikata.length %>),
    tukurikataTemplate : 'レシピ__count__：<input id="tukurikata__count__" type="text" size=50 required>',
    add : function () {
        this.currentNumber++;

        var field = document.getElementById('tukurikatafield' + this.currentNumber);
        var newTukurikata = this.tukurikataTemplate.replace(/__count__/mg, this.currentNumber); //mは複数行の入力文字列を複数行として扱う（^及び$が各行の先頭末尾にマッチする） gはグローバルサーチ。文字列全体に対してマッチングするか（無指定の場合は1度マッチングした時点で処理を終了）
        field.innerHTML = newTukurikata;

        var nextNumber = this.currentNumber + 1;
        var new_area = document.createElement("div");
        new_area.setAttribute("id", "tukurikatafield" + nextNumber);
        field.appendChild(new_area);
    },
    remove : function () {
        if ( this.currentNumber == 1 ) { return; }

        var field = document.getElementById('tukurikatafield' + this.currentNumber);
        field.removeChild(field.lastChild);
        field.innerHTML = '';

        this.currentNumber--;
    }
}
//画像ファイルサイズ制限 1MBまで
const sizeLimit = 1024 * 1024 * 1; // 制限サイズ
const fileInput = document.getElementById('ryouripic'); // input要素
// changeイベントで呼び出す関数
const handleFileSelect = () => {
  const files = fileInput.files;
  for (var i = 0; i < files.length; i++) {
    if (files[i].size > sizeLimit) {
      // ファイルサイズが制限以上
      alert('ファイルサイズは1MB以下にしてください'); // エラーメッセージを表示
      fileInput.value = ''; // inputの中身をリセット
      return; // この時点で処理を終了する
    }
  }
}
// フィールドの値が変更された時（≒ファイル選択時）に、handleFileSelectを発火
fileInput.addEventListener('change', handleFileSelect);
//必須項目の入力チェック すべて正常ならexistCheck()を実行する
function completeCheck() {
	var isComp = true;
	if (document.getElementById('ryourimei').value == '') isComp = false;
	if (document.getElementById('ryourikana').value == '') isComp = false;
	if (document.getElementById('syoukai').value == '') isComp = false;
	for (var i = 1; i <= ItemField.currentNumber; i++) {
		if (document.getElementById('syokuzai' + i).value == '') isComp = false;
		if (document.getElementById('bunryou' + i).value == '') isComp = false;
	}
	for (var i = 1; i <= TukurikataField.currentNumber; i++) {
		if (document.getElementById('tukurikata' + i).value == '') isComp = false;
	}
	if (!isComp) {
		alert('入力されていない欄があります');
		return;
	}
	//ふりがなが平仮名、全角スペース、長音のみかチェックする
	var patternKana = /^[ぁ-んー　]*$/;
	if (!patternKana.test(document.getElementById('ryourikana').value)) {
		alert('ふりがなが平仮名ではありません');
		return;
	}
	//分量がdecimal(5,2)で表されるかどうかを判定する
	var patternInt = /^(0|([1-9]([0-9]{0,2})))$/; //0～999の整数
	var patternDecimal = /^(0|([1-9]([0-9]{0,2})))\.([0-9]{1,2})$/; //0.00～999.99の小数 小数部分は1～2桁
	for (var i = 1; i <= ItemField.currentNumber; i++) {
		if (!patternInt.test(document.getElementById('bunryou' + i).value) && !patternDecimal.test(document.getElementById('bunryou' + i).value)) {
			alert('分量が不正な値です');
			return;
		}
	}
	existCheck();
}
//入力された食材の正常性チェック すべて正常ならdupCheck()を実行する
function existCheck() {
	for (var i = 1; i <= ItemField.currentNumber; i++) {
		if (syokuzaikanalist.indexOf(document.getElementById('syokuzai' + i).value) == -1) {
			alert('入力された食材がデータベースに存在しません');
			return;
		}
	}
	dupCheck();
}
//入力された食材の重複チェック 重複がなければtukurikataCheck()を実行する
function dupCheck() {
	var isDup = false;
	var inputList = [];
	for (var i = 1; i <= ItemField.currentNumber; i++) {
		if (inputList.indexOf(document.getElementById('syokuzai' + i).value) == -1) {
			inputList.push(document.getElementById('syokuzai' + i).value);
		} else {
			isDup = true;
			break;
		}
	}
	if (isDup) {
		alert('入力された食材に重複があります');
		return;
	} else tukurikataCheck();
}
//作り方が8000文字を超えていないかチェックする 超えてなければsubmitする
function tukurikataCheck() {
	var tukurikataTotal = '';
	for (var i = 1; i <= TukurikataField.currentNumber; i++) {
		if (i == 1) {
			tukurikataTotal += document.getElementById('tukurikata' + i).value;
		} else {
			tukurikataTotal += '/' + document.getElementById('tukurikata' + i).value;
		}
	}
	console.log(tukurikataTotal);
	if (tukurikataTotal.length > 8000) {
		alert('レシピが長すぎます。8000文字以内にしてください(ボックス同士の連結も1文字としてカウントされます)。');
		return;
	} else {
		document.getElementById('tukurikataTotal').value = tukurikataTotal;
		confirm("献立を1件登録します。");
		document.recipeRegisterForm.submit();
	}
}
</script>

<!-- フッター部分 -->

</body>
</html>