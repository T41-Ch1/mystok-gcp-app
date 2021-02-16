<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
 <meta charset="utf-8" >
 <meta http-equiv="X-UA-Compatible" content="IE=11">
 <title>レシピコンシェル | 新規登録</title>
 <link href="CSS/NewUserStyle.css" rel="stylesheet">
 <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
 <div id="wrapper">
<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<div class="content">
 <div class="form-wrapper">
  <h1>新規登録</h1>
 <div class="border"></div>

  <form action="UserRegisterServlet" name="registerform" method="post" onSubmit="return false;">

    <div class="form-item">
      <input type="text" id="nameNew" name="name" required maxlength=15
       placeholder="　希望のアカウント名を入力" onChange="clearIcon();">
    </div>

  <div class="checkb">
   <input type="button" value="重複確認" id="dupchkbtn" onclick="dupCheck(this);"/>
  </div>

  <div id="kekkaicon" class="kekka">
   <img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/unchecked.png" alt="UNCHECKED" width="24" height="24">
  </div>

    <div class="form-item">
     <input type="password" id="passNew1" name="password" placeholder="　希望のパスワード">
    </div>

    <div class="form-item">
     <input type="password" id="passNew2" name="password" placeholder="　希望のパスワード(確認用)">
    </div>

    <div class="button-panel">
      <input type="button" class="button" title="Sign In" value="新規登録をする" onClick="func(this);">
    </div>

  </form>

  <div class="form-footer"></div>
</div>
</div>

<script>
//アカウント名重複チェック 1文字以上であるか、使用できない文字を含んでいないかもチェックする
var regexp = /&|<|>|\"|\'/;
function dupCheck(btn) {
	if (document.getElementById('nameNew').value.length == 0) {
		alert('名前が入力されていません');
		return false;
	}
	if (regexp.test(document.getElementById('nameNew').value)) {
		alert('&、<、>、\"、\'は使用できません');
		return false;
	}
	btn.disabled = true;
	document.getElementById('userNameForDup').value = document.getElementById('nameNew').value;
	document.usernameDupCheckForm.submit();
}
//アカウント名のテキストボックスが変更されたら重複確認アイコンをデフォルトに戻す 重複チェックボタンを押せるようにする
function clearIcon() {
	document.getElementById('kekkaicon').innerHTML = '<img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/unchecked.png" alt="UNCHECKED" width="24" height="24">';
	document.getElementById('dupchkbtn').disabled = false;
}
</script>

<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="usernameDupCheckForm" action="UsernameDupCheckServlet" method="post" target="vessel">
<input type="hidden" name="userName" id="userNameForDup">
</form>

<script>
//二度押し防止機能 使用できない文字を含んでいないかもチェックする
var sendflag = false;
function func(btn) {
	if (regexp.test(document.getElementById('nameNew').value)) {
		alert('&、<、>、\"、\'は使用できません');
		return false;
	}
	if (document.getElementById('passNew1').value != document.getElementById('passNew2').value) {
		alert('パスワードが一致しません');
		return false;
	}
	if (document.getElementById('passNew1').value.length < 6) {

		alert('パスワードは6文字以上にしてください');
		return false;
	}
	if (!sendflag) {
		sendflag = true;
		btn.disabled = true;
		document.registerform.submit();
	}
}
</script>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
</body>
</html>
