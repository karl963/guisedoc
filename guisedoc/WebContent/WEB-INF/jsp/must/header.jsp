<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 

<!DOCTYPE html>

<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/menu.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/contentBody.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/other.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/user.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/products.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/client.css" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/statistics.css" type="text/css" />

<script>var contextPath = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/scripts/jquery-1.10.2.min.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/other.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/menu.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/products.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/user.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/client.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/scripts/statistics.js" type="text/javascript" ></script>

<title><tiles:insertAttribute name="title" ignore="true" /></title> <!-- ignore="true" -->

</head>
<body>

<img id="fullPageLoadingDiv" class="hidden" src="${pageContext.request.contextPath}/images/big-loader.gif" />