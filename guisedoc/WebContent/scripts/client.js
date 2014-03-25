$(document).ready(function() {
	
	makeSortable("clientsTable");
	
	/*
	 * search history input
	 */
	if(localStorage.getItem("clientName") != null){
		$("#clientSearchNameInput").focus(); // for default search class remove
		$("#clientSearchNameInput").val(localStorage.getItem("clientName"));
		$("#clientSearchNameInput").blur(); // for unFocus
	}
	
	if(localStorage.getItem("clientContectPerson") != null){
		$("#clientSearchContactPersonInput").focus(); // for default search class remove
		$("#clientSearchContactPersonInput").val(localStorage.getItem("clientContectPerson"));
		$("#clientSearchContactPersonInput").blur(); // for unFocus
	}
	if(localStorage.getItem("nonBuyers") != null){
		$("#includeNonBuyers").prop("checked",true);
	}
	else{
		$("#includeNonBuyers").prop("checked",false);
	}
	if(localStorage.getItem("sellers") != null){
		$("#includeSellers").prop("checked",true);
	}
	else{
		$("#includeSellers").prop("checked",false);
	}
	if(localStorage.getItem("realBuyers") != null){
		$("#includeRealBuyers").prop("checked",true);
	}
	else{
		$("#includeRealBuyers").prop("checked",false);
	}

	/*
	 * add client
	 */
	// on enter press
	$(document).on("keypress",".clientSearchInputField",function(e){
		if(e.which == 13){
			$("#clientAddButton").click();
		}
	});
	$(document).on("click","#clientAddButton",function(){
		
		var clientJSON = makeAddClientsJSON();
		if(clientJSON == null){
			hideLoadingDiv();
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/add",
	        data : {clientJSON: JSON.stringify(clientJSON)},
	        success : function(response) {

	        	responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response=="success"){
	        		addClientRowToTable(responseJSON.ID,clientJSON.name,
	        				clientJSON.selectedContactPerson.name,0.0);
	        		
	        		if(cleanClientSearchAfterAdd){
	        			$(".clientSearchInputField").val(null);
	        			$(".clientSearchInputField").blur();
	        		}
	        		
	        		if(focusClientNameAfterAdd){
	        			$("#clientSearchNameInput").focus();
	        		}
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * add contactperson for client
	 */
	$(document).on("click","#contactPersonAddButton",function(){
		
		showLoadingDiv();
		
		var name = $("#addContactPersonInput").val();
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"addContactPersonInput")
				))){
			return;
		}
		if(name == $("#addContactPersonInput").data("default_val")){
			name = "";
		}
		
		var clientID = $(this).closest("#leftSideDetailDiv").children(".clientIDDiv").html();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/contacts/add",
	        data : {name:name, clientID:clientID},
	        success : function(response) {

	        	responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response=="success"){
	        		addContactPersonToTable(responseJSON.ID,name);
	        		showSuccessNotification(responseJSON.message);
	        	}
	        	else{
	        		showErrorNotification(responseJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	var addContactPersonToTable = function(id,name){
		var table = document.getElementById("contactPersonsTable").getElementsByTagName("tbody")[0];
		var row = table.insertRow(0);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);

		row.className = "contactRow";
		row.setAttribute("id","contactRow"+id);
		
		cell1.innerHTML = name;
		cell1.className = "tableBorderRight";
		
		cell2.innerHTML = "<label class='contactPersonLabel' ><input type='checkBox' class='contactPersonCheckbox'/></label>";
		cell2.className = "contactDeleteTd";
	};
	
	/*
	 * makes add client json
	 */
	var makeAddClientsJSON = function(){
		var name = $("#clientSearchNameInput").val();
		var contactPersonName = $("#clientSearchContactPersonInput").val();

		if(name == $("#clientSearchNameInput").data("default_val")){
			name = "";
		}
		if(contactPersonName == $("#clientSearchContactPersonInput").data("default_val")){
			contactPersonName = "";
		}

		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"clientSearchNameInput"),
				new Array(contactPersonName,"clientSearchContactPersonInput")
				))){
			return;
		}
		
		var clientJSON = {};
		clientJSON.name = name;
		clientJSON.selectedContactPerson = {};
		clientJSON.selectedContactPerson.name = contactPersonName;
		
		return clientJSON;
	};
	
	/*
	 * delete clients
	 */
	$(document).on("click","#deleteClientsButton",function(){
		showConfirmationDialog("Kustuta valitud kliendid ?"
				,deleteClients);
	});
	var deleteClients = function(){
		
		showLoadingDiv();
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			closeDetailedDataDiv("clientsTable", function(){
				$(".detailedTr").remove(); // remove the old row
			});
		}
		
		var forDeleteJSON = makeDeleteClientsJSON();
		if(forDeleteJSON == null){
			hideLoadingDiv();
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/delete",
	        data : {forDeleteJSON: JSON.stringify(forDeleteJSON)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		deleteSelectedClientsFromTable();
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	/*
	 * delete contact persons
	 */
	$(document).on("click","#deleteContactPersonsButton",function(){
		showConfirmationDialog("Kustuta valitud kontaktisikud ?"
				,deleteContacts);
	});
	var deleteContacts = function(){
		
		showLoadingDiv();

		var forDeleteJSON = makeDeleteContactsJSON();
		if(forDeleteJSON == null){
			hideLoadingDiv();
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/contacts/delete",
	        data : {forDeleteJSON: JSON.stringify(forDeleteJSON)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		deleteSelectedContactsFromTable();
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	/*
	 * delete selected objects from table after post success
	 */
	function deleteSelectedClientsFromTable(){
		$(".clientDeleteCheckbox").each(function(){
			if($(this).is(":checked")){
				$(this).closest(".clientRow").remove();
			}
		});
	}
	
	function deleteSelectedContactsFromTable(){
		$(".contactPersonCheckbox").each(function(){
			if($(this).is(":checked")){
				$(this).closest(".contactRow").remove();
			}
		});
	}
	
	/*
	 * deleteable clients json
	 */
	var makeDeleteClientsJSON = function(){
		
		var objects = [];
		
		$(".clientDeleteCheckbox").each(function(){

			if($(this).is(":checked")){

				var object = {};
				object.ID = $(this).closest(".clientRow").attr("id").replace("clientRow","");
				
				objects.push(object);
			}
			
		});

		if(objects.length == 0){ // there were no objects selected, no need to post
			return;
		}

		return objects;
	};
	
	/*
	 * deleteable contact persons json
	 */
	var makeDeleteContactsJSON = function(){
		
		var objects = [];
		
		$(".contactPersonCheckbox").each(function(){

			if($(this).is(":checked")){

				var object = {};
				object.ID = $(this).closest(".contactRow").attr("id").replace("contactRow","");
				
				objects.push(object);
			}
			
		});

		if(objects.length == 0){ // there were no objects selected, no need to post
			return;
		}

		return objects;
	};
	
	/*
	 * Get the detailed data about the client from server, animate the row insert
	 */
	function makeDetailClientPost(clickedRow){

		var id = clickedRow.attr("id").replace("clientRow","");
		var rowIndex = $(clickedRow).index();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/detailed",
	        data : {id: id},
	        success : function(jsonString) {

	        	var clientJSON = jQuery.parseJSON(jsonString);

	        	if(clientJSON.response=="success"){
	        		
	        		addClientDetailedDataRow(rowIndex,clientJSON);
	        		openDetailedDataDiv("clientsTable");
	        		
	        		showSuccessNotification(clientJSON.message);
	        	}
	        	else{
	        		showErrorNotification(clientJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	}
	
	/*
	 * Searching for clients documents with a number input
	 */
	$(document).on("click","#searchDocumentsButton",function(){
		showLoadingDiv();
		
		var id = $("#leftSideDetailDiv").children(".clientIDDiv").html();
		var number = $("#searchClientDetailNumber").val();
		
		if(number == $("#searchClientDetailNumber").data("default_val")){
			number = "";
		}
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(number,"searchClientDetailNumber")
				))){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/searchDocument",
	        data : {documentNumber:number, clientID:id},
	        success : function(responseJSON) {
	        	
	        	var documentsJSON = jQuery.parseJSON(responseJSON);
	        	
	        	if(documentsJSON.response=="success"){
	        		showSuccessNotification(documentsJSON.message);
	        		
	        		updateClientDetailDocuments(documentsJSON.documents);
	        	}
	        	else{
	        		showErrorNotification(documentsJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * updates the documents in clients detail row
	 */
	var updateClientDetailDocuments = function(documentsJSON){
		$(".documentNumberTable").remove();
		
		var table = document.createElement("table");
		table.createTHead();
		table.createTFoot();
		
		table.setAttribute("id","numbersTable");
		var headerRow = table.createTHead().insertRow(0);
		headerRow.className += "tableHeaderRow";
		
		var headerCell1 = document.createElement("th");
		var headerCell2 = document.createElement("th");
		var headerCell3 = document.createElement("th");
		
		headerCell1.innerHTML = "Number";
		headerCell1.className += "tableBorderRight";
		
		headerCell2.innerHTML = "Summa";
		headerCell2.className += "tableBorderRight numberColumn";
		
		headerCell3.innerHTML = "Kuupäev";
		headerCell3.className += "dateColumn";
		
		headerRow.appendChild(headerCell1);
		headerRow.appendChild(headerCell2);
		headerRow.appendChild(headerCell3);
		
		var tbody = table.createTBody();
		
		for(var i = 0; i<documentsJSON.length ;i++){
			var doc = documentsJSON[i];
			
			var row = tbody.insertRow(0);
			var cell1 = row.insertCell(0);
			var cell2 = row.insertCell(1);
			var cell3 = row.insertCell(2);
			
			cell1.innerHTML = doc.fullNumber;
			cell1.className += "tableBorderRight";
			
			cell2.innerHTML = parseFloat(doc.totalSum).toFixed(2);
			cell2.className += "numbersSumCell tableBorderRight";
			
			cell3.innerHTML = doc.formatedDate;
			cell3.className += "numbersDateCell";
		}
		
		$("#clientDetailNumbersDiv").html(table);
		makeSortable("numbersTable");
	};
	
	/*
	 * click on table row, get detailed client data
	 */
	$(document).on("click", ".clientRowClickable", function(){
		
		showLoadingDiv();
		var clickedRow = $(this).closest(".clientRow"); // the row we clicked on
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it

			closeDetailedDataDiv("clientsTable", function(){
			 	$(".detailedTr").remove(); // remove the old row

			 	makeDetailClientPost(clickedRow); // make a new detailed client data row
			});
		}
		else{
			makeDetailClientPost(clickedRow); // make the detailed client data row
		}
	});
	
	/*
	 * Save client data post
	 */
	$(document).on("click", ".clientDetailSaveButton", function(){
		
		showLoadingDiv();
		
		var clientID = $(this).closest(".clientDetailedDataDiv").children("#leftSideDetailDiv").children(".clientIDDiv").html();

		var clientJSONString = makeClientDetailJSON();
		if(clientJSONString == null){
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/save",
	        data : {clientJSONString: JSON.stringify(clientJSONString)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		
	        		updateClientRegularRowData(clientID);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
		
	});
	
	/*
	 * close the detailed div on button click
	 */
	$(document).on("click", ".closeDetailDiv",function(){
		closeDetailedDataDiv("clientsTable",function(){
			$(".detailedTr").remove();
		});
	});
	
	/*
	 * Updates the regular data row's data after saving the detail data
	 */
	function updateClientRegularRowData(ID){
		var row = $("#clientRow"+ID);
		row.children(".clientNameTd").html($("#clientDetailNameInput").val());
		row.children(".clientContactPersonTd").html($("#clientDetailContactPersonInput").val());
	}
	
	/*
	 * SEARCH POST
	 */
	$(document).on("click", "#clientSearchButton", function(){
		
		showLoadingDiv();
		
		addClientSearchHistory();
		
		$(".clientRow").remove();
		$(".detailedTr").remove();
		
		var searchJSON = makeClientSearchJSON();
		if(searchJSON == null){
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/search",
	        data : {searchJSON : JSON.stringify(searchJSON)},
	        success : function(clientsJSONString) {
	        	
	        	var clientsJSON = jQuery.parseJSON(clientsJSONString);
	        	
	        	if(clientsJSON.response=="success"){
	        		addAllClientsToTable(clientsJSON);
	        		showSuccessNotification(clientsJSON.message);
	        	}
	        	else{
	        		showErrorNotification(clientsJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
		
	});
	
	var makeClientSearchJSON = function(){
		
		var name = $("#clientSearchNameInput").val();
		var contactPerson = $("#clientSearchContactPersonInput").val();
		var sellers = $("#includeSellers").is(":checked");
		var nonBuyers = $("#includeNonBuyers").is(":checked");
		var realBuyers = $("#includeRealBuyers").is(":checked");
		
		if(name == $("#clientSearchNameInput").data("default_val")){
			name = "";
		}
		if(contactPerson == $("#clientSearchContactPersonInput").data("default_val")){
			contactPerson = "";
		}
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"clientSearchNameInput"),
				new Array(contactPerson,"clientSearchContactPersonInput")
				))){
			return;
		}
		
		searchJSON = {};
		searchJSON.name = name;
		searchJSON.contactPerson = contactPerson;
		searchJSON.sellers = sellers;
		searchJSON.realBuyers = realBuyers;
		searchJSON.nonBuyers = nonBuyers;

		return searchJSON;
	};
	
	var makeClientDetailJSON = function(){
		
		var ID = $("#leftSideDetailDiv").children(".clientIDDiv").html();
		var name = $("#clientDetailNameInput").val();
		var phone = $("#clientDetailPhoneInput").val();
		var email = $("#clientDetailEmailInput").val();
		var address = $("#clientDetailAddressInput").val();

		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"clientDetailNameInput"),
				new Array(phone,"clientDetailPhoneInput"),
				new Array(email,"clientDetailEmailInput"),
				new Array(address,"clientDetailAddressInput")
				))){
			return;
		}
		
		var clientJSON = {};
		
		clientJSON.ID = ID;
		clientJSON.name = name;
		clientJSON.phone = phone;
		clientJSON.email = email;
		clientJSON.address = address;
		
		return clientJSON;
	};
	
	var addClientSearchHistory = function(){
		
		if($("#clientSearchNameInput").val() != $("#clientSearchNameInput").data("default_val")){
			localStorage.setItem("clientName", $("#clientSearchNameInput").val());
		}
		else{
			localStorage.removeItem("clientName");
		}
		
		if($("#clientSearchContactPersonInput").val() != $("#clientSearchContactPersonInput").data("default_val")){
			localStorage.setItem("clientContectPerson", $("#clientSearchContactPersonInput").val());
		}
		else{
			localStorage.removeItem("clientContectPerson");
		}
		
		if($("#includeNonBuyers").is(":checked")){
			localStorage.setItem("nonBuyers", "");
		}
		else{
			localStorage.removeItem("nonBuyers");
		}
		
		if($("#includeRealBuyers").is(":checked")){
			localStorage.setItem("realBuyers", "");
		}
		else{
			localStorage.removeItem("realBuyers");
		}
		
		if($("#includeSellers").is(":checked")){
			localStorage.setItem("sellers", "");
		}
		else{
			localStorage.removeItem("sellers");
		}
		
	};

	function addClientDetailedDataRow(rowIndex,clientJSON){

		var table = document.getElementById("clientsTable").getElementsByTagName('tbody')[0];
		
		var row = table.insertRow(rowIndex+1);
		var cell=row.insertCell(0);
		
		var saveButton = "";
		if(allowedChangeClients == "true"){
			saveButton = "<input type='button' class='clientDetailSaveButton defaultButton' value='Salvesta'/>";
		}
		
		// make the contact persons html
		var contactPersonsHTML = "<div id='cntactSearchPanelDiv'>" +
			"<span id='contactPersonInputDiv'>"+
				"<input type='search' value='Kontaktisiku nimi' id='addContactPersonInput' class='documentSearchInputField defaultInputField searchInputField' maxlength='45'/>" +
			"</span>"+
			"<span id='contactAddButtonDiv'>"+
				"<input type='button' class='defaultButton' id='contactPersonAddButton' value='Lisa'/>" +
			"</span>"+
		"</div>"+
		"<div id='contactPersonsTableDiv'>";
		
		contactPersonsHTML += "<table id='contactPersonsTable'><thead>" +
				"<tr class='tableHeaderRow'>" +
					"<th>Nimi</th>" +
					"<th class='contactDeleteTd'>" +
						"<input type='button' value='Kustuta' class='defaultButton' id='deleteContactPersonsButton'/>" +
					"</th>" +
				"</tr>" +
				"</thead><tbody>";
		
		for(var i = 0; i < clientJSON.contactPersons.length; i++){
			contactPersonsHTML += "<tr id='contactRow"+clientJSON.contactPersons[i].ID+"' class='contactRow'>" +
					"<td class='tableBorderRight'>"+clientJSON.contactPersons[i].name+"</td>" +
					"<td class='contactDeleteTd'>" +
						"<label class='contactPersonLabel'><input type='checkbox' class='contactPersonCheckbox'/></label>" +
					"</td>" +
				"</tr>";
		}
		
		contactPersonsHTML += "</tbody></table></div>";
		
		// make the html
		cell.innerHTML = "<div class='clientDetailedDataDiv'>" +
		
			"<div id='leftSideDetailDiv'>"+
					"<div class='clientIDDiv hidden'>"+clientJSON.ID+"</div>"+
					"<div><span class='clientDetailNameDiv'>Tehinguid kokku:</span> <span class='clientDetailInputDiv'>"+clientJSON.totalDeals+"</span></div>" +
					"<div><span class='clientDetailNameDiv'>Viimase tehingu nr:</span> <span class='clientDetailInputDiv'>"+clientJSON.lastDealNR+"</span></div>" +
					"<div><span class='clientDetailNameDiv'>Viimane tehing:</span> <span class='clientDetailInputDiv'>"+clientJSON.lastDealDate+"</span></div>" +
				
					"<div>" +
						"<span class='clientDetailNameDiv'>Nimi</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailNameInput' value='"+clientJSON.name+"'/> </span>" +
					"</div>" +
		
					"<div>" +
						"<span class='clientDetailNameDiv'>Telefon</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailPhoneInput' value='"+clientJSON.phone+"'/> </span>" +
					"</div>" +
					
					"<div>" +
						"<span class='clientDetailNameDiv'>Email</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='60' id='clientDetailEmailInput' value='"+clientJSON.email+"' /> </span>" +
					"</div>" +
					
					"<div>" +
						"<span class='clientDetailNameDiv'>Aadress</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailAddressInput' value='"+clientJSON.address+"'/> </span>" +
					"</div><br>" +
					
					contactPersonsHTML+
					
			"</div>"+
			
			"<div id='rightsideDetailDiv'>"+
				"<div>" +
					"<span id='documentSearchInputDiv'>"+
						"<input type='search' value='Otsi Numbriga' id='searchClientDetailNumber' class='documentSearchInputField defaultInputField searchInputField' maxlength='45'/>" +
					"</span>"+
					"<span id='documentSearchButtonDiv'>"+
						"<input type='button' class='defaultButton' id='searchDocumentsButton' value='Otsi'/>" +
					"</span>"+
				"</div>"+
				"<div><small>Tehtud dokumendid (summa koos käibemaksuga)</small></div>"+
				
				"<div id='clientDetailNumbersDiv'>" +
				"</div>"+
			
			"</div>"+
			
			"<div>" +
				saveButton +
				"<input type='button' class='closeDetailDiv defaultButton' value='Sulge'/>" +
			"</div>" +
			
			"</div>";
		cell.colSpan = "4";
		cell.className = "clientDetailedDataRow";
		
		row.className = "detailedTr";
		
		$("#searchClientDetailNumber").data("default_val",$("#searchClientDetailNumber").val());
		$("#addContactPersonInput").data("default_val",$("#addContactPersonInput").val());
		
		updateClientDetailDocuments(clientJSON.documents);
	}
	
	function addAllClientsToTable(clientsJSON){
		for(var i = 0;i < clientsJSON.clients.length ; i++){
			
			var client = clientsJSON.clients[i];

			var ID = client.ID;
			var name = client.name;
			// make contactpersons string
			var contactPersons = "";
			for(var j = 0; j < client.contactPersons.length; j++){
				if(j>0){ // not the first person
					contactPersons += ", ";
				}
				contactPersons += client.contactPersons[j].name;
			}
			
			var totalBoughtFor = client.totalBoughtFor;
			
			addClientRowToTable(ID,name,contactPersons,totalBoughtFor);
		}
	}
	
	function addClientRowToTable(id,name,contactPerson,totalBoughtFor){
		
		var table = document.getElementById("clientsTable").getElementsByTagName("tbody")[0];
		var row = table.insertRow(0);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		
		row.className = "clientRow";
		row.setAttribute("id","clientRow"+id);
		
		cell1.innerHTML = name;
		cell1.className = "clientNameTd tableBorderRight clientRowClickable";
		
		cell2.innerHTML = contactPerson;
		cell2.className = "clientContactPersonTd tableBorderRight clientRowClickable";
		
		cell3.innerHTML = parseFloat(totalBoughtFor).toFixed(2);
		cell3.className = "alignRightTd clientTotalBoughtForTd tableBorderRight clientRowClickable";
		
		cell4.innerHTML = "<label class='clientDeleteLabel' ><input type='checkBox' class='clientDeleteCheckbox'/></label>";
		cell4.className = "clientDeleteTd";
	}

	// load clients if user has set it true
	if(loadAllClientsOnOpen){
		$("#clientSearchButton").trigger("click");
	}
});