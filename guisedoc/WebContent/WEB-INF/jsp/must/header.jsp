<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>

<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/other.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/menu.css" type="text/css" />

<script>var contextPath = "${pageContext.request.contextPath}";
		var activeLink = "${pageRequest}"</script>
<script src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.10.2.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/other.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/menu.js" type="text/javascript" ></script>

<!-- Check what page is opening, no need to load all the scripts and
	styles in	
 -->
<c:choose>
	<c:when test="${pageRequest == 'documents'}">
		<script src="${pageContext.request.contextPath}/scripts/jquery/jquery-ui-1.10.3.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/jquery/jquery.cookie.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/jquery/jquery.tablesorter.min.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/documents.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/clientSelect.js" type="text/javascript" ></script>
		<script src="${pageContext.request.contextPath}/scripts/importDocument.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/documents.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/clientSelect.css" type="text/css" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/importDocument.css" type="text/css" />
	</c:when>
	<c:when test="${pageRequest == 'manage-products'}">
		<script src="${pageContext.request.contextPath}/scripts/products.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css" />
	</c:when>
	<c:when test="${pageRequest == 'clients'}">
		<script src="${pageContext.request.contextPath}/scripts/client.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/client.css" type="text/css" />
	</c:when>
	<c:when test="${pageRequest == 'statistics'}">
		<script src="${pageContext.request.contextPath}/scripts/statistics.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/statistics.css" type="text/css" />
	</c:when>
	<c:when test="${pageRequest == 'manage-user-data'}">
		<script src="${pageContext.request.contextPath}/scripts/user.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/user.css" type="text/css" />
	</c:when>
	<c:when test="${pageRequest == 'settings'}">
		<script src="${pageContext.request.contextPath}/scripts/settings.js" type="text/javascript" ></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/settings.css" type="text/css" />
	</c:when>
</c:choose>

<title><tiles:insertAttribute name="title" ignore="true" /></title>

</head>
<body>

<img id="fullPageLoadingDiv" class="hidden" src="${pageContext.request.contextPath}/images/big-loader.gif" />