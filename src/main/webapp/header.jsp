<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- header開始 --><!--0120-->
<link rel="stylesheet" href="CSS/header.css" type="text/css">
<%
if(request.isUserInRole("user")) {
%>
<header>
 <div class="logo">
  <a href="javascript:sendHeader('top.jsp')"><img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/logo.png" alt="Logo"></a>
 </div>
  <nav>
   <ul class="global-nav">
    <li><a href="javascript:sendHeader('MypageServlet')">マイページ</a></li>
    <li><a href="javascript:sendHeader('logout.jsp')">ログアウト</a></li>
   </ul>
  </nav>
</header>
<%
} else {
%>
<header>
 <div class="logo">
  <a href="javascript:sendHeader('top.jsp')"><img src="https://storage.googleapis.com/mystok-gcp-dev-image-bucket/images/logo.png" alt="Logo"></a>
 </div>
  <nav>
   <ul class="global-nav">
    <li><a href="javascript:sendHeader('newuser.jsp')">会員登録</a></li>
    <li><a href="javascript:sendHeader('MypageServlet')">ログイン</a></li>
   </ul>
  </nav>
</header>
<%
}
%>
<script>
//二度押し防止機能
var sendflag = false;
function sendHeader(uri) {
	if (!sendflag) {
		sendflag = true;
		location.href = uri;
	}
}
</script>
<!-- header終了 -->
