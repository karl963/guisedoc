<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${user.profile.isAllowed('ViewStatistics') == true}">
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
		<label><input type="radio" name="statisticsClientGroup" value="realBuyer" checked/>Ostnud kliendid</label>
		<br>
		<label><input type="radio" name="statisticsClientGroup" value="nonBuyer" />Hinnapakkumise kliendid</label>
		<br>
		<label><input type="radio" name="statisticsClientGroup" value="seller" />Müüjad</label>
		<br>
		<input type="text" id="statisticsClientInput" disabled value="Kliendi nimi" class="searchInputField defaultInputField" />
		<select id="clientsSelectBox">
		</select>
		<span id="clientIDDiv" class="hidden">0</span>
	</span>
	<span class="separateOptionDiv">
		<label><input type="radio" name="statisticsTypeGroup" value="sumAll" checked/>Kõigi tehingute tooted kokku</label>
		<br>
		<label><input type="radio" name="statisticsTypeGroup" value="separate" />Tehingute tooted eraldi</label>
	</span>
	<div class="separateOptionDiv">
		<input id="searchForStatistics" type="button" value="Otsi" class="defaultButton" />
		<c:if test="${user.profile.isAllowed('DownloadStatistics') == true}">
			<input id="downloadPDFOfStatistics" type="button" value="Lae PDF alla" class="defaultButton" />
			<input id="viewPDFOfStatistics" type="button" value="PDF eelvaade" class="defaultButton" />
		</c:if>
	</div>
</div>

<div id="hideOrShowStatisticsOptionDiv">Peida otsingu täpsustused</div>
</c:if>
<br>
<div id="statisticsTableDiv"></div>