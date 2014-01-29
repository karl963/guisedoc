<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${user.profile.isAllowed('ViewClients') == true}">

<label><input type="checkbox" id="includeRealBuyers" checked/>Ostnud kliendid</label>
&ensp;&ensp;&ensp;
<label><input type="checkBox" id="includeNonBuyers" />Hinnapakkumise kliendid</label>
&ensp;&ensp;&ensp;
<label><input type="checkBox" id="includeSellers" />Müüjad</label>

<table id="clientsTable">
	<thead>
	<tr>
		<td class="clientNameTd"><input type="search" class="clientSearchInputField searchInputField defaultInputField" id="clientSearchNameInput" value="Nimi"/></td>
		<td class="clientContactPersonTd"><input type="search" id="clientSearchContactPersonInput" class="clientSearchInputField searchInputField defaultInputField" value="Kontaktisik"/></td>
		<td class="clientTotalBoughtForTd">
			<input type="button" class="defaultButton" id="clientSearchButton" value="otsi"/>
			<input type="button" class="defaultButton" id="clientAddButton" value="lisa"/>
		</td>
		<td class="clientDeleteTd"></td>
	</tr>
	
	<tr class="tableHeaderRow">
		<th class="clientNameTd tableBorderRight">Klient</th>
		<th class="clientContactPersonTd tableBorderRight">Kontaktisik</th>
		<th class="clientTotalBoughtForTd tableBorderRight numberColumn">Ostusumma</th>
		<th class="clientDeleteTd">
		<c:if test="${user.profile.isAllowed('DeleteClients') == true}">
			<input type="button" value="Kustuta" id="deleteClientsButton" class="defaultButton"/>
		</c:if>
		</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${clients}" var="client">
	<tr class="clientRow" id="clientRow${client.ID}">
		<td class="clientNameTd tableBorderRight clientRowClickable">
			${client.name}
		</td>
		
		<td class="clientContactPersonTd tableBorderRight clientRowClickable">
			${client.contactPerson}
		</td>
		
		<td class="alignRightTd clientTotalBoughtForTd tableBorderRight clientRowClickable">
			${client.totalBoughtFor}
		</td>
		
		<td class="clientDeleteTd">
			<label class="clientDeleteLabel" ><input type="checkBox" class="clientDeleteCheckbox"/></label>
		</td>
		
	</tr>
	</c:forEach>
	</tbody>
</table>

</c:if>