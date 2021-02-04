<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="pac1.func.Util" %>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
 <meta charset="utf-8" >
 <meta http-equiv="X-UA-Compatible" content="IE=11">
 <title>レシピコンシェル | 会員情報変更</title>
 <link href="CSS/ChangeStyle.css" rel="stylesheet">
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

<h1>会員情報変更</h1>

<div class="content">
<!--アカウント名変更のbox-->
 <div class="form-wrapper">
  <h1 class="h1ti">アカウント名変更</h1>
 <div class="border"></div>

<form action="UserUpdateServlet" method="post" name="userUpdateForm1" onSubmit="return false;">
  <input type="hidden" name="mode" value="namechange">
  <div class="form-item">
   <input type="text" id="nameNew" name="nameNew" maxlength=15 required placeholder=" 希望のアカウント名を入力" value="<%= request.getRemoteUser() %>" onChange="clearIcon()">
  </div>

 <div class="checkb">
   <input type="button" value="重複確認" id="dupchkbtn" onclick="dupCheck(this);"/>
  </div>

 <div id="kekkaicon" class="kekka">
  <img src="images/unchecked.png" alt="UNCHECKED" width="24" height="24">
 </div>

 <div class="form-item">
   <input type="password" name="passwordOld" placeholder="　現パスワードを入力">
 </div>

 <div class="button-panel">
  <input type="button" class="button" title="アカウント名変更" value="アカウント名を変更する" onClick="return nameCheck();">
 </div>
</form>
<div class="form-footer"></div>
</div>

<!--パスワード変更のbox-->
<div class="form-wrapper">
  <h1 class="h1ti">パスワード変更</h1>
 <div class="border"></div>
<form action="UserUpdateServlet" method="post" name="userUpdateForm2" onSubmit="return false;">
 <input type="hidden" name="mode" value="passchange">
 <div class="form-item">
  <input type="password" id="passOld" name="passwordOld" placeholder="　現パスワードを入力">
 </div>

 <div class="form-item">
  <input type="password" id="passNew1" name="passwordNew" placeholder="　新パスワードを入力">
 </div>

 <div class="form-item">
  <input type="password" id="passNew2" name="passwordNew2" placeholder="　新パスワードを入力(確認)">
 </div>

 <div class="button-panel">
  <input type="button" class="button" title="パスワード変更" value="パスワードを変更する" onClick="return passCheck();">
 </div>
</form>
 <div class="form-footer"></div>
</div>

</div>

<div class="taikai">
  <input type="button" class="taikaibutton" title="退会" value="退会する" onclick="userExit();">
</div>

<script>
var sendflag = false;
//名前の変化確認 変更前と一致しなかったらsubmitする
function nameCheck() {
	if (document.getElementById('nameNew').value == '<%= request.getRemoteUser() %>') {
		alert('名前が変更されていません、入力し直してください');
		return false;
	} else if (!sendflag) {
		sendflag = true;
		document.userUpdateForm1.submit();
	}
}
//パスワードの変化/一致確認 新しいパスワードが一致して、古いパスワードと一致しなかったらsubmitする
function passCheck() {
	if (document.getElementById('passNew1').value == document.getElementById('passNew2').value) {
		if (document.getElementById('passNew1').value == document.getElementById('passOld').value) {
			alert('パスワードが変更されていません、入力し直してください');
			return false;
		} else if (document.getElementById('passNew1').value.length < 5) {
			alert('パスワードは6文字以上にしてください');
			return false;
		} else if (!sendflag) document.userUpdateForm2.submit();
	} else {
		alert('新しいパスワードが一致していません、入力し直してください');
		return false;
	}
}
//アカウント名重複チェック ユーザ名が変更されているか、1文字以上であるかもチェックする
function dupCheck(btn) {
	if (document.getElementById('nameNew').value.length == 0) {
		alert('名前が入力されていません');
		return false;
	} else if (document.getElementById('nameNew').value == '<%= request.getRemoteUser() %>') {
		alert('名前が変更されていません、入力し直してください');
		return false;
	}
	btn.disabled = true;
	document.getElementById('userNameForDup').value = document.getElementById('nameNew').value;
	document.usernameDupCheckForm.submit();
}
//アカウント名のテキストボックスが変更されたら重複確認アイコンをデフォルトに戻す 重複チェックボタンを押せるようにする
function clearIcon() {
	document.getElementById('kekkaicon').innerHTML = '<img src="images/unchecked.png" alt="UNCHECKED" width="24" height="24">';
	document.getElementById('dupchkbtn').disabled = false;
}
//退会時、2回確認をする
function userExit() {
	if (!sendflag && window.confirm('退会処理をします。よろしいですか？')) {
		if (window.confirm('退会処理をすると登録したレシピやお気に入りの情報などがすべて削除されます。本当によろしいですか？')) {
			document.userExitForm.submit();
		} else {
			alert('退会処理をキャンセルしました');
			return false;
		}
	} else {
		alert('退会処理をキャンセルしました');
		return false;
	}
}
</script>

<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="usernameDupCheckForm" action="UsernameDupCheckServlet" method="post" target="vessel">
<input type="hidden" name="userName" id="userNameForDup">
</form>
<form name="userExitForm" action="UserExitServlet" method="post">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
</form>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
</body>
</html>