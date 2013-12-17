var productsJSON;
var productsInEstonian = true;
var searchIsBeingProcessed = false;
var changedProductsIDs;

$(document).ready(function(){
	
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

	
	var sortingUp = true;
	var HTMLarrowUp="&#8679;";
	var HTMLarrowDown="&#8681;";
	
	/*
	 * SORTING PRODUCTS TABLE
	 */
	$("#codeField").click(function() {
		
	});
	$("#nameField").click(function() {
		
	});
	$("#priceField").click(function() {
		
	});
	$("#unitField").click(function() {
		
	});
	
	/*
	 * Changing shown language fields
	 */
	$("#productsInEnglish").click(function() {
		showLoadingDiv();
		
		$(".productEstonianDiv").hide();
		$(".productEnglishDiv").show();
		
		$("#productsInEnglish").attr("class","selectedLanguageButton");
		$("#productsInEstonian").attr("class","defaultButton");
		
		productsInEstonian = false;
		
		hideLoadingDiv();
	});
	
	$("#productsInEstonian").click(function() {
		showLoadingDiv();
		
		$(".productEnglishDiv").hide();
		$(".productEstonianDiv").show();
		
		$("#productsInEstonian").attr("class","selectedLanguageButton");
		$("#productsInEnglish").attr("class","defaultButton");
		
		productsInEstonian = true;
		
		hideLoadingDiv();
	});
	
	/*
	 * Get the detailed data about the client from server, animate the row insert
	 */
	function showDetailProductView(clickedRow){

		var id = clickedRow.children(".productCodeTd").children(".productID").html();
		var rowIndex = clickedRow.index();

		$.ajax({
	        type : "POST",
	        url : contextPath+"/manage-products",
	        data : {id: id},
	        success : function(jsonString) {

	        	var productJSON = jQuery.parseJSON(jsonString);

	        	addProductDetailedDataRow(rowIndex,productJSON);
	        	
	        	// make the animation
	        	$('#productsTable > tbody > tr.detailedTr')
	        	 .find('td')
	        	 .wrapInner('<div style="display: none;" />')
	        	 .parent()
	        	 .find('td > div')
	        	 .slideDown(600, function(){

	        	 });
	        	
	        	if(productJSON.response=="success"){
	        		showSuccessNotification(productJSON.message);
	        	}
	        	else{
	        		showErrorNotification(productJSON.message);
	        	}
	        	
	        	hideLoadingDiv();
	        },
	        error : function(e) {
	        	hideLoadingDiv();
	        	showErrorNotification("Viga serveriga �hendumisel");
	        }
	    });
	}
	
	/*
	 * SHOWING DETAILED VIEW of product ON CLICK, this method also applies for dynamically added elements
	 */
	$(document).on("click", ".productTableRow", function() {
		
		showLoadingDiv();
		
		var clickedRow = $(this); // the row we clicked on
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			$('#productsTable > tbody > tr.detailedTr')
			 	.find('td')
			 	.wrapInner('<div style="display: block;" />')
			 	.parent()
			 	.find('td > div')
			 	.slideUp(200, function(){
				 
			 	$(".detailedTr").remove(); // remove the old row
			 	showDetailProductView(clickedRow); // make a new detailed product data row
			});
		}
		else{
			showDetailProductView(clickedRow); // make the detailed product data row
		}
		
	});
    
	/*
	 * Check for changed language, and then autoclick for data update
	 */
	if(!productsInEstonian){
		$("#productsInEnglish").trigger("click");
	}
	
	/*
	 * POST: ADD NEW PRODUCT
	 */
	$("#addNewProduct").click(function() {
		
		showLoadingDiv();
		$(".productDetailedDataRow").remove();

		var addProductJSON = makeAddProductJSON();
		if(addProductJSON == null){
			return;
		}
		
	    $.ajax({
	        type : "POST",
	        url : contextPath+"/manage-products",
	        data : {addProductJSON: addProductJSON},
	        success : function(response) {
	        	if(response.split(";")[0]=="success"){
	        		var id = response.split(";")[2];
	        		
	        		productJSON = jQuery.parseJSON(addProductJSON);
	        		
	        		addNewProductToTable(id,productJSON.code,productJSON.name,
	        				productJSON.e_name,productJSON.unit,
	        				productJSON.e_unit,productJSON.price,
	        				productJSON.o_price);
	        		
	        		$("#productCodeSearchInput").val(null);
	        		$("#productNameSearchInput").val(null);
	        		$("#productPriceSearchInput").val(null);
	        		$("#productUnitSearchInput").val(null);
	        		
	        		$("#productCodeSearchInput").blur();
	        		$("#productNameSearchInput").blur();
	        		$("#productPriceSearchInput").blur();
	        		$("#productUnitSearchInput").blur();
	        		
	        		showSuccessNotification(response.split(";")[1]);
	        		
	        		if(!productsInEstonian){
	        			$("#productsInEnglish").trigger("click");
	        		}
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga �hendumisel");
	        }
	    });
	});
	
	/*
	 * Searching product
	 */
	$(document).on("click", "#searchForProduct", function(){
		
		if(!searchIsBeingProcessed){

			searchIsBeingProcessed = true;
			$(".productTableRow").remove();

			showLoadingDiv();
			
			var searchProductJSON = makeSearchProductJSON();
			if(searchProductJSON == null){
				return;
			}
			
		    $.ajax({
		        type : "POST",
		        url : contextPath+"/manage-products",
		        data : {searchProductJSON : searchProductJSON, inEstonian : productsInEstonian},
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
		        	
	        		if(!productsInEstonian){
	        			$("#productsInEnglish").trigger("click");
	        		}
		        },
		        error : function(e) {
		        	showErrorNotification(e);
		        	searchIsBeingProcessed = false;
		        }
		    });
		}
	});
	
	$(document).on("click",".saveProductDetailDataButton", function() {

		showLoadingDiv();
		var rowIndex = $(this).closest(".detailedTr").index()-1;

		var productJSONString = makeChangedProductJSON();
		if(productJSONString == null){
			return;
		}

		$.ajax({
			traditional: true,
	        type : "POST",
	        url : contextPath+"/manage-products",
	        datatype: 'json',
	        data : {productJSONString: productJSONString},
	        success : function(response) {
	        	
	        	if(response.split(";")[0]=="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        		
	        		updateProductRegularRowData(rowIndex);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Error serveriga �hendumisel");
	        }
	    });
	});
	
	/*
	 * Updates the regular data row's data after saving the detail data
	 */
	var updateProductRegularRowData = function(index){

		var row = $('tr', $("#productsTable")).eq(index);
		
		row.children(".productCodeTd").children("div").eq(1).html($("#productDetailCodeInput").val());
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
		
		if(productsInEstonian){
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
		
		newProductJSON = '{'+
			'"name":"'+name+'",'+
			'"e_name":"'+e_name+'",'+
			'"unit":"'+unit+'",'+
			'"e_unit":"'+e_unit+'",'+
			'"price":"'+price+'",'+
			'"o_price":"'+o_price+'",'+
			'"code":"'+code+'"'+
		'}';
		
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
		
		var searchProductJSON = "{"+
			"'code':'"+code+"',"+
			"'name':'"+name+"',"+
			"'price':"+price+","+
			"'unit':'"+unit+"'"+
		"}";
		
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
		
		var productJSONString =
		'{'+
			'"ID":'+		rowProductID+','+
			'"code":"'+		code		+'",'+
			'"name":"'+		name		+'",'+
			'"e_name":"'+	e_name		+'",'+
			'"unit":"'+		unit		+'",'+
			'"e_unit":"'+	e_unit		+'",'+
			'"price":'+		price		+','+
			'"o_price":'+	o_price		+','+
			'"storage":'+	storage		+''+
		'}';
		
		return productJSONString;
	};
});

/*
 * ADDS A NEW PRODUCT TO THE TABLE
 */
function addNewProductToTable(id,code,name,e_name,unit,e_unit,price,o_price,storage){
	
	var table=document.getElementById("productsTable");
	var row=table.insertRow(2);
	row.className = "productTableRow";
	var cell1=row.insertCell(0);
	var cell2=row.insertCell(1);
	var cell3=row.insertCell(2);
	var cell4=row.insertCell(3);
	var cell5=row.insertCell(4);
	
	// CELL code
	cell1.innerHTML="" +
		"<div class='productID hidden'>"+id+"</div>" +
		"<div>"+code+"</div>";
	cell1.className = "productCodeTd tableBorderRight";
	
	// CELL name
	cell2.innerHTML="" +
		"<div class='productEstonianDiv'>"+name+"</div>" +
		"<div class='hidden productEnglishDiv'>"+e_name+"</div>";
	cell2.className = "productNameTd tableBorderRight";
	
	// CELL price
	cell3.innerHTML="" +
		"<div class='productEstonianDiv'>"+price+"</div>" +
		"<div class='hidden productEnglishDiv'>"+o_price+"</div>";
	cell3.className = "productPriceTd tableBorderRight";

	// CELL unit
	cell4.innerHTML="" +
		"<div class='productEstonianDiv'>"+unit+"</div>" +
		"<div class='hidden productEnglishDiv'>"+e_unit+"</div>";
	cell4.className = "productUnitTd tableBorderRight";

	// CELL unit
	cell5.innerHTML="" +
		"<div>"+storage+"</div>";
	cell5.className = "storageTd";

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
	
	cell.innerHTML = "<div class='productDetailedDataDiv'>" +
	
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
				"<input type='text' maxlength='45' id='productDetailPriceInput' value='"+productJSON.price+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailUnitDiv'>�hik</span>" +
				"<input type='text' maxlength='20' id='productDetailUnitInput' value='"+productJSON.unit+"' />" +
			"</div>" +

			"<br>"+
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailENameDiv'>Inglise nimi</span>" +
				"<input type='text' maxlength='100' id='productDetailENameInput' value='"+productJSON.e_name+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailOPriceDiv'>Ostu hind</span>" +
				"<input type='text' maxlength='45' id='productDetailOPriceInput' value='"+productJSON.o_price+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailEUnitDiv'>Inglise �hik</span>" +
				"<input type='text' maxlength='20' id='productDetailEUnitInput' value='"+productJSON.e_unit+"' />" +
			"</div>"+
			
			"<br>"+
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailStorageDiv'>Laoseis</span>" +
				"<input type='text' maxlength='20' id='productDetailStorageInput' value='"+productJSON.storage+"' />" +
			"</div>" +
			
			"<input type='button' class='saveProductDetailDataButton defaultButton' value='Salvesta' />"+
			
		"</div>"+
			
		"<div class='rightSideProductDetailViewDiv'>"+
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailCodeDiv'>Code</span>" +
				"<input type='text' maxlength='45' class='productDetailCodeInput' value='"+productJSON.code+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailNameDiv'>Nimi</span>" +
				"<input type='text' maxlength='100' class='productDetailNameInput' value='"+productJSON.name+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailPriceDiv'>Hind</span>" +
				"<input type='text' maxlength='45' class='productDetailPriceInput' value='"+productJSON.price+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailUnitDiv'>�hik</span>" +
				"<input type='text' maxlength='20' class='productDetailUnitInput' value='"+productJSON.unit+"' />" +
			"</div>" +
			
			
			"<br>"+
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailENameDiv'>Inglise nimi</span>" +
				"<input type='text' maxlength='100' class='productDetailENameInput' value='"+productJSON.e_name+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailOPriceDiv'>Ostu hind</span>" +
				"<input type='text' maxlength='45' class='productDetailOPriceInput' value='"+productJSON.o_price+"' />" +
			"</div>" +
			
			"<div class='productDetailDataPieceDiv'>" +
				"<span class='productDetailEUnitDiv'>Inglise �hik</span>" +
				"<input type='text' maxlength='20' class='productDetailEUnitInput' value='"+productJSON.e_unit+"' />" +
			"</div>"+
	
		"</div>"+
			
	"</div>";
			
	cell.colSpan = "5";
	cell.className = "productDetailedDataRow";
	
	row.className = "detailedTr";
}