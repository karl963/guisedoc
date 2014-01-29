<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Dokumendid</h2>

<c:choose>
	<c:when test="${user.settings.getSettingValue('autoSetDocNumber')==true}">
		<label><input type="checkBox" class="settingsInput" id="autoSetDocNumberSetting" checked/>${user.settings.getSettingDescription('autoSetDocNumber')}</label>
	</c:when>
	<c:otherwise>
		<label><input type="checkBox" class="settingsInput" id="autoSetDocNumberSetting" />${user.settings.getSettingDescription('autoSetDocNumber')}</label>
	</c:otherwise>
</c:choose>

<h2>Tooted</h2>

<c:choose>
	<c:when test="${user.settings.getSettingValue('loadAllProductsOnOpen')==true}">
		<label><input type="checkBox" class="settingsInput" id="loadAllProductsOnOpenSetting" checked/>${user.settings.getSettingDescription('loadAllProductsOnOpen')}</label>
	</c:when>
	<c:otherwise>
		<label><input type="checkBox" class="settingsInput" id="loadAllProductsOnOpenSetting" />${user.settings.getSettingDescription('loadAllProductsOnOpen')}</label>
	</c:otherwise>
</c:choose>

<h2>Kliendid</h2>

<c:choose>
	<c:when test="${user.settings.getSettingValue('loadAllClientsOnOpen')==true}">
		<label><input type="checkBox" class="settingsInput" id="loadAllClientsOnOpenSetting" checked/>${user.settings.getSettingDescription('loadAllClientsOnOpen')}</label>
	</c:when>
	<c:otherwise>
		<label><input type="checkBox" class="settingsInput" id="loadAllClientsOnOpenSetting" />${user.settings.getSettingDescription('loadAllClientsOnOpen')}</label>
	</c:otherwise>
</c:choose>

<h2>Teised</h2>

<c:choose>
	<c:when test="${user.settings.getSettingValue('saveSearchResultType')==true}">
		<label><input type="checkBox" class="settingsInput" id="saveSearchResultTypeSetting" checked/>${user.settings.getSettingDescription('saveSearchResultType')}</label>
	</c:when>
	<c:otherwise>
		<label><input type="checkBox" class="settingsInput" id="saveSearchResultTypeSetting" />${user.settings.getSettingDescription('saveSearchResultType')}</label>
	</c:otherwise>
</c:choose>
<br>
<c:choose>
	<c:when test="${user.settings.getSettingValue('saveSearchResultInput')==true}">
		<label><input type="checkBox" class="settingsInput" id="saveSearchResultInputSetting" checked/>${user.settings.getSettingDescription('saveSearchResultInput')}</label>
	</c:when>
	<c:otherwise>
		<label><input type="checkBox" class="settingsInput" id="saveSearchResultInputSetting" />${user.settings.getSettingDescription('saveSearchResultInput')}</label>
	</c:otherwise>
</c:choose>