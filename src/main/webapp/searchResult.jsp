<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%@ page import="java.net.URLEncoder" %>
<%
final int DATA_PER_PAGE = 10; //1ページごとに表示する最大件数
String NFOUND_ERRORMSG = "レシピが見つかりませんでした。"; //検索結果がなかった場合表示するエラーメッセージ
int recipeNum = (int)(request.getAttribute("recipeNum")); //検索結果の件数
int pageNum = (int)(request.getAttribute("pageNum")); //10件ごとに表示した場合何ページ目か 1からスタートする
String searchMode = (String)(request.getAttribute("searchMode")); //検索モード 料理名検索ならryouri 食材名検索ならsyokuzaiが格納される
String[] inputData = (String[])(request.getAttribute("inputData")); //検索窓に入力された文字列をスペースで分割したもの
String input = "";
if (inputData.length > 0) input = inputData[0]; //inputDataに格納された文字列をスペースで連結したもの
for (int i = 1; i < inputData.length; i++){
	input += "　" + inputData[i];
}
ArrayList<Integer> recipeID = (ArrayList)(request.getAttribute("recipeID")); //表示するレシピのID(最大10件)
ArrayList<String> recipeTitle = (ArrayList)(request.getAttribute("recipeTitle")); //表示するレシピ名(最大10件)
ArrayList<String> recipeIntro = (ArrayList)(request.getAttribute("recipeIntro")); //表示するレシピの紹介文(最大10件)
ArrayList<String> imageName = (ArrayList)(request.getAttribute("imageName")); //表示する画像名(最大10件)
ArrayList<ArrayList<String[]>> list = new ArrayList<>(); //表示するレシピの分量(最大10件)
list = (ArrayList<ArrayList<String[]>>)request.getAttribute("recipeBunryouList");
ArrayList<Boolean> tabetaList = (ArrayList)(request.getAttribute("tabetaList"));
ArrayList<Boolean> favoList = (ArrayList)(request.getAttribute("favoList"));
%>
<!DOCTYPE html>
<html>

<!--head開始-->
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | 検索結果一覧</title>
  <link href="CSS/SearchResultStyle.css" rel="stylesheet">
  <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
<div id="wrapper">

<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<div id="wrap" class="clearfix">
  <div class="content">
<%
//食材名検索で複数の食材が指定された場合のみ特に消費したい食材を選択し直せる枠を表示
if (searchMode.equals("syokuzai") && inputData.length > 1) {
%>
  <!--aside開始-->
    <aside><!--サイドバー部分開始-->
      <h3>１番消費したい食材</h3>

        <form action="SearchResultServlet" method="get"><!--サイドバー側のラジオボタン-->
          <div class = "sradio-font">
            <ul>
            <%
            for (int i = 0; i < inputData.length; i++) {
            	//選択された食材に対する検索文字列を生成しnewInputに格納する
        		String newInput = inputData[i];
        		int j;
            	for (j = 0; j < i; j++) {
            		newInput += "　" + inputData[j];
            	}
            	for (j++; j < inputData.length; j++) {
            		newInput += "　" + inputData[j];
            	}
            %>
             <li>
               <!-- パラメータinputにnewInputを格納して送信できるようにする 最初の要素にチェックを入れる -->
               <input type="radio" id="option<%= i %>" name="input" value="<%= newInput %>" <% if (i == 0) out.print("checked"); %>><label for="option<%= i %>"><%= inputData[i] %></label>
               <div class="scheck"></div>
             </li>
            <%
            }
            %>
             <li>
              <input id="narabikae" type="submit" value="並び替え">
             </li>
            </ul>
          </div>
        </form><!--サイドバー側のラジオボタン終了-->
    </aside><!--サイドバー部分終了-->
<%
}
%>

  <!--main開始-->
      <main  class="main">
       <article>
         <h1>検索結果 <%= recipeNum %> 件</h1>
         <div class="formparts">
           <form action="SearchResultServlet" method="get">
             <div class = "radio-font"><!--ラジオボタンのdiv４-->
               <ul class ="radiolist"><!--ラジオボタンリストのul-->
                 <li>
                   <input type="radio" id = "f-option" name="searchMode" value="syokuzai" <% if (searchMode.equals("syokuzai")) out.print("checked"); %>>
                   <label for="f-option">食材名検索</label>
                   <div class ="check"></div><!--ラジオボタンチェックする円のdiv５-->
                 </li>
                 <li>
                   <input type="radio" id ="s-option" name="searchMode" value="ryouri" <% if (!searchMode.equals("syokuzai")) out.print("checked"); %>>
                   <label for="s-option">料理名検索</label>
                   <div class ="check"></div><!--ラジオボタンチェックする円のdiv６-->
                 </li>
               </ul>
             </div>

      <!--検索窓開始-->

     <div class="kennsaku">
                <!-- \u3041-\u3096は平仮名、\u3000は全角スペース、\u30fcは長音 これらの文字の組み合わせのみ許可する 正規表現で書いたのがpatternの所 -->
                <input id="mado" type="text" name="input" size=50 pattern="[\u3041-\u3096|\u3000|\u30fc]*" maxlength=50 value="<%=input%>" title="ひらがなで入力して下さい" required>
                <input id="mbutton" type="submit" value="レシピ検索" onclick="func1()">
                <script>
                 //二度押し防止機能
                 function func1() {
                  document.mkensaku.submit();
                  document.getElementById('mbutton').disabled = true;
                 }
                </script>
               </div>
              </form>
      <!--検索窓終了-->
         </div>
<%
if (recipeNum == 0) {
%>
	<!--レシピ情報がヒットしなかった-->
    <div class="nodata"><br>
     <%= NFOUND_ERRORMSG %>
    </div>
<%
} else {
%>
<iframe id="cFrame" width=0 height=0 name="vessel" style="width: 0; height: 0; border: 0; border: none; position: absolute;"></iframe>
<form name="tabetaDeleteForm" action="TabetaDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform1">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="tabetaInsertForm" action="TabetaInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform2">
<input type="hidden" name="buttonType" value="tabeta">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="favoDeleteForm" action="FavoDeleteServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform3">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="on">
<input type="hidden" name="buttonSize" value=35>
</form>
<form name="favoInsertForm" action="FavoInsertServlet" method="post" target="vessel">
<input type="hidden" name="userName" value="<%= request.getRemoteUser() %>">
<input type="hidden" name="recipeID" id="recipeIDform4">
<input type="hidden" name="buttonType" value="favo">
<input type="hidden" name="buttonState" value="off">
<input type="hidden" name="buttonSize" value=35>
</form>
<script>
//Favoや食べたの初期状態
var tabeta = [];
var favo = [];
//Favoや食べたの押した回数
var tabetaCount = [];
var favoCount = [];
<%
for (int i = 0; i < recipeID.size(); i++) {
%>
	tabeta.push(<%= tabetaList.get(i) %>);
	favo.push(<%= favoList.get(i) %>);
	tabetaCount.push(0);
	favoCount.push(0);
<%
}
%>
//ボタンを押した時刻の初期状態
var pushTime = new Date();
var now;
//食べたボタンを押したとき
function tabetabutton(i, j) {
	if (now != null) {
		now = new Date();
		if (now.getTime() - pushTime.getTime() < 2000) return; //2回目以降は2秒間隔をあけないと押せない
	} else {
		now = new Date(); //初回は必ず押せる
	}
	if (tabetaCount[i] >= 10) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで11回以上押されたら反応しない
	}
	else {
		if (tabeta[i]) {
			document.getElementById('recipeIDform1').value = j;
			tabetaDeleteForm.submit(); //Favoに登録されていたら削除
		} else {
			document.getElementById('recipeIDform2').value = j;
			tabetaInsertForm.submit(); //Favoに登録されていなかったら登録
		}
		tabeta[i] = !tabeta[i];
		pushTime = new Date();
		tabetaCount[i]++;
	}
}
//Favoボタンを押したとき
function favobutton(i, j) {
	if (now != null) {
		now = new Date();
		if (now.getTime() - pushTime.getTime() < 2000) return; //2回目以降は2秒間隔をあけないと押せない
	} else {
		now = new Date(); //初回は必ず押せる
	}
	if (favoCount[i] >= 10) {
		alert('ボタン押下回数の上限に達しました');
		return; //同一ページで11回以上押されたら反応しない
	}
	else {
		if (favo[i]) {
			document.getElementById('recipeIDform3').value = j;
			favoDeleteForm.submit(); //Favoに登録されていたら削除
		} else {
			document.getElementById('recipeIDform4').value = j;
			favoInsertForm.submit(); //Favoに登録されていなかったら登録
		}
		favo[i] = !favo[i];
		pushTime = new Date();
		favoCount[i]++;
	}
}
</script>
<%
	for (int i = 0; i < recipeID.size(); i++) {
%>
         <div class="recipebox">
           <div class="recipeimage">
            <img alt="<%= recipeTitle.get(i) %>" width="200" height="200" src="images/RyouriPIC/<%= imageName.get(i) %>" class="recipetori">
           </div>
           <div class ="recipe-text">
            <h2 class="recipetitle">
              <a class="recipetitlelink" href="RecipeServlet?recipeID=<%= recipeID.get(i) %>&input=<%= URLEncoder.encode(input, "UTF-8") %>&searchMode=<%= searchMode %>"><%= recipeTitle.get(i) %></a>
              <br>
              <%
              String face = "";
              if (tabetaList.get(i)) face = "aceat.png";
              else face = "bceat.png";
              %>
              <a href="javascript:tabetabutton(<%= i %>, <%= recipeID.get(i) %>)" class="face<%= recipeID.get(i) %>"><img src="images/<%= face %>"
                 alt="今日食べたボタン" width="35" height="35"></a>
              <%
              String heart = "";
              if (favoList.get(i)) heart = "pink_heart.png";
              else heart = "clear_heart.png";
              %>
              <a href="javascript:favobutton(<%= i %>, <%= recipeID.get(i) %>)" class="heart<%= recipeID.get(i) %>"><img src="images/<%= heart %>"
                 alt="お気に入りボタン" width="35" height="35"></a>

           </h2>
           <div class ="material">
<%
out.println(recipeIntro.get(i) + "<br>");
out.println("材料：");
if (searchMode.equals("syokuzai")) {
	//食材名検索の場合
	for (int j = 0; j < list.get(i).size(); j++) {
		if (j == 0) {
			if (list.get(i).get(j)[1].equals("0.00")) out.println(list.get(i).get(j)[0] + " 適量<br>");
			else {
				//右端のゼロと小数点を削る処理
				while (list.get(i).get(j)[1].length() > 0 && list.get(i).get(j)[1].substring(list.get(i).get(j)[1].length() - 1).equals("0")) {
					list.get(i).get(j)[1] = list.get(i).get(j)[1].substring(0, list.get(i).get(j)[1].length() - 1);
				}
				if (list.get(i).get(j)[1].substring(list.get(i).get(j)[1].length() - 1).equals(".")) {
					list.get(i).get(j)[1] = list.get(i).get(j)[1].substring(0, list.get(i).get(j)[1].length() - 1);
				}
				out.println(list.get(i).get(j)[0] + " " + list.get(i).get(j)[1] + " " + list.get(i).get(j)[2] + "<br>");
			}
		} else if (j >= 15) {
			out.print("…等");
			break;
		} else if (j == 1) out.print(list.get(i).get(j)[0]);
		else out.print("、" + list.get(i).get(j)[0]);
	}
} else {
	//料理名検索の場合
	for (int j = 0; j < list.get(i).size(); j++) {
		if (j >= 15) {
			out.print("…等");
			break;
		}
		else if (j == 0) out.print(list.get(i).get(j)[0]);
		else out.print("、" + list.get(i).get(j)[0]);
	}
}
%>
           </div>
           <div class="clear"></div>
           </div>
        </div>


<%
	}
}
%>

<%
//検索結果が11件以上ならページ送りするためのリンクを用意する
if (recipeNum > DATA_PER_PAGE) {
	int pageTotal = (recipeNum - 1) / DATA_PER_PAGE + 1;
	String inputDataStr = URLEncoder.encode(inputData[0], "UTF-8");
	for (int i = 1; i < inputData.length; i++)	inputDataStr += URLEncoder.encode("　" + inputData[i], "UTF-8");
%>
      <div class="page-number">
        <ul class="page-list">
      <!-- ｢<<｣の表示 -->
      <li><%
      if (pageNum == 1) out.print("<<");
      else out.print("<a href=\"SearchResultServlet?searchMode=" + searchMode +"&input=" + inputDataStr + "&pageNum=1\" class=\"page-link\"><<</a>");
      %></li>
      <!-- ｢< 前へ｣の表示 -->
      <li><%
      if (pageNum == 1) out.print("< 前へ");
      else out.print("<a href=\"SearchResultServlet?searchMode=" + searchMode +"&input=" + inputDataStr + "&pageNum=" + (pageNum - 1) + "\" class=\"page-link\">< 前へ</a>");
      %></li>
      <!-- ｢... 4 5 6 7 8 9 10 11 12 ...｣の表示 pageNumの前後4件まで -->
      <%
      for (int i = Math.max(1, pageNum - 5); i <= Math.min(pageNum + 5, pageTotal); i++) {
    	  if (i == pageNum - 5 || i == pageNum + 5) out.println("<li>...</li>");
    	  else if (i == pageNum) out.println("<li>" + i + "</li>");
    	  else out.println("<li><a href=\"SearchResultServlet?searchMode=" + searchMode +"&input=" + inputDataStr + "&pageNum=" + i + "\" class=\"page-link\">" + i + "</a></li>");
      }
      %>
      <!-- ｢次へ >｣の表示 -->
      <li><%
      if (pageNum == pageTotal) out.print("次へ >");
      else out.print("<a href=\"SearchResultServlet?searchMode=" + searchMode +"&input=" + inputDataStr + "&pageNum=" + (pageNum + 1) + "\" class=\"page-link\">次へ ></a>");
      %></li>
      <!-- ｢>>｣の表示 -->
      <li><%
      if (pageNum == pageTotal) out.print(">>");
      else out.print("<a href=\"SearchResultServlet?searchMode=" + searchMode +"&input=" + inputDataStr + "&pageNum=" + pageTotal +"\" class=\"page-link\">>></a>");
      %></li>
    </ul>
    </div>
<%
}
%>
  </article>
  </main>

</div>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div>

  <!-- wrap終了 -->

</body>
</html>
