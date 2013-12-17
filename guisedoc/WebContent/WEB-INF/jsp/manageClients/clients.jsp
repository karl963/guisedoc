<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="contentBody">

<label><input type="checkbox" id="includeRealBuyers" checked/>Ostnud kliendid</label>
&ensp;&ensp;&ensp;
<label><input type="checkBox" id="includeNonBuyers" />Hinnapakkumise kliendid</label>
&ensp;&ensp;&ensp;
<label><input type="checkBox" id="includeSellers" />Müüjad</label>

<table id="clientsTable">
	
	<tr>
		<td class="clientNameTd"><input type="search" class="clientSearchInputField searchInputField defaultInputField" id="clientSearchNameInput" value="Otsitava nimi"/></td>
		<td class="clientContactPersonTd"><input type="search" id="clientSearchContactPersonInput" class="clientSearchInputField searchInputField defaultInputField" value="Otsitava nimi"/></td>
		<td class="clientTotalBoughtForTd"><input type="button" class="defaultButton" id="clientSearchButton" value="otsi"/></td>
	</tr>
	
	<tr class="tableHeaderRow">
		<th class="clientNameTd tableBorderRight">Klient</th>
		<th class="clientContactPersonTd tableBorderRight">Kontaktisik</th>
		<th class="clientTotalBoughtForTd">Ostusumma</th>
	</tr>
	
	<c:forEach items="${clients}" var="client">
	<tr class="clientRow" id="clientRow${client.ID}">
		<td class="clientNameTd tableBorderRight">
			${client.name}
		</td>
		
		<td class="clientContactPersonTd tableBorderRight">
			${client.contactPerson}
		</td>
		
		<td class="clientTotalBoughtForTd">
			${client.totalBoughtFor}
		</td>
	</tr>
	</c:forEach>
	
</table>

</div>