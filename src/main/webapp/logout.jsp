<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset=UTF-8">
<title>Insert title here</title>
<link rel="icon" href="images/fav32.ico">
</head>
<body>
<%
session.invalidate();

response.sendRedirect("top.jsp");
%>

</body>
</html>