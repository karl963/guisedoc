$(document).ready(function() {
	
	/*
	 * saving the user and firm data
	 */
	$(document).on("click","#saveUserData", function() {
		
		showLoadingDiv();

		// password fields do not match and are not default
		if( ($("#userNewPassword1Input").val() != $("#userNewPassword1Input").data("default_val") || 
				$("#userNewPassword2Input").val() != $("#userNewPassword2Input").data("default_val")) &&
				($("#userNewPassword1Input").val() != $("#userNewPassword2Input").val())){
			
			$("#userNewPassword1Input, #userNewPassword2Input").addClass("invalidInputCharacter");

			showErrorNotification("Parool ja tema kinnitus ei kattu");
			return;
		}
		
		var dataJSON = makeUserAndFirmDataJson();
		if(dataJSON == null){
			return;
		}
		
		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/manage-user-data/save",
	        datatype: 'json',
	        data : {dataJSON: JSON.stringify(dataJSON)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Error serveriga ühendumisel");
	        }
	    });
	});
	
	var makeUserAndFirmDataJson = function(){
		
		var firmName = $("#firmNameInput").val();
		var firmAddress = $("#firmAddressInput").val();
		var firmRegNR = $("#firmRegNRInput").val();
		var firmKmkr = $("#firmKmkrInput").val();
		var firmPhone = $("#firmPhoneInput").val();
		var firmFax = $("#firmFaxInput").val();
		var firmEmail = $("#firmEmailInput").val();
		var firmBank = $("#firmBankInput").val();
		var firmBankAccountNr = $("#firmBankAccountNRInput").val();
		var firmLogoURL = $("#firmLogoURLInput").val();
		var logoWidth = $("#firmLogoWidthInput").val();
		var logoHeight = $("#firmLogoHeightInput").val();
		
		var userID = $("#userIDDiv").html();
		var userName = $("#userNameInput").val();
		var userPhone = $("#userPhoneInput").val();
		var userEmail = $("#userEmailInput").val();
		var userSkype = $("#userSkypeInput").val();
		var userPassword = "";
		
		// are not default values and equal to each other
		if($("#userNewPassword1Input").val() != $("#userNewPassword1Input").data("default_val") && 
				$("#userNewPassword2Input").val() != $("#userNewPassword2Input").data("default_val") &&
				$("#userNewPassword1Input").val() == $("#userNewPassword2Input").val()){
			userPassword = $("#userNewPassword1Input").val();
		}
		
		/*
		 * Check all input for errors
		 */
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(firmName,"firmNameInput"),
				new Array(firmAddress,"firmAddressInput"),
				new Array(firmRegNR,"firmRegNRInput"),
				new Array(firmKmkr,"firmKmkrInput"),
				new Array(firmPhone,"firmPhoneInput"),
				new Array(firmFax,"firmFaxInput"),
				new Array(firmEmail,"firmEmailInput"),
				new Array(firmBank,"firmBankInput"),
				new Array(firmBankAccountNr,"firmBankAccountNRInput"),
				new Array(userName,"userNameInput"),
				new Array(userPhone,"userPhoneInput"),
				new Array(userEmail,"userEmailInput"),
				new Array(userSkype,"userSkypeInput"),
				new Array(firmLogoURL,"firmLogoURLInput")
				))){
			return;
		}
		
		if(checkForInvalidNumberCharacters(new Array(
				new Array(logoWidth,"firmLogoWidthInput"),
				new Array(logoHeight,"firmLogoHeightInput")
				))){
			return;
		}
		if(logoWidth == ""){
			logoWidth = $("#firmLogo").width();
		}
		if(logoHeight == ""){
			logoHeight = $("#firmLogo").height();
		}
		
		
		if(firmLogoURL.match(/\.(jpeg|jpg|gif|png)$/) == null && firmLogoURL != ""){
			giveInvalidInputMessage("firmLogoURLInput","Tegemist pole pildifailiga");
			return;
		}
		
		
		/*
		 * Make a JSON out of data
		 */
		var dataJSON = [];
		
		var firmJSON = {};
		firmJSON.name = firmName;
		firmJSON.address = firmAddress;
		firmJSON.regNR = firmRegNR;
		firmJSON.kmkr = firmKmkr;
		firmJSON.phone = firmPhone;
		firmJSON.fax = firmFax;
		firmJSON.email = firmEmail;
		firmJSON.bank = firmBank;
		firmJSON.bankAccountNR = firmBankAccountNr;
		firmJSON.logoURL = firmLogoURL;
		firmJSON.logoWidth = logoWidth;
		firmJSON.logoheight = logoHeight;
		
		var userJSON = {};
		userJSON.ID = userID;
		userJSON.name = userName;
		userJSON.phone = userPhone;
		userJSON.email = userEmail;
		userJSON.skype = userSkype;
		userJSON.password = userPassword;
		
		dataJSON.push(firmJSON);
		dataJSON.push(userJSON);
		
		return dataJSON;
	};
	
	/*
	 * changing the image
	 */
	$(document).on("change","#firmLogoURLInput", function(){
		$("#firmLogoDiv").html("<img id='firmLogo' src='"+$(this).val()+"' />");
		fitImageToSize();
	});
	
	/*
	 * changing the image size as user does it
	 */
	$(document).on("input","#firmLogoHeightInput, #firmLogoWidthInput",function(){
		fitImageToSize();
	});
	
	/*
	 * fitting the image to the sizes user has added
	 */
	var fitImageToSize = function(){
		if($("#firmLogoHeightInput").val() != null && $("#firmLogoHeightInput").val() != ""){
			$("#firmLogoDiv").css("height",$("#firmLogoHeightInput").val());
		}
		if($("#firmLogoWidthInput").val() != null && $("#firmLogoWidthInput").val() != ""){
			$("#firmLogoDiv").css("width",$("#firmLogoWidthInput").val());
		}
	};
	
	
	
	
	
	
	fitImageToSize();
});