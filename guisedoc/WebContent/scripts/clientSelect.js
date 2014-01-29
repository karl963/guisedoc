$(document).ready(function(){
	
	makeSortable("clientSelectTable");
	/*
	 * open choose client view
	 */
	$(document).on("click", "#chooseClientButton", function(){
		if(allowedChangeDocuments == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		$("#clientSelectDiv").show();
	});
	
	/*
	 * close choose client view
	 */
	$(document).on("click", "#closeClientSelect, #clientSelectBackground", function(){
		$("#newDocumentSelect").val("default");
		$("#clientSelectDiv").hide();
	});
	
	/*
	 * typing the name into the search field
	 */
	$(document).on("input", "#clientSearchName", function(){
		if($("#clientTypeSelect").val() == "default" ||
				$("#clientTypeSelect").val()==null){ // the type isn't selected
			return;
		}

		$("#clientTypeSelect").change();
	});
	
	/*
	 * choosing the client type and making post according to it
	 */
	$(document).on("change", "#clientTypeSelect", function(){
		
		var type = $(this).val();
		
		if(type == "default"){
			return;
		}
		
		showLoadingDiv();
		
		var name = $("#clientSearchName").val();
		if(name == $("#clientSearchName").data("default_val")){
			name = "";
		}
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"clientSearchName")
				))){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/client/search",
	        data : {clientType: type, clientName:name},
	        success : function(response) {
	        	
	        	var clientsJSON = jQuery.parseJSON(response);
	        	
	        	if(clientsJSON.response=="success"){
	        		$(".clientTableRow").remove();
	        		addSearchResultClients(clientsJSON.clients);
	        		
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
	
	/*
	 * user selects a client by clicking on the row
	 */
	$(document).on("click", ".clientTableRow", function(){
		
		var id = $(this).children(".clientNameTd").children(".clientID").html();
		var type = $("#clientTypeSelect").val();
		var documentID = $("#insertDocumentID").html();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/client/select",
	        data : {selectedClientID:id, selectedClientType:type, currentDocumentID:documentID},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response=="success"){
	        		addSelectedClientToDocument(responseJSON.client);
	        		$("#clientSelectDiv").hide();
	        		
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
	 * adds all the search result clients to the table
	 */
	var addSearchResultClients = function(clients){
		for(var i = 0; i < clients.length; i++){
			addSearchResultClientToTable(clients[i]);
		}
	};
	
	/*
	 * adds a client to the table
	 */
	var addSearchResultClientToTable = function(client){
		var table = document.getElementById("clientSelectTable").getElementsByTagName("tbody")[0];
		
		var row = table.insertRow(0);
		row.className += "clientTableRow";
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		
		cell1.className += "tableBorderRight clientNameTd";
		cell1.innerHTML = "<div>"+client.name+"</div>";
		cell1.innerHTML += "<div class='clientID hidden'>"+client.ID+"</div>";
		
		cell2.className += "tableBorderRight";
		cell2.innerHTML = client.contactPerson;
		
		cell3.className += "tableBorderRight";
		cell3.innerHTML = client.totalDeals;
		
		cell4.innerHTML = client.totalSum;
	};
	
	/*
	 * adds the selected client to the document
	 */
	var addSelectedClientToDocument = function(client){
		$("#insertClientID").html(client.ID);
		$("#insertClientName").val(client.name);
		$("#insertContactPerson").val(client.contactPerson);
		$("#insertClientAddress").val(client.address);
		$("#insertClientAdditionalAddress").val(client.additionalAddress);
		$("#insertClientPhone").val(client.phone);
		$("#insertEmail").val(client.email);
	};
});