<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="outerDiv">
<div id="commercialDiv">
	<div id="leftDiv">
		<h3>Peamised tegevused, mida rakendus võimaldab</h3>
		<ul>
			<li>Tekita dokumente</li>
			<li>Lae PDF alla või vaata brauseris eelvaadet</li>
			<li>Halda kliente</li>
			<li>Halda tooteid</li>
			<li>Halda kasutajaid</li>
			<li>Statistika</li>
			<li>Vali enda kasutajale sobivad seaded</li>
		</ul>
	</div>
	<div id="rightDiv">
		<input type="button" value="Taotle oma firmale proovikasutus" class="defaultButton" id="showRegister"/>
		<div id="registerResponseDiv">
			<div id="successMessageDiv">${registerResponseGood}</div>
			<div id="errorMessageDiv">${registerResponseBad}</div>
		</div>
		<div id="formDiv">
		<form action="${pageContext.request.contextPath}/login/register" method="post">
			<input type="text" name="registerName" class="searchInputField defaultInputField" value="Teie ees- ja perekonna nimi" maxlength="45"/>
			<input type="text" name="registerEmail" class="searchInputField defaultInputField" value="Teie email" maxlength="60"/>
			<%
	        ReCaptcha c = ReCaptchaFactory.newReCaptcha(
	            "6LdSHe4SAAAAAI9lWK2XhBg5wHT7xvauG4satahj",
	            "6LdSHe4SAAAAACmbZW-MH_QTaIpQu2G_OcqFowd4",
	            false);
	        out.print(c.createRecaptchaHtml(null, null));
	        %>
	        <br>
			<input type="submit" value="Saada taotlus" class="defaultButton"/>
		</form>
		</div>
	</div>
</div>
<div id="loginFormDiv">
	<!-- <form name="loginForm" action="/j_spring_security_check" method="POST">-->
		<input type="text" value="kasutajanimi" class="defaultInputField searchInputField" id="inputUsername" maxlength="45"/>
		<!--<input name="j_username" type="text" value="kasutajanimi" class="defaultInputField searchInputField" id="inputUsername" maxlength="45"/>-->
		<br>
		<input type="password" value="parool" class="defaultInputField searchInputField" id="inputPassword" maxlength="45"/>
		<!--<input name="j_password" type="password" value="parool" class="defaultInputField searchInputField" id="inputPassword" maxlength="45"/>-->
		<br>
		<input type="button" value="Logi sisse" class="defaultButton" id="loginButton"/>
		<!--<input type="submit" value="Logi sisse" class="defaultButton"/>-->
	<!--</form>-->
	<div id="errorMessageDiv">
		${errorMessage}
	</div>

</div>
</div>
<div id="imagesDiv">
	<div id="image1" style="display:block;"></div>
	<div id="image2"></div>
	<div id="image3"></div>
	<div id="image4"></div>
	<div id="image5"></div>
	<div id="image6"></div>
	<div id="image7"></div>
</div>