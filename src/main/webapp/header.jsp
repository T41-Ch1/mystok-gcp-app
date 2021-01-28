<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- header開始 --><!--0120-->
<link rel="stylesheet" href="CSS/header.css" type="text/css">
<%
if(request.isUserInRole("user")) {
%>
<header>
 <div class="logo">
  <a href="top.jsp"><img src="images/logo.png" alt="Logo"></a>
 </div>
  <nav>
   <ul class="global-nav">
    <li><a href="MypageServlet">マイページ</a></li>
    <li><a href="logout.jsp">ログアウト</a></li>
   </ul>
  </nav>
</header>
<%
} else {
%>
<header>
 <div class="logo">
  <a href="top.jsp"><img src="images/logo.png" alt="Logo"></a>
 </div>
  <nav>
   <ul class="global-nav">
    <li><a href="newuser.jsp">会員登録</a></li>
    <li><a href="MypageServlet">ログイン</a></li>
   </ul>
  </nav>
</header>
<%
}
%>
<!-- header終了 -->