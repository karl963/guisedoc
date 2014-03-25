var isEstonian = true;

$(document).ready(function() {
	$.cookie("downloadStarted","");
	/*************************************************************
	 * downloading the pdf
	 *************************************************************/
	$(document).on("click", "#downloadDocumentPdf", function getPDF(){
		$.cookie("documentsDownload","false",{ expires: 1, path: contextPath+"/documents/pdf" });
		
		showSuccessNotification("Teie dokument laetakse alla mõne sekundi pärast!");
		showLoadingDiv();
				
		window.location.href=contextPath+"/documents/pdf/download?ID="+getCurrentDocumentID();
		
		waitForDownloadStart();
	});
	
	$(document).on("click", "#viewDocumentPdf", function getPDF(){
		showSuccessNotification("Teie dokument avatakse mõne sekundi pärast!");
		window.open(contextPath+"/documents/pdf/view?ID="+getCurrentDocumentID(),"_blank");
	});
		
	/*
	 * check every 200 ms if the download is starting
	 */
	var waitForDownloadStart = function() {
		var cookieVal = $.cookie("documentsDownload");

	    if(cookieVal == "false") {
	        setTimeout(function(){ waitForDownloadStart(); }, 200);
	    }else{
	    	$.cookie("documentsDownload",null,{ expires: 0, path:contextPath+"/documents/pdf" });
	    	hideLoadingDiv();
	    }
	    return false;
	};
	
	/*************************************************************
	 * changing the total sum of document depending on the language
	 *************************************************************/
	$(document).on("click","#productsInEstonian, #productsInEnglish",function(){
		
		$(".productRow").each(function(){
			if($(this).children(".calculateSumTd").html() == "true"){

				var amount = $(this).children(".documentsAmountTd").html();
				var price = 0;
				var discount = $(this).children(".documentsDiscountTd").html();
				
				if(getCurrentDocType() != "order"){
					price = $(this).children(".documentsPriceTd").children(".documentsTablePriceDiv").html();
				}
				else{
					price = $(this).children(".documentsPriceTd").children(".documentsTableOPriceDiv").html();
				}

				var sum = calculateSum(amount,price,discount).toFixed(2);
				$(this).children(".documentsSumTd").html(sum);
			}
		});
		calculateTotalSum();
	});
	
	/*************************************************************
	 * DRAG N DROP for changing product queue number
	 *************************************************************/
	var fixHelperModified = function(e, tr) {
	    var $originals = tr.children();
	    var $helper = tr.clone();
	    $helper.children().each(function(index) {
	        $(this).width($originals.eq(index).width());
	    });
	    return $helper;
	};
	
	var updateQueueNumbers = function(){
		
		if(allowedChangeDocuments == "false"){
			return;
		}
		
		var i = 0;
		var documentID = getCurrentDocumentID();
		var wasProduct = false;
		var productsJSON = "[";
		
		$(".productRow").each(function(){
			
			if(wasProduct){
				productsJSON += ",";
			}
			productsJSON += "{"+
				"'queueNumber':"+i+","+
				"'unitID':"+$(this).attr("id").replace("documentsProduct","")+
			"}";
				
			i++;
			wasProduct = true;
		});
		
		productsJSON += "]";
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/queue",
	        data : {documentID:documentID, productsQueueNumbers: productsJSON},
	        success : function(response) {
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	$("#documentsTable tbody").sortable({
	    helper: fixHelperModified,
	    stop: updateQueueNumbers,
	    items: ".productRow"
	}).disableSelection();
	
	/*************************************************************
	 * MAKING NEW DOCUMENT
	 *************************************************************/
	/*
	 * making a new document by selecting the type from selectBox
	 */
	$(document).on("change", "#newDocumentSelect", function(){

		var type = $(this).val();

		if(type == "existing"){ // selected the old/existing document
			$("#importDocumentDiv").show();
		}
		else if(type != "-" && type != "default"){ // selected new document
			showLoadingDiv();
			
			$.ajax({
		        type : "POST",
		        url : contextPath+"/documents/add",
		        data : {newDocumentType: type},
		        success : function(response) {
		        	
		        	if(response.split(";")[0]=="success"){
		        		if(response.split(";")[3] == "1"){ // it was the first document, reload page
		        			window.location = contextPath+"/documents";
		        		}
		        		makeNewTabAndOpenIt(response.split(";")[4],response.split(";")[2],false);
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
		}
		
	});
	
	/*************************************************************
	 * CLOSING CURRENT DOCUMENT
	 *************************************************************/
	$(document).on("click","#closeCurrentDocumentButton", function(){
		
		var id = getCurrentDocumentID();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/close",
	        data : {closeDocumentID:id},
	        success : function(response) {

	        	if(response.split(";")[0]=="success"){
	        		
	        		$(".selectedTab").remove();
	        		localStorage.removeItem("lastSelectedDocumentID");
	        		
	        		// check if we have more documents loaded
	        		if($(".selectableDocumentTab").length != 0){
	        			$(".selectableDocumentTab")[0].click();
	        		}
	        		else{
	        			window.location = window.location;
	        		}
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
	
	/*************************************************************
	 * CHOOSING DOCUMENT FROM TABS
	 *************************************************************/
	/*
	 * user chooses a document
	 */
	$(document).on("click", ".selectableDocumentTab", function(){
		
		if($(this).hasClass("selectedTab")){ // if it's already opened
			return;
		}
		
		showLoadingDiv();
		
		closeDetailedDataDiv("documentsTable", function(){
			$(".detailedTr").remove(); // remove the old row
		});
		
		var id = $(this).children(".tabDocumentID").html();
		var tab = $(this);
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/import/select",
	        data : {selectedDocumentID: id, currentDocumentID:0},
	        success : function(responseJSON) {
	        	
	        	var documentJSON = jQuery.parseJSON(responseJSON);
	        	
	        	if(documentJSON.response=="success"){
	        		
	        		cleanAndFillDocument(tab,documentJSON);
	        		showSuccessNotification(documentJSON.message);
	        		localStorage.setItem("lastSelectedDocumentID",id);
	        	}
	        	else{
	        		showErrorNotification(documentJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	var cleanAndFillDocument = function(tab,documentJSON){
		$(".documentsTab").removeClass("selectedTab");
		tab.addClass("selectedTab");
		
		$(".productRow").remove();
		addSelectedDocumentData(documentJSON.document,true,false);
		calculateTotalSum();
	};
	
	/*
	 * if there's no active document, we make the last selected/first one active
	 */
	if($(".selectedTab").length == 0){
		showLoadingDiv();
		if(localStorage.getItem("lastSelectedDocumentID") != null){ // make last selected active
			$(".tabDocumentID").each(function(){
				if($(this).html() == localStorage.getItem("lastSelectedDocumentID")){
					$(this).closest(".selectableDocumentTab").click();
					return;
				}
			});
		}
		else{ // make first one active
			if($(".selectableDocumentTab").length != 0){
				$(".selectableDocumentTab")[0].click();
			}
		}
		hideLoadingDiv();
	};
	
	/*************************************************************
	 * AUTOSAVING DOCUMENT DATA ON CHANGE
	 *************************************************************/
	$(document).on("blur","#documentsOptionsDiv input[type='text']," +
			" #documentsOptionsDiv input[type='date']", function(){

		if(allowedChangeDocuments == "false"){
			return;
		}
		if($(this).attr("id") == "insert_contactPersonName"){ // contactperson name, doesn't belong to update with doc
			return;
		}

		showLoadingDiv();
		
		var value = $(this).val();
		var attributeName = $(this).attr("id").replace("insert_","");
		
		var clientID = 0;
		if($(this).hasClass("inputClient")){ // we change client's field
			clientID = $("#insertClientID").html();
		}

		var type = "string";
		if($(this).attr("type") == "date"){
			type = "date";
		}
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(value,$(this).attr("id"))
				))){
			return;
		}
		
		changeDocumentDataPost(attributeName,value,type,clientID);
	});
	
	$(document).on("blur","#documentsOptionsDiv input[type='number']", function(){
		
		if(allowedChangeDocuments == "false"){
			return;
		}
		
		showLoadingDiv();
		
		var value = parseFloat($(this).val());
		var attributeName = $(this).attr("id").replace("insert_","");
		var type = "number";
		var clientID = 0;
		if($(this).hasClass("inputClient")){ // we change client's field
			clientID = $("#insertClientID").html();
		}
		
		if(checkForInvalidNumberCharacters(new Array(
				new Array(value.toString(),$(this).attr("id"))
				))){
			return;
		}
		
		changeDocumentDataPost(attributeName,value,type,clientID);
	});
	
	$(document).on("click","#documentsOptionsDiv input[type='checkbox']", function(){
		
		if(allowedChangeDocuments == "false"){
			return;
		}
		
		showLoadingDiv();
		
		var value = $(this).is(":checked");
		var attributeName = $(this).attr("id").replace("insert_","");
		var type = "boolean";
		var clientID = 0;
		if($(this).hasClass("inputClient")){ // we change client's field
			clientID = $("#insertClientID").html();
		}
		
		// check if the changeable was a verification
		if(attributeName == "verified"){
			$(".selectableDocumentTab").each(function(){
				
				// change the right tab's html accordingly
				
				if(value){ // we verified the document
					if($(this).children("span").eq(1).html() == getCurrentDocumentID()){
						$(this).children("span").eq(2).html(""); // empty it
						return false;
					}
				}
				else{
					if($(this).children("span").eq(1).html() == getCurrentDocumentID()){
						$(this).children("span").eq(2).html(" (kinnitamata)"); // add the ending
						return false;
					}
				}
			});
		}
		
		changeDocumentDataPost(attributeName,value,type,clientID);
	});
	
	/*
	 * POSTS changed document data
	 */
	var changeDocumentDataPost = function(attributeName,value,type,clientID){

		var id = getCurrentDocumentID();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/save",
	        data : {changedDocumentID:id, value: value, 
	        attributeName:attributeName, valueType:type,
	        clientID:clientID},
	        success : function(response) {

	        	if(response.split(";")[0]=="success"){

	        		// we change the tab's number also
	        		if(attributeName == "fullNumber"){
	        			$(".selectedTab").children("span").eq(0).html(value);
	        		}
	        		
	        		showSuccessNotification(response.split(";")[1]);
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
	};
	
	/*************************************************************
	 * CLIENT AND CONTACTPERSON HANDLING
	 *************************************************************/
	/*
	 * adding a new client
	 */
	$(document).on("click", "#addNewClientButton", function(){
		
		if(allowedChangeDocuments == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		
		showLoadingDiv();

		var documentID = getCurrentDocumentID();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/client/add",
	        data : {documentID: documentID},
	        success : function(responseJson) {
	        	
	        	var response = jQuery.parseJSON(responseJson);
	        	
	        	if(response.response=="success"){
	        		addSelectedClientToDocument(response.client);
	        		showSuccessNotification(response.message);
	        	}
	        	else{
	        		showErrorNotification(response.message);
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
	 * choosing contactperson from select
	 */
	$(document).on("change","#contactPersonSelect",function(){
		
		if($(this).val() == "default"){
			return;
		}
		
		showLoadingDiv();
		
		var contactID = $(this).val();
		var documentID = getCurrentDocumentID();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/contacts/change/document",
	        data : {documentID: documentID,contactID:contactID},
	        success : function(responseJson) {
	        	
	        	var response = jQuery.parseJSON(responseJson);
	        	
	        	if(response.response=="success"){
	        		changeContactPersonData(contactID,$("#contactPersonSelect option:selected").text());
	        		showSuccessNotification(response.message);
	        	}
	        	else{
	        		showErrorNotification(response.message);
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
	 * changes contactperson for document
	 */
	var changeContactPersonData = function(id,name){
		$("#insert_contactPersonName").prop("disabled",false);
		$("#contactPersonID").html(id);
		$("#insert_contactPersonName").val(name);
	};
	
	/*
	 * changing current contact name
	 */
	$(document).on("blur","#insert_contactPersonName",function(){
		
		showLoadingDiv();
		
		var name = $(this).val();
		
		if(checkForInvalidStringCharacters(new Array(
				new Array(name,"insert_contactPersonName")
				))){
			return;
		}
		
		var contactID = $("#contactPersonID").html();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/contacts/change/name",
	        data : {contactID: contactID,name:name},
	        success : function(responseJson) {
	        	
	        	var response = jQuery.parseJSON(responseJson);
	        	
	        	if(response.response=="success"){
	        		changeContactPersonName(contactID,name);
	        		showSuccessNotification(response.message);
	        	}
	        	else{
	        		showErrorNotification(response.message);
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
	 * changes contactperson name in select
	 */
	var changeContactPersonName = function(id,name){
		$("#contactPersonSelect option[value='"+id+"'").text(name);
	};
	
	/*
	 * making new contactperson
	 */
	$(document).on("click","#addNewContactPerson",function(){

		showLoadingDiv();
		
		var clientID = $("#insertClientID").html();
		
		if(clientID == 0){
			showErrorNotification("Klient on veel valimata !");
		}
		
		var name = ""; // default name for contact person
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/clients/contacts/add",
	        data : {clientID:clientID,name:name},
	        success : function(responseJson) {
	        	
	        	var response = jQuery.parseJSON(responseJson);
	        	
	        	if(response.response=="success"){
	        		addContactPerson(response.ID,name);
	        		showSuccessNotification(response.message);
	        	}
	        	else{
	        		showErrorNotification(response.message);
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
	 * adds new contactperson to select
	 */
	var addContactPerson = function(id,name){
		$("#contactPersonSelect")
		.append($("<option class='contactPersonOption'></option>")
		.attr("value",id)
		.text(name)).val(id);

		$("#insert_contactPersonName").val(name);
		$("#contactPersonID").html(id);
	};
	
	/*************************************************************
	 * PRODUCTS HANDLING
	 *************************************************************/
	/*
	 * adding a new product to list by clicking add button
	 */
	$(document).on("click", "#addProductToDocument", function(){
		
		if(allowedChangeDocuments == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		
		showLoadingDiv();
		
		var productJSON = makeAddDocumentProductJSON();
		if(productJSON == null){
			return;
		}
		
		var documentID = getCurrentDocumentID();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/addInput",
	        data : {productJSON: JSON.stringify(productJSON),
	        	documentID: documentID},
	        success : function(responseJson) {
	        	
	        	var response = jQuery.parseJSON(responseJson);
	        	
	        	if(response.response=="success"){

	        		addProductToDocumentsTable(response.ID,productJSON);
	        		
	        		cleanSearchInputAndResults();
	        		calculateTotalSum();
	        		
	        		showSuccessNotification(response.message);
	        	}
	        	else{
	        		showErrorNotification(response.message);
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
	 * click on delete selected products from the document
	 */
	$(document).on("click", "#documentsDeleteSelectedProducts", function(){
		
		if(allowedChangeDocuments == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		
		showLoadingDiv();
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("documentsTable", function(){
				$(".detailedTr").remove(); // remove the old row
			});

		}
		
		var forDeleteJSON = makeDeleteJSON();
		if(forDeleteJSON == null){
			hideLoadingDiv();
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/delete",
	        data : {forDeleteJSON: JSON.stringify(forDeleteJSON)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		deleteSelectedObjectsFromTable();
	        		calculateTotalSum();
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
	 * searching or entering a new product, we try to search for it
	 * and display to the user
	 */
	$(document).on("input", ".productSearchField", function(){
		
		if(allowedChangeDocuments == "false"){
			return;
		}
		
		showLoadingDiv();
		
		var code = $("#documentsCodeInput").val();
		var amount = $("#documentsAmountInput").val();
		var name = $("#documentsNameInput").val();
		var price = $("#documentsPriceInput").val();
		var unit = $("#documentsUnitInput").val();
		var includesPrice = true;
		
		// if there are all default values, no need to search
		if(code == $("#documentsCodeInput").data("default_val") &&
				name == $("#documentsNameInput").data("default_val") &&
				amount == $("#documentsAmountInput").data("default_val") &&
				price == $("#documentsPriceInput").data("default_val") &&
				unit == $("#documentsUnitInput").data("default_val")){
			return;
		}
		
		if(code == $("#documentsCodeInput").data("default_val")){
			code = "";
		}
		if(amount == $("#documentsAmountInput").data("default_val")){
			amount = 0.0;
		}
		if(name == $("#documentsNameInput").data("default_val")){
			name = "";
		}
		if(price == $("#documentsPriceInput").data("default_val") || price == ""){
			price = 0.0;
			includesPrice = false;
		}
		if(unit == $("#documentsUnitInput").data("default_val")){
			unit = "";
		}
		
		/*
		 * check for incorrect input
		 */
		if(checkForInvalidStringCharacters(new Array(
				new Array(code,"documentsCodeInput"),
				new Array(name,"documentsNameInput"),
				new Array(unit,"documentsUnitInput")
				))){
			return;
		}
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"documentsAmountInput"),
				new Array(price,"documentsPriceInput")
				))){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/search",
	        data : {code:code, name: name, price:price, 
	        	unit:unit, isEstonian:isEstonian,includesPrice:includesPrice},
	        success : function(responseJSONString) {
	        	
	        	var responseJSON = jQuery.parseJSON(responseJSONString);
	        	
	        	if(responseJSON.response=="success"){
	        		addSearchResultProducts(responseJSON.products);
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
	 * click on search result product
	 */
	$(document).on("click", ".searchResult", function(){
		
		if($(this).children(".productID").length == 0){ // if the click was on empty div
			return;
		}
		
		showLoadingDiv();
		
		var productID = $(this).children(".productID").html();
		var amount = $("#documentsAmountInput").val();
		
		if(amount == $("#documentsAmountInput").data("default_val")){
			amount = 0.0;
		}
		
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"documentsAmountInput")
				))){
			return;
		}
		
		var documentID = getCurrentDocumentID();
		var type = getCurrentDocType();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/add",
	        data : {productID:productID, documentType:type, 
	        	amount:amount, documentID : documentID},
	        success : function(responseJSONString) {
	        	
	        	var responseJSON = jQuery.parseJSON(responseJSONString);
	        	
	        	if(responseJSON.response=="success"){
	        		
	        		addProductToDocumentsTable(responseJSON.product.unitID,responseJSON.product);

	        		if($("#cleanSearchCheckBox").is(":checked")){
	        			cleanSearchInputAndResults();
	        		}
	        		
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
	 * click on product row, for additional data (detailed)
	 */
	$(document).on("click", ".productRowClickable", function(){
		
		showLoadingDiv();
		
		var rowIndex = $(this).closest(".productRow").index()+1; // the row we clicked on
		var id = $(this).closest(".productRow").attr("id").replace("documentsProduct","");
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("documentsTable", function(){
				 
			 	if($(".detailedTr").index() < rowIndex){ // clicked after the opened row
			 		rowIndex--; // we deleted the current row, which means we have 1 less row
			 	}
			 	
			 	$(".detailedTr").remove(); // remove the old row
			 	
			 	showDetailDocumentsProductView(rowIndex,id); // make a new detailed product data row
			});
		}
		else{
			showDetailDocumentsProductView(rowIndex,id); // make the detailed product data row
		}
		
	});
	
	/*
	 * closing the detailed div on button click
	 */
	$(document).on("click", ".closeDocumentDetail",function(){
		closeDetailedDataDiv("documentsTable", function(){
			$(".detailedTr").remove(); // remove the old row
		});
	});
	
	/*
	 * saves the document product detailed data
	 */
	$(document).on("click", ".saveDocumentsDetailDataButton", function(){
		
		if(allowedChangeDocuments == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		
		showLoadingDiv();
		
		var productJSON = checkInputAndMakeProductJSON();
		if(productJSON == null){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/save",
	        data : {savedProductJSON:JSON.stringify(productJSON)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		
	        		productJSON.comments = productJSON.comments.replace(/\n/g,"\\n"); // replace newline with newline
	        		productJSON.additional_info = productJSON.additional_info.replace(/\n/g,"\\n"); // replace newline with newline
	        		
	        		changeTableRowData(productJSON);
	        		calculateTotalSum();
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
	 * shows the detailed product view
	 */
	var showDetailDocumentsProductView = function(rowIndex,id){
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/detail",
	        data : {detailedProductID:id},
	        success : function(responseJSONString) {
	        	
	        	var responseJSON = jQuery.parseJSON(responseJSONString.replace(/\n/g,"\\n")); // replace newline with newline

	        	if(responseJSON.response=="success"){
	        		addDetailedDocumentsProductDataRow(rowIndex,responseJSON.product);
	        		
	    			openDetailedDataDiv("documentsTable");
		        	
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
	};
	
	/*
	 * makes JSON out of detailed changed product 
	 */
	var checkInputAndMakeProductJSON = function(){
		
		var ID = $("#documentsDetailIDDiv").html();
		/*var code = $("#documentsDetailCodeInput").val();
		var name = $("#documentsDetailNameInput").val();
		var e_name = $("#documentsDetailENameInput").val();
		var unit = $("#documentsDetailUnitInput").val();
		var e_unit = $("#documentsDetailEUnitInput").val();*/
		var price = $("#documentsDetailPriceInput").val();
		var o_price = $("#documentsDetailOPriceInput").val();
		var amount = $("#documentsDetailAmountInput").val();
		var discount = $("#documentsDetailDiscountInput").val();
		var sum = $("#documentsDetailSumInput").val();
		var additional_info = $("#documentsDetailInfoInput").val();
		var comments = $("#documentsDetailCommentInput").val();
		
		var calculateSum = $(".calculateSum").is(":checked");
		
		/*
		 * check for incorrect input
		 */
		if(checkForInvalidStringCharacters(new Array(
				//new Array(code,"documentsDetailCodeInput"),
				//new Array(name,"documentsDetailNameInput"),
				//new Array(unit,"documentsDetailUnitInput"),
				//new Array(e_name,"documentsDetailENameInput"),
				//new Array(e_unit,"documentsDetailEUnitInput"),
				new Array(additional_info,"documentsDetailInfoInput"),
				new Array(comments,"documentsDetailCommentInput")
				))){
			return;
		}
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"documentsDetailAmountInput"),
				new Array(price,"documentsDetailPriceInput"),
				new Array(o_price,"documentsDetailOPriceInput"),
				new Array(discount,"documentsDetailDiscountInput"),
				new Array(sum,"documentsDetailSumInput")
				))){
			return;
		}
		
		
		var productJSON = {};
		
		productJSON.unitID = ID;
		/*productJSON.code = code;
		productJSON.name = name;
		productJSON.e_name = e_name;
		productJSON.unit = unit;
		productJSON.e_unit = e_unit;*/
		productJSON.price = price;
		productJSON.o_price= o_price;
		productJSON.amount = amount;
		productJSON.discount = discount;
		productJSON.totalSum = sum;
		productJSON.calculateSum = calculateSum;
		productJSON.additional_info = additional_info;
		productJSON.comments = comments;

		return productJSON;
	};
	
	/*
	 * changes table row data according to product changed
	 */
	var changeTableRowData = function(product){
		
		var productRow = $("#documentsProduct"+product.unitID);
		
		//productRow.children(".documentsCodeTd").html(product.code);
		productRow.children(".documentsAmountTd").html(parseFloat(product.amount).toFixed(2));
		productRow.children(".documentsDiscountTd").html(product.discount);
		
		//productRow.children(".documentsNameTd").children(".documentsTableNameDiv").html(product.name);
		//productRow.children(".documentsNameTd").children(".documentsTableENameDiv").html(product.e_name);
		
		productRow.children(".documentsPriceTd").children(".documentsTablePriceDiv").html(parseFloat(product.price).toFixed(2));
		productRow.children(".documentsPriceTd").children(".documentsTableOPriceDiv").html(parseFloat(product.o_price).toFixed(2));
		
		//productRow.children(".documentsUnitTd").children(".documentsTableUnitDiv").html(product.unit);
		//productRow.children(".documentsUnitTd").children(".documentsTableEUnitDiv").html(product.e_unit);
		
		productRow.children(".documentsSumTd").html(parseFloat(product.totalSum).toFixed(2));
	};
	
	/*
	 * adds a detailed product row to the table
	 */
	var addDetailedDocumentsProductDataRow = function(rowIndex, product){
		var table = document.getElementById("documentsTable");
		
		var row = table.insertRow(rowIndex+1);
		var cell=row.insertCell(0);
		
		var price = product.price;
		if(getCurrentDocType() == "order"){
			price = product.o_price;
		}
		
		var isSelectedCalculateSum = "";
		var totalSum;
		if(product.calculateSum == true){
			isSelectedCalculateSum = "checked";
			totalSum = calculateSum(product.amount,price,product.discount);
		}
		else{
			totalSum = product.totalSum;
		}
		
		cell.innerHTML +=
		
			"<div class='leftSideDiv'>"+
		
				"<div id='documentsDetailIDDiv' class='hidden'>"+product.unitID+"</div>"+
				"<div id='documentsDetailProductIDDiv' class='hidden'>"+product.ID+"</div>"+
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailCodeDiv documentsDetailedNameDiv'>Kood:</span>" +
					"<span class='documentsDetailedValue'>"+product.code+"</span>"+
				"</div>" +
				
				"<br>"+
	
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailNameDiv documentsDetailedNameDiv'>Nimetus:</span>" +
					"<span class='documentsDetailedValue'>"+product.name+"</span>"+
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailENameDiv documentsDetailedNameDiv'>Inglise nimetus:</span>" +
					"<span class='documentsDetailedValue'>"+product.e_name+"</span>"+
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailUnitDiv documentsDetailedNameDiv'>Ühik:</span>" +
					"<span class='documentsDetailedValue'>"+product.unit+"</span>"+
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailEUnitDiv documentsDetailedNameDiv'>Inglise ühik:</span>" +
					"<span class='documentsDetailedValue'>"+product.e_unit+"</span>"+
				"</div>" +
				
				"<br>"+
	
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailOPriceDiv documentsDetailedNameDiv'>Ostu hind</span>" +
					"<input type='text' id='documentsDetailOPriceInput' class='documentsDetailedInput priceCalculation' value='"+product.o_price+"' maxlength='20'/>" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailPriceDiv documentsDetailedNameDiv'>Hind</span>" +
					"<input type='text' id='documentsDetailPriceInput' class='documentsDetailedInput priceCalculation' value='"+product.price+"' maxlength='20'/>" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailAmountDiv documentsDetailedNameDiv'>Kogus</span>" +
					"<input type='text' id='documentsDetailAmountInput' class='documentsDetailedInput priceCalculation' value='"+product.amount+"' maxlength='20'/>" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailDiscountDiv documentsDetailedNameDiv'>Allahindlus (%)</span>" +
					"<input type='text' id='documentsDetailDiscountInput' class='documentsDetailedInput priceCalculation' value='"+product.discount+"' maxlength='20'/>" +
				"</div>" +
					
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailSumDiv documentsDetailedNameDiv'>Summa</span>" +
					"<input type='text' id='documentsDetailSumInput' class='documentsDetailedInput' value='"+totalSum+"' maxlength='20'/>" +
				"</div>" +
				
				"<div >" +
					"<label><input type='checkbox' class='calculateSum' "+isSelectedCalculateSum+"/> Arvuta summa automaatselt </label>" +
				"</div>" +
			
				"<input type='button' class='saveDocumentsDetailDataButton defaultButton' value='Salvesta' />"+
				"<input type='button' class='closeDocumentDetail defaultButton' value='Sulge' />"+
			"</div>"+
			
			"<div class='rightSideDiv'>"+
			
				"<div class='documentsDetailDataPieceDiv'>" +
					"<div>Lisainfo</div>" +
					"<textarea id='documentsDetailInfoInput' maxlength='200'>"+product.additional_info+"</textarea>" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<div>Märkused</div>" +
					"<div><textarea id='documentsDetailCommentInput' maxlength='200'>"+product.comments+"</textarea></div>" +
				"</div>" +
			
			"</div>";
				
		cell.colSpan = "8";
		cell.className = "documentsDetailedDataRow";
		
		row.className = "detailedTr";
	};
	
	/*
	 * cleans the search result field and search input fields
	 */
	var cleanSearchInputAndResults = function(){
		
		$(".searchResult").remove();
		$(".productSearchField").each(function(){
			$(this).val("");
			$(this).blur();
		});
	};
	
	/*
	 * makes a json for product added to the document
	 */
	var makeAddDocumentProductJSON = function(){
		
		var code = $("#documentsCodeInput").val();
		var amount = $("#documentsAmountInput").val();
		
		if(code == $("#documentsCodeInput").data("default_val")){
			code = "";
		}
		if(amount == $("#documentsAmountInput").data("default_val")){
			amount = 0.0;
		}
		
		
		var name,price,unit,e_unit,e_name,o_price,totalSum;
		var discount = 0.0;
		
		if(getCurrentDocType() != "order"){
			name = $("#documentsNameInput").val();
			price = $("#documentsPriceInput").val();
			unit = $("#documentsUnitInput").val();
			
			e_unit = "";
			e_name = "";
			o_price = 0.0;
			
			if(name == $("#documentsNameInput").data("default_val")){
				name = "";
			}
			if(price == $("#documentsPriceInput").data("default_val")){
				price = 0.0;
			}
			if(unit == $("#documentsUnitInput").data("default_val")){
				unit = "";
			}
			
			totalSum = calculateSum(amount,price,discount);
		}
		else{
			name = "";
			price = 0.0;
			unit = "";
			
			e_unit = $("#documentsUnitInput").val();
			e_name = $("#documentsNameInput").val();
			o_price = $("#documentsPriceInput").val();
			
			if(e_name == $("#documentsNameInput").data("default_val")){
				e_name = "";
			}
			if(o_price == $("#documentsPriceInput").data("default_val")){
				o_price = 0.0;
			}
			if(e_unit == $("#documentsUnitInput").data("default_val")){
				e_unit = "";
			}
			
			totalSum = calculateSum(amount,o_price,discount);
		}
		
		/*
		 * check for incorrect input
		 */
		if(checkForInvalidStringCharacters(new Array(
				new Array(code,"documentsCodeInput"),
				new Array(name,"documentsNameInput"),
				new Array(e_name,"documentsNameInput"),
				new Array(unit,"documentsUnitInput"),
				new Array(e_unit,"documentsUnitInput")
				))){
			return;
		}
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"documentsAmountInput"),
				new Array(price,"documentsPriceInput")
				))){
			return;
		}
		
		var productJSON = {};

		productJSON.code = code;
		productJSON.name = name;
		productJSON.e_name = e_name;
		productJSON.unit = unit;
		productJSON.e_unit = e_unit;
		productJSON.price = price;
		productJSON.o_price= o_price;
		productJSON.amount = amount;
		productJSON.discount = discount;
		productJSON.totalSum = totalSum;
		
		return productJSON;
		
	};
	
	/*
	 * makes a josn out of products selected for deletion
	 */
	var makeDeleteJSON = function(){
		
		var forDeleteJSON = {};
		var productsArray = [];

		$(".documentsDeleteCheckbox").each(function(){

			if($(this).is(":checked")){
				
				productJSON = {};
				productJSON.unitID = $(this).closest(".productRow").attr("id").replace("documentsProduct","");
				
				productsArray.push(productJSON);
			}
		});

		forDeleteJSON.products = productsArray;
		
		if(productsArray.length == 0){ // there were no objects selected, no need to post
			return;
		}

		return forDeleteJSON;
	};
	
	/*
	 * deletes selected objects from the table
	 */
	var deleteSelectedObjectsFromTable = function(){
		$(".documentsDeleteCheckbox").each(function(){
			if($(this).is(":checked")){
				$(this).closest(".productRow").remove();
			}
		});
	};
	
	/*
	 * adds all the search result products to the div
	 */
	var addSearchResultProducts = function(productsJSON){
		$(".searchResult").remove();
		
		if(productsJSON.length == 0){ // no products were found, let the user know
			
			var productDiv = document.createElement("div");
			productDiv.className += "searchResult";
			
			productDiv.innerHTML += 
				"<b>Leitud 0 toodet</b>";
			
			document.getElementById("documentsSearchResultsDiv").appendChild(productDiv);
			
			return;
		}
		
		for(var i = 0; i < productsJSON.length ; i++){
			addSearchResultProduct(productsJSON[i]);
		}
	};
	
	/*
	 * adds a search result product to the div (makes the html div)
	 */
	var addSearchResultProduct = function(product){
		
		var productDiv = document.createElement("div");
		productDiv.className += "searchResult";
		
		var priceLabel = "Hind: ";
		if(getCurrentDocType() == "order"){
			priceLabel = "O-Hind: ";
		}
		
		productDiv.innerHTML += 
			"<div class='hidden productID'>"+product.ID+"</div>"+
			product.code+"<br>"+
			product.name+"<br>"+
			"<b>"+priceLabel+"</b>"+product.price+" / "+product.unit+"<br>"+
			"<b>Laoseis: </b> <span class='storageText'>"+product.storage+"</span>";
		
		document.getElementById("documentsSearchResultsDiv").appendChild(productDiv);
	};
	
	/*************************************************************
	 * OTHER
	 *************************************************************/
	/*
	 * Animation for hiding the options div
	 */
	$(document).on("click","#hideOrShowDocumentsOptionDiv",function(){
		
		if($("#hideOrShowDocumentsOptionDiv").hasClass("isHidden")){
			$('#documentsOptionsDiv').slideDown(600, function(){
				
				$("#hideOrShowDocumentsOptionDiv").html("Peida dokumendi andmed");
				$("#hideOrShowDocumentsOptionDiv").removeClass("isHidden");
				localStorage.setItem("documentsOptionsOpened","true");
			});
		}
		else{
			$('#documentsOptionsDiv').slideUp(200, function(){
				
				$("#hideOrShowDocumentsOptionDiv").html("Ava dokumendi andmed");
				$("#hideOrShowDocumentsOptionDiv").addClass("isHidden");
				localStorage.setItem("documentsOptionsOpened","false");
			});
		}
	});
	
	/*
	 * user checks the calculation
	 */
	$(document).on("click",".calculateSum",function(){
		if($(this).is(":checked")){
			$(".priceCalculation").first().trigger("input");
		}
	});
	
	/*
	 * user is chaning the price and amount, we calculate automaticly the sum
	 */
	$(document).on("input",".priceCalculation",function(){

		if(!($(".calculateSum").is(":checked"))){
			return;
		}
		
		var price, amount, discount;
		if(getCurrentDocType() != "order"){
			price = $("#documentsDetailPriceInput").val();
			if(checkForInvalidNumberCharacters(new Array(
					new Array(price,"documentsDetailPriceInput")
					))){
				return;
			}
		}
		else{
			price = $("#documentsDetailOPriceInput").val();
			if(checkForInvalidNumberCharacters(new Array(
					new Array(price,"documentsDetailOPriceInput")
					))){
				return;
			}
		}
		
		amount = $("#documentsDetailAmountInput").val();
		discount = $("#documentsDetailDiscountInput").val();
		
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"documentsDetailAmountInput"),
				new Array(discount,"documentsDetailDiscountInput")
				))){
			return;
		}
		
		var totalSum = calculateSum(amount,price,discount);
		
		$("#documentsDetailSumInput").val(totalSum);
	});
	
	/*
	 * loading saved options from localstorage
	 */
	if(localStorage.getItem("cleanDocumentsSearch") == "true"){
		$("#cleanSearchCheckBox").attr("checked",true);
	}
	if(localStorage.getItem("documentsOptionsOpened") == "false"){ // hide the div fast
		$('#documentsOptionsDiv').slideUp(0, function(){
			$("#hideOrShowDocumentsOptionDiv").html("Ava dokumendi andmed");
			$("#hideOrShowDocumentsOptionDiv").addClass("isHidden");
		});
	}
	
	$(document).on("change", "#cleanSearchCheckBox", function(){
		localStorage.setItem("cleanDocumentsSearch",$(this).is(":checked"));
	});
});

/*
 * adding a product to the documents table
 */
var addProductToDocumentsTable = function(id, product){

	var table = document.getElementById("documentsTable").getElementsByTagName('tbody')[0];

	var row = table.insertRow(table.rows.length);
	row.setAttribute("id","documentsProduct"+id);
	row.className += "productRow";
	
	var cell1 = row.insertCell(0);
	var cell2 = row.insertCell(1);
	var cell3 = row.insertCell(2);
	var cell4 = row.insertCell(3);
	var cell5 = row.insertCell(4);
	var cell6 = row.insertCell(5);
	var cell7 = row.insertCell(6);
	var cell8 = row.insertCell(7);

	var cell0 = row.insertCell(8);
	cell0.innerHTML = product.calculateSum;
	cell0.className = "hidden calculateSumTd";
	
	cell1.innerHTML = product.code;
	cell1.className = "documentsCodeTd tableBorderRight productRowClickable";
	
	cell3.innerHTML = parseFloat(product.amount).toFixed(2);
	cell3.className = "alignRightTd documentsAmountTd tableBorderRight productRowClickable";

	var nameDivs = "<div class='documentsTableNameDiv productEstonianDiv'>"+product.name+"</div>"+
					"<div class='documentsTableENameDiv productEnglishDiv'>"+product.e_name+"</div>";
	
	var priceDivs = "<div class='documentsTablePriceDiv regularPriceDiv'>"+parseFloat(product.price).toFixed(2)+"</div>"+
					"<div class='documentsTableOPriceDiv orderPriceDiv'>"+parseFloat(product.o_price).toFixed(2)+"</div>";
	
	var unitDivs = "<div class='documentsTableUnitDiv productEstonianDiv'>"+product.unit+"</div>"+
					"<div class='documentsTableEUnitDiv productEnglishDiv'>"+product.e_unit+"</div>";
	
	cell2.innerHTML = nameDivs;
	cell2.className = "documentsNameTd tableBorderRight productRowClickable";

	cell4.innerHTML = priceDivs;
	cell4.className = "documentsPriceTd tableBorderRight productRowClickable alignRightTd";
	
	cell5.innerHTML = unitDivs;
	cell5.className = "documentsUnitTd tableBorderRight productRowClickable alignMiddleTd";
	
	cell6.innerHTML = product.discount;
	cell6.className = "alignRightTd documentsDiscountTd tableBorderRight productRowClickable";
	
	cell7.innerHTML = parseFloat(product.totalSum).toFixed(2);
	cell7.className = "alignRightTd documentsSumTd tableBorderRight productRowClickable";
	
	cell8.innerHTML = "<label class='documentsDeleteCheckboxLabel'><input class='documentsDeleteCheckbox' type='checkbox'/></label>";
	cell8.className = "documentsDeleteTd";
	
	if(isEstonian){
		$("#productsInEstonian").click();
	}
	else{
		$("#productsInEnglish").click();
	}
};

/*
 * calculates the total sum price of a product
 */
var calculateSum = function(amount,price,discount){
	
	var sum = 0.0;
	
	try{
		sum = (amount*price) - (amount*price*discount/100);
	}
	catch(err){}

	return sum;
};

/*
 * calculates the total sum of products in the document
 */
var calculateTotalSum = function(){
	var totalSum = 0.0;
	$(".documentsSumTd ").each(function(){
		totalSum += parseFloat($(this).html());
	});
	var VAT = totalSum*0.2;
	$("#totalSumDiv").html(totalSum.toFixed(2)+" + "+VAT.toFixed(2)+"(km) = "+(totalSum+VAT).toFixed(2));
};

/*
*
*/
var getCurrentDocumentID = function(){
	return $("#insertDocumentID").html();
};

var getCurrentDocType = function(){
	return $("#insertDocumentType").html();
};

/*
 * makes a new tab of selected doument and loads it
 */
var makeNewTabAndOpenIt = function(id,number,verified){

	var verifiedHTML = "";
	if(!verified){ // not verified, then add the note
		verifiedHTML = " (kinnitamata)";
	}
	
	var tabHTML = "<span class='documentsTab selectableDocumentTab'>"+
		"<span>"+number+"</span>"+
		"<span class='tabDocumentID hidden'>"+id+"</span>"+
		"<span>"+verifiedHTML+"</span>"+
	"</span>";
	
	$(tabHTML).insertBefore("#newDocumentTab"); // insert it before the newTab span
	
	$("#newDocumentSelect").val("default");
	
	/*
	 * if we added first tab, we don't click on it because there will be 
	 * a click later in autocheck if first document (on load)
	 */
	if($(".selectableDocumentTab").length==1){
		return;
	}
	
	$(".selectableDocumentTab")[$(".selectableDocumentTab").length-1].click();
};