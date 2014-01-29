<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${user.profile.isAllowed('ViewUsers') == true}">
<table id="usersTable">
<thead>
<c:if test="${user.profile.isAllowed('AddUsers') == true}">
<tr>
	<td colspan=4>
		<input type="text" id="newUserName" class="searchInputField defaultInputField" value="nimi"/>
		<input type="text" id="newUserPassword" class="searchInputField defaultInputField" value="parool"/>
		<input type="button" class="defaultButton" value="Lisa kasutaja" id="addUserButton"/>
	</td>
</tr>
</c:if>
<tr class="tableHeaderRow">
	<th class="tableBorderRight">Kasutajanimi</th>
	<th class="tableBorderRight dateColumn">Viimane sisselogimine</th>
	<th class="tableBorderRight numberColumn">Tehinguid kokku</th>
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
	<td class="tableBorderRight">${user.lastOnlineString}</td>
	<td class="tableBorderRight">${user.totalDeals}</td>
	<td class="userProfileTd">${user.profile.name}</td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
<br>

<c:if test="${user.profile.isAllowed('ViewProfiles') == true}">
<table id="profilesTable">
<thead>
<c:if test="${user.profile.isAllowed('AddProfiles') == true}">
<tr>
	<td colspan=3>
		<input type="text" id="newProfileName" class="searchInputField defaultInputField" value="Nimetus"/>
		<input type="button" class="defaultButton" value="Lisa profiil" id="addProfileButton"/>
	</td>
</tr>
</c:if>
<tr class="tableHeaderRow">
	<th class="tableBorderRight">Profiili nimi</th>
	<th class="tableBorderRight">Lubatud tegevusi</th>
	<th>Kasutajaid m��ratud</th>
</tr>
</thead>
<tbody>
<c:forEach items="${profiles}" var="profile">
<tr class="profileRow">
	<td class="tableBorderRight">
		<div>${profile.name}</div>
		<div class="profileIDDiv hidden">${profile.ID}</div>
	</td>
	<td class="tableBorderRight">${profile.allowedActionsCount}</td>
	<td>${profile.usersCount}</td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>