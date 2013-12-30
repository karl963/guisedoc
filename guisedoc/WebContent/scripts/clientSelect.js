$(document).ready(function(){
	/*
	 * open choose client view
	 */
	$(document).on("click", "#chooseClientButton", function(){
		$("#clientSelectDiv").show();
	});
	
	/*
	 * close choose client view
	 */
	$(document).on("click", "#closeClientSelect, #clientSelectBackground", function(){
		$("#clientSelectDiv").hide();
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
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents",
	        data : {clientType: type},
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
		var table = document.getElementById("clientSelectTable");
		
		var row = table.insertRow(1);
		row.className += "clientTableRow";
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		
		cell1.className += "tableBorderRight";
		cell1.innerHTML = client.name;
		
		cell2.className += "tableBorderRight";
		cell2.innerHTML = client.contactPerson;
		
		cell3.className += "tableBorderRight";
		cell3.innerHTML = client.totalDeals;
		
		cell4.innerHTML = client.totalSum;
	};
	
});