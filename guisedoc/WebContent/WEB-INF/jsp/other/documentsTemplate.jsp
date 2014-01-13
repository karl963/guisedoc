<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<tiles:insertAttribute name="tabs" />

<c:choose>
<c:when test="${howManyDocuments > 0}" >
	<tiles:insertAttribute name="options" />
	<tiles:insertAttribute name="documentsBody" />
	<tiles:insertAttribute name="searchResults" />
	
	<tiles:insertAttribute name="clientSelect" />
	<tiles:insertAttribute name="importDocument"  />
</c:when>
</c:choose>
