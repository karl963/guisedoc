<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h2>Kasutaja Andmed</h2>

<div id="userIDDiv" class="hidden">${user.ID}</div>
<div class="userDataDiv">
	<span class="userDataDescription">Nimi</span>
	<span class="userDataInput"><input id="userNameInput" type="text" value="${user.name}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Skype</span>
	<span class="userDataInput"><input id="userSkypeInput" type="text" value="${user.skype}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Email</span>
	<span class="userDataInput"><input id="userEmailInput" type="text" value="${user.email}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Telefon</span>
	<span class="userDataInput"><input id="userPhoneInput" type="text" value="${user.phone}" maxlength="45"/></span>
</div>

<div id="passwordsInputDiv">
	Uus parool<input id="userNewPassword1Input" type="password" maxlength="45"/>
	Parooli kinnitus:<input id="userNewPassword2Input" type="password" maxlength="45"/>
</div>

<input type="button" id="saveUserData" class="defaultButton" value="Salvesta enda andmed"/>

<c:if test="${user.profile.isAllowed('ViewPrefixes') == true}">
<h2>Dokumentide eesliited</h2>

<div class="userDataDiv">
	<span class="prefixDataDescription">Arve</span>
	<span class="userDataInput"><input id="prefixInvoice" type="text" value="${prefixes[0]}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="prefixDataDescription">Ettemaksu arve</span>
	<span class="userDataInput"><input id="prefixAdvanceInvoice" type="text" value="${prefixes[1]}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="prefixDataDescription">Hinnapakkumine</span>
	<span class="userDataInput"><input id="prefixQuotation" type="text" value="${prefixes[2]}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="prefixDataDescription">Tallimuse kinnitus</span>
	<span class="userDataInput"><input id="prefixOrderConfirmation" type="text" value="${prefixes[3]}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="prefixDataDescription">Tellimus (order)</span>
	<span class="userDataInput"><input id="prefixOrder" type="text" value="${prefixes[4]}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="prefixDataDescription">Saateleht</span>
	<span class="userDataInput"><input id="prefixDeliveryNote" type="text" value="${prefixes[5]}" maxlength="45"/></span>
</div>

<c:if test="${user.profile.isAllowed('ChangePrefixes') == true}">
	<input type="button" id="savePrefixes" class="defaultButton" value="Salvesta eesliited"/>
</c:if>

</c:if>

<c:if test="${user.profile.isAllowed('ViewFirm') == true}">
<h2>Firma andmed:</h2>

<div class="userDataDiv">
	<span class="userDataDescription">Nimi</span>
	<span class="userDataInput"><input id="firmNameInput" type="text" value="${firm.name}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Aadress</span>
	<span class="userDataInput"><input id="firmAddressInput" type="text" value="${firm.address}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">RegNR</span>
	<span class="userDataInput"><input id="firmRegNRInput" type="text" value="${firm.regNR}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">KMKR</span>
	<span class="userDataInput"><input id="firmKmkrInput" type="text" value="${firm.kmkr}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Telefon</span>
	<span class="userDataInput"><input id="firmPhoneInput" type="text" value="${firm.phone}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Fax</span>
	<span class="userDataInput"><input id="firmFaxInput" type="text" value="${firm.fax}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">E-mail</span>
	<span class="userDataInput"><input id="firmEmailInput" type="text" value="${firm.email}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Pank</span>
	<span class="userDataInput"><input id="firmBankInput" type="text" value="${firm.bank}" maxlength="45"/></span>
</div>
<div class="userDataDiv">
	<span class="userDataDescription">Pangakonto nr</span>
	<span class="userDataInput"><input id="firmBankAccountNRInput" type="text" value="${firm.bankAccountNR}" maxlength="45"/></span>
</div>

<br>

<div class="userDataDiv">
	<span class="userDataDescription">Logo link</span>
	<span class="userDataInput"><input id="firmLogoURLInput" type="text" value="${firm.logoURL}"/></span>
</div>
<div id="userDataDiv">
	<span class="userDataDescription">Logo optimaalsed suurused:</span>
	<br>
	<span class="userDataInput">pikkus:<input id="firmLogoWidthInput" type="number" value="${firm.logoWidth}"/></span>
	<span class="userDataInput">kõrgus:<input id="firmLogoHeightInput" type="number" value="${firm.logoHeight}"/></span>
</div>

<br>

<div id="firmLogoDiv"><img id="firmLogo" src="${firm.logoURL}" /></div>

<c:if test="${user.profile.isAllowed('ChangeFirm') == true}">
	<input type="button" id="saveFirmData" class="defaultButton" value="Salvesta firma andmed"/>
</c:if>
</c:if>
