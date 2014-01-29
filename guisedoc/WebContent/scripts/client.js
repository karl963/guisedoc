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
	$(document).on("click","#clientAddButton",function(){
		
		var name = $("#clientSearchNameInput").val();
		var contactPerson = $("#clientSearchContactPersonInput").val();
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"clientSearchNameInput"),
				new Array(contactPerson,"clientSearchContactPersonInput")
				))){
			return;
		}
		
		var clientJSON = {};
		clientJSON.name = name;
		clientJSON.contactPerson = contactPerson;
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/add",
	        data : {clientJSON: JSON.stringify(clientJSON)},
	        success : function(response) {

	        	responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response=="success"){
	        		addClientRowToTable(responseJSON.ID,clientJSON.name,clientJSON.contactPerson,0.0);
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
	 * delete clients
	 */
	$(document).on("click","#deleteClientsButton",function(){
		
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
	        		deleteSelectedObjectsFromTable();
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * delete selected objects from table after post success
	 */
	function deleteSelectedObjectsFromTable(){
		$(".clientDeleteCheckbox").each(function(){
			if($(this).is(":checked")){
				$(this).closest(".clientRow").remove();
			}
		});
	}
	
	/*
	 * deleteable objects json
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
	$(document).on("input","#searchClientDetailNumber",function(){
		
		showLoadingDiv();
		
		var number = $(this).val();
		
		if(number == $(this).data("default_val")){
			return;
		}
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(number,"searchClientDetailNumber")
				))){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/searchDocument",
	        data : {documentNumber:number},
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
		if(contactPerson == $("#clientSearchNameInput").data("default_val")){
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
		
		var ID = $(".leftSideDetailDiv").children(".clientIDDiv").val();
		var name = $("#clientDetailNameInput").val();
		var phone = $("#clientDetailPhoneInput").val();
		var contactPerson = $("#clientDetailContactPersonInput").val();
		var email = $("#clientDetailEmailInput").val();
		var address = $("#clientDetailAddressInput").val();
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"clientDetailNameInput"),
				new Array(phone,"clientDetailPhoneInput"),
				new Array(contactPerson,"clientDetailContactPersonInput"),
				new Array(email,"clientDetailEmailInput"),
				new Array(address,"clientDetailAddressInput")
				))){
			return;
		}
		
		
		var clientJSON = {};
		
		clientJSON.ID = ID;
		clientJSON.name = name;
		clientJSON.phone = phone;
		clientJSON.contactPerson = contactPerson;
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
						"<span class='clientDetailNameDiv'>Kontaktisik</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailContactPersonInput' value='"+clientJSON.contactPerson+"' /> </span>" +
					"</div>" +
		
					"<div>" +
						"<span class='clientDetailNameDiv'>Telefon</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailPhoneInput' value='"+clientJSON.phone+"'/> </span>" +
					"</div>" +
					
					"<div>" +
						"<span class='clientDetailNameDiv'>Email</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailEmailInput' value='"+clientJSON.email+"' /> </span>" +
					"</div>" +
					
					"<div>" +
						"<span class='clientDetailNameDiv'>Aadress</span>" +
						"<span class='clientDetailInputDiv'> <input type='text' maxlength='45' id='clientDetailAddressInput' value='"+clientJSON.address+"'/> </span>" +
					"</div>" +
					
			"</div>"+
			
			"<div id='rightsideDetailDiv'>"+
				"<div><input type='search' value='Otsi Numbriga' id='searchClientDetailNumber' class='clientSearchInputField defaultInputField searchInputField'/></div>"+
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
		
		updateClientDetailDocuments(clientJSON.documents);
		
		return row;
	}
	
	function addAllClientsToTable(clientsJSON){
		
		for(var i = 0;i < clientsJSON.clients.length ; i++){
			
			var ID = clientsJSON.clients[i].ID;
			var name = clientsJSON.clients[i].name;
			var contactPerson = clientsJSON.clients[i].contactPerson;
			var totalBoughtFor = clientsJSON.clients[i].totalBoughtFor;
			
			addClientRowToTable(ID,name,contactPerson,totalBoughtFor);
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

});