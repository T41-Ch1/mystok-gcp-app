<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="pac1.func.Util" %>
<%
String NFOUND_ERRORMSG = "この日は食べたボタンを押していません。"; //表示する日に食べたボタンを押されてない場合表示するエラーメッセージ
String year = (String)request.getAttribute("year"); //表示する日
String month = (String)request.getAttribute("month");
String day = (String)request.getAttribute("day");
ArrayList<String> ryourimei = (ArrayList)(request.getAttribute("ryourimei")); //表示する料理名(最大5件)
ArrayList<Integer> recipeID = (ArrayList)(request.getAttribute("ryouriID")); //表示するレシピのID(最大5件)
ArrayList<String> imageName = (ArrayList)(request.getAttribute("imageName")); //表示する画像名(最大5件)
ArrayList<Boolean> isMyRecipe = (ArrayList)(request.getAttribute("isMyRecipe")); //表示する料理がマイレシピか(最大5件)
ArrayList<Boolean> tabetaList = (ArrayList)(request.getAttribute("tabetaList")); //表示する料理のTabeta情報(最大5件)
ArrayList<Boolean> favoList = (ArrayList)(request.getAttribute("favoList")); //表示する料理のFavo情報(最大5件)
Calendar c = Calendar.getInstance();
c.clear();
c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
int dayMax = c.getActualMaximum(Calendar.DATE); //表示する月の月末が何日か(30日まであれば30が格納される)
String yearNext;
String monthNext;
String dayNext;
if (day.equals(String.valueOf(dayMax))) {
	if (month.equals("12")) {
		yearNext = String.valueOf(Integer.parseInt(year) + 1);
		monthNext = "01";
	}
	else {
		yearNext = year;
		monthNext = String.format("%02d", Integer.parseInt(month) + 1);
	}
	dayNext = "01";
} else {
	yearNext = year;
	monthNext = month;
	dayNext = String.format("%02d", Integer.parseInt(day) + 1);
}
String yearPrev;
String monthPrev;
String dayPrev;
if (day.equals("01")) {
	c.clear();
	if (month.equals("01")) {
		yearPrev = String.valueOf(Integer.parseInt(year) - 1);
		monthPrev = "12";
		c.set(Integer.parseInt(year) - 1, 11, 1);
	} else {
		yearPrev = year;
		monthPrev= String.valueOf(Integer.parseInt(month) - 1);
		c.set(Integer.parseInt(year), Integer.parseInt(month) - 2, 1);
	}
	dayPrev = String.format("%02d", c.getActualMaximum(Calendar.DATE)); //表示する月の先月の月末が何日か(30日まであれば30が格納される)
} else {
	yearPrev = year;
	monthPrev = month;
	dayPrev = String.format("%02d", Integer.parseInt(day) - 1);
}
c = Calendar.getInstance();
String now = String.valueOf(c.get(Calendar.YEAR)) + String.format("%02d", c.get(Calendar.MONTH) + 1) + String.format("%02d", c.get(Calendar.DATE));
%>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | 食べた履歴</title>
  <link rel="stylesheet" href="CSS/tabetaday.css">
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

  <h1><span><%= year %>年<%= month %>月<%= day %>日</span>の食べた履歴<span id="iconnotes">(<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/aceat.png" alt="お気に入りボタン" width="35" height="35">は"今日"食べたレシピです)</span></h1>
<!--レシピ情報-->

<%
if (recipeID.size() > 0) {
	for (int i = 0; i < recipeID.size(); i++) {
		String face = "";
		if (tabetaList.get(i)) face = "aceat.png";
		else face = "bceat.png";
		String heart = "";
		if (favoList.get(i)) heart = "pink_heart.png";
		else heart = "clear_heart.png";
%>
<div class="recipebox">
 <div class="recipeimage">
  <img alt="料理画像" width="200" height="200"
     src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/RyouriPIC/<%= imageName.get(i) %>" class="recipetori">
 </div>
  <div class ="recipe-text">
   <h2 class="recipetitle">
    <a class="recipetitlelink" href="javascript:send('RecipeServlet?recipeID=<%= recipeID.get(i) %>')"><%= ryourimei.get(i) %></a><br>
   </h2>

   <a href="javascript:tabetabutton(<%= i %>, <%= recipeID.get(i) %>)" class="face<%= recipeID.get(i) %>"><img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/<%= face %>"
      alt="今日食べたボタン" width="35" height="35"></a>
   <a href="javascript:favobutton(<%= i %>, <%= recipeID.get(i) %>)" class="heart<%= recipeID.get(i) %>"><img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/<%= heart %>"
      alt="お気に入りボタン" width="35" height="35"></a>
  <div class ="hennsaku">
   <button onclick="javascript:delbutton(<%= recipeID.get(i) %>);" class="dbox">
     <font size="4">食べた履歴の削除</font>
     <img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/dustbox.png" alt="食べた履歴削除ボタン" width="30" height="30">
   </button>
<%
		if (isMyRecipe.get(i)) {
%>
   <div class ="hennsaku">
   <a href="javascript:editbutton(<%= recipeID.get(i) %>)">レシピ編集<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/pen.png"
       alt="レシピ編集ボタン" width="30" height="30"></a>
  </div>
<%
		}
%>
  </div>
<div class ="clear"></div>
</div>
</div>
<%
	}
} else {
%>
<h2><%= NFOUND_ERRORMSG %></h2>
<%
}
%>

<h2 class="rtitle">食べた履歴を追加</h2>

<div class="kennsaku">
  <span class="rtitle1">レシピID:</span>
 <span><input type="text" id="previewRecipeID" maxlength=8 title="レシピIDを入力して下さい"
   onInput="value = value.replace(/[^0-9]+/i,'');" onChange="document.getElementById('previewRecipe').disabled = false;">
 <input type="button" id="previewRecipe" value="検索" onclick="javascript:previewbutton(this);"></span>
</div>

<iframe id="cFrame2" width=600 height=150 name="vessel2"></iframe>

<div class="pagemove">
<input type="button" onclick="location.href='TabetaDayPageServlet?year=<%= yearPrev %>&month=<%= monthPrev %>&day=<%= dayPrev %>'" value="前日" class="dayprev">
<input type="button" onclick="location.href='TabetaPageServlet?year=<%= year %>&month=<%= month %>'" value="<%= year %>年<%= month %>月に戻る" class="backcalendar">
<input type="button" onclick="location.href='TabetaDayPageServlet?year=<%= yearNext %>&month=<%= monthNext %>&day=<%= dayNext %>'" value="翌日" class="daynext">
</div>


  </main>
 </div>
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

<form name="tabetaDayDeleteForm" action="TabetaDayDeleteServlet" method="post" target="vessel2">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="year" value="<%= year %>">
<input type="hidden" name="month" value="<%= month %>">
<input type="hidden" name="day" value="<%= day %>">
<input type="hidden" name="recipeID" id="recipeIDform5">
</form>
<form name="tabetaDayPreviewForm" action="TabetaDayPreviewServlet" method="post" target="vessel2">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="year" value="<%= year %>">
<input type="hidden" name="month" value="<%= month %>">
<input type="hidden" name="day" value="<%= day %>">
<input type="hidden" name="recipeID" id="recipeIDform6">
</form>
<form name="tabetaDayPageForm" action="TabetaDayPageServlet" method="post">
<input type="hidden" name="year" value="<%= year %>">
<input type="hidden" name="month" value="<%= month %>">
<input type="hidden" name="day" value="<%= day %>">
</form>
<form method="post" name="updateForm" action="RecipeRegisterPageServlet">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDUpdateForm">
</form>

<script>
//二度押し防止機能
var sendflag = false;
function send(uri) {
	if (!sendflag) {
		sendflag = true;
		location.href = uri;
	}
}
function delbutton(i) {
	if (!sendflag && window.confirm('このレシピを食べた履歴を削除しますか？')) {
		document.getElementById('recipeIDform5').value = i;
		tabetaDayDeleteForm.submit(); //食べた履歴削除
	} else {
		return false;
	}
}
document.getElementById('previewRecipeID').addEventListener('keypress', pushpreview); //レシピ検索フォーム入力中にキーが押されたらpushpreview(e)を実行
function pushpreview(e) {
  	if (e.keyCode === 13) previewbutton(document.getElementById('previewRecipe')); //keyCode13はEnterキー
	return false;
}
function previewbutton(btn) {
	if (<%= recipeID.size() %> == 5) {
		alert('この日の食べた登録数の上限です。');
	} else if (<%= Integer.parseInt(year + month + day) %> > <%= Integer.parseInt(now) %>) {
		alert('予め食べたを登録しておくことは出来ません。');
	} else if (document.getElementById('previewRecipeID').value.length == 0) {
		alert('何も入力されていません。');
	} else if (document.getElementById('previewRecipeID').value == 0) {
		alert('0は入力できません。');
	} else if (document.getElementsByClassName('face' + document.getElementById('previewRecipeID').value).length > 0) {
		alert('既に登録されています。');
	} else {
		btn.disabled = true;
		document.getElementById('recipeIDform6').value = document.getElementById('previewRecipeID').value;
		tabetaDayPreviewForm.submit(); //プレビュー表示
	}
}
function editbutton(i) {
	document.getElementById('recipeIDUpdateForm').value = i;
	updateForm.submit(); //レシピ編集画面遷移
}
</script>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
</body>
</html>
