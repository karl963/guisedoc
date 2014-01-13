
<div id="statisticsOptionsDiv">
		
	<span class="separateOptionDiv">
		Ajavahemik:
		<input id="statisticsDateFrom" type="date" />
		 - 
		<input id="statisticsDateTo" type="date" />
	</span>
	<span class="separateOptionDiv">
		<input type="search" id="statisticsCodeInput" class="searchInputField defaultInputField" value="Otsitava toote kood" />
	</span>
	<span class="separateOptionDiv">
		<label><input type="radio" name="statisticsClientGroup" value="actualBuyer" checked/>Ostnud kliendid</label>
		<br>
		<label><input type="radio" name="statisticsClientGroup" value="nonBuyer" />Hinnapakkumise kliendid</label>
		<br>
		<label><input type="radio" name="statisticsClientGroup" value="seller" />Müüjad</label>
		<br>
		<input type="search" id="statisticsClientInput" value="Kliendi nimi" class="searchInputField defaultInputField" />
		<select id="clientsSelectBox">
		</select>
		<div id="clientIDDiv" class="hidden">0</div>
	</span>
	<span class="separateOptionDiv">
		<label><input type="radio" name="statisticsTypeGroup" value="sumAll" checked/>Kõigi tehingute tooted kokku</label>
		<br>
		<label><input type="radio" name="statisticsTypeGroup" value="separate" />Tehingute tooted eraldi</label>
	</span>
	<div class="separateOptionDiv">
		<input id="searchForStatistics" type="button" value="Otsi" class="defaultButton" />
		<input id="createPDFOfStatistics" type="button" value="Koosta PDF" class="defaultButton" />
	</div>
</div>

<div id="hideOrShowStatisticsOptionDiv">Peida otsingu täpsustused</div>

<div id="statisticsTableDiv"></div>