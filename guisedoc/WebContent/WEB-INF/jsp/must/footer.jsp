<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="notificationDiv" class="noNotification hidden"></div>
<c:choose>
	<c:when test="${noteMessage != null}">
		<script>showErrorNotification('${noteMessage}');</script>
	</c:when>
</c:choose>

</body>
</html>