<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>食べたボタン/Favoボタンの応答用</title>
<link rel="icon" href="images/fav32.ico">
</head>
<body>
<%
int recipeID = (int)(request.getAttribute("recipeID")); //押されたボタンのレシピのID
String buttonType = (String)(request.getAttribute("buttonType")); //tabetaかfavoか
String buttonState = (String)(request.getAttribute("buttonState")); //onかoffか
String buttonSize = (String)(request.getAttribute("buttonSize")); //表示するボタンサイズ
String logMessage = (String)(request.getAttribute("logMessage")); //ダイアログに表示する文章
%>
<script>
if ('<%= logMessage %>' == '' ) {
	if ('<%= buttonType %>' == 'tabeta') {
		var recipeClass = window.parent.document.getElementsByClassName('face<%= recipeID %>');
		if ('<%= buttonState %>' == 'on') {
			for (var i = 0; i < recipeClass.length; i++) recipeClass[i].innerHTML = '<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/eatanime2.png" alt="今日食べたボタン" width=<%= buttonSize %> height=<%= buttonSize %>>';
		}
		else {
			for (var i = 0; i < recipeClass.length; i++) recipeClass[i].innerHTML = '<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/eatanime1.png" alt="今日食べたボタン" width=<%= buttonSize %> height=<%= buttonSize %>>';
		}
	} else {
		var recipeClass = window.parent.document.getElementsByClassName('heart<%= recipeID %>');
		if ('<%= buttonState %>' == 'on') {
			for (var i = 0; i < recipeClass.length; i++) recipeClass[i].innerHTML = '<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/heartanime2.png" alt="お気に入りボタン" width=<%= buttonSize %> height=<%= buttonSize %>>';
		}
		else {
			for (var i = 0; i < recipeClass.length; i++) recipeClass[i].innerHTML = '<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/heartanime1.png" alt="お気に入りボタン" width=<%= buttonSize %> height=<%= buttonSize %>>';
		}
	}
} else {
	alert('<%= logMessage %>');
}
</script>
</body>
</html>
