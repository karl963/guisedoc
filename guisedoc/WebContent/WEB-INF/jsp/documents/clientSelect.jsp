<div id="clientSelectDiv">

<div id="clientSelectBackground"></div>

<div id="clientSelectForeground">
	
	Kliendi tyyp: 
	<select id="clientTypeSelect">
		<option value="default" disabled selected>-- valige t��p --</option>
		<option value="nonBuyer">Hinnapakkumise kliendid</option>
		<option value="realBuyer">Ostnud kliendid</option>
		<option value="seller">M��jad</option>
		<option value="other">K�ik kliendid</option>
	</select>
	<span id="clientNameInputDiv"><input type="search" class="defaultInputField searchInputField" id="clientSearchName" value="Nimi" maxlength="45"/></span>
	<input type="button" id="closeClientSelect" class="defaultButton" value="Tagasi" />
	
	<div id="clientSelectTableDiv">
		<table id="clientSelectTable">
			<thead>
			<tr>
				<th class="tableHeaderRow">Nimi</th>
				<th class="tableHeaderRow">Kontaktisikud</th>
				<th class="tableHeaderRow">Tehinguid</th>
				<th class="tableHeaderRow">Summa</th>
			</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
	
</div>

</div>