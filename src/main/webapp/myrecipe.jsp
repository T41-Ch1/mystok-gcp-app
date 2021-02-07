<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pac1.func.Util" %>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | マイレシピ一覧</title>
  <link href="CSS/MyRecipeStyle.css" rel="stylesheet">
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
<div id="wrap" class="clearfix">
 <div class="content">
  <main class="main">

   <div class="new">
    <button onclick="location.href='RecipeRegisterPageServlet';" class="btnn">MYレシピ新規登録 ≫</button>
   </div>

  <h1>MYレシピ一覧</h1>
<%
final int DATA_PER_PAGE = 10;
ArrayList<Integer> ryouriID = (ArrayList)(request.getAttribute("ryouriID")); //
ArrayList<String> ryourimei = (ArrayList)(request.getAttribute("ryourimei")); //
ArrayList<String> imageName = (ArrayList)(request.getAttribute("imageName")); //
ArrayList<String> syoukai = (ArrayList)(request.getAttribute("syoukai")); //
ArrayList<ArrayList<String[]>> list = new ArrayList<>(); //表示するレシピの分量(最大10件)
list = (ArrayList<ArrayList<String[]>>)request.getAttribute("recipeBunryouList");
ArrayList<Boolean> favoList = (ArrayList)(request.getAttribute("favoList")); //
ArrayList<Boolean> tabetaList = (ArrayList)(request.getAttribute("tabetaList")); //
int recipeNum = (int)(request.getAttribute("recipeNum")); //検索結果の件数
int pageNum = (int)(request.getAttribute("pageNum")); //
if (ryouriID.size() > 0) {
%>
<form method="post" name="updateForm" action="RecipeRegisterPageServlet">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDUpdateForm">
</form>
<form method="post" name="deleteForm" action="RecipeDeleteServlet">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDDeleteForm">
</form>
<!--レシピ情報-->
<%

	for (int i = 0; i < ryouriID.size();i++) {
%>
  <div class ="recipebox">
   <div class ="recipeimage">
    <img alt="<%= ryourimei.get(i) %>" width="200" height="200"
     src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/RyouriPIC/<%= imageName.get(i) %>" class="recipetori">
   </div>
  <div class ="recipe-text">
   <h2 class="recipetitle" style="margin-block-end: 0.83em;">
    <a class="recipetitlelink" href="javascript:sendA('RecipeServlet?recipeID=<%= ryouriID.get(i) %>')"><%= ryourimei.get(i) %></a><br>
    <%
      String face = "";
      if (tabetaList.get(i)) face = "aceat.png";
      else face = "bceat.png";
      %>
      <a href="javascript:tabetabutton(<%= i %>, <%= ryouriID.get(i) %>)" class="face<%= ryouriID.get(i) %>"><img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/<%= face %>" alt="今日食べたボタン" width="35" height="35"></a>
      <%
      String heart = "";
      if (favoList.get(i)) heart = "pink_heart.png";
      else heart = "clear_heart.png";
      %>
      <a href="javascript:favobutton(<%= i %>, <%= ryouriID.get(i) %>)" class="heart<%= ryouriID.get(i) %>"><img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/<%= heart %>" alt="お気に入りボタン" width="35" height="35"></a>
   </h2>
  <div class ="material">
    <div><%= syoukai.get(i) %></div>
    材料：
		<%
		for (int j = 0; j < list.get(i).size(); j++) {
			if (j >= 15) {
				out.print("…等");
				break;
			}
			else if (j == 0) out.print(list.get(i).get(j)[0]);
			else out.print("、" + list.get(i).get(j)[0]);
		}
		%>
  </div>
  <div class ="hennsaku">
   <a href="javascript:editbutton(<%= ryouriID.get(i) %>)">レシピ編集<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/pen.png"
       alt="レシピ編集ボタン" width="30" height="30"></a>
   <a href="javascript:deletebutton(<%= ryouriID.get(i) %>)">レシピ削除<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/dustbox.png"
       alt="レシピ削除ボタン" width="30" height="30"></a>
  </div>

<div class ="clear"></div>
  </div>
</div><!--"recipebox" fin-->
<%
	}
%>

<script>
var sendflag = false;
function sendA(uri) {
	if (!sendflag) {
		sendflag = true;
		location.href = uri;
	}
}
function editbutton(i) {
	if (!sendflag) {
		document.getElementById('recipeIDUpdateForm').value = i;
		updateForm.submit(); //レシピ編集画面遷移
	}
}
function deletebutton(i) {
	if (!sendflag && confirm('レシピを削除します。よろしいですか？')) {
		document.getElementById('recipeIDDeleteForm').value = i;
		deleteForm.submit(); //レシピ削除
	}
}
</script>
<%
} else if (recipeNum == 0) {
%>
  <div class="nodata"><br>
   MYレシピが登録されていません。上のボタンからレシピを登録してみませんか？
  </div>
<%
} else {
%>
  <div class="nodata"><br>
   該当するレシピが見つかりませんでした。
  </div>
<%
}
//検索結果が11件以上ならページ送りするためのリンクを用意する
if (recipeNum > DATA_PER_PAGE) {
	int pageTotal = (recipeNum - 1) / DATA_PER_PAGE + 1;
%>
    <form action="MyRecipePageServlet" method="get" name="Myrecipeform">
    <input type="hidden" name="pageNum" id="pageNumForm">
    </form>
    <div class="page-number">
      <ul class="page-list">
    <!-- ｢<<｣の表示 -->
    <li><%
    if (pageNum == 1) out.print("<<");
    else out.print("<a href=\"javascript:myrecipe(1)\"><<</a>");
    %></li>
    <!-- ｢< 前へ｣の表示 -->
    <li><%
    if (pageNum == 1) out.print("< 前へ");
    else out.print("<a href=\"javascript:myrecipe(" + (pageNum - 1) + ")\" class=\"page-link\">< 前へ</a>");
    %></li>
    <!-- ｢... 4 5 6 7 8 9 10 11 12 ...｣の表示 pageNumの前後4件まで -->
    <%
    for (int i = Math.max(1, pageNum - 5); i <= Math.min(pageNum + 5, pageTotal); i++) {
      if (i == pageNum - 5 || i == pageNum + 5) out.println("<li>...</li>");
      else if (i == pageNum) out.println("<li>" + i + "</li>");
  	  else out.println("<li><a href=\"javascript:myrecipe(" + i + ")\" class=\"page-link\">" + i + "</a></li>");
    }
    %>
    <!-- ｢次へ >｣の表示 -->
    <li><%
    if (pageNum == pageTotal) out.print("次へ >");
    else out.print("<a href=\"javascript:myrecipe(" + (pageNum + 1) + ")\" class=\"page-link\">次へ ></a>");
    %></li>
    <!-- ｢>>｣の表示 -->
    <li><%
    if (pageNum == pageTotal) out.print(">>");
    else out.print("<a href=\"javascript:myrecipe(" + pageTotal + ")\" class=\"page-link\">>></a>");
    %></li>
  </ul>
  </div>
  <script>
   //ページ送りしたとき
   function myrecipe(i) {
     if (!sendflag) {
         sendflag = true;
         document.getElementById('pageNumForm').value = i;
         Myrecipeform.submit();
     }
   }
  </script>
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
for (int i = 0; i < ryouriID.size(); i++) {
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
<%
}
%>
  </main>
 </div>
</div>
<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div><!-- wrapper fin--><!--ここに</div>があるのが正当ですFix-->
</body>
</html>
