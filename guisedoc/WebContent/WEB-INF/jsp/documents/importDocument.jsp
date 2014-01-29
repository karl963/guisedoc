<div id="importDocumentDiv">

<div id="importDocumentBackground"></div>

<div id="importDocumentForeground">
	
	Dokumendi tyyp: 
	<select id="documentTypeSelect">
		<option value="default" disabled selected>-- valige tüüp --</option>
		<option value="quotation">Hinnapakkumine</option>
		<option value="order_confirmation">Tellimuse kinnitus</option>
		<option value="invoice">Arve</option>
		<option value="advance_invoice">Ettemaksu Arve</option>
		<option value="delivery_note">Saateleht</option>
		<option value="order">Order</option>
	</select>
	<span id="documentNumberInputDiv"><input type="search" class="defaultInputField searchInputField" id="documentSearchNumber" value="Number" /></span>
	<input type="button" id="closeImportDocument" class="defaultButton" value="Tagasi" />
	
	<div id="importDocumentTableDiv">
		<table id="importDocumentTable">
			<thead>
			<tr>
				<th class="tableHeaderRow">Number</th>
				<th class="tableHeaderRow">Kliendi nimi</th>
				<th class="tableHeaderRow">Summa</th>
				<th class="tableHeaderRow">Kuupäev</th>
			</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
	
</div>

</div>