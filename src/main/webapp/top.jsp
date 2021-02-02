<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<!--head開始-->
<head>
  <meta charset="utf-8" >
  <meta http-equiv="X-UA-Compatible" content="IE=11">
  <title>レシピコンシェル | TOP</title>
  <link rel="stylesheet" href="CSS/TOPStyle.css">
  <link rel="icon" href="images/fav32.ico">
</head>
<!--head終了-->

<!--body開始-->
<body>
<div id="wrapper">

<jsp:include page="header.jsp" /><!-- ヘッダー部分 -->

<div id="wrap" class="clearfix">
  <div class="content">


  <!--aboxが開始-->
  <div class="abox"><!--三要素構成-"abox"div２-->
    <div class ="abox-image"></div>
    <h1>あなたの消費したい食材を教えてください</h1>
    <h2>特に消費したい食材からレシピを検索できます。<br>
      入力した食材の消費が多い順にレシピを表示します！</h2>
    <!--ラジオボタン開始-->
    <form name="mkensaku" action="SearchResultServlet" method="get" onSubmit="return func1();">
      <div class="radio-font"><!--ラジオボタンのdiv４-->
        <ul class="radiolist"><!--ラジオボタンリストのul-->
          <li>
            <input type="radio" id="f-option" name="searchMode" value="syokuzai" checked>
            <label for="f-option">
              食材名検索
            </label>
            <div class ="check"></div><!--ラジオボタンチェックする円のdiv５-->
          </li>
          <li>
            <input type="radio" id="s-option" name="searchMode" value="ryouri">
            <label for="s-option">料理名検索</label>
            <div class="check"></div><!--ラジオボタンチェックする円のdiv６-->
          </li>
        </ul>
      </div>

      <!--ラジオボタン終了-->

  <!--検索窓開始-->
            <!-- \u3041-\u3096は平仮名、\u3000は全角スペース、\u30fcは長音 これらの文字の組み合わせのみ許可する 正規表現で書いたのがpatternの所 -->
            <input id="mado" type="text" name="input" size=50
             placeholder=" 例）じゃがいも　かれー等　【ひらがな入力のみ】" title="ひらがなで入力して下さい" required>
            <input id="mbutton" type="submit" value="レシピ検索">
                <script>
                 //二度押し防止機能
                 function func1() {
                     if (!sendflag) {
	                     var patternKana = /^[ぁ-んー　]*$/;
	                     if (!patternKana.test(document.getElementById('mado').value)) {
		                     alert('ひらがなと全角スペースのみで入力してください');
		                     return false;
	                     }
	                     document.getElementById('mbutton').disabled = true;
	                     sendflag = true;
	                     document.searchform.submit();
	                 }
                 }
                </script>
          </form>
  <!--検索窓終了-->

  </div>
  <!--aboxが終了-->


  <!--aboxとbboxの間-->
  <div class ="about">
    <h1 class ="how">HOW　TO</h1>
  </div>

  <!--bboxが開始-->
  <div class = "bbox">
    <div class="bboximage">

    <div class="bleftbox">
      <p class="bsetumeil"><span class="underline">消費したい食材で検索</span><br></p>
      <div class="bleftboximg"><img src="images/left.png" alt="サイト説明"></div>
    </div>


    <div class="sand">
     <div class="kunozi"></div>
    </div>

    <div class="bcenterbox">
     <p class="bsetumei"><span class="underline">特に消費したい食材を<br>指定可能</span><br></p>
     <img src="images/center.png" alt="サイト説明">
    </div>

    <div class="sand">
     <div class="kunozi"></div>
    </div>

    <div class="brightbox">
      <p class="bsetumei"><span class="underline">選んだ食材の消費量が<br>多いレシピを優先表示</span><br></p>
      <img src="images/right.png" alt="サイト説明">
    </div>

  </div>
 </div>
  <!--bboxが終了-->

  <!--cboxが開始-->
  <div class ="cbox"><!--三要素構成-"cbox"div１０-->
   <div class ="cboxtext">
    <h3>レシピ検索サイト<br>　　レシピコンシェル</h3>
    <p class="cboxhowto">
      レシピコンシェルでは
      <span class="underline">"特に消費したい食材"</span>を<br>
      指定して検索ができます<br>
      ご自身で<span class="underline"> マイレシピを登録 </span>したり<br>
      最近食べていない<span class="underline"> レシピのを提案 </span>
      する機能もございます
    </p>
   </div>
   <div class ="cboximage"><br>
    <img src="images/date18test.PNG" alt="コンシェルジュ">
   </div>
  </div>
    <!--cboxが終了-->

</div>
</div>

<jsp:include page="footer.jsp" /><!-- フッター部分 -->

</div><!-- wrapperの/divはここ　Fix -->
</body>
</html>
