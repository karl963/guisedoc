<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="documentsTabsDiv">
	<c:forEach items="${documents}" var="document">
		<span class="documentsTab selectableDocumentTab"><span>${document.fullNumber}</span><span class="tabDocumentID hidden">${document.ID}</span></span>
	</c:forEach>
	<span class="documentsTab" id="newDocumentTab">
		Uus dokument: 
		<select id="newDocumentSelect">
			<option value="default" selected="selected" disabled="disabled">-- valige tüüp --</option>
			<option value="Quotation" >Hinnapakkumine</option>
			<option value="Invoice" >Arve</option>
			<option value="Advance_invoice" >Ettemaksu Arve</option>
			<option value="Order_confirmation" >Tellimuse kinnitus</option>
			<option value="Delivery_note" >Saateleht</option>
			<option value="Order" >Order</option>
			<option value="-" disabled >----------------</option>
			<option value="existing">Ava vana dokument</option>
		</select>
	</span>
</div>