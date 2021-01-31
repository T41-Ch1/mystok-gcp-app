<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" import="java.io.*" %>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=11">
<title>レシピコンシェル | レシピページ</title>
<link href="CSS/RecipePageStyle.css" rel="stylesheet">
<link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
<div id="wrapper">

<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<%
String recipe_name = (String)request.getAttribute("recipe_name"); //表示するレシピ名
String tukurikata = (String)request.getAttribute("tukurikata"); //表示するレシピの作り方
ArrayList<String[]> bunryouList = new ArrayList<>(); //表示するレシピの分量
bunryouList = (ArrayList<String[]>)request.getAttribute("recipe_bunryou");
int recipeID = (int)(request.getAttribute("recipeID")); //表示するレシピのID
String imageName = (String)request.getAttribute("imageName"); //表示する画像名
boolean isMyRecipe = (boolean)request.getAttribute("isMyRecipe");; //マイレシピかどうか
boolean tabeta= (boolean)request.getAttribute("tabeta"); //食べた登録されているかどうか
boolean favo = (boolean)request.getAttribute("favo"); //お気に入り登録されているかどうか
String searchMode = (String)request.getAttribute("searchMode"); //検索窓のラジオボタンに最初からチェックを入れる方
String input = (String)request.getAttribute("input"); //検索窓に表示する文字列
%>

<div id="wrap" class="clearfix">
  <div class="content">
    <main class="main">
  <!--ラジオボタン開始-->
    <form action="SearchResultServlet" method="get">
    <div class ="radio-font"><!--ラジオボタンのdiv４-->
      <ul class ="radiolist"><!--ラジオボタンリストのul-->
        <li>
          <input type="radio" id ="f-option" name="searchMode" value="syokuzai" <% if (searchMode.equals("syokuzai")) out.print("checked"); %>><label for="f-option">食材名検索</label>
          <div class ="check"><!--ラジオボタンチェックする円のdiv５-->
          </div>
        </li>
        <li>
          <input type="radio" id ="s-option" name="searchMode" value="ryouri" <% if (!searchMode.equals("syokuzai")) out.print("checked"); %>><label for ="s-option">料理名検索</label>
          <div class ="check">
          </div><!--ラジオボタンチェックする円のdiv６-->
        </li>
      </ul>
    </div>
  <!--ラジオボタン終了-->

  <!--検索窓開始-->
  <!-- \u3041-\u3096は平仮名、\u3000は全角スペース、\u30fcは長音 これらの文字の組み合わせのみ許可する 正規表現で書いたのがpatternの所 -->
      <input id="mado" type="text" name="input" value="<%= input %>" size=50 pattern="[\u3041-\u3096|\u3000|\u30fc]*" maxlength=50 placeholder=" 例）じゃがいも　かれー等　【ひらがな入力のみ】" required>
      <input id="mbutton" type="submit" value="レシピ検索" onclick="func1()">
      <script>
       //二度押し防止機能
       function func1() {
        document.getElementById('mbutton').disabled = true;
       }
      </script>
    </form>
  <!--検索窓終了-->

<div class ="titlebox">
  <div class ="title"><%= recipe_name %></div>
<div class ="iconbutton">
<%
String face = "";
if (tabeta) face = "aceat.png";
else face = "bceat.png";
%>
  <a href="javascript:tabetabutton()" class="face<%= recipeID %>"><img src="images/<%= face %>"
   alt="今日食べたボタン" width="35" height="35"></a>
<%
String heart = "";
if (favo) heart = "pink_heart.png";
else heart = "clear_heart.png";
%>
  <a href="javascript:favobutton()" class="heart<%= recipeID %>"><img src="images/<%= heart %>"
   alt="お気に入りボタン" width="35" height="35" style="padding-left: 8px;"></a>
<%
if (isMyRecipe) {
%>
   <a href="javascript:updateForm.submit();"><img src="images/pen.png"
    alt="レシピ編集ボタン" width="35" height="35" style="padding-left: 8px;"></a>
<form method="post" name="updateForm" action="RecipeRegisterPageServlet">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" value="<%= recipeID %>">
</form>
<%
}
%>
</div>
</div>


<!-- 料理の写真 -->
<img src="images/RyouriPIC/<%= imageName %>" alt="写真" width="45%" height="450" border="1" align="left" class="recipetori">



<!--写真右側の必要材料入力開始-->
<div style=" padding:10px; border-radius: 10px; border: 3px dotted #ffb6c1;width:300px;margin-left:auto;margin-right:100px;">
	<h2 style="text-align:center">必要な材料</h2><!-- 必要な材料を書く場所 -->
<%
for (int i = 0; i < bunryouList.size(); i++) {
	if (bunryouList.get(i)[1].equals("0.00")) {
		out.println("<DIV style=\"text-align:left;\">");
		out.println("<DIV style=\"text-align:right;float:right;\">適量</DIV>　★" + bunryouList.get(i)[0] + "</div>");
	}
	else {
		//個数のデータの右端にあるゼロを消す　整数の場合小数点も消す
		while (bunryouList.get(i)[1].length() > 0 && bunryouList.get(i)[1].substring(bunryouList.get(i)[1].length() - 1).equals("0")) {
			bunryouList.get(i)[1] = bunryouList.get(i)[1].substring(0, bunryouList.get(i)[1].length() - 1);
		}
		if (bunryouList.get(i)[1].substring(bunryouList.get(i)[1].length() - 1).equals(".")) {
			bunryouList.get(i)[1] = bunryouList.get(i)[1].substring(0, bunryouList.get(i)[1].length() - 1);
		}
		out.println("<DIV style=\"text-align:left;\">");
		out.println("<DIV style=\"text-align:right;float:right;\">" + bunryouList.get(i)[1] + " " + bunryouList.get(i)[2] + "</DIV>　★" + bunryouList.get(i)[0] + "</div>");
	}
}
%>
</div>
<!-- 写真右側の必要材料入力終了-->
<p style="clear:both;">
</p>




<!--下側のレシピ文章 -->
<section>
  <h1 style="text-align:center">レシピ</h1><%
//作り方を｢/｣で分割する
String[] tukurikataSplit;
tukurikataSplit = tukurikata.split("/");
for (int i = 0; i < tukurikataSplit.length; i++) {
	if (i == 0) {
		out.println("  <h2 style=\"justify\">" + (i + 1) + ".　" + tukurikataSplit[i] + "</h2>");
	} else {
		out.println("<p> <h2 style=\"justify\">" + (i + 1) + ".　" + tukurikataSplit[i] + "</h2></p>");
	}
}
%>
</section>
<!--下側のレシピ文章終了 -->
</main>
</div>
</div>
  <!-- wrap終了 -->


<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="tabetaDeleteForm" action="TabetaDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" value="<%= recipeID %>">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="tabetaInsertForm" action="TabetaInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" value="<%= recipeID %>">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="favoDeleteForm" action="FavoDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" value="<%= recipeID %>">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="favoInsertForm" action="FavoInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" value="<%= recipeID %>">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=35>
</form>
<script>
//Favoや食べたの初期状態
var tabeta = <%= tabeta %>;
var favo = <%= favo %>;
//Favoや食べたの押した回数
var tabetaCount = 0;
var favoCount = 0;
//ボタンを押した時刻の初期状態
var pushTime = new Date();
var now;
//食べたボタンを押したとき
function tabetabutton() {
	if (now != null) {
		now = new Date();
		if (now.getTime() - pushTime.getTime() < 2000) return; //2回目以降は2秒間隔をあけないと押せない
	} else {
		now = new Date(); //初回は必ず押せる
	}
	if (tabetaCount >= 5) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで6回以上押されたら反応しない
	}
	else {
		if (tabeta) tabetaDeleteForm.submit(); //Favoに登録されていたら削除
		else tabetaInsertForm.submit(); //Favoに登録されていなかったら登録
		tabeta = !tabeta;
		pushTime = new Date();
		tabetaCount++;
	}
}
//Favoボタンを押したとき
function favobutton() {
	if (now != null) {
		now = new Date();
		if (now.getTime() - pushTime.getTime() < 2000) return; //2回目以降は2秒間隔をあけないと押せない
	} else {
		now = new Date(); //初回は必ず押せる
	}
	if (favoCount >= 5) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで6回以上押されたら反応しない
	}
	else {
		if (favo) favoDeleteForm.submit(); //Favoに登録されていたら削除
		else favoInsertForm.submit(); //Favoに登録されていなかったら登録
		favo = !favo;
		pushTime = new Date();
		favoCount++;
	}
}
</script>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
</body>
</html>