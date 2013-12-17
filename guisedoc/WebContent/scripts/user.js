$(document).ready(function() {
	
	$(document).on("click","#saveUserData", function() {

		// password fields do not match and are not default
		if( ($("#userNewPassword1Input").val() != $("#userNewPassword1Input").data("default_val") || 
				$("#userNewPassword2Input").val() != $("#userNewPassword2Input").data("default_val")) &&
				($("#userNewPassword1Input").val() != $("#userNewPassword2Input").val())){
			
			$("#userNewPassword1Input, #userNewPassword2Input").addClass("invalidInputCharacter");

			showErrorNotification("Parool ja tema kinnitus ei kattu");
			return;
		}
		
		showLoadingDiv();
		
		var firmName = $("#firmNameInput").val();
		var firmAddress = $("#firmAddressInput").val();
		var firmRegNR = $("#firmRegNRInput").val();
		var firmKmkr = $("#firmKmkrInput").val();
		var firmPhone = $("#firmPhoneInput").val();
		var firmFax = $("#firmFaxInput").val();
		var firmEmail = $("#firmEmailInput").val();
		var firmBank = $("#firmBankInput").val();
		var firmBankAccountNr = $("#firmBankAccountNRInput").val();

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
				new Array(userSkype,"userSkypeInput")
				))){
			return;
		}
		
		/*
		 * Make a JSON out of data
		 */
		var dataJSON = '[';

		dataJSON+=
		'{'+
			'"name":"'+			firmName		+'",'+
			'"address":"'+		firmAddress		+'",'+
			'"regNR":"'+		firmRegNR		+'",'+
			'"kmkr":"'+			firmKmkr		+'",'+
			'"phone":"'+		firmPhone		+'",'+
			'"fax":"'+			firmFax			+'",'+
			'"email":"'+		firmEmail		+'",'+
			'"bank":"'+			firmBank		+'",'+
			'"bankAccountNR":"'+firmBankAccountNr+'"'+
		'},';
		
		dataJSON+=
		'{'+
			'"ID":'+		userID		+','+
			'"name":"'+		userName	+'",'+
			'"phone":"'+	userPhone	+'",'+
			'"email":"'+	userEmail	+'",'+
			'"skype":"'+	userSkype	+'",'+
			'"password":"'+	userPassword+'"'+
		'}';

		dataJSON += "]";

		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/manage-user-data",
	        datatype: 'json',
	        data : {dataJSON: dataJSON},
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
	
});