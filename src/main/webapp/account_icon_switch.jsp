<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>アカウント名重複確認ボタンの応答用</title>
<link rel="icon" href="images/fav32.ico">
</head>
<body>
<script>
if (<%= request.getAttribute("result") %>) window.parent.document.getElementById('kekkaicon').innerHTML = '<img src="images/out.png" alt="OUT" width="24" height="24">';
else window.parent.document.getElementById('kekkaicon').innerHTML = '<img src="images/ok.png" alt="OK" width="24" height="24">';
</script>
</body>
</html>