$(document).ready(function() {
	
	/*
	 * fill in search history on a page load
	 */
	if(localStorage.getItem("statisticsClient") != null){
		$(":radio[value='"+localStorage.getItem("statisticsClient")+"']").prop("checked", true);
	}
	if(localStorage.getItem("statisticsProduct") != null){
		$(":radio[value='"+localStorage.getItem("statisticsProduct")+"']").prop("checked", true);
	}
	if(localStorage.getItem("statisticsClientName") != null){
		$("#statisticsClientInput").focus(); // for change of default input class
		$("#statisticsClientInput").val(localStorage.getItem("statisticsClientName"));
		$("#statisticsClientInput").blur(); // for unFocus
	}
	if(localStorage.getItem("statisticsCode") != null){
		$("#statisticsCodeInput").focus(); // for change of default input class
		$("#statisticsCodeInput").val(localStorage.getItem("statisticsCode"));
		$("#statisticsCodeInput").blur(); // for unFocus
	}
	if(localStorage.getItem("statisticsStartDate") != null){
		$("#statisticsDateFrom").val(localStorage.getItem("statisticsStartDate"));
	}
	if(localStorage.getItem("statisticsEndDate") != null){
		$("#statisticsDateTo").val(localStorage.getItem("statisticsEndDate"));
	}

	/*************************************************************
	 * SEARCHING
	 *************************************************************/
	/*
	 * Animation for hiding the options div
	 */
	$(document).on("click","#hideOrShowStatisticsOptionDiv",function(){
		
		if($("#hideOrShowStatisticsOptionDiv").hasClass("isHidden")){
			$('#statisticsOptionsDiv').slideDown(600, function(){
				
				$("#hideOrShowStatisticsOptionDiv").html("Peida otsingu täpsustused");
				$("#hideOrShowStatisticsOptionDiv").removeClass("isHidden");
			});
		}
		else{
			$('#statisticsOptionsDiv').slideUp(200, function(){
				
				$("#hideOrShowStatisticsOptionDiv").html("Ava otsingu täpsustused");
				$("#hideOrShowStatisticsOptionDiv").addClass("isHidden");
			});
		}
	});
	
	/*
	 * search for statistics post
	 */
	$(document).on("click","#searchForStatistics",function(){
		
		showLoadingDiv();

		$("#statisticsTable").remove();
		var statisticsJSONString = makeStatisticsJSON();
		if(statisticsJSONString == null){
			return;
		}
		
		addSearchElementsToStorage();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/statistics/search",
	        data : {statisticsJSONString: JSON.stringify(statisticsJSONString)},
	        success : function(jsonString) {
	        	
	        	var statisticsJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(statisticsJSON.response=="success"){
	        		showSuccessNotification(statisticsJSON.message);
	        		addNewStatisticsTableWithData(statisticsJSON);
	        	}
	        	else{
	        		showErrorNotification(statisticsJSON.message);
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
	 * CLIENT NAME SELECTION
	 *************************************************************/
	/*
	 * changed the client type, load the new names
	 */
	$(document).on("click","input:radio[name='statisticsClientGroup']",function(){
		
		var type = $("input:radio[name='statisticsClientGroup']:checked").val();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/statistics/clients",
	        data : {clientType: type},
	        success : function(jsonString) {

	        	var clientsJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(clientsJSON.response=="success"){
		        	addClientNamesToBox(clientsJSON.clients);
		        	
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
	 * adds the clients to the dropdown box
	 */
	var addClientNamesToBox = function(clients){
		$('#clientsSelectBox').find("option").remove().end()
			.append("<option value='default' disabled>--valige klient--</option>")
			.val("default");
		
		for(var i = 0; i < clients.length; i++){
			var option = $('<option value="'+clients[i].ID+'">'+clients[i].name+'</option>');
			$('#clientsSelectBox').append(option);
		}
	};
	
	/*
	 * user changes client name and we invalidate the ID
	 */
	$(document).on("change","#statisticsClientInput",function(){
		if($(this).val() != $("#clientsSelectBox option:selected").text()){
			$("#clientsSelectBox").val("default");
			$("#clientIDDiv").html("0");
		}
	});
	
	/*
	 * user selected the client from the select field
	 */
	$(document).on("change","#clientsSelectBox",function(){
		$("#clientIDDiv").html($("#clientsSelectBox option:selected").val());
		$("#statisticsClientInput").focus();
		$("#statisticsClientInput").val($("#clientsSelectBox option:selected").text());
	});
	
	/*************************************************************
	 * OBJECTS HANDLING
	 *************************************************************/
	/*
	 * delete selected statistics objects
	 */
	$(document).on("click","#deleteSelectedStatistics", function(){
		
		showLoadingDiv();
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("statisticsTable", function(){
				$(".detailedTr").remove(); // remove the old row
			});

		}
		
		var forDeleteStatisticsJSON = makeDeleteStatisticsJSON();
		if(forDeleteStatisticsJSON == null){
			return;
		}

		$.ajax({
	        type : "POST",
	        url : contextPath+"/statistics/delete",
	        data : {forDeleteStatisticsJSON: JSON.stringify(forDeleteStatisticsJSON)},
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
	 * click on row, open detailed data
	 */
	$(document).on("click",".statisticsRowClickable", function(){
		
		showLoadingDiv();
		
		var rowIndex = $(this).closest(".statisticsRow").index(); // the row we clicked on
		var id = $(this).closest(".statisticsRow").attr("id").replace("statisticsRow","");
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("statisticsTable", function(){
			 	if($(".detailedTr").index() < rowIndex){ // clicked after the opened row
			 		rowIndex--; // we deleted the current row, which means we have 1 less row
			 	}
			 	
				$(".detailedTr").remove(); // remove the old row
			 	
			 	showDetailStatisticsView(rowIndex,id); // make a new detailed product data row
			});
		}
		else{
			showDetailStatisticsView(rowIndex,id); // make the detailed product data row
		}

	});
	
	/*
	 * close the detailed div on button click
	 */
	$(document).on("click", ".closeDetailDiv",function(){
		closeDetailedDataDiv("statisticsTable",function(){
			$(".detailedTr").remove();
		});
	});
	
	/*
	 * Get the detailed data about statistics object
	 */
	function showDetailStatisticsView(rowIndex, ID){

		$.ajax({
	        type : "POST",
	        url : contextPath+"/statistics/detail",
	        data : {ID: ID},
	        success : function(jsonString) {
	        	
	        	var statObjJSON = jQuery.parseJSON(jsonString);
	        	
	        	if(statObjJSON.response=="success"){
	        		
		        	addDetailedStatisticsDataRow(rowIndex,statObjJSON.statisticObject);
		        	
		        	openDetailedDataDiv("statisticsTable");
		        	
	        		showSuccessNotification(statObjJSON.message);
	        	}
	        	else{
	        		showErrorNotification(statObjJSON.message);
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
	 * delete selected objects from table after post success
	 */
	function deleteSelectedObjectsFromTable(){
		$(".statisticsDeleteCheckbox").each(function(){
			if($(this).is(":checked")){
				$(this).closest(".statisticsRow").remove();
			}
		});
	}
	
	/*
	 * saving the changed detailed statistics data
	 */
	$(document).on("click", ".saveStatisticDetailDataButton", function(){
		
		showLoadingDiv();
		
		var changedStatisticsJSON = makeChangedStatisticsJSON();
		if(changedStatisticsJSON == null){
			return;
		}
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/statistics/save",
	        data : {changedStatisticsJSON: JSON.stringify(changedStatisticsJSON)},
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
	});
	
	/*
	 * add search elements to history (storage)
	 */
	var addSearchElementsToStorage = function(){
		
		localStorage.setItem("statisticsClient", $("input:radio[name='statisticsClientGroup']:checked").val());
		localStorage.setItem("statisticsProduct", $("input:radio[name='statisticsTypeGroup']:checked").val());
		
		localStorage.setItem("statisticsStartDate", $("#statisticsDateFrom").val());
		localStorage.setItem("statisticsEndDate", $("#statisticsDateTo").val());

		if($("#statisticsClientInput").val() != $("#statisticsClientInput").data("default_val")){
			localStorage.setItem("statisticsClientName", $("#statisticsClientInput").val());
		}
		else{
			localStorage.removeItem("statisticsClientName");
		}
		if($("#statisticsCodeInput").val() != $("#statisticsCodeInput").data("default_val")){
			localStorage.setItem("statisticsCode", $("#statisticsCodeInput").val());
		}
		else{
			localStorage.removeItem("statisticsCode");
		}
	};
	
	/*
	 * statistics seach json
	 */
	var makeStatisticsJSON = function(){

		var startDate = $("#statisticsDateFrom").val();
		var endDate = $("#statisticsDateTo").val();

		var code = $("#statisticsCodeInput").val();
		var clientName = $("#statisticsClientInput").val();
		var clientID = $("#clientIDDiv").html();
		
		if(code == $("#statisticsCodeInput").data("default_val")){
			code = "";
		}
		if(clientName == $("#statisticsClientInput").data("default_val")){
			clientName = "";
		}
		
		/*
		 * validate code, client name input
		 */
		if(checkForInvalidStringCharacters(new Array(
				new Array(code,"statisticsCodeInput"),
				new Array(clientName,"statisticsClientInput")
				))){
			return;
		}
		
		
		
		var statisticsJSON = {};
		
		statisticsJSON.statisticsType = $("input:radio[name='statisticsTypeGroup']:checked").val();
		statisticsJSON.dataGroup = $("input:radio[name='statisticsClientGroup']:checked").val();
			
		statisticsJSON.startDate = startDate;
		statisticsJSON.endDate = endDate;
		statisticsJSON.code = code;
		statisticsJSON.clientID = clientID;
		statisticsJSON.clientName = clientName;
		
		return statisticsJSON;
	};
	
	var makeDeleteStatisticsJSON = function(){
		
		var forDeleteStatisticsJSON = "{'statisticObjects':[";
		var wasObjectBefore = false;
		
		$(".statisticsDeleteCheckbox").each(function(){

			if($(this).is(":checked")){
				
				if(wasObjectBefore){
					forDeleteStatisticsJSON += ",";
				}
				forDeleteStatisticsJSON += "{'ID':"+$(this).closest(".statisticsRow").attr("id").replace("statisticsRow","")+"}";
				
				wasObjectBefore = true;
			}
			
		});
		
		forDeleteStatisticsJSON += "]}";
		
		if(!wasObjectBefore){ // there were no objects selected, no need to post
			return;
		}
		
		return forDeleteStatisticsJSON;
	};
	
	/*
	 * make changed statsitics json
	 */
	var makeChangedStatisticsJSON = function(){
		
		var id = $("#statisticDetailIDDiv").html();
		//var code = $("#statisticDetailCodeInput").val();
		var totalPrice = $("#statisticDetailPriceInput").val();
		//var clientName = $("#statisticDetailClientInput").val();
		var amount = $("#statisticDetailAmountInput").val();
		
		/*
		 * check for invalid input
		 *//*
		if(checkForInvalidStringCharacters(new Array(
				new Array(code,"statisticDetailCodeInput"),
				new Array(clientName,"statisticDetailClientInput")
				))){
			return;
		}*/
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"statisticDetailAmountInput"),
				new Array(totalPrice,"statisticDetailPriceInput")
				))){
			return;
		}
		
		var changedStatisticsJSON = {};
		
		changedStatisticsJSON.id = id;
		changedStatisticsJSON.totalPrice = totalPrice;
		//changedStatisticsJSON.code = code;
		changedStatisticsJSON.amount = amount;
		//changedStatisticsJSON.clientName = clientName;
		
		return changedStatisticsJSON;
	};
	
	/*
	 * load the client names on page load
	 */
	$("input:radio[name='statisticsClientGroup']").click();
});

function addNewStatisticsTableWithData(statisticsJSON){
	
	var hasExtraCells = false;
	if(statisticsJSON.statisticsType == "separate"){
		hasExtraCells = true;
	}
	
	var table = document.createElement("table");
	table.setAttribute("id","statisticsTable");

	var headerRow = table.insertRow(0);
	headerRow.className = "tableHeaderRow";
	
	var headerCell1 = headerRow.insertCell(0);
	var headerCell2 = headerRow.insertCell(1);
	var headerCell3 = headerRow.insertCell(2);
	var headerCell4 = headerRow.insertCell(3);
	var headerCell5 = headerRow.insertCell(4);
	
	headerCell1.innerHTML = "Kood";
	headerCell1.className = "statisticsCodeTd tableBorderRight";
	
	headerCell2.innerHTML = "Nimetus";
	headerCell2.className = "statisticsNameTd tableBorderRight";
	
	headerCell3.innerHTML = "Ühik";
	headerCell3.className = "statisticsUnitTd tableBorderRight";
	
	headerCell4.innerHTML = "Kogus";
	headerCell4.className = "statisticsAmountTd tableBorderRight";
	
	headerCell5.innerHTML = "Summa";
	headerCell5.className = "statisticsTotalPriceTd";

	if(hasExtraCells){
		headerCell5.className += " tableBorderRight";
		
		var headerCell6 = headerRow.insertCell(5);
		var headerCell7 = headerRow.insertCell(6);
		var headerCell8 = headerRow.insertCell(7);
		
		headerCell6.innerHTML = "Aeg";
		headerCell6.className = "statisticsDateTd tableBorderRight";
		
		headerCell7.innerHTML = "Klient";
		headerCell7.className = "statisticsClientNameTd tableBorderRight";
		
		headerCell8.innerHTML = "<input type='button' id='deleteSelectedStatistics' value='Kustuta valitud' class='defaultButton' />";
		headerCell8.className = "statisticsDeleteTd";
	}
	
	for(var i = 0; i < statisticsJSON.statisticsObjects.length; i++){
		
		var statObj = statisticsJSON.statisticsObjects[i];
		var statRow = table.insertRow(1);
		var cell1 = statRow.insertCell(0);
		var cell2 = statRow.insertCell(1);
		var cell3 = statRow.insertCell(2);
		var cell4 = statRow.insertCell(3);
		var cell5 = statRow.insertCell(4);
		
		if(hasExtraCells){
			statRow.setAttribute("id","statisticsRow"+statObj.ID);
			statRow.className = "statisticsRow";
		}
		
		cell1.innerHTML = statObj.code;
		cell1.className = "statisticsCodeTd tableBorderRight";
		
		cell2.innerHTML = statObj.name;
		cell2.className = "statisticsNameTd tableBorderRight";
		
		cell3.innerHTML = statObj.unit;
		cell3.className = "statisticsUnitTd tableBorderRight";
		
		cell4.innerHTML = statObj.amount;
		cell4.className = "statisticsAmountTd tableBorderRight";
		
		cell5.innerHTML = statObj.totalPrice;
		cell5.className = "statisticsTotalPriceTd";
		
		if(hasExtraCells){
			cell1.className += " statisticsRowClickable";
			cell2.className += " statisticsRowClickable";
			cell3.className += " statisticsRowClickable";
			cell4.className += " statisticsRowClickable";
			cell5.className += " tableBorderRight statisticsRowClickable";
			
			var cell6 = statRow.insertCell(5);
			var cell7 = statRow.insertCell(6);
			var cell8 = statRow.insertCell(7);
			
			cell6.innerHTML = statObj.date;
			cell6.className = "statisticsDateTd tableBorderRight statisticsRowClickable";
			
			cell7.innerHTML = statObj.clientName;
			cell7.className = "statisticsClientNameTd tableBorderRight statisticsRowClickable";
			
			cell8.innerHTML = "<label class='statisticsDeleteCheckboxLabel'><input class='statisticsDeleteCheckbox' type='checkbox'/></label>";
			cell8.className = "statisticsDeleteTd";
		}
		
	}
	
	document.getElementById("statisticsTableDiv").appendChild(table);

}

function addDetailedStatisticsDataRow(rowIndex,statObjJSON){

	var table = document.getElementById("statisticsTable");
	
	var row = table.insertRow(rowIndex+1);
	var cell=row.insertCell(0);
	
	cell.innerHTML = "<div class='statisticDetailedDataDiv'>" +

		"<div id='statisticDetailIDDiv' class='hidden'>"+statObjJSON.ID+"</div>"+
		/*
		"<div class='statisticDetailDataPieceDiv'>" +
			"<span class='statisticDetailCodeDiv statisticsDetailedNameDiv'>Kood</span>" +
			"<input type='text' maxlength='45' id='statisticDetailCodeInput' class='statisticsDetailedInput' value='"+statObjJSON.code+"' />" +
		"</div>" +
		 */
		"<div class='statisticDetailDataPieceDiv'>" +
			"<span class='statisticDetailAmountDiv statisticsDetailedNameDiv'>Kogus</span>" +
			"<input type='text' id='statisticDetailAmountInput' class='statisticsDetailedInput' value='"+statObjJSON.amount+"' />" +
		"</div>" +

		"<div class='statisticDetailDataPieceDiv'>" +
			"<span class='statisticDetailTotalPriceDiv statisticsDetailedNameDiv'>Summa</span>" +
			"<input type='text' id='statisticDetailPriceInput' class='statisticsDetailedInput' value='"+statObjJSON.totalPrice+"' />" +
		"</div>" +
		/*
		"<div class='statisticDetailDataPieceDiv'>" +
			"<span class='statisticDetailClientDiv statisticsDetailedNameDiv'>Klient</span>" +
			"<input type='text' maxlength='45' id='statisticDetailClientInput' class='statisticsDetailedInput' value='"+statObjJSON.clientName+"' />" +
		"</div>"+
		*/
		"<input type='button' class='saveStatisticDetailDataButton defaultButton' value='Salvesta' />"+
		"<input type='button' class='closeDetailDiv defaultButton' value='Sulge' />"+
		
	"</div>";
			
	cell.colSpan = "8";
	cell.className = "statisticsDetailedDataRow";
	
	row.className = "detailedTr";
	
}