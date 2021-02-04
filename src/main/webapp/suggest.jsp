<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="utf-8" >
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | レシピ提案</title>
  <link rel="stylesheet" href="CSS/RecipeTeiannStyle.css" type="text/css">
  <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
<div id="wrapper">
<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<%
final int DATA_PER_PAGE = 4; //1ページごとに表示する最大件数
ArrayList<Integer> recipeID = (ArrayList)(request.getAttribute("ryouriID")); //表示するレシピのID(最大4件)
ArrayList<String> ryourimei = (ArrayList)(request.getAttribute("ryourimei")); //表示するレシピ名(最大4件)
ArrayList<String> imageName = (ArrayList)(request.getAttribute("imageName")); //表示する画像名(最大4件)
ArrayList<Boolean> tabetaList = (ArrayList)(request.getAttribute("tabetaList"));
ArrayList<Boolean> favoList = (ArrayList)(request.getAttribute("favoList"));
%>

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
for (int i = 0; i < recipeID.size(); i++) {
%>
	tabeta.push(<%= tabetaList.get(i) %>);
	favo.push(<%= favoList.get(i) %>);
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
	if (tabetaCount[i] >= 4) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで5回以上押されたら反応しない
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
	if (favoCount[i] >= 4) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで5回以上押されたら反応しない
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

<div id="wrap" class="clearfix">
 <div class="content">
  <main  class="main">

   <div class="title">
    <h1>あなたにこちらのレシピをオススメします</h1>
   </div>
 <div class="abox">
  <div class="bbox">
   <section id="okiniiri">
    <ul class="okiniirilist">
<%
for (int i = 0; i < DATA_PER_PAGE; i++) {
	String face = "";
	if (tabetaList.get(i)) face = "aceat.png";
	else face = "bceat.png";
	String heart = "";
	if (favoList.get(i)) heart = "pink_heart.png";
	else heart = "clear_heart.png";
%>
    <li><a class="recipetitlelink" href="javascript:send('RecipeServlet?recipeID=<%= recipeID.get(i) %>')">
        <img src= "images/RyouriPIC/<%= imageName.get(i) %>" alt="<%= ryourimei.get(i) %>" width="200" height="200"><br>
        <span class="titletitle"><%= ryourimei.get(i) %></span></a><br>
        <a href="javascript:tabetabutton(<%= i %>, <%= recipeID.get(i) %>)" class="face<%= recipeID.get(i) %>"><img src="images/<%= face %>"
           alt="今日食べたボタン" width="35" height="35"></a>
        <a href="javascript:favobutton(<%= i %>, <%= recipeID.get(i) %>)" class="heart<%= recipeID.get(i) %>"><img src="images/<%= heart %>"
           alt="お気に入りボタン" width="35" height="35"></a>
    </li>
<%
}
%>
    </ul>
  </section>
 </div>

<div class="clearfix cbox">
  <div class="huki">
  <h2><span class="h1bold">
    他のレシピも見たいときは<br>
    <button onclick="javascript:send('RecipeSuggestServlet')" class="btnn">もう一度提案する</button>を<br>
    押してください。<br></span></h2>
  </div>
   <img src="images/date18test.PNG" alt="コンシェルジュ"><br><br>
  <p>※1週間以上食べてない料理の中から<br>　　ランダムに4件選んでいます</p>
 </div>

 </div>
</main>
</div>
</div>

<script>
var sendflag = false;
function send(uri) {
	if (!sendflag) {
		sendflag = true;
		location.href = uri;
	}
}
</script>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div><!--wrapper Fix-->
</body>
</html>