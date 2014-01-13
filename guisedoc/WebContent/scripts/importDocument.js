$(document).ready(function(){
	/*
	 * open choose document view
	 */
	$(document).on("click", "#importDocumentButton", function(){
		$("#importDocumentDiv").show();
	});
	
	/*
	 * close choose document view
	 */
	$(document).on("click", "#closeImportDocument, #importDocumentBackground", function(){
		$("#importDocumentDiv").hide();
	});
	
	/*
	 * typing the name into the search field
	 */
	$(document).on("input", "#documentSearchNumber", function(){
		if($("#documentTypeSelect").val() == "default" ||
				$("#documentTypeSelect").val()==null){ // the type isn't selected
			return;
		}

		$("#documentTypeSelect").change();
	});
	
	/*
	 * choosing the document type and making post according to it
	 */
	$(document).on("change", "#documentTypeSelect", function(){
		
		var type = $(this).val();
		
		if(type == "default"){
			return;
		}
		
		showLoadingDiv();
		
		var number = $("#documentSearchNumber").val();
		if(number == $("#documentSearchNumber").data("default_val")){
			number = "";
		}
		if(checkForInvalidStringCharacters(new Array(
				new Array(number,"documentSearchNumber")
				))){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/import/search",
	        data : {importDocumentType: type, importDocumentNumber:number},
	        success : function(response) {
	        	
	        	var documentsJSON = jQuery.parseJSON(response);
	        	
	        	if(documentsJSON.response=="success"){
	        		$(".documentTableRow").remove();
	        		addSearchResultDocuments(documentsJSON.documents);
	        		
	        		showSuccessNotification(documentsJSON.message);
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
	 * user selects a document by clicking on the row
	 */
	$(document).on("click", ".documentTableRow", function(){
		
		var id = $(this).children(".documentNumberTd").children(".documentID").html();
		var currentID = $("#insertDocumentID").html();
		var type = $("#documentTypeSelect").val();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/import/select",
	        data : {selectedDocumentID:id, selectedDocumentType:type, isEstonian:isEstonian,currentDocumentID:currentID},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response=="success"){
	        		addSelectedDocumentData(responseJSON.document);
	        		$("#importDocumentDiv").hide();
	        		calculateTotalSum();
	        	
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
	 * adds all the search result documents to the table
	 */
	var addSearchResultDocuments = function(documents){
		for(var i = 0; i < documents.length; i++){
			addSearchResultDocument(documents[i]);
		}
	};
	
	/*
	 * adds a document to the table
	 */
	var addSearchResultDocument = function(foundDocument){
		var table = document.getElementById("importDocumentTable");
		
		var row = table.insertRow(1);
		row.className += "documentTableRow";
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		
		cell1.className += "tableBorderRight documentNumberTd";
		cell1.innerHTML = foundDocument.fullNumber;
		cell1.innerHTML += "<div class='documentID hidden'>"+foundDocument.ID+"</div>";
		
		cell2.className += "tableBorderRight";
		cell2.innerHTML = foundDocument.client.name;
		
		cell3.className += "tableBorderRight";
		cell3.innerHTML = foundDocument.totalSum;
		
		cell4.innerHTML = foundDocument.date;
	};

});

/*
 * adds the selected document to the document
 */
var addSelectedDocumentData = function(document){
	
	// if we are selecting document to open, not importing
	if(!importingDocument){
		makeNewTabAndOpenIt(document.ID,document.fullNumber);
	}
	
	$("#insertDocumentID").html(document.ID);
	$("#insertNumber").val(document.fullNumber);
	$("#insertValidDue").val(document.validDue);
	$("#insertAdvance").val(document.advance);
	$("#insertPaymentRequirement").val(document.paymentRequirement);
	$("#insertShipmentTime").val(document.shipmentTime);
	$("#insertShipmentAddress").val(document.shipmentAddress);
	$("#insertShipmentPlace").val(document.shipmentPlace);
	$("#insertOrderNumber").val(document.orderNumber);
	
	$("#insertDocumentDate").val(document.html5FormatedDate);
	$("#insertAddToStatistics").attr("checked",document.addToStatistics);
	$("#insertShowDiscount").attr("checked",document.showDiscount);
	$("#insertPaydInCash").attr("checked",document.paydInCash);
	$("#insertShowCE").attr("checked",document.showCE);
	
	var client = document.client;
	$("#insertClientID").html(client.ID);
	$("#insertClientName").val(client.name);
	$("#insertContactPerson").val(client.contactPerson);
	$("#insertClientAddress").val(client.address);
	$("#insertClientAdditionalAddress").val(client.additionalAddress);
	$("#insertClientPhone").val(client.phone);
	$("#insertEmail").val(client.email);
	
	/*
	 * only show input that the document needs
	 */
	$("#documentsOptionsDiv").children(".optionSubDiv").children("span").hide();
	$(".allTypes").show();
	$("."+document.type+"_type").show();

	/*
	 * add products
	 */
	for(var i=0; i<document.products.length;i++){
		var product = document.products[i];
		addProductToDocumentsTable(product.unitID,product);
	}
	
	if(isEstonian){
		$("#productsInEstonian").click();
	}
	else{
		$("#productsInEnglish").click();
	}
};