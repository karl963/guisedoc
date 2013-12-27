<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
	<c:when test="${postResult==null}">
		<div id="notificationDiv" class="noNotification hidden"></div>
	</c:when>
	<c:when  test="${postResult=='success'}" >
		<div id="notificationDiv" class="positiveNotification">${message}</div>
	</c:when>
	<c:otherwise>
		<div id="notificationDiv" class="negativeNotification">${message}</div>
	</c:otherwise>
</c:choose>

</body>
</html>