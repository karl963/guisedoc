<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="documentsTabsDiv">

	<c:forEach items="${documents}" var="document">
		<c:choose>
			<c:when test="${document.verified}">
				<span class="documentsTab selectableDocumentTab"><span>${document.fullNumber}</span><span class="tabDocumentID hidden">${document.ID}</span><span></span></span>
			</c:when>
			<c:otherwise>
				<span class="documentsTab selectableDocumentTab"><span>${document.fullNumber}</span><span class="tabDocumentID hidden">${document.ID}</span><span> (kinnitamata)</span></span>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<span class="documentsTab" id="newDocumentTab">
		Uus dokument: 
		<select id="newDocumentSelect">
			<option value="default" selected="selected" disabled="disabled">-- valige tüüp --</option>
			<c:if test="${user.profile.isAllowed('CreateDocuments') == true}">
			<option value="quotation" >Hinnapakkumine</option>
			<option value="invoice" >Arve</option>
			<option value="advance_invoice" >Ettemaksu Arve</option>
			<option value="order_confirmation" >Tellimuse kinnitus</option>
			<option value="delivery_note" >Saateleht</option>
			<option value="order" >Order</option>
			<option value="-" disabled >----------------</option>
			</c:if>
			<option value="existing">Ava vana dokument</option>
		</select>
	</span>
</div>
