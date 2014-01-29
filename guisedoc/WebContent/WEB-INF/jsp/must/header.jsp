<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>

<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/other.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/menu.css" type="text/css" />

<script>var contextPath = "${pageContext.request.contextPath}";
		var activeLink = "${fn:replace(requestScope['javax.servlet.forward.servlet_path'],'/','')}";
		var permissionDeniedMessage = "See tegevus pole teile lubatud !";
</script>
<script src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.10.2.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/other.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/menu.js" type="text/javascript" ></script>

<!-- Check what page is opening, no need to load all the scripts and
	styles in -->
<c:choose>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/login')}">
		<script src="${pageContext.request.contextPath}/scripts/login.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/login.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/documents')}">
		<script>
			var allowedChangeDocuments = "${user.profile.isAllowed('DownloadDocuments')}";
		</script>
		<script src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.10.3.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/jquery/jquery.cookie.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/documents.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/clientSelect.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/importDocument.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/documents.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/clientSelect.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/importDocument.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/products')}">
		<script>
			var allowedChangeProducts = "${user.profile.isAllowed('ChangeProducts')}";	
		</script>
		<script src="${pageContext.request.contextPath}/scripts/products.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/clients')}">
		<script>
			var allowedAddClients = "${user.profile.isAllowed('AddClients')}";
			var allowedDeleteClients = "${user.profile.isAllowed('DeleteClients')}";
			var allowedChangeClients = "${user.profile.isAllowed('ChangeClients')}";	
		</script>
		<script src="${pageContext.request.contextPath}/scripts/client.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/client.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/statistics')}">
		<script>
			var allowedDeleteStatistics = "${user.profile.isAllowed('DeleteStatistics')}";
			var allowedChangeStatistics = "${user.profile.isAllowed('ChangeStatistics')}";	
		</script>
		<script src="${pageContext.request.contextPath}/scripts/jquery/jquery.cookie.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/statistics.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/statistics.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/user-firm')}">
		<script src="${pageContext.request.contextPath}/scripts/user.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/user.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/settingsUsers')}">
		<script>
			var allowedDeleteUsers = "${user.profile.isAllowed('DeleteUsers')}";
			var allowedDeleteProfiles = "${user.profile.isAllowed('DeleteProfiles')}";
		</script>
		<script src="${pageContext.request.contextPath}/scripts/settingsUsers.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/settingsUsers.css" type="text/css" />
	</c:when>
	<c:when test="${fn:contains(requestScope['javax.servlet.forward.servlet_path'],'/settingsMy')}">
		<script src="${pageContext.request.contextPath}/scripts/settingsMy.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/settingsMy.css" type="text/css" />
	</c:when>
</c:choose>

<title><tiles:insertAttribute name="title" ignore="true" /></title>

</head>
<body>

<img id="fullPageLoadingDiv" src="${pageContext.request.contextPath}/images/big-loader.gif" />