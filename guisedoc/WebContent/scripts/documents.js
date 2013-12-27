var isEstonian = true;

$(document).ready(function() {
	
	/*
	 * Animation for hiding the options div
	 */
	$(document).on("click","#hideOrShowDocumentsOptionDiv",function(){
		
		if($("#hideOrShowDocumentsOptionDiv").hasClass("isHidden")){
			$('#documentsOptionsDiv').slideDown(600, function(){
				
				$("#hideOrShowDocumentsOptionDiv").html("Peida dokumendi andmed");
				$("#hideOrShowDocumentsOptionDiv").removeClass("isHidden");
			});
		}
		else{
			$('#documentsOptionsDiv').slideUp(200, function(){
				
				$("#hideOrShowDocumentsOptionDiv").html("Ava dokumendi andmed");
				$("#hideOrShowDocumentsOptionDiv").addClass("isHidden");
			});
		}
	});
	
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
	        url : contextPath+"/documents",
	        data : {productJSON: productJSON},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		
	        		var product = jQuery.parseJSON(productJSON);
	        		addProductToDocumentsTable(response.split(";")[2],product);
	        		
	        		cleanSearchInputAndResults();
	        		
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
			
			$('#documentsTable > tbody > tr.detailedTr')
			 	.find('td')
			 	.wrapInner('<div style="display: block;" />')
			 	.parent()
			 	.find('td > div')
			 	.slideUp(200, function(){
				 
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
	        url : contextPath+"/documents",
	        data : {forDeleteJSON: forDeleteJSON},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		deleteSelectedObjectsFromTable();
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
			return
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
	        url : contextPath+"/documents",
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
	        url : contextPath+"/documents",
	        data : {productID:productID, isEstonian:isEstonian, amount:amount},
	        success : function(responseJSONString) {
	        	
	        	var responseJSON = jQuery.parseJSON(responseJSONString);
	        	
	        	if(responseJSON.response=="success"){
	        		addProductToDocumentsTable(responseJSON.product.ID,responseJSON.product);
	        		
	        		if($("#cleanSearchCheckBox").is(":checked")){
	        			cleanSearchInputAndResults();
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
	 * click on product row, for additional data (detailed)
	 */
	$(document).on("click", ".productRowClickable", function(){
		
		showLoadingDiv();
		
		var rowIndex = $(this).closest(".productRow").index(); // the row we clicked on
		var id = $(this).closest(".productRow").attr("id").replace("documentsProduct","");
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			$('#documentsTable > tbody > tr.detailedTr')
			 	.find('td')
			 	.wrapInner('<div style="display: block;" />')
			 	.parent()
			 	.find('td > div')
			 	.slideUp(200, function(){
				 
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
	 * saves the document product detailed data
	 */
	$(document).on("click", ".saveDocumentsDetailDataButton", function(){
		
		
		
	});
	
	/*
	 * shows the detailed product view
	 */
	var showDetailDocumentsProductView = function(rowIndex,id){
		$.ajax({
	        type : "POST",
	        url : contextPath+"/documents",
	        data : {detailedProductID:id},
	        success : function(responseJSONString) {
	        	
	        	var responseJSON = jQuery.parseJSON(responseJSONString);
	        	
	        	if(responseJSON.response=="success"){
	        		addDetailedDocumentsProductDataRow(rowIndex,responseJSON.product);
	        		
		        	// make the animation
		        	$('#documentsTable > tbody > tr.detailedTr')
		        	 .find('td')
		        	 .wrapInner('<div style="display: none;" />')
		        	 .parent()
		        	 .find('td > div')
		        	 .slideDown(600, function(){
		        	 });
		        	
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
		var totalSum = calculateSum(product.amount,price,product.discount);
		
		cell.innerHTML =
		
			"<div class='leftSideDiv'>"+
		
				"<div id='documentsDetailIDDiv' class='hidden'>"+product.ID+"</div>"+
			
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailCodeDiv documentsDetailedNameDiv'>Kood</span>" +
					"<input type='text' maxlength='45' id='documentsDetailCodeInput' class='documentsDetailedInput' value='"+product.code+"' />" +
				"</div>" +
				
				"<br>"+
	
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailNameDiv documentsDetailedNameDiv'>Nimetus</span>" +
					"<input type='text' id='documentsDetailNameInput' class='documentsDetailedInput' value='"+product.name+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailENameDiv documentsDetailedNameDiv'>Inglise nimetus</span>" +
					"<input type='text' id='documentsDetailENameInput' class='documentsDetailedInput' value='"+product.e_name+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailUnitDiv documentsDetailedNameDiv'>Ühik</span>" +
					"<input type='text' id='documentsDetailUnitInput' class='documentsDetailedInput' value='"+product.unit+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailEUnitDiv documentsDetailedNameDiv'>Inglise ühik</span>" +
					"<input type='text' id='documentsDetailEUnitInput' class='documentsDetailedInput' value='"+product.e_unit+"' />" +
				"</div>" +
				
				"<br>"+
	
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailOPriceDiv documentsDetailedNameDiv'>Ostu hind</span>" +
					"<input type='text' id='documentsDetailOPriceInput' class='documentsDetailedInput' value='"+product.o_price+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailPriceDiv documentsDetailedNameDiv'>Hind</span>" +
					"<input type='text' id='documentsDetailPriceInput' class='documentsDetailedInput' value='"+product.price+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailAmountDiv documentsDetailedNameDiv'>Kogus</span>" +
					"<input type='text' id='documentsDetailAmountInput' class='documentsDetailedInput' value='"+product.amount+"' />" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailDiscountDiv documentsDetailedNameDiv'>Allahindlus (%)</span>" +
					"<input type='text' id='documentsDetailDiscountInput' class='documentsDetailedInput' value='"+product.discount+"' />" +
				"</div>" +
					
				"<div class='documentsDetailDataPieceDiv'>" +
					"<span class='documentsDetailSumDiv documentsDetailedNameDiv'>Summa</span>" +
					"<input type='text' id='documentsDetailSumInput' class='documentsDetailedInput' value='"+totalSum+"' />" +
				"</div>" +
				
				"<input type='button' class='saveDocumentsDetailDataButton defaultButton' value='salvesta' />"+
			
			"</div>"+
			
			"<div class='rightSideDiv'>"+
			
				"<div class='documentsDetailDataPieceDiv'>" +
					"<div>Lisainfo</div>" +
					"<textarea id='documentsDetailInfoInput' >"+product.additionalInfo+"</textarea>" +
				"</div>" +
				
				"<div class='documentsDetailDataPieceDiv'>" +
					"<div>Märkused</div>" +
					"<div><textarea id='documentsDetailCommentInput' >"+product.comment+"</textarea></div>" +
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
		
		productJSON = '{'+
			'"code":"'+code+'",'+
			'"name":"'+name+'",'+
			'"price":'+price+','+
			'"o_price":'+o_price+','+
			'"unit":"'+unit+'",'+
			'"e_unit":"'+e_unit+'",'+
			'"e_name":"'+e_name+'",'+
			'"amount":'+amount+","+
			'"discount":'+discount+
		'}';
		
		return productJSON;
		
	};
	
	/*
	 * adding a product to the documents table
	 */
	var addProductToDocumentsTable = function(id, product){
		
		var table = document.getElementById("documentsTable");

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
		
		cell1.innerHTML = product.code;
		cell1.className = "documentsCodeTd tableBorderRight productRowClickable";
		
		cell3.innerHTML = product.amount;
		cell3.className = "documentsAmountTd tableBorderRight productRowClickable";
		
		var price,name,unit;
		
		if(isEstonian){
			price = product.price;
			name = product.name;
			unit = product.unit;
		}
		else{
			price = product.o_price;
			name = product.e_name;
			unit = product.e_unit;
		}
		
		cell2.innerHTML = name;
		cell2.className = "documentsNameTd tableBorderRight productRowClickable";

		cell4.innerHTML = price;
		cell4.className = "documentsPriceTd tableBorderRight productRowClickable";
		
		cell5.innerHTML = unit;
		cell5.className = "documentsUnitTd tableBorderRight productRowClickable";
		
		cell6.innerHTML = product.discount;
		cell6.className = "documentsDiscountTd tableBorderRight productRowClickable";
		
		cell7.innerHTML = calculateSum(product.amount,price,product.discount);
		cell7.className = "documentsSumTd tableBorderRight productRowClickable";
		
		cell8.innerHTML = "<label class='documentsDeleteCheckboxLabel'><input class='documentsDeleteCheckbox' type='checkbox'/></label>";
		cell8.className = "documentsDeleteTd";
	};
	
	/*
	 * calculates the total sum price of a product
	 */
	var calculateSum = function(amount,price,discount){
		
		var sum = 0.0;
		
		try{
			sum = (amount*price) - (amount*price*discount);
		}
		catch(err){}
		
		return sum;
	};
	
	/*
	 * makes a josn out of products selected for deletion
	 */
	var makeDeleteJSON = function(){
		
		var forDeleteJSON = "{'products':[";
		var wasObjectBefore = false;
		
		$(".documentsDeleteCheckbox").each(function(){

			if($(this).is(":checked")){
				
				if(wasObjectBefore){
					forDeleteJSON += ",";
				}
				forDeleteJSON += "{'ID':"+$(this).closest(".productRow").attr("id").replace("documentsProduct","")+"}";
				
				wasObjectBefore = true;
			}
			
		});
		
		forDeleteJSON += "]}";
		
		if(!wasObjectBefore){ // there were no objects selected, no need to post
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
			"<div class='hidden productID'>"+product.ID+"</div>"+
			product.code+"<br>"+
			product.name+"<br>"+
			"<b>"+priceLabel+"</b>"+product.price+" / "+product.unit+"<br>"+
			"<b>Laoseis: </b> <span class='storageText'>"+product.storage+"</span>";
		
		document.getElementById("documentsSearchResultsDiv").appendChild(productDiv);
	};
});