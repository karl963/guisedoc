<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="insertDocumentID" class="hidden">0</div>
<div id="insertDocumentType" class="hidden">quotation</div>
<div id="insertClientID" class="hidden">0</div>

<div id="documentsOptionsDiv">
	<span class="optionSubDiv">
		<input type="button" class="defaultButton" id="importDocumentButton" value="Impordi dokument"/>
		<input type="button" class="defaultButtonRed" id="closeCurrentDocumentButton" value="Sulge dokument"/>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<span class="allTypes">
			Tehingu number <input type="text" id="insert_fullNumber" maxlength="45"/></span>
		<span class="quotation_type order_confirmation_type invoice_type advance_invoice_type">
			Kehtivus (päeva) <input type="number" id="insert_validDue" step="any" value="0" maxlength="20"/></span>
		<span class="invoice_type">
			Ettemaks <input type="number" id="insert_advance" step="any" value="0" maxlength="20"/></span>
		<span class="quotation_type order_confirmation_type">
			Maksetingimus <input type="text" id="insert_paymentRequirement" step="any" value="0" maxlength="45"/></span>
		<span class="quotation_type order_confirmation_type order_type">
			Tarneaeg <input type="text" id="insert_shipmentTime" maxlength="45"/></span>
		<span class="quotation_type order_confirmation_type order_type">
			Tarneaadress <input type="text" id="insert_shipmentAddress" maxlength="45"/></span>
		<span class="order_type">
			Tarnekoht <input type="text" id="insert_shipmentPlace" maxlength="45"/></span>
		<span class="invoice_type advance_invoice_type">
			Tellimuse number <input type="text" id="insert_orderNR" maxlength="45"/></span>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<input type="button" class="defaultButton" id="chooseClientButton" value="Valige klient"/>
		<input type="button" class="defaultButton" id="addNewClientButton" value="Looge uus klient"/>
		<span class="allTypes_client">
			Nimi <input type="text" id="insert_name" class="inputClient" maxlength="45"/></span>
		<span class="invoice_type_client advance_invoice_type_client quotation_type_client order_confirmation_type_client delivery_note_type_client">
			Kontaktisik <input type="text" id="insert_contactPerson" class="inputClient" maxlength="45"/></span>
		<span class="invoice_type_client advance_invoice_type_client delivery_note_type_client">
			Aadress <input type="text" id="insert_address" class="inputClient" maxlength="45"/></span>
		<span class="invoice_type_client advance_invoice_type_client">
			Aadressi lisa <input type="text" id="insert_additionalAddress" class="inputClient" maxlength="45"/></span>
		<span class="quotation_type_client order_confirmation_type_client delivery_note_type_client">
			Telefon <input type="text" id="insert_phone" class="inputClient" maxlength="45"/></span>
		<span class="quotation_type_client order_confirmation_type_client">
			Email <input type="text" id="insert_email" class="inputClient" maxlength="60"/></span>
		<span id="clientAlertDiv">Klient valimata !</span>
	</span>
	<div class="separator"></div>
	<span class="optionSubDiv">
		<span class="allTypes">
			Dokumendi kuupäev <input type="date" id="insert_documentDate"/></span>
		<span class="order_confirmation_type order_type quotation_type">
			<label><input type="checkbox" id="insert_addToStatistics"/> Lisa statistikasse</label></span>
		<span class="invoice_type advance_invoice_type quotation_type order_confirmation_type">
			<label><input type="checkbox" id="insert_showDiscount" /> Näita allahindlust</label></span>
		<span class="invoice_type">
			<label><input type="checkbox" id="insert_paydInCash" /> Tasutud sularahas</label></span>
		<span class="order_confirmation_type order_type delivery_note_type">
			<label><input type="checkbox" id="insert_showCE" /> Näita CE tähist</label></span>
		<span class="order_confirmation_type delivery_note_type quotation_type">
			- CE täpsustus: <input type="text" id="insert_CeSpecification" maxlength="45"/></span>
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