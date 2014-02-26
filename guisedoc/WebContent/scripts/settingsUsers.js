$(document).ready(function(){
	
	makeSortable("usersTable");
	makeSortable("profilesTable");
	
	/*********************************************************
	 * user handling
	 ********************************************************/
	/*
	 * adding user
	 */
	$(document).on("click","#addUserButton",function(){
		
		var name = $("#newUserName").val();
		var pw = $("#newUserPassword").val();
		
		/*
		 * Check for invalid characters
		 */
		if(name == $("#newUserName").data("default_val")){
			giveInvalidInputMessage("newUserName","Palun sisestage uuele kasutajale kasutajanimi");
			return;
		}
		if(pw == $("#newUserPassword").data("default_val")){
			giveInvalidInputMessage("newUserPassword","Palun sisestage uuele kasutajale parool");
			return;
		}
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"newUserName"),
				new Array(pw,"newUserPassword")
				))){	
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/user/add",
	        data : {name:name,password:pw},
	        success : function(jsonString) {

				var userJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(userJSON.response=="success"){
	        		addUserRow(userJSON.user);
	        		showSuccessNotification(userJSON.message);
	        	}
	        	else{
	        		showErrorNotification(userJSON.message);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * adds the new user to the table
	 */
	var addUserRow = function(userJSON){
		var table = document.getElementById("usersTable");
		
		var row = table.insertRow(2);
		row.className += "userRow";
		
		var cell1 = row.insertCell(0);
		cell1.innerHTML = "<div>"+userJSON.userName+"</div>";
		cell1.innerHTML += "<div class='userIDDiv hidden'>"+userJSON.ID+"</div>";
		cell1.className += "tableBorderRight";
		
		var cell1 = row.insertCell(1);
		cell1.innerHTML = userJSON.lastOnlineString;
		cell1.className += "tableBorderRight lastLoginTd alignCenterTd";
		
		var cell1 = row.insertCell(2);
		cell1.innerHTML = userJSON.totalDeals;
		cell1.className += "tableBorderRight totalDealsTd alignCenterTd";
		
		var cell1 = row.insertCell(3);
		cell1.innerHTML = userJSON.profile.name;
		cell1.className += "userProfileTd alignCenterTd alignCenterTd";
	};
	
	/*
	 * detail data
	 */
	$(document).on("click",".userRow",function(){
		var id = $(this).children("td").children(".userIDDiv").html();
		var rowIndex = $(this).index()+1; // the row we clicked on
		
		if($(".detailedUserTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("usersTable", function(){
				 
			 	if($(".detailedUserTr").index() < rowIndex){ // clicked after the opened row
			 		rowIndex--; // we deleted the current row, which means we have 1 less row
			 	}
			 	
			 	$(".detailedUserTr").remove(); // remove the old row
			 	
			 	showDetailUserDiv(rowIndex,id); // make a new detailed user data row
			},"detailedUserTr");
		}
		else{
			showDetailUserDiv(rowIndex,id); // make the detailed user data row
		}
	});
	
	/*
	 * gets the data and passes the info
	 */
	var showDetailUserDiv = function(rowIndex, id){
		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/user/detail",
	        data : {ID: id},
	        success : function(jsonString) {

				var userJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(userJSON.response=="success"){
	        		addUserDetailDiv(rowIndex,userJSON);
	        		openDetailedDataDiv("usersTable",undefined,"detailedUserTr");
	        		showSuccessNotification(userJSON.message);
	        	}
	        	else{
	        		showErrorNotification(userJSON.message);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	/*
	 * add detailed data to the table
	 */
	var addUserDetailDiv = function(rowIndex,userJSON){
		var table = document.getElementById("usersTable");
		
		var row = table.insertRow(rowIndex+2);
		row.className = "detailedUserTr";
		var cell=row.insertCell(0);
		cell.className = "userDetailDiv";
		
		cell.innerHTML += "<div class='userIDDiv hidden'>"+userJSON.user.ID+"</div>";
		
		var selectHTML = "Kasutaja profiil: <select id='userProfileSelect'>";
		
		/*
		 * there's no name on profile which means no profile selected
		 * so we add the default value to the options
		 */
		if(userJSON.user.profile.name == ""){
			selectHTML += "<option value='default' selected disabled >-- valige profiil --</option>";
		}

		for(var i = 0; i < userJSON.profiles.length; i++){
			var profile = userJSON.profiles[i];
			var selected = "";
			
			if(userJSON.user.profile.name != "" && profile.name == userJSON.user.profile.name){
				selected = "selected";
			}
			selectHTML += "<option value='"+profile.ID+"' "+selected+" >"+profile.name+"</option>";
		}
		
		selectHTML += "</select>";
		cell.innerHTML += selectHTML;
		
		var deleteButton = "";
		if(allowedDeleteUsers == "true"){
			deleteButton = "<input type='button' id='deleteUserButton' value='Kustuta kasutaja' class='defaultButtonRed'/>";
		}
		
		cell.innerHTML += 
			"<div>" +
			deleteButton +
				"<input type='button' id='closeUserDetailDiv' value='Sulge' class='defaultButton'/>" +
			"</div>";
		
		cell.colSpan = "4";
	};
	
	/*
	 * deleting a user
	 */
	$(document).on("click","#deleteUserButton",function(){
		showConfirmationDialog("Kustuta valitud kasutaja ?"
				,deleteUser,$(this));
	});
	var deleteUser = function(button){
		showLoadingDiv();
		
		var id = button.closest(".userDetailDiv").children("div").children(".userIDDiv").html();
		var userRowIndex = $("#usersTable tr").index($(".detailedUserTr"))-1;
		var username = $("#usersTable tr").eq(userRowIndex)
						.children("td").eq(0).children("div").eq(0).html();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/user/delete",	
	        data : {ID:id, username:username},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response =="success"){
	        		
	        		$("#usersTable tr").eq(userRowIndex).remove(); // remove user row
	        		$(".detailedUserTr").remove(); // remove the detail div, as we dont have the user
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}

	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	/*
	 * changing the users profile
	 */
	$(document).on("change","#userProfileSelect",function(){
		
		var userID = $(this).closest(".detailedUserTr").children("td").children("div").children(".userIDDiv").html();
		var profileID = $(this).val();
		
		var userRowIndex = $("#usersTable tr").index($(".detailedUserTr"))-1;
		var profileName = $(this).find(":selected").text();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/user/save/profile",
	        data : {userID:userID, profileID:profileID},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response =="success"){

	        		$("#usersTable tr").eq(userRowIndex).children(".userProfileTd").html(profileName);
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}

	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * closing the detailed div
	 */
	$(document).on("click","#closeUserDetailDiv", function(){
		closeDetailedDataDiv("usersTable", function(){
		 	$(".detailedUserTr").remove();
		},"detailedUserTr");
	});
	
	/*********************************************************
	 * profile handling
	 ********************************************************/
	
	/*
	 * adding user
	 */
	$(document).on("click","#addProfileButton",function(){
		
		var name = $("#newProfileName").val();
		
		/*
		 * Check for invalid characters
		 */
		if(name == $("#newProfileName").data("default_val")){
			giveInvalidInputMessage("newProfileName","Palun sisestage uue profiili nimetus");
			return;
		}

		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"newProfileName")
				))){	
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/profile/add",
	        data : {name:name},
	        success : function(jsonString) {

				var profileJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(profileJSON.response=="success"){
	        		addProfileRow(profileJSON.profile);
	        		showSuccessNotification(profileJSON.message);
	        	}
	        	else{
	        		showErrorNotification(profileJSON.message);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * adds the new user to the table
	 */
	var addProfileRow = function(profileJSON){
		var table = document.getElementById("profilesTable");
		
		var row = table.insertRow(2);
		row.className += "profileRow";
		
		var cell1 = row.insertCell(0);
		cell1.innerHTML = "<div>"+profileJSON.name+"</div>";
		cell1.innerHTML += "<div class='profileIDDiv hidden'>"+profileJSON.ID+"</div>";
		cell1.className += "tableBorderRight";
		
		var cell2 = row.insertCell(1);
		cell2.innerHTML = profileJSON.allowedActionsCount;
		cell2.className += "tableBorderRight alignCenterTd";
		
		var cell3 = row.insertCell(2);
		cell3.innerHTML = profileJSON.usersCount;
		cell3.className += "alignCenterTd";

	};
	
	/*
	 * profile detail data
	 */
	$(document).on("click",".profileRow",function(){
		var id = $(this).children("td").children(".profileIDDiv").html();
		var rowIndex = $(this).index()+1; // the row we clicked on

		if($(".detailedProfileTr").length != 0){// if we have an opened row already, then close it

			closeDetailedDataDiv("profilesTable", function(){
				 
			 	if($(".detailedProfileTr").index() < rowIndex){ // clicked after the opened row
			 		rowIndex--; // we deleted the current row, which means we have 1 less row
			 	}
			 	
			 	$(".detailedProfileTr").remove(); // remove the old row
			 	
			 	showDetailProfileDiv(rowIndex,id); // make a new detailed profile data row
			},"detailedProfileTr");
		}
		else{
			showDetailProfileDiv(rowIndex,id); // make the detailed profile data row
		}
	});
	
	/*
	 * gets the data and passes the info
	 */
	var showDetailProfileDiv = function(rowIndex, id){

		$.ajax({
			type : "POST",
			url : contextPath+"/settingsUsers/profile/detail",
			data : {ID : id},
			success : function(jsonString) {
				
				var profileJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(profileJSON.response=="success"){
	        		addProfileDetailDiv(rowIndex,profileJSON.profile);
	        		openDetailedDataDiv("profilesTable",undefined,"detailedProfileTr");
	        		showSuccessNotification(profileJSON.message);
	        	}
	        	else{
	        		showErrorNotification(profileJSON.message);
	        	}
			},
			error : function(e){
	        	showErrorNotification("Viga serveriga ühendumisel");
			}
		});
	};
	
	/*
	 * add detailed data to the table
	 */
	var addProfileDetailDiv = function(rowIndex,profile){

		var table = document.getElementById("profilesTable");
		
		var row = table.insertRow(rowIndex+2);
		row.className = "detailedProfileTr";
		var cell=row.insertCell(0);
		cell.className = "profileDetailDiv";
		
		cell.innerHTML += "<div class='profileIDDiv hidden'>"+profile.ID+"</div>";
		cell.innerHTML += "Profiili nimi: <input id='inputProfileName' type='text' value='"+profile.name+"' maxlength='45'/>";
		
		var groups = [];
		
		for(var i = 0; i < profile.rules.length ; i++){
			var rule = profile.rules[i];
			
			var checked = "";
			if(rule.value == true){
				checked = "checked";
			}
			
			var ruleHTML= 
				"<span class='profileRuleDiv'>" +
					"<label>" +
						"<input type='checkbox' id='rule"+rule.key+"' "+checked+" class='profileRuleCheckbox'/>" + rule.description+
					"</label>"+
				"</span>";
			
			/*
			 * check for rule groups and add to the right one
			 */
			var foundGroup = false;
			for(var j = 0 ; j < groups.length ; j++){
				if(groups[j][0] == rule.group){
					groups[j][groups[j].length] = ruleHTML;
					foundGroup = true;
					break;
				}
			}
			if(!foundGroup){ // didn't find the group, then we add it
				groups[groups.length] = [];
				groups[groups.length-1].push(rule.group);
				groups[groups.length-1].push(ruleHTML);
			}
		}

		/*
		 * for each rule group, we make a separate div
		 */
		for(var i = 0; i < groups.length; i++){
			var porifleDiv = "<div class='profileRuleGroupDiv'>";

			for(var j = 1; j < groups[i].length; j++){ // starting from 1, cuz 0 is the group name
				porifleDiv += groups[i][j];
			}
			
			porifleDiv += "</div>";
			
			cell.innerHTML += porifleDiv;
		}

		var deleteButton = "";
		if(allowedDeleteProfiles == "true"){
			deleteButton = "<input type='button' id='deleteProfileButton' value='Kustuta profiil' class='defaultButtonRed'/>";
		}
		
		cell.innerHTML += 
			"<div>" +
				deleteButton +
				"<input type='button' id='closeProfileDetailDiv' value='Sulge' class='defaultButton'/>" +
			"</div>";

		cell.colSpan = "3";
	};
	
	/*
	 * on change of profile rule, we update it by clicking checkboxes
	 */
	$(document).on("click",".profileRuleCheckbox",function(){
		
		showLoadingDiv();
		
		var id = $(this).closest(".profileDetailDiv").children("div").children(".profileIDDiv").html();
		var value = $(this).is(":checked");
		var ruleKey = $(this).attr("id").replace("rule","");
		var index = $(this).closest("tr").index()-1;
		
		var count = -1;
		if($(this).is(":checked")){
			count = 1;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/profile/save/rule",
	        data : {ID:id, value: value, ruleKey:ruleKey},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	if(responseJSON.response =="success"){
	        		changeProfileTableAllowedCount(index,count);
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * changes the count of allowed actions in profiles table
	 */
	var changeProfileTableAllowedCount = function(index,count){
		var countRow = $("#profilesTable tbody").children(".profileRow").eq(index)
				.children("td").eq(1);
		
		countRow.html(countRow.html()+count);
	};
	
	/*
	 * update profile name on blur
	 */
	$(document).on("blur","#inputProfileName",function(){
		showLoadingDiv();
		
		var id = $(this).closest(".profileDetailDiv").children("div").children(".profileIDDiv").html();
		var name = $(this).val();
		var index = $(this).closest("tr").index()-1;

		/*
		 * Check for invalid characters
		 */
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"inputProfileName")
				))){	
			return;
		}
		else if(!name){ // if the name is empty
			giveInvalidInputMessage("inputProfileName","Profiili nimi ei tohi olla tühi");
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/profile/save/name",
	        data : {ID:id, name: name},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	if(responseJSON.response =="success"){
	        		changeProfileTableName(index, name);
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * changes the regular data row's profile name of the profiles table
	 */
	var changeProfileTableName = function(index,name){
		$("#profilesTable tbody").children(".profileRow").eq(index)
				.children("td").eq(0).children("div").eq(0).html(name);
	};
	
	/*
	 * delete product on click
	 */
	$(document).on("click","#deleteProfileButton",function(){
		showConfirmationDialog("Kustuta valitud profiil ?"
				,deleteProfile,$(this));
	});
	var deleteProfile = function(button){
		
		showLoadingDiv();
		
		var id = button.closest(".profileDetailDiv").children("div").children(".profileIDDiv").html();
		var profileRowIndex = $("#profilesTable tr").index($(".detailedProfileTr"))-1;

		$.ajax({
	        type : "POST",
	        url : contextPath+"/settingsUsers/profile/delete",
	        data : {ID:id},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response =="success"){
	        		
	        		$("#profilesTable tr").eq(profileRowIndex).remove(); // remove profile row
	        		$(".detailedProfileTr").remove(); // remove the detailed div, as we dont have the profile
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	/*
	 * closing the detailed div
	 */
	$(document).on("click","#closeProfileDetailDiv", function(){
		closeDetailedDataDiv("profilesTable", function(){
		 	$(".detailedProfileTr").remove();
		},"detailedProfileTr");
	});
});