<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="pac1.func.Util" %>
<%@ page import="java.util.Calendar" %>
<%
String NFOUND_ERRORMSG = "この月は食べたボタンを押していません。"; //表示する月に食べたボタンを押されてない場合表示するエラーメッセージ
String NTABETA_ERRORMSG = "食べたボタンをまだ押していません。"; //食べたボタンが押されたことがない場合表示するエラーメッセージ
String year = (String)request.getAttribute("year"); //表示する年と月
String month = (String)request.getAttribute("month");
int youbi = (int)request.getAttribute("youbi");
ArrayList<String> tabetaMonthList = (ArrayList)(request.getAttribute("tabetaMonthList")); //食べたことのある年と月のリスト
ArrayList<Integer> tabetaCountList = (ArrayList)(request.getAttribute("tabetaCountList")); //上に対応する食べた回数
ArrayList<String> ryourimei = (ArrayList)(request.getAttribute("ryourimei")); //表示する料理名(最大25件)
ArrayList<Integer> recipeID = (ArrayList)(request.getAttribute("ryouriID")); //表示するレシピのID(最大25件)
ArrayList<Integer> tabetaDayList = (ArrayList)(request.getAttribute("tabetaDayList")); //表示するレシピの食べた日(最大25件)
int recipeNum = 0; //表示する年と月に食べた件数
if (tabetaMonthList.indexOf(year + month) >= 0) recipeNum = tabetaCountList.get(tabetaMonthList.indexOf(year + month));
%>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="utf-8" >
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | 食べた履歴</title>
  <link rel="stylesheet" href="CSS/tabeta.css">
  <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->
<body>
 <div id="wrapper">
<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->
<%
//認証チェック
if (!Util.checkAuth(request, response)) return;
%>
 <div id="wrap" class="clearfix">
  <div class="content">

<!--サイドバー部分開始-->
  <aside>
   <h3>月別</h3>
    <div class="sidecontent">
<%
if (tabetaMonthList.size() > 0) {
	ArrayList<String> yearList = new ArrayList<>();
	for (int i = 0; i < tabetaMonthList.size(); i++) {
		String tabetaYear = tabetaMonthList.get(i).substring(0, 4);
		if (i == 0) {
			yearList.add(tabetaYear);
%>
<details<% if (tabetaYear.equals(year)) out.print(" open"); %>>
	<summary><%= tabetaYear %>年</summary>
	<ul class="tabetaYear">
<%
		} else if (yearList.indexOf(tabetaYear) == -1) {
			yearList.add(tabetaYear);
%>
	</ul>
</details>
<details<% if (tabetaYear.equals(year)) out.print(" open"); %>>
	<summary><%= tabetaYear %>年</summary>
	<ul class="tabetaYear">
<%
		}
%>
		<li><a href="TabetaPageServlet?year=<%= tabetaYear %>&month=<%= tabetaMonthList.get(i).substring(4) %>"><%= tabetaMonthList.get(i).substring(4) %>月(<%= tabetaCountList.get(i) %>)</a></li>
<%
	}
%>
	</ul>
</details>
<%
} else {
%>
            <details open>
              <summary><%= year %>年</summary>
              <ul class="tabetaYear">
                <li><a href="TabetaPageServlet?year=<%= year %>&month=<%= month %>"><%= month %>月(0)</a></li>
              </ul>
            </details>
<%
}
%>
          </div><!--sidecontent-->
  </aside>

<!--mainコンテンツ開始-->
<main>
<h1><%= year %>年<%= month %>月の食べた履歴(日付クリックで編集可能)</h1>
 <hr color="#696969" width="100%" size="2">
<%
Calendar c = Calendar.getInstance();
c.clear();
c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
int dayMax = c.getActualMaximum(Calendar.DATE); //月末が何日か(30日まであれば30が格納される)
%>
<div class="cal">
      <ul id="tabetalist">
<%
for (int i = 1; i < youbi; i++) {
	if (youbi != 7) {
%>
        <div class="pic_frame">
          <li>
          </li>
        </div>
<%
	}
}
int index = 0;
for (int i = 0; i < dayMax; i++) {
%>
        <div class="pic_frame">
          <li>
            <p class="datetitle"><a href="TabetaDayPageServlet?year=<%= year %>&month=<%= month %>&day=<%= String.format("%02d", i + 1) %>"><%= i + 1 %>日</a></p>
<%
	if (index < tabetaDayList.size() && tabetaDayList.get(index) == i + 1) {
		for (; tabetaDayList.get(index) == i + 1; index++) {
			String shortTitle = ryourimei.get(index);
			if (ryourimei.get(index).length() > 5) shortTitle = ryourimei.get(index).substring(0, 5) + "…";
%>
            <p class="title"><a href="RecipeServlet?recipeID=<%= recipeID.get(index) %>" title="<%= ryourimei.get(index) %>"><%= shortTitle %></a></p>
<%
			if (index == tabetaDayList.size() - 1) {
				break;
			}
		}
	} else {
%>
			<p class="title">&emsp;</p>
<%
	}
%>
          </li>
        </div>
<%
}
for (int i = 0; i < 7 - (dayMax + youbi - 1) % 7 ; i++) {
	if ((dayMax + youbi - 1) % 7 != 0) {
%>
        <div class="pic_frame">
          <li>
          </li>
        </div>
<%
	}
}
%>
        </ul>
</div>

<div class="pageokuri">
<%
if (month.equals("01")) {
	out.print("<a href=\"TabetaPageServlet?year=" + (Integer.parseInt(year) - 1) + "&month=12\" id=\"pageokurimae\"><<前月</a>");
	out.print("<a href=\"TabetaPageServlet?year=" + year + "&month=02\" id=\"pageokuritugi\">次月>></a>");
} else if (month.equals("12")) {
	out.print("<a href=\"TabetaPageServlet?year=" + year + "&month=11\" id=\"pageokurimae\"><<前月</a>");
	out.print("<a href=\"TabetaPageServlet?year=" + (Integer.parseInt(year) + 1) + "&month=01\" id=\"pageokuritugi\">次月>></a>");
} else { //月は08月のようにあらわすためゼロパディングする
	out.print("<a href=\"TabetaPageServlet?year=" + year + "&month=" + String.format("%02d", (Integer.parseInt(month) - 1)) + "\" id=\"pageokurimae\"><<前月</a>");
	out.print("<a href=\"TabetaPageServlet?year=" + year + "&month=" + String.format("%02d", (Integer.parseInt(month) + 1)) + "\" id=\"pageokuritugi\">次月>></a>");
}
%>
</div>

</main>
</div>
</div>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->
</div>
</body>
</html>