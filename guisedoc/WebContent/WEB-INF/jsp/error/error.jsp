<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="userInformation">
	<h1>Kahjuks läks midagi katki.</h1>
	Soovituslikud lingid edasi minekuks:<br>
	<a href="${pageContext.request.contextPath}/documents">dokumendid</a><br>
	<a href="${pageContext.request.contextPath}/products">tooted</a><br>
	<a href="${pageContext.request.contextPath}/clients">kliendid</a><br>
</div>
<div id="errorInformation">
	<h1>Veateade:</h1>
	<div><h3>DATE</h3>${date}</div>
	<div><h3>CODE</h3>${errorCode}</div>
	<div><h3>URI</h3>${errorURI}</div>
	<div><h3>MESSAGE</h3>${errorMessage}</div>
	<div><h3>EXCEPTION</h3>${exception}</div>
	<div><h3>STACKTRACE</h3>${stackTrace}</div>
</div>