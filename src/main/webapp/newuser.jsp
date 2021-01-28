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

  <form action="UserRegisterServlet" method="post">

    <div class="form-item">
      <input type="text" id="nameNew" name="name" required maxlength=15
       placeholder="　希望のアカウント名を入力" onChange="clearIcon();">
    </div>

  <div class="checkb">
   <input type="button" value="重複確認" onclick="dupCheck();"/>
  </div>

  <div id="kekkaicon" class="kekka">
   <img src="images/unchecked.png" alt="UNCHECKED" width="24" height="24">
  </div>

    <div class="form-item">
     <input type="password" name="password" minlength=6 placeholder="　希望のパスワード">
    </div>

    <div class="form-item">
     <input type="password" name="password" minlength=6 placeholder="　希望のパスワード(確認用)">
    </div>

    <div class="button-panel">
      <input type="submit" class="button" title="Sign In" value="新規登録をする">
    </div>

  </form>

  <div class="form-footer"></div>
</div>
</div>

<script>
//アカウント名重複チェック ユーザ名が変更されているか、1文字以上であるかもチェックする
function dupCheck() {
	if (document.getElementById('nameNew').value.length == 0) {
		alert('名前が入力されていません');
		return false;
	}
	document.getElementById('userNameForDup').value = document.getElementById('nameNew').value;
	document.usernameDupCheckForm.submit();
}
//重複確認アイコンをデフォルトに戻す
function clearIcon() {
	document.getElementById('kekkaicon').innerHTML = '<img src="images/unchecked.png" alt="UNCHECKED" width="24" height="24">';
}
</script>

<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="usernameDupCheckForm" action="UsernameDupCheckServlet" method="post" target="vessel">
<input type="hidden" name="userName" id="userNameForDup">
</form>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
</body>
</html>
