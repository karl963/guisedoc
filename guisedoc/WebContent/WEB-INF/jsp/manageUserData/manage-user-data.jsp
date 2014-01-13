
<h2>Kasutaja Andmed:</h2>
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
	Uus parool:<input id="userNewPassword1Input" type="password" maxlength="45"/>
	Parooli kinnitus:<input id="userNewPassword2Input" type="password" maxlength="45"/>
</div>

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
	<span class="userDataInput">pikkus:<input id="firmLogoWidthInput" type="number" /></span>
	<span class="userDataInput">kõrgus:<input id="firmLogoHeightInput" type="number" /></span>
</div>

<br>

<div id="firmLogoDiv"><img id="firmLogo" src="${firm.logoURL}" /></div>

<input type="button" id="saveUserData" class="defaultButton" value="Salvesta"/>
