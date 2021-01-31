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
    <title>レシピコンシェル | お気に入り</title>
    <link rel="stylesheet" href="CSS/okiniiri.css" type="text/css">
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
ArrayList<Integer> recipeID = (ArrayList<Integer>)request.getAttribute("recipeID");
ArrayList<String> ryourimei = (ArrayList<String>)request.getAttribute("ryourimei");
ArrayList<String> imageName = (ArrayList<String>)request.getAttribute("imageName");
ArrayList<Boolean> tabetaList = (ArrayList<Boolean>)request.getAttribute("tabetaList");
ArrayList<Boolean> favoList = new ArrayList<>();
for (int i = 0; i < recipeID.size(); i++) favoList.add(true);
int recipeNum = (int)(request.getAttribute("recipeNum")); //検索結果の件数
int pageNum = (int)(request.getAttribute("pageNum")); //25件ごとに表示した場合何ページ目か 1からスタートする
final int DATA_PER_PAGE = 25; //1ページごとに表示する最大件数
final String NFOUND_ERRORMSG = "まだお気に入り登録されたレシピがありません。";
%>

<div id="wrap">
<h1 style="text-align: left;">お気に入り</h1>
<div class="border"></div>
<%
if (recipeID.size() > 0) {
%>
<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="tabetaDeleteForm" action="TabetaDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="aaa">
<input type="hidden" name="recipeID" id="recipeIDform1">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=20>
</form>
<form name="tabetaInsertForm" action="TabetaInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="aaa">
<input type="hidden" name="recipeID" id="recipeIDform2">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=20>
</form>
<form name="favoDeleteForm" action="FavoDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="aaa">
<input type="hidden" name="recipeID" id="recipeIDform3">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=20>
</form>
<form name="favoInsertForm" action="FavoInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="aaa">
<input type="hidden" name="recipeID" id="recipeIDform4">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=20>
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
	if (tabetaCount[i] >= 25) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで26回以上押されたら反応しない
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
	if (favoCount[i] >= 25) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで26回以上押されたら反応しない
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
<ul id="recipebox">
<%
for (int i = 0; i < recipeID.size(); i++) {
	String face = "";
	if (tabetaList.get(i)) face = "aceat.png";
	else face = "bceat.png";
	String heart = "";
	if (favoList.get(i)) heart = "pink_heart.png";
	else heart = "clear_heart.png";
%>
<div class="pic_frame"><li><a href="RecipeServlet?recipeID=<%= recipeID.get(i) %>"><img src="images/RyouriPIC/<%= imageName.get(i) %>" alt="レシピページ遷移"><p><%= ryourimei.get(i) %></p></a></li>
<a href="javascript:tabetabutton(<%= i %>, <%= recipeID.get(i) %>)" class="face<%= recipeID.get(i) %>"><img src="images/<%= face %>" alt="今日食べたボタン" width="20" height="20"></a>
<a href="javascript:favobutton(<%= i %>, <%= recipeID.get(i) %>)" class="heart<%= recipeID.get(i) %>"><img src="images/<%= heart %>" alt="お気に入りボタン" width="20" height="20"></a>
</div>
<%
}
%>
</ul>
<%
} else {
%>
<h2><%= NFOUND_ERRORMSG %></h2>
<%
}
%>
<script>
//ページ送りしたとき
function favopage(i) {
	document.getElementById('pageNumForm').value = i;
	favopageform.submit();
}
</script>
<%
//検索結果が26件以上ならページ送りするためのリンクを用意する
if (recipeNum > DATA_PER_PAGE) {
	int pageTotal = (recipeNum - 1) / DATA_PER_PAGE + 1;
%>
      <form action="FavoPageServlet" method="get" name="favopageform">
      <input type="hidden" name="pageNum" id="pageNumForm">
      </form>
      <div class="page-number">
        <ul class="page-list">
      <!-- ｢<<｣の表示 -->
      <li><%
      if (pageNum == 1) out.print("<<");
      else out.print("<a href=\"javascript:favopage(1)\" class=\"page-link\"><<</a>");
      %></li>
      <!-- ｢< 前へ｣の表示 -->
      <li><%
      if (pageNum == 1) out.print("< 前へ");
      else out.print("<a href=\"javascript:favopage(" + (pageNum - 1) + ")\" class=\"page-link\">< 前へ</a>");
      %></li>
      <!-- ｢... 4 5 6 7 8 9 10 11 12 ...｣の表示 pageNumの前後4件まで -->
      <%
      for (int i = Math.max(1, pageNum - 5); i <= Math.min(pageNum + 5, pageTotal); i++) {
    	  if (i == pageNum - 5 || i == pageNum + 5) out.println("<li>...</li>");
          else if (i == pageNum) out.println("<li>" + i + "</li>");
    	  else out.println("<li><a href=\"javascript:favopage(" + i + ")\" class=\"page-link\">" + i + "</a></li>");
      }
      %>
      <!-- ｢次へ >｣の表示 -->
      <li><%
      if (pageNum == pageTotal) out.print("次へ >");
      else out.print("<a href=\"javascript:favopage(" + (pageNum + 1) + ")\" class=\"page-link\">次へ ></a>");
      %></li>
      <!-- ｢>>｣の表示 -->
      <li><%
      if (pageNum == pageTotal) out.print(">>");
      else out.print("<a href=\"javascript:favopage(" + pageTotal + ")\" class=\"page-link\">>></a>");
      %></li>
    </ul>
    </div>
<%
}
%>
</div>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
  </body>
<!--head終了-->
</html>
