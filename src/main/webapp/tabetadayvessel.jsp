<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>日別食べた履歴削除ボタン</title>
</head>
<body>
<div id="result"></div>
<script>
var previewHtml = '<div class="edgebox" style="display: flex; justify-content: space-between;">'
	+ '<form action="TabetaDayUpdateServlet" method="post">'
	+ '<font size="5" face="serif" style="font-weight: bold;">レシピ<br>'
	+ '<span><%= request.getAttribute("ryourimei") %></span><br>'
	+ 'を追加します。よろしいですか？'
	+ '<input type="hidden" name="recipeID" value="<%= request.getAttribute("recipeID") %>">'
	+ '<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">'
	+ '<input type="hidden" name="year" value="<%= request.getAttribute("year") %>">'
	+ '<input type="hidden" name="month" value="<%= request.getAttribute("month") %>">'
	+ '<input type="hidden" name="day" value="<%= request.getAttribute("day") %>">'
	+ '<input type="submit" value="OK">'
	+ '</font>'
	+ '</form>'
	+ '<img src="images/RyouriPIC/<%= request.getAttribute("imageName") %>" alt="<%= request.getAttribute("ryourimei") %>" width="125" height="125">'
	+ '</div>';
var notfoundHtml = '<font size="5" face="serif" weight="bold">指定されたレシピが見つかりませんでした。</font>';
if ('<%= request.getAttribute("logMessage") %>' == '' ) {
	if ('<%= request.getAttribute("mode") %>' == 'Preview') document.getElementById('result').innerHTML = previewHtml;
	else if ('<%= request.getAttribute("mode") %>' == 'NotFound') document.getElementById('result').innerHTML = notfoundHtml;
	else window.parent.document.tabetaDayPageForm.submit();
} else {
	alert('<%= request.getAttribute("logMessage") %>');
}
</script>
</body>
</html>