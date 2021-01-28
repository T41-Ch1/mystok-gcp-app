<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="utf-8" >
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | LOGIN</title>
  <link href="CSS/login.css" rel="stylesheet">
  <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
<div id="wrapper">
<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<!--form-wrapper start-->
<div class="form-wrapper">
 <h1>LOGIN</h1>

<form action="LoginServlet" method="post">
  <input type="hidden" name="targetURI" value="<%= (String)session.getAttribute("targetURI") %>">

  <div class="form-item">
   <input type="text" name="username" required placeholder="　アカウント名を入力">
  </div>

  <div class="form-item">
   <input type="password" name="password" required placeholder="　パスワードを入力">
  </div>

  <div class="button-panel">
   <input type="submit" class="button" title="Sign In" value="ログイン">
  </div>

</form>

<div class="form-footer">
 <p><a href="newuser.jsp">新規会員登録</a></p>
</div>

</div>
<!--form-wrapper fin-->

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>
</body>
</html>