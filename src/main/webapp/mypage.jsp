<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pac1.func.Util" %>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="utf-8" >
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | Mypage</title>
  <link rel="stylesheet" href="CSS/MypageStyle.css" type="text/css">
  <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
<div id="wrapper">
<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<%
//認証チェック
if (!Util.checkAuth(request, response)) return;
%>
<%
final int DATA_PER_PAGE = 5; //1ページごとに表示する最大件数
ArrayList<Integer> recipeIDFavo = (ArrayList)(request.getAttribute("ryouriIDFavo")); //表示するお気に入りレシピのID(最大5件)
ArrayList<String> ryourimeiFavo = (ArrayList)(request.getAttribute("ryourimeiFavo")); //表示するお気に入りのレシピ名(最大5件)
ArrayList<String> imageNameFavo = (ArrayList)(request.getAttribute("imageNameFavo")); //表示するお気に入りの画像名(最大5件)
ArrayList<Boolean> tabetaListFavo = (ArrayList)(request.getAttribute("tabetaListFavo")); //表示するお気に入りの今日食べたかどうか(最大5件)
ArrayList<Boolean> favoListFavo = new ArrayList<>(); //表示するお気に入りのお気に入りかどうか(最大5件) すべてtrueが入る
for (int i = 0; i < recipeIDFavo.size(); i++) favoListFavo.add(true);
ArrayList<Integer> recipeIDTabeta = (ArrayList)(request.getAttribute("ryouriIDTabeta")); //表示する食べたレシピのID(最大5件)
ArrayList<String> tabetaTimeList = (ArrayList)(request.getAttribute("tabetaTimeList")); //表示する食べたレシピのTabetaした時刻(最大5件)
ArrayList<String> ryourimeiTabeta = (ArrayList)(request.getAttribute("ryourimeiTabeta")); //表示する食べたレシピ名(最大5件)
ArrayList<String> imageNameTabeta = (ArrayList)(request.getAttribute("imageNameTabeta")); //表示する食べたレシピの画像名(最大5件)
ArrayList<Boolean> tabetaListTabeta = (ArrayList)(request.getAttribute("tabetaListTabeta")); //表示する食べたレシピの今日食べたかどうか(最大5件)
ArrayList<Boolean> favoListTabeta = (ArrayList)(request.getAttribute("favoListTabeta")); //表示する食べたレシピのお気に入りかどうか(最大5件)
%>

<div id="wrap" class="clearfix">
 <div class="content">
  <main  class="main">

  <div class="title">
  <h1><span class="h1bold"><%= request.getRemoteUser() %>さんのマイページ</span></h1>
  <div class="gia">
   <a href="userinfo.jsp"><img src="images/config.png"
    alt="会員情報変更" width="50" height="50" class="kaiinngia">
    <span class="hukidashi">会員情報の変更</span></a>
  </div>
  </div>

  <div class="abox">

    <div class="touroku">
     <a href="MyRecipePageServlet"><img src="images/cutlery.png"
        alt="MYレシピ管理" class="tourokupic"><br>
     <span>MYレシピ管理</span></a>
    </div>

    <div class="teiann">
     <a href="RecipeSuggestServlet"><img src="images/sebastian.png"
        alt="レシピを提案" class="teiannpic"><br>
     <span>レシピ提案</span></a>
    </div>

  </div>

  <div class="bbox">
  <section id="okiniiri">
   <h2 class="icon"><a href="FavoPageServlet">お気に入り >></a>&emsp;<img src="images/pink_heart.png" alt="お気に入りボタン" width="35" height="35">でお気に入りレシピ登録できます</h2>
   <div class="border"></div>

<%
if (recipeIDFavo.size() > 0) {
%>
   <ul class="okiniirilist">
<%
	for (int i = 0; i < recipeIDFavo.size(); i++) {
%>
    <li><a class="recipetitlelink" href="RecipeServlet?recipeID=<%= recipeIDFavo.get(i) %>">
      <img src="images/RyouriPIC/<%= imageNameFavo.get(i) %>" alt="<%= ryourimeiFavo.get(i) %>" width="200" height="200"><br>
      <span class="titletitle"><%= ryourimeiFavo.get(i) %></span></a><br>
<%
		String face = "";
		if (tabetaListFavo.get(i)) face = "aceat.png";
		else face = "bceat.png";
%>
      <a href="javascript:tabetabutton(<%= i %>, <%= recipeIDFavo.get(i) %>)" class="face<%= recipeIDFavo.get(i) %>"><img src="images/<%= face %>" alt="今日食べたボタン" width="35" height="35"></a>
<%
		String heart = "";
		if (favoListFavo.get(i)) heart = "pink_heart.png";
		else heart = "clear_heart.png";
%>
      <a href="javascript:favobutton(<%= i %>, <%= recipeIDFavo.get(i) %>)" class="heart<%= recipeIDFavo.get(i) %>"><img src="images/<%= heart %>" alt="お気に入りボタン" width="35" height="35"></a>
    </li>
<%
	}
%>
    <span class="more">
    <a href="FavoPageServlet">>>MORE</a>
    </span>

   </ul>
<%
} else {
%>
   <h2 class="okiniirinashi" style="text-align: center; height: 200px;">お気に入りがまだ追加されていません。</h2>
<%
}
%>
  </section>


  <section id="rireki">
   <h2 class="icon"><a href="TabetaPageServlet">食べた履歴 >></a>&emsp;<img src="images/aceat.png" alt="お気に入りボタン" width="35" height="35">は"今日"食べたレシピです</h2>
   <div class="border"></div>
<%
if (recipeIDFavo.size() > 0) {
%>
   <ul class="rirekilist">
<%
	for (int i = 0; i < recipeIDTabeta.size(); i++) {
		tabetaTimeList.set(i, tabetaTimeList.get(i).substring(0, 10)); //"2021/01/20"の部分を切り出す
%>
     <li><a class="recipetitlelink" href="RecipeServlet?recipeID=<%= recipeIDTabeta.get(i) %>">
       <img src= "images/RyouriPIC/<%= imageNameTabeta.get(i) %>" alt="<%= ryourimeiTabeta.get(i) %>" width="200" height="200"><br>
       <span class="titletitle"><%= ryourimeiTabeta.get(i) %></span></a><br>
<%
		String face = "";
		if (tabetaListTabeta.get(i)) face = "aceat.png";
		else face = "bceat.png";
%>
       <a href="javascript:tabetabutton(<%= recipeIDFavo.size() + i %>, <%= recipeIDTabeta.get(i) %>)" class="face<%= recipeIDTabeta.get(i) %>"><img src="images/<%= face %>" alt="今日食べたボタン" width="35" height="35"></a>
<%
		String heart = "";
		if (favoListTabeta.get(i)) heart = "pink_heart.png";
		else heart = "clear_heart.png";
%>
       <a href="javascript:favobutton(<%= recipeIDFavo.size() + i %>, <%= recipeIDTabeta.get(i) %>)" class="heart<%= recipeIDTabeta.get(i) %>"><img src="images/<%= heart %>" alt="お気に入りボタン" width="35" height="35"></a><br>
       <a href="TabetaPageServlet?year=<%= tabetaTimeList.get(i).substring(0, 4) %>&month=<%= tabetaTimeList.get(i).substring(5, 7) %>"><%= tabetaTimeList.get(i) %></a>
     </li>
<%
	}
%>
     <span class="more">
      <a href="TabetaPageServlet">>>MORE</a>
     </span>
   </ul>
<%
} else {
%>
   <h2 class="tabetanashi" style="text-align: center; height: 200px;">食べた履歴がまだ追加されていません。</h2>
<%
}
%>
  </section>
</div><!--bbox fin-->
  </main>
</div><!--content fin-->
</div>

<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="tabetaDeleteForm" action="TabetaDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform1">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="tabetaInsertForm" action="TabetaInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform2">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="favoDeleteForm" action="FavoDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform3">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="favoInsertForm" action="FavoInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform4">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=35>
</form>
<script>
//Favoや食べたの初期状態
var tabeta = [];
var favo = [];
//Favoや食べたの押した回数
var tabetaCount = [];
var favoCount = [];
<%
for (int i = 0; i < recipeIDFavo.size(); i++) {
%>
	tabeta.push(<%= tabetaListFavo.get(i) %>);
	favo.push(<%= favoListFavo.get(i) %>);
	tabetaCount.push(0);
	favoCount.push(0);
<%
}
for (int i = 0; i < recipeIDTabeta.size(); i++) {
%>
	tabeta.push(<%= tabetaListTabeta.get(i) %>);
	favo.push(<%= favoListTabeta.get(i) %>);
	tabetaCount.push(0);
	favoCount.push(0);
<%
}
%>
//ボタンを押した時刻の初期状態
var pushTime = new Date();
var now;
//食べたボタンを押したとき
function tabetabutton(i, j) {
	if (now != null) {
		now = new Date();
		if (now.getTime() - pushTime.getTime() < 2000) return; //2回目以降は2秒間隔をあけないと押せない
	} else {
		now = new Date(); //初回は必ず押せる
	}
	if (tabetaCount[i] >= 10) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで11回以上押されたら反応しない
	}
	else {
		if (tabeta[i]) {
			document.getElementById('recipeIDform1').value = j;
			tabetaDeleteForm.submit(); //Favoに登録されていたら削除
		} else {
			document.getElementById('recipeIDform2').value = j;
			tabetaInsertForm.submit(); //Favoに登録されていなかったら登録
		}
		tabeta[i] = !tabeta[i];
		pushTime = new Date();
		tabetaCount[i]++;
	}
}
//Favoボタンを押したとき
function favobutton(i, j) {
	if (now != null) {
		now = new Date();
		if (now.getTime() - pushTime.getTime() < 2000) return; //2回目以降は2秒間隔をあけないと押せない
	} else {
		now = new Date(); //初回は必ず押せる
	}
	if (favoCount[i] >= 10) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで11回以上押されたら反応しない
	}
	else {
		if (favo[i]) {
			document.getElementById('recipeIDform3').value = j;
			favoDeleteForm.submit(); //Favoに登録されていたら削除
		} else {
			document.getElementById('recipeIDform4').value = j;
			favoInsertForm.submit(); //Favoに登録されていなかったら登録
		}
		favo[i] = !favo[i];
		pushTime = new Date();
		favoCount[i]++;
	}
}
</script>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div><!--wrapper fin-->
</body>
</html>