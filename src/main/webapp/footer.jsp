<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- footer開始 --><!--0120-->
<link rel="stylesheet" href="CSS/footer.css" type="text/css">
<footer>
 <h4><a href="javascript:sendFooter('top.jsp')">TOPページへ</a></h4>
 <p class="danger">ブラウザバック非推奨</p>
</footer>
<script>
//二度押し防止機能
var sendflag = false;
function sendFooter(uri) {
	if (!sendflag) {
		sendflag = true;
		location.href = uri;
	}
}
</script>
<!-- footer終了 -->