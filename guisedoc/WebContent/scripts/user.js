$(document).ready(function() {
	
	/*
	 * saving the user data
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
		
		var dataJSON = makeUserDataJson();
		if(dataJSON == null){
			return;
		}
		
		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/user-firm/save/user",
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
	
	/*
	 * saving the firm data
	 */
	$(document).on("click","#saveFirmData", function() {
		
		showLoadingDiv();
		
		var dataJSON = makeFirmDataJson();
		if(dataJSON == null){
			return;
		}
		
		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/user-firm/save/firm",
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
	
	/*
	 * saving the prefixes
	 */
	$(document).on("click","#savePrefixes", function() {
		
		showLoadingDiv();
		
		var dataJSON = makePrefixesJson();
		if(dataJSON == null){
			return;
		}
		
		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/user-firm/save/prefixes",
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
	
	// user data json
	var makeUserDataJson = function(){
		
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
				new Array(userName,"userNameInput"),
				new Array(userPhone,"userPhoneInput"),
				new Array(userEmail,"userEmailInput"),
				new Array(userSkype,"userSkypeInput"),
				new Array(userPassword,"userNewPassword1Input")
				))){
			return;
		}
		
		/*
		 * Make a JSON out of data
		 */
		var userJSON = {};
		userJSON.ID = userID;
		userJSON.name = userName;
		userJSON.phone = userPhone;
		userJSON.email = userEmail;
		userJSON.skype = userSkype;
		userJSON.password = userPassword;

		return userJSON;
	};
	
	// firm data json
	var makeFirmDataJson = function(){
		
		var firmName = $("#firmNameInput").val();
		var firmAddress = $("#firmAddressInput").val();
		var firmRegNR = $("#firmRegNRInput").val();
		var firmKmkr = $("#firmKmkrInput").val();
		var firmPhone = $("#firmPhoneInput").val();
		var firmFax = $("#firmFaxInput").val();
		var firmEmail = $("#firmEmailInput").val();
		var firmBank = $("#firmBankInput").val();
		var firmIban = $("#firmIbanInput").val();
		var firmSwift = $("#firmSwiftInput").val();
		var firmLogoURL = $("#firmLogoURLInput").val();
		var logoWidth = $("#firmLogoWidthInput").val();
		var logoHeight = $("#firmLogoHeightInput").val();

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
				new Array(firmIban,"firmIbanInput"),
				new Array(firmSwift,"firmSwiftInput")
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
		var firmJSON = {};
		firmJSON.name = firmName;
		firmJSON.address = firmAddress;
		firmJSON.regNR = firmRegNR;
		firmJSON.kmkr = firmKmkr;
		firmJSON.phone = firmPhone;
		firmJSON.fax = firmFax;
		firmJSON.email = firmEmail;
		firmJSON.bank = firmBank;
		firmJSON.iban = firmIban;
		firmJSON.swift = firmSwift;
		firmJSON.logoURL = firmLogoURL;
		firmJSON.logoWidth = logoWidth;
		firmJSON.logoHeight = logoHeight;

		return firmJSON;
	};
	
	// prefixes json
	var makePrefixesJson = function(){
		
		var invoice = $("#prefixInvoice").val();
		var advanceInvoice = $("#prefixAdvanceInvoice").val();
		var quotation = $("#prefixQuotation").val();
		var orderConfirmation = $("#prefixOrderConfirmation").val();
		var order = $("#prefixOrder").val();
		var deliveryNote = $("#prefixDeliveryNote").val();

		/*
		 * Check all input for errors
		 */
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(invoice,"prefixInvoice"),
				new Array(advanceInvoice,"prefixAdvanceInvoice"),
				new Array(quotation,"prefixQuotation"),
				new Array(orderConfirmation,"prefixOrderConfirmation"),
				new Array(order,"prefixOrder"),
				new Array(deliveryNote,"prefixDeliveryNote")
				))){
			return;
		}

		/*
		 * Make a JSON out of data
		 */
		var prefixes = {};
		prefixes.invoice = invoice;
		prefixes.advanceInvoice = advanceInvoice;
		prefixes.quotation = quotation;
		prefixes.orderConfirmation = orderConfirmation;
		prefixes.order = order;
		prefixes.deliveryNote = deliveryNote;
		
		return prefixes;
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