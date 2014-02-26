var isEstonian = true;
var searchIsBeingProcessed = false;
var changedProductsIDs;

$(document).ready(function(){

	makeSortable("productsTable");
	
	/*
	 * add search history input
	 */
	if(localStorage.getItem("productCode") != null){
		$("#productCodeSearchInput").focus();
		$("#productCodeSearchInput").val(localSotorage.getItem("productCode"));
		$("#productCodeSearchInput").blue();
	}
	if(localStorage.getItem("productName") != null){
		$("#productNameSearchInput").focus();
		$("#productNameSearchInput").val(localSotorage.getItem("productName"));
		$("#productNameSearchInput").blue();
	}
	if(localStorage.getItem("productPrice") != null){
		$("#productPriceSearchInput").focus();
		$("#productPriceSearchInput").val(localSotorage.getItem("productPrice"));
		$("#productPriceSearchInput").blue();
	}
	if(localStorage.getItem("productUnit") != null){
		$("#productUnitSearchInput").focus();
		$("#productUnitSearchInput").val(localSotorage.getItem("productUnit"));
		$("#productUnitSearchInput").blue();
	}
	
	/*
	 * Get the detailed data about the client from server, animate the row insert
	 */
	function showDetailProductView(clickedRow,rowIndex){

		var id = clickedRow.attr("id").replace("productRow","");

		$.ajax({
	        type : "POST",
	        url : contextPath+"/products/detail",
	        data : {id: id},
	        success : function(jsonString) {

	        	var productJSON = jQuery.parseJSON(jsonString);

	        	if(productJSON.response=="success"){

	        		addProductDetailedDataRow(rowIndex,productJSON.product);
	        		openDetailedDataDiv("productsTable");
	        		
	        		showSuccessNotification(productJSON.message);
	        	}
	        	else{
	        		showErrorNotification(productJSON.message);
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
	 * SHOWING DETAILED VIEW of product ON CLICK
	 */
	$(document).on("click", ".productRowClickable", function() {
		console.log("clicked");
		showLoadingDiv();

		var clickedRow = $(this).closest(".productTableRow"); // the row we clicked on
		var rowIndex = clickedRow.index()+1;

		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			closeDetailedDataDiv("productsTable", function(){
			 	
				if($(".detailedTr").index() < rowIndex){ // clicked after the opened row
			 		rowIndex--; // we deleted the current row, which means we have 1 less row
			 	}

			 	$(".detailedTr").remove(); // remove the old row

			 	showDetailProductView(clickedRow,rowIndex); // make a new detailed product data row
			});
		}
		else{
			showDetailProductView(clickedRow,rowIndex); // make the detailed product data row
		}
		
	});
    
	/*
	 * Check for changed language, and then autoclick for data update
	 */
	if(!isEstonian){
		$("#productsInEnglish").trigger("click");
	}
	
	/*
	 * POST: ADD NEW PRODUCT
	 */
	// on enter press
	$(document).on("keypress",".productSearchInputField",function(e){
		if(e.which == 13){
			$("#addNewProduct").click();
		}
	});
	$(document).on("click","#addNewProduct",function() {
		
		showLoadingDiv();
		$(".productDetailedDataRow").remove();

		var addProductJSON = makeAddProductJSON();
		if(addProductJSON == null){
			return;
		}

	    $.ajax({
	        type : "POST",
	        url : contextPath+"/products/add",
	        data : {addProductJSON: JSON.stringify(addProductJSON)},
	        success : function(response) {
	        	if(response.split(";")[0]=="success"){
	        		var id = response.split(";")[2];
	        		
	        		addNewProductToTable(id,addProductJSON.code,addProductJSON.name,
	        				addProductJSON.e_name,addProductJSON.unit,
	        				addProductJSON.e_unit,addProductJSON.price,
	        				addProductJSON.o_price,addProductJSON.storage);
	        		
	        		if(cleanProductSearchAfterAdd){
		        		$(".productSearchInputField").val(null);
		        		$(".productSearchInputField").blur();
	        		}
	        		
	        		showSuccessNotification(response.split(";")[1]);
	        		
	        		if(!isEstonian){
	        			$("#productsInEnglish").trigger("click");
	        		}

	        		if(focusProductCodeAfterAdd){
	        			$("#productCodeSearchInput").focus();
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
	});
	
	/*
	 * Searching product
	 */
	$(document).on("click", "#searchForProduct", function(){
		
		if(!searchIsBeingProcessed){

			$(".detailedTr").remove();
			
			showLoadingDiv();
			
			searchIsBeingProcessed = true;
			$(".productTableRow").remove();
			
			var searchProductJSON = makeAddProductJSON();//makeSearchProductJSON();
			if(searchProductJSON == null){
				return;
			}
			
		    $.ajax({
		        type : "POST",
		        url : contextPath+"/products/search",
		        data : {searchProductJSON : JSON.stringify(searchProductJSON), inEstonian : isEstonian},
		        success : function(responseString) {
		        	
		        	responseJSON = jQuery.parseJSON(responseString);
		        	if(responseJSON.response=="success"){
		        		addAllProductsIntoTable(responseJSON);
		        		showSuccessNotification(responseJSON.message);
		        	}
		        	else{
		        		showErrorNotification(responseJSON.message);
		        	}
		        	
		        	searchIsBeingProcessed = false;
		        	
	        		if(!isEstonian){
	        			$("#productsInEnglish").trigger("click");
	        		}
	        		
	        		hideLoadingDiv();
		        },
		        error : function(e) {
		        	hideLoadingDiv();
		        	showErrorNotification(e);
		        	searchIsBeingProcessed = false;
		        }
		    });
		}
	});
	
	/*
	 * saving product data
	 */
	$(document).on("click",".saveProductDetailDataButton", function() {
		
		showLoadingDiv();
		var rowIndex = $(this).closest(".detailedTr").index();

		var productJSONString = makeChangedProductJSON();
		if(productJSONString == null){
			return;
		}

		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/products/save",
	        datatype: 'json',
	        data : {productJSONString: JSON.stringify(productJSONString)},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		
	        		updateProductRegularRowData(rowIndex);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Error serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * close the detailed div on button click
	 */
	$(document).on("click", ".closeDetailDiv",function(){
		closeDetailedDataDiv("productsTable",function(){
			$(".detailedTr").remove();
		});
	});
	
	/*
	 * delete selected products
	 */
	$(document).on("click","#deleteProductsButton",function(){
		showConfirmationDialog("Kustuta valitud tooted ?"
				,deleteProducts);
	});
	var deleteProducts = function(){
		showLoadingDiv();
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("productsTable", function(){
				$(".detailedTr").remove(); // remove the old row
			});

		}
		
		var forDeleteJSON = makeDeleteProductsJSON();
		if(forDeleteJSON == null){
			hideLoadingDiv();
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/products/delete",
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
	};

	/*
	 * delete selected objects from table after post success
	 */
	function deleteSelectedObjectsFromTable(){
		$(".productDeleteCheckbox").each(function(){
			if($(this).is(":checked")){
				$(this).closest(".productTableRow").remove();
			}
		});
	}
	
	/*
	 * deleteable objects json
	 */
	var makeDeleteProductsJSON = function(){
		
		var objects = [];
		
		$(".productDeleteCheckbox").each(function(){

			if($(this).is(":checked")){

				var object = {};
				object.ID = $(this).closest(".productTableRow").attr("id").replace("productRow","");
				
				objects.push(object);
			}
			
		});

		if(objects.length == 0){ // there were no objects selected, no need to post
			return;
		}

		return objects;
	};
	
	/*
	 * Updates the regular data row's data after saving the detail data
	 */
	var updateProductRegularRowData = function(index){

		var row = $('tr', $("#productsTable")).eq(index);
		
		row.children(".productCodeTd").html($("#productDetailCodeInput").val());
		row.children(".productNameTd").children("div").eq(0).html($("#productDetailNameInput").val());
		row.children(".productNameTd").children("div").eq(1).html($("#productDetailENameInput").val());
		row.children(".productUnitTd").children("div").eq(0).html($("#productDetailUnitInput").val());
		row.children(".productUnitTd").children("div").eq(1).html($("#productDetailEUnitInput").val());
		row.children(".productPriceTd").children("div").eq(0).html($("#productDetailPriceInput").val());
		row.children(".productPriceTd").children("div").eq(1).html($("#productDetailOPriceInput").val());
	};
	
	/*
	 * makes json out of new (add) product
	 */
	var makeAddProductJSON = function(){
		var code,name,e_name,unit,e_unit,price,o_price;
		
		code = $("#productCodeSearchInput").val();
		
		if(code == $("#productCodeSearchInput").data("default_val")){
			code = "";
		}

		if(checkForInvalidStringCharacters(new Array(
				new Array(code,"productCodeSearchInput")))){
			return;
		}
		
		if(isEstonian){
			name = $("#productNameSearchInput").val();
			unit = $("#productUnitSearchInput").val();
			price = $("#productPriceSearchInput").val();

			e_name = "";
			e_unit = "";
			o_price = "0.0";
			
			if(name == $("#productNameSearchInput").data("default_val")){
				name = "";
			}
			if(unit == $("#productUnitSearchInput").data("default_val")){
				unit = "";
			}
			if(price == $("#productPriceSearchInput").data("default_val")){
				price = "0.0";
			}
			
			/*
			 * Check for invalid characters
			 */
			if(checkForInvalidStringCharacters(new Array(
					new Array(name,"productNameSearchInput"),
					new Array(unit,"productUnitSearchInput")
					))){
				return;
			}
			
			if(checkForInvalidNumberCharacters(new Array(
					new Array(price,"productPriceSearchInput")
					))){
				return;
			}
		}
		else{
			e_name = $("#productNameSearchInput").val();
			e_unit = $("#productUnitSearchInput").val();
			o_price = $("#productPriceSearchInput").val();
			
			name = "";
			unit = "";
			price = "0.0";
			
			if(e_name == $("#productNameSearchInput").data("default_val")){
				e_name = "";
			}
			if(e_unit == $("#productUnitSearchInput").data("default_val")){
				e_unit = "";
			}
			if(o_price == $("#productPriceSearchInput").data("default_val")){
				o_price = "0.0";
			}
			
			/*
			 * Check for invalid characters
			 */
			if(checkForInvalidStringCharacters(new Array(
					new Array(e_name,"productNameSearchInput"),
					new Array(e_unit,"productUnitSearchInput")
					))){
				return;
			}
			
			if(checkForInvalidNumberCharacters(new Array(
					new Array(o_price,"productPriceSearchInput")
					))){
				return;
			}
		}
		
		newProductJSON = {};
		
		newProductJSON.name = name;
		newProductJSON.e_name = e_name;
		newProductJSON.unit = unit;
		newProductJSON.e_unit = e_unit;
		newProductJSON.price = price;
		newProductJSON.o_price = o_price;
		newProductJSON.code = code;
		newProductJSON.storage = 0.0;
		
		return newProductJSON;
	};
	
	/*
	 * makes json for search product
	 */
	var makeSearchProductJSON = function(){
		var code = $("#productCodeSearchInput").val();
		var name = $("#productNameSearchInput").val();
		var price = $("#productPriceSearchInput").val();
		var unit = $("#productUnitSearchInput").val();
		
		if(code == $("#productCodeSearchInput").data("default_val") || code == null){
			code = "";
		}
		if(name == $("#productNameSearchInput").data("default_val") || name == null){
			name = "";
		}
		if(price == $("#productPriceSearchInput").data("default_val") || price == null){
			price = "0.0";
		}
		if(unit == $("#productUnitSearchInput").data("default_val") || unit == null){
			unit = "";
		}
		
		searchProductJSON = {};
		
		searchProductJSON.name = name;
		searchProductJSON.unit = unit;
		searchProductJSON.price = price;
		searchProductJSON.code = code;
		
		return searchProductJSON;
	};
	
	/*
	 * Make a JSON out of changed product
	 */
	var makeChangedProductJSON = function(){

		var rowProductID = $(".productDetailIDDiv").html();

		var code = $("#productDetailCodeInput").val();
		var name = $("#productDetailNameInput").val();
		var e_name = $("#productDetailENameInput").val();
		var price = $("#productDetailPriceInput").val();
		var o_price = $("#productDetailOPriceInput").val();
		var unit = $("#productDetailUnitInput").val();
		var e_unit = $("#productDetailEUnitInput").val();
		var storage = $("#productDetailStorageInput").val();
		
		/*
		 * Check for invalid characters
		 */
		if(checkForInvalidStringCharacters(new Array(
				new Array(code,"productDetailCodeInput"),
				new Array(name,"productDetailNameInput"),
				new Array(e_name,"productDetailENameInput"),
				new Array(unit,"productDetailUnitInput"),
				new Array(e_unit,"productDetailEUnitInput")
				))){
			return;
		}
		
		if(checkForInvalidNumberCharacters(new Array(
				new Array(price,"productDetailPriceInput"),
				new Array(o_price,"productDetailOPriceInput"),
				new Array(storage,"productDetailStorageInput")
				))){
			return;
		}
		
		productJSON = {};
		
		productJSON.ID = rowProductID;
		productJSON.name = name;
		productJSON.e_name = e_name;
		productJSON.unit = unit;
		productJSON.e_unit = e_unit;
		productJSON.price = price;
		productJSON.o_price = o_price;
		productJSON.code = code;
		productJSON.storage = storage;
		
		return productJSON;
	};
});

/*
 * ADDS A NEW PRODUCT TO THE TABLE
 */
function addNewProductToTable(id,code,name,e_name,unit,e_unit,price,o_price,storage){
	
	var table=document.getElementById("productsTable").getElementsByTagName('tbody')[0];
	
	var row=table.insertRow(0);
	row.className = "productTableRow";
	row.setAttribute("id","productRow"+id);
	
	var cell1=row.insertCell(0);
	var cell2=row.insertCell(1);
	var cell3=row.insertCell(2);
	var cell4=row.insertCell(3);
	var cell5=row.insertCell(4);
	var cell6=row.insertCell(5);
	
	// CELL code
	cell1.innerHTML=code;
	cell1.className = "productCodeTd tableBorderRight productRowClickable";
	
	// CELL name
	cell2.innerHTML="" +
		"<div class='productEstonianDiv'>"+name+"</div>" +
		"<div class='hidden productEnglishDiv'>"+e_name+"</div>";
	cell2.className = "productNameTd tableBorderRight productRowClickable";
	
	// CELL price
	cell3.innerHTML="" +
		"<div class='productEstonianDiv'>"+parseFloat(price).toFixed(2)+"</div>" +
		"<div class='hidden productEnglishDiv'>"+parseFloat(o_price).toFixed(2)+"</div>";
	cell3.className = "productPriceTd tableBorderRight productRowClickable alignRightTd";

	// CELL unit
	cell4.innerHTML="" +
		"<div class='productEstonianDiv'>"+unit+"</div>" +
		"<div class='hidden productEnglishDiv'>"+e_unit+"</div>";
	cell4.className = "productUnitTd tableBorderRight productRowClickable";

	// CELL storage
	cell5.innerHTML="" +
		"<div>"+parseFloat(storage).toFixed(3)+"</div>";
	cell5.className = "storageTd tableBorderRight productRowClickable alignRightTd";

	// CELL delete
	cell6.innerHTML="" +
		"<label class='productsDeleteLabel'><input type='checkbox' class='productDeleteCheckbox' /></label>";
	cell6.className = "productDeleteTd";
	
}

function addAllProductsIntoTable(productsJSON){
	
	for(var i = 0; i < productsJSON.products.length ; i++){
		var product = productsJSON.products[i];
		var id = product.ID;
		var code = product.code;
		var name = product.name;
		var e_name = product.e_name;
		var unit = product.unit;
		var e_unit = product.e_unit;
		var price = product.price;
		var o_price = product.o_price;
		var storage = product.storage;
		
		addNewProductToTable(id,code,name,e_name,unit,e_unit,price,o_price,storage);
	}
}

function addProductDetailedDataRow(rowIndex,productJSON){
	
	var table = document.getElementById("productsTable");

	var row = table.insertRow(rowIndex+1);
	var cell=row.insertCell(0);
	
	var saveButton = "";
	if(allowedChangeProducts == "true"){
		saveButton = "<input type='button' class='saveProductDetailDataButton defaultButton' value='Salvesta' />";
	}
	
	cell.innerHTML =
	
		"<div class='leftSideProductDetailViewDiv'>"+
		
			"<div class='productDetailIDDiv hidden'>"+productJSON.ID+"</div>"+
		
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailCodeDiv'>Kood</span>" +
				"<input type='text' maxlength='45' id='productDetailCodeInput' value='"+productJSON.code+"' />" +
			"</div>" +
			
			"<br>"+
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailNameDiv'>Nimi</span>" +
				"<input type='text' maxlength='100' id='productDetailNameInput' value='"+productJSON.name+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailPriceDiv'>Hind</span>" +
				"<input type='text' maxlength='45' id='productDetailPriceInput' value='"+parseFloat(productJSON.price).toFixed(2)+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailUnitDiv'>Ühik</span>" +
				"<input type='text' maxlength='45' id='productDetailUnitInput' value='"+productJSON.unit+"' />" +
			"</div>" +

			"<br>"+
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailENameDiv'>Inglise nimi</span>" +
				"<input type='text' maxlength='100' id='productDetailENameInput' value='"+productJSON.e_name+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailOPriceDiv'>Ostu hind</span>" +
				"<input type='text' maxlength='45' id='productDetailOPriceInput' value='"+parseFloat(productJSON.o_price).toFixed(2)+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailEUnitDiv'>Inglise ühik</span>" +
				"<input type='text' maxlength='45' id='productDetailEUnitInput' value='"+productJSON.e_unit+"' />" +
			"</div>"+
			
			"<br>"+
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailStorageDiv'>Laoseis</span>" +
				"<input type='text' maxlength='20' id='productDetailStorageInput' value='"+parseFloat(productJSON.storage).toFixed(3)+"' />" +
			"</div>" +
			
			saveButton +
			"<input type='button' class='closeDetailDiv defaultButton' value='Sulge' />"+
		"</div>"+
			
		"<div class='rightSideProductDetailViewDiv'>"+
			
		"</div>";
			
	cell.colSpan = "6";
	cell.className = "productDetailedDataRow";
	
	row.className = "detailedTr";
}