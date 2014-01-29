<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="documentsOptionsDiv">
	<span class="optionSubDiv">
		<input type="button" class="defaultButton" id="importDocumentButton" value="Impordi dokument"/>
		<input type="button" class="defaultButtonRed" id="closeCurrentDocumentButton" value="Sulge dokument"/>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<div id="insertDocumentID" class="hidden">0</div>
		<span class="allTypes">
			Tehingu number <input type="text" id="insertNumber"/></span>
		<span class="quotation_type order_confirmation_type invoice_type advance_invoice_type">
			Kehtivus <input type="number" id="insertValidDue" step="any" value="0"/></span>
		<span class="invoice_type">
			Ettemaks <input type="number" id="insertAdvance" step="any" value="0"/></span>
		<span class="quotation_type order_confirmation_type">
			Maksetingimus <input type="text" id="insertPaymentRequirement" step="any" value="0"/></span>
		<span class="quotation_type order_confirmation_type order_type">
			Tarneaeg <input type="text" id="insertShipmentTime"/></span>
		<span class="quotation_type order_confirmation_type order_type">
			Tarneaadress <input type="text" id="insertShipmentAddress"/></span>
		<span class="order_type">
			Tarnekoht <input type="text" id="insertShipmentPlace"/></span>
		<span class="invoice_type advance_invoice_type">
			Tellimuse number <input type="text" id="insertOrderNumber"/></span>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<input type="button" class="defaultButton" id="chooseClientButton" value="Valige klient"/>
		<div id="insertClientID" class="hidden">0</div>
		<span class="allTypes">
			Nimi <input type="text" id="insertClientName"/></span>
		<span class="invoice_type advance_invoice_type quotation_type order_confirmation_type delivery_note_type">
			Kontaktisik <input type="text" id="insertContactPerson" /></span>
		<span class="invoice_type advance_invoice_type delivery_note_type">
			Aadress <input type="text" id="insertClientAddress"/></span>
		<span class="invoice_type advance_invoice_type">
			Aadressi lisa <input type="text" id="insertClientAdditionalAddress"/></span>
		<span class="quotation_type order_confirmation_type delivery_note_type">
			Telefon <input type="text" id="insertClientPhone"/></span>
		<span class="quotation_type order_confirmation_type">
			Email <input type="text" id="insertEmail"/></span>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<span class="allTypes">
			Dokumendi kuupäev <input type="date" id="insertDocumentDate"/></span>
		<span class="order_confirmation_type order_type">
			<label><input type="checkbox" id="insertAddToStatistics"/> Lisa statistikasse</label></span>
		<span class="invoice_type advance_invoice_type quotation_type order_confirmation_type">
			<label><input type="checkbox" id="insertShowDiscount" /> Näita allahindlust</label></span>
		<span class="invoice_type">
			<label><input type="checkbox" id="insertPaydInCash" /> Tasutud sularahas</label></span>
		<span class="order_confirmation_type order_type delivery_note_type">
			<label><input type="checkbox" id="insertShowCE" /> Näita CE tähist</label></span>
		<span class="order_confirmation_type delivery_note_type quotation_type">
			CE täpsustus: <input type="text" id="insertCeSpecification"/></span>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<input type="button" value="EST" id="productsInEstonian" class="selectedLanguageButton"/>
		<input type="button" value="ENG" id="productsInEnglish" class="defaultButton"/>
		
		<c:if test="${user.profile.isAllowed('DownloadDocuments') == true}">
			<input type="button" class="defaultButton" id="downloadDocumentPdf" value="Lae PDF alla"/>
		</c:if>
		
		<input type="button" class="defaultButton" id="viewDocumentPdf" value="PDF eelvaade"/>
		<span class="allTypes">Summa kokku: <span id="totalSumDiv"></span></span>
	</span>
</div>

<div id="hideOrShowDocumentsOptionDiv">Peida dokumendi andmed</div>