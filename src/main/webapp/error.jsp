<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=11">
<title>レシピコンシェル | エラーページ</title>
<link rel="stylesheet" href="CSS/ErrorStyle.css" type="text/css">
<link rel="icon" href="images/fav32.ico">
</head>

<body>
<div id="wrapper">
<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<div class="content">
 <h1>エラーページ</h1>
 <h2>
 <%
 if(request.getAttribute("errorMessage") == null) out.print("エラーが発生しました。");
 else out.print(request.getAttribute("errorMessage"));
 %>
 </h2>
</div>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->
</div>
</body>
</html>