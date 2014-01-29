<div id="clientSelectDiv">

<div id="clientSelectBackground"></div>

<div id="clientSelectForeground">
	
	Kliendi tyyp: 
	<select id="clientTypeSelect">
		<option value="default" disabled selected>-- valige tüüp --</option>
		<option value="nonBuyers">Hinnapakkumise kliendid</option>
		<option value="realBuyers">Ostnud kliendid</option>
		<option value="sellers">Müüjad</option>
	</select>
	<span id="clientNameInputDiv"><input type="search" class="defaultInputField searchInputField" id="clientSearchName" value="Nimi" /></span>
	<input type="button" id="closeClientSelect" class="defaultButton" value="Tagasi" />
	
	<div id="clientSelectTableDiv">
		<table id="clientSelectTable">
			<thead>
			<tr>
				<th class="tableHeaderRow">Nimi</th>
				<th class="tableHeaderRow">Kontaktisik</th>
				<th class="tableHeaderRow">Tehinguid</th>
				<th class="tableHeaderRow">Summa</th>
			</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
	
</div>

</div>