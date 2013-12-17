<div class="contentBody">

<div id="statisticsOptionsDiv">
		
	<span class="separateOptionDiv">
		Ajavahemik:
		<input id="statisticsDateFrom" type="date" />
		 - 
		<input id="statisticsDateTo" type="date" />
	</span>
	<span class="separateOptionDiv">
		<input type="text" id="statisticsCodeInput" class="searchInputField defaultInputField" value="Otsitava toote kood" />
	</span>
	<!--&ensp;&ensp;&ensp;-->
	
	<span class="separateOptionDiv">
		<label><input type="radio" name="statisticsClientGroup" value="actualBuyer" checked/>Ostnud kliendid</label>
		<br>
		<label><input type="radio" name="statisticsClientGroup" value="nonBuyer" />Hinnapakkumise kliendid</label>
		<br>
		<label><input type="radio" name="statisticsClientGroup" value="seller" />Müüjad</label>
		<br>
		<input type="text" id="statisticsClientInput" value="Kliendi nimi" class="searchInputField defaultInputField" />
		<select>
		
		</select>
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
	
</div>
