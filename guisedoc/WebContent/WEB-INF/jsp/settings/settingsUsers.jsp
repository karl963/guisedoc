<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${user.profile.isAllowed('ViewUsers') == true}">
<table id="usersTable">
<thead>
<tr>
<c:if test="${user.profile.isAllowed('AddUsers') == true}">
	<td colspan=4>
		<input type="text" id="newUserName" class="searchInputField defaultInputField" value="nimi" maxlength="45"/>
		<input type="text" id="newUserPassword" class="searchInputField defaultInputField" value="parool" maxlength="45"/>
		<input type="button" class="defaultButton" value="Lisa kasutaja" id="addUserButton"/>
	</td>
</c:if>
</tr>
<tr class="tableHeaderRow">
	<th class="tableBorderRight">Kasutajanimi</th>
	<th class="tableBorderRight dateColumn lastLoginTd dateColumn">Viimane sisselogimine</th>
	<th class="tableBorderRight numberColumn totalDealsTd numberColumn">Tehinguid kokku</th>
	<th>Profiil</th>
</tr>
</thead>
<tbody>
<c:forEach items="${users}" var="user">
<tr class="userRow">
	<td class="tableBorderRight">
		<div>${user.userName}</div>
		<div class="userIDDiv hidden">${user.ID}</div>
	</td>
	<td class="tableBorderRight lastLoginTd alignCenterTd">${user.lastOnlineString}</td>
	<td class="tableBorderRight totalDealsTd alignCenterTd">${user.totalDeals}</td>
	<td class="userProfileTd alignCenterTd">${user.profile.name}</td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
<br>

<c:if test="${user.profile.isAllowed('ViewProfiles') == true}">
<table id="profilesTable">
<thead>
<tr>
<c:if test="${user.profile.isAllowed('AddProfiles') == true}">
	<td colspan=3>
		<input type="text" id="newProfileName" class="searchInputField defaultInputField" value="Nimetus" maxlength="45"/>
		<input type="button" class="defaultButton" value="Lisa profiil" id="addProfileButton"/>
	</td>
</c:if>
</tr>
<tr class="tableHeaderRow">
	<th class="tableBorderRight">Profiili nimi</th>
	<th class="tableBorderRight numberColumn">Lubatud tegevusi</th>
	<th class="numberColumn">Kasutajaid m‰‰ratud</th>
</tr>
</thead>
<tbody>
<c:forEach items="${profiles}" var="profile">
<tr class="profileRow">
	<td class="tableBorderRight">
		<div>${profile.name}</div>
		<div class="profileIDDiv hidden">${profile.ID}</div>
	</td>
	<td class="tableBorderRight alignCenterTd">${profile.allowedActionsCount}</td>
	<td class="alignCenterTd">${profile.usersCount}</td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>