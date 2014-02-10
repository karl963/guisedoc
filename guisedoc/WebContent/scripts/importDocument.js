$(document).ready(function(){
	
	makeSortable("importDocumentTable");
	
	/*
	 * open choose document view
	 */
	$(document).on("click", "#importDocumentButton", function(){
		if(allowedChangeDocuments == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		$("#importDocumentDiv").show();
	});
	
	/*
	 * close choose document view
	 */
	$(document).on("click", "#closeImportDocument, #importDocumentBackground", function(){
		$("#newDocumentSelect").val("default");
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

		// check if we had no document opened or selected to open an existing one
		if(currentID == undefined || $("#newDocumentSelect").val() == "existing"){
			currentID = 0;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/import/select",
	        data : {selectedDocumentID:id, selectedDocumentType:type, 
	        	isEstonian:isEstonian,currentDocumentID:currentID},
	        success : function(response) {
	        	
	        	var responseJSON = jQuery.parseJSON(response);
	        	
	        	if(responseJSON.response=="success"){
	        		
	        		if(currentID == 0){ // if we didn't have any documents opened yet, we reload to make content
	        			window.location = contextPath+"/documents";
	        			return;
	        		}
	        		
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
		var table = document.getElementById("importDocumentTable").getElementsByTagName("tbody")[0];
		
		var row = table.insertRow(0);
		row.className += "documentTableRow";
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);
		var cell4 = row.insertCell(3);
		
		cell1.className += "tableBorderRight documentNumberTd";
		cell1.innerHTML = "<div>"+foundDocument.fullNumber+"</div>";
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
	$("#insertDocumentType").html(document.type);

	$("#insert_fullNumber").val(document.fullNumber);
	$("#insert_validDue").val(document.validDue);
	$("#insert_advance").val(document.advance);
	$("#insert_paymentRequirement").val(document.paymentRequirement);
	$("#insert_shipmentTime").val(document.shipmentTime);
	$("#insert_shipmentAddress").val(document.shipmentAddress);
	$("#insert_shipmentPlace").val(document.shipmentPlace);
	$("#insert_orderNumber").val(document.orderNumber);
	
	$("#insert_documentDate").val(document.html5FormatedDate);
	$("#insert_addToStatistics").attr("checked",document.addToStatistics);
	$("#insert_showDiscount").attr("checked",document.showDiscount);
	$("#insert_paydInCash").attr("checked",document.paydInCash);
	$("#insert_showCE").attr("checked",document.showCE);
	
	/*
	 * only show input that the document needs
	 */
	$("#documentsOptionsDiv").children(".optionSubDiv").children("span").hide();
	$(".allTypes").show();
	$("."+document.type+"_type").show();
	
	// add the client
	var client = document.client;
	addSelectedClientToDocument(client);
	
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