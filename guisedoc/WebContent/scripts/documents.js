var isEstonian = true;
var importingDocument = false;

$(document).ready(function() {

	/*************************************************************
	 * downloading the pdf
	 *************************************************************/
	$(document).on("click", "#getDocumentPdf", function getPDF(){

		$.cookie("pdfDownload", "finished",{ path: contextPath+"/documents" });
		showSuccessNotification("Teie dokument laetakse alla mõne sekundi pärast!");
		showLoadingDiv();
		
		window.location.href=contextPath+"/documents/pdf?ID="+getCurrentDocumentID();

		waitForDownloadStart();
	});
	
	/*
	 * check every 200 ms if the download is starting
	 */
	var waitForDownloadStart = function() {
	    if($.cookie("pdfDownload") == "finished") {
	        setTimeout(function(){ waitForDownloadStart(); }, 200);
	    }else {
	    	hideLoadingDiv();
	    }
	    return false;
	};
	
	/*************************************************************
	 * changing the total sum of document depending on the language
	 *************************************************************/
	$(document).on("click","#productsInEstonian, #productsInEnglish",function(){

		clickedEstonian = false;
		if($(this) == $("#productsInEstonian")){
			clickedEstonian = true;
		}

		$(".productRow").each(function(){
			if($(this).children(".calculateSumTd").html() == "true"){
				var amount = $(this).children(".documentsAmountTd").html();
				var price = 0;
				var discount = $(this).children(".documentsDiscountTd").html();
				
				if(clickedEstonian){
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
				"'ID':"+$(this).attr("id").replace("documentsProduct","")+
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
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	$("#documentsTable tbody").sortable({
	    helper: fixHelperModified,
	    stop: updateQueueNumbers
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
			importingDocument = false;
			$("#importDocumentDiv").show();
		}
		else if(type != "-" && type != "default"){ // selected new document
			
			importingDocument = true;
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
		        		makeNewTabAndOpenIt(response.split(";")[4],response.split(";")[2]);
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
		
		showLoadingDiv();
		
		closeDetailedDataDiv("documentsTable", function(){
			$(".detailedTr").remove(); // remove the old row
		});
		
		var id = $(this).children(".tabDocumentID").html();
		var tab = $(this);
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/import/select",
	        data : {selectedDocumentType:"tab", selectedDocumentID: id, isEstonian:isEstonian, currentDocumentID:id},
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
		importingDocument = true;
		addSelectedDocumentData(documentJSON.document);
		calculateTotalSum();
	};
	
	/*
	 * id there's no active document, we make the last selected/first one active
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
	$(document).on("blur","#documentsOptionsDiv input[type='text'], #documentsOptionsDiv input[type='date']", function(){

		showLoadingDiv();
		
		var value = $(this).val();
		var attributeName = $(this).attr("id").replace("insert","");

		if(checkForInvalidStringCharacters(new Array(
				new Array(value,$(this).attr("id"))
				))){
			return;
		}
		
		changeDocumentDataPost(attributeName,value);
	});
	
	$(document).on("blur","#documentsOptionsDiv input[type='number']", function(){
		
		showLoadingDiv();
		
		var value = parseFloat($(this).val());
		var attributeName = $(this).attr("id").replace("insert","");

		if(checkForInvalidNumberCharacters(new Array(
				new Array(value.toString(),$(this).attr("id"))
				))){
			return;
		}
		
		changeDocumentDataPost(attributeName,value);
	});
	
	$(document).on("click","#documentsOptionsDiv input[type='checkbox']", function(){
		
		showLoadingDiv();
		
		var value = $(this).is(":checked");
		var attributeName = $(this).attr("id").replace("insert","");
		
		changeDocumentDataPost(attributeName,value);
	});
	
	/*
	 * POSTS changed data
	 */
	var changeDocumentDataPost = function(attributeName,value){

		var id = getCurrentDocumentID();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/save",
	        data : {changedDocumentID:id, value: value, attributeName:attributeName},
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
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	};
	
	
	/*************************************************************
	 * PRODUCTS HANDLING
	 *************************************************************/
	/*
	 * adding a new product to list by clicking add button
	 */
	$(document).on("click", "#addProductToDocument", function(){

		showLoadingDiv();
		
		var productJSON = makeAddDocumentProductJSON();
		if(productJSON == null){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/addInput",
	        data : {productJSON: JSON.stringify(productJSON)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){

	        		addProductToDocumentsTable(response.split(";")[2],productJSON);
	        		
	        		cleanSearchInputAndResults();
	        		calculateTotalSum();
	        		
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
	});
	
	/*
	 * click on delete selected products from the document
	 */
	$(document).on("click", "#documentsDeleteSelectedProducts", function(){
		
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
		if(price == $("#documentsPriceInput").data("default_val") || price == null){
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
	        data : {code:code, name: name, price:price, unit:unit, isEstonian:isEstonian,includesPrice:includesPrice},
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

		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents/product/add",
	        data : {productID:productID, isEstonian:isEstonian, amount:amount},
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
		
		productJSON.ID = ID;
		/*productJSON.code = code;
		productJSON.name = name;
		productJSON.e_name = e_name;
		productJSON.unit = unit;
		productJSON.e_unit = e_unit;*/
		productJSON.price = price;
		productJSON.o_price= o_price;
		productJSON.amount = amount;
		productJSON.discount = discount;
		productJSON.sum = sum;
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
		productRow.children(".documentsAmountTd").html(product.amount);
		productRow.children(".documentsDiscountTd").html(product.discount);
		
		//productRow.children(".documentsNameTd").children(".documentsTableNameDiv").html(product.name);
		//productRow.children(".documentsNameTd").children(".documentsTableENameDiv").html(product.e_name);
		
		productRow.children(".documentsPriceTd").children(".documentsTablePriceDiv").html(product.price);
		productRow.children(".documentsPriceTd").children(".documentsTableOPriceDiv").html(product.o_price);
		
		//productRow.children(".documentsUnitTd").children(".documentsTableUnitDiv").html(product.unit);
		//productRow.children(".documentsUnitTd").children(".documentsTableEUnitDiv").html(product.e_unit);
		
		productRow.children(".documentsSumTd").html(parseFloat(product.sum).toFixed(2));
	};
	
	/*
	 * adds a detailed product row to the table
	 */
	var addDetailedDocumentsProductDataRow = function(rowIndex, product){
		var table = document.getElementById("documentsTable");
		
		var row = table.insertRow(rowIndex+1);
		var cell=row.insertCell(0);
		
		var price = product.o_price;
		if(isEstonian){
			price = product.price;
		}
		
		var isSelectedCalculateSum = "";
		var totalSum;
		if(product.calculateSum == true){
			isSelectedCalculateSum = "checked";
			totalSum = calculateSum(product.amount,price,product.discount);
		}
		else{
			totalSum = product.sum;
		}
		
		cell.innerHTML =
		
			"<div class='leftSideDiv'>"+
		
				"<div id='documentsDetailIDDiv' class='hidden'>"+product.unitID+"</div>"+
				"<div id='documentsDetailProductIDDiv' class='hidden'>"+product.ID+"</div>"+
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailCodeDiv documentsDetailedNameDiv'>Kood:</span>" +
					//"<input type='text' maxlength='45' id='documentsDetailCodeInput' class='documentsDetailedInput' value='"+product.code+"' />" +
					"<span class='documentsDetailedValue'>"+product.code+"</span>"+
				"</div>" +
				
				"<br>"+
	
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailNameDiv documentsDetailedNameDiv'>Nimetus:</span>" +
					//"<input type='text' id='documentsDetailNameInput' class='documentsDetailedInput' value='"+product.name+"' />" +
					"<span class='documentsDetailedValue'>"+product.name+"</span>"+
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailENameDiv documentsDetailedNameDiv'>Inglise nimetus:</span>" +
					//"<input type='text' id='documentsDetailENameInput' class='documentsDetailedInput' value='"+product.e_name+"' />" +
					"<span class='documentsDetailedValue'>asdsadsaasddasdasd  asdsa asdsaddd adsa asdd adddasd asd asd"+product.e_name+"</span>"+
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailUnitDiv documentsDetailedNameDiv'>Ühik:</span>" +
					//"<input type='text' id='documentsDetailUnitInput' class='documentsDetailedInput' value='"+product.unit+"' />" +
					"<span class='documentsDetailedValue'>"+product.unit+"</span>"+
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailEUnitDiv documentsDetailedNameDiv'>Inglise ühik:</span>" +
					//"<input type='text' id='documentsDetailEUnitInput' class='documentsDetailedInput' value='"+product.e_unit+"' />" +
					"<span class='documentsDetailedValue'>"+product.e_unit+"</span>"+
				"</div>" +
				
				"<br>"+
	
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailOPriceDiv documentsDetailedNameDiv'>Ostu hind</span>" +
					"<input type='text' id='documentsDetailOPriceInput' class='documentsDetailedInput priceCalculation' value='"+product.o_price+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailPriceDiv documentsDetailedNameDiv'>Hind</span>" +
					"<input type='text' id='documentsDetailPriceInput' class='documentsDetailedInput priceCalculation' value='"+product.price+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailAmountDiv documentsDetailedNameDiv'>Kogus</span>" +
					"<input type='text' id='documentsDetailAmountInput' class='documentsDetailedInput priceCalculation' value='"+product.amount+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailDiscountDiv documentsDetailedNameDiv'>Allahindlus (%)</span>" +
					"<input type='text' id='documentsDetailDiscountInput' class='documentsDetailedInput priceCalculation' value='"+product.discount+"' />" +
				"</div>" +
					
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailSumDiv documentsDetailedNameDiv'>Summa</span>" +
					"<input type='text' id='documentsDetailSumInput' class='documentsDetailedInput' value='"+totalSum+"' />" +
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
					"<textarea id='documentsDetailInfoInput' >"+product.additional_info+"</textarea>" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<div>Märkused</div>" +
					"<div><textarea id='documentsDetailCommentInput' >"+product.comments+"</textarea></div>" +
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
		
		
		var name,price,unit,e_unit,e_name,o_price;
		var discount = 0.0;
		
		if(isEstonian){
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
				productJSON.ID = $(this).closest(".productRow").attr("id").replace("documentsProduct","");
				
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
		
		var priceLabel = "O-Hind: ";
		if(isEstonian){
			priceLabel = "Hind: ";
		}
		
		productDiv.innerHTML += 
			"<div class='hidden productID'>"+product.unitID+"</div>"+
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
		if(isEstonian){
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
	
	var sum;
	if(product.calculatedSum == true || product.sum == undefined){
		sum = "";
	}
	else{
		sum = product.sum;
	}
	
	cell1.innerHTML = product.code;
	cell1.className = "documentsCodeTd tableBorderRight productRowClickable";
	
	cell3.innerHTML = product.amount;
	cell3.className = "documentsAmountTd tableBorderRight productRowClickable";

	var nameDivs = "<div class='documentsTableNameDiv productEstonianDiv'>"+product.name+"</div>"+
					"<div class='documentsTableENameDiv productEnglishDiv'>"+product.e_name+"</div>";
	
	var priceDivs = "<div class='documentsTablePriceDiv productEstonianDiv'>"+product.price+"</div>"+
					"<div class='documentsTableOPriceDiv productEnglishDiv'>"+product.o_price+"</div>";
	
	var unitDivs = "<div class='documentsTableUnitDiv productEstonianDiv'>"+product.unit+"</div>"+
					"<div class='documentsTableEUnitDiv productEnglishDiv'>"+product.e_unit+"</div>";
	
	cell2.innerHTML = nameDivs;
	cell2.className = "documentsNameTd tableBorderRight productRowClickable";

	cell4.innerHTML = priceDivs;
	cell4.className = "documentsPriceTd tableBorderRight productRowClickable";
	
	cell5.innerHTML = unitDivs;
	cell5.className = "documentsUnitTd tableBorderRight productRowClickable";
	
	cell6.innerHTML = product.discount;
	cell6.className = "documentsDiscountTd tableBorderRight productRowClickable";
	
	cell7.innerHTML = ""+sum;
	cell7.className = "documentsSumTd tableBorderRight productRowClickable";
	
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

/*
 * makes a new tab of selected doument and loads it
 */
var makeNewTabAndOpenIt = function(id,number){
	
	var tabHTML = "<span class='documentsTab selectableDocumentTab'>"+
		"<span>"+number+"</span>"+
		"<span class='tabDocumentID hidden'>"+id+"</span>"+
	"</span>";
	
	$(tabHTML).insertBefore("#newDocumentTab"); // insert it before the newTab span
	
	$("#newDocumentSelect").val("default");
	
	/*
	 * if we added first tab, we don't click on it because ther will be 
	 * a click later in autocheck if first document (on load)
	 */
	if($(".selectableDocumentTab").length==1){
		return;
	}
	
	$(".selectableDocumentTab")[$(".selectableDocumentTab").length-1].click();
};