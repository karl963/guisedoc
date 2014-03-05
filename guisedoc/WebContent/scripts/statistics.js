$(document).ready(function() {

	var clientType = "";
	var startDate = "";
	var endDate = "";
	var clientName = "";
	var code = "";
	
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
		$("#clientIDDiv").html(localStorage.getItem("statisticsClientID")); // for change of default input class
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
	 * downloading the pdf
	 *************************************************************/
	$(document).on("click", "#downloadPDFOfStatistics", function getPDF(){
		$.cookie("statisticsDownload","false",{ expires: 1, path: contextPath+"/statistics/pdf" });

		showSuccessNotification("Teie dokument laetakse alla mõne sekundi pärast!");
		showLoadingDiv();

		var path = contextPath+"/statistics/pdf/download";

		var json = makeStatisticsJSON();
		if(json == null){
			return;
		}
		
		path += "?statisticsJSON="+JSON.stringify(json);
		
		window.location.href = path;

		waitForDownloadStart();
	});
	
	$(document).on("click", "#viewPDFOfStatistics", function getPDF(){
		showSuccessNotification("Teie dokument avatakse mõne sekundi pärast!");
		
		var path = contextPath+"/statistics/pdf/view";
		
		var json = makeStatisticsJSON();
		if(json == null){
			return;
		}
		
		path += "?statisticsJSON="+JSON.stringify(json);
		
		window.open(path,"_blank");
	});
	
	/*
	 * check every 200 ms if the download is starting
	 */
	/*
	 * check every 200 ms if the download is starting
	 */
	var waitForDownloadStart = function() {
		var cookieVal = $.cookie("statisticsDownload");

	    if(cookieVal == "false") {
	        setTimeout(function(){ waitForDownloadStart(); }, 200);
	    }else{
	    	$.cookie("statisticsDownload",null,{ expires: 0, path:contextPath+"/statistics/pdf" });
	    	hideLoadingDiv();
	    }
	    return false;
	};

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
		var statisticsJSON = makeStatisticsJSON();
		if(statisticsJSON == null){
			return;
		}
		
		addSearchElementsToStorage();
		
		$.ajax({
	        type : "POST",
	        url : contextPath+"/statistics/search",
	        data : {statisticsJSONString: JSON.stringify(statisticsJSON)},
	        success : function(response) {

	        	var responseJSON = jQuery.parseJSON(response);

	        	if(responseJSON.response=="success"){
	        		showSuccessNotification(responseJSON.message);
	        		addNewStatisticsTableWithData(responseJSON);
	        		
	        		clientType = statisticsJSON.dataGroup;
	        		startDate = statisticsJSON.startDate;
	        		endDate = statisticsJSON.endDate;
	        		clientName = statisticsJSON.clientName;
	        		code = statisticsJSON.code;
	        		
	        		makeSortable("statisticsTable");
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
	
	/*************************************************************
	 * CLIENT NAME SELECTION
	 *************************************************************/
	/*
	 * changed the client type, load the new names
	 */
	$(document).on("click","input:radio[name='statisticsClientGroup']",function(){
		
		showLoadingDiv();
		
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
			.append("<option value='0' >-- kõik kliendid --</option>")
			.val("0");
		
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
		//$("#statisticsClientInput").focus();
		$("#statisticsClientInput").val($("#clientsSelectBox option:selected").text());
	});
	
	/*************************************************************
	 * OBJECTS HANDLING
	 *************************************************************/
	/*
	 * delete selected statistics objects
	 */
	$(document).on("click","#deleteSelectedStatistics",function(){
		showConfirmationDialog("Kustuta valitud statistika objektid ?"
				,deleteStatistics);
	});
	var deleteStatistics = function(){
		
		if(allowedDeleteStatistics == "false"){
			showErrorNotification(permissionDeniedMessage);
			return;
		}
		
		showLoadingDiv();
		
		if($(".detailedTr").length != 0){// if we have an opened row already, then close it
			
			closeDetailedDataDiv("statisticsTable", function(){
				$(".detailedTr").remove(); // remove the old row
			});

		}
		
		var forDeleteStatisticsJSON = makeDeleteStatisticsJSON();
		if(forDeleteStatisticsJSON == null){
			hideLoadingDiv();
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
	};
	
	/*
	 * click on row, open detailed data
	 */
	$(document).on("click",".statisticsRowClickable", function(){
		
		showLoadingDiv();
		
		var rowIndex = $(this).closest(".statisticsRow").index()+1; // the row we clicked on
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
		
		var index = $(this).closest(".detailedTr").index();

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
	        		changeRegularDataRow(changedStatisticsJSON,index);
	        		showSuccessNotification(response.split(";")[1]);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
	        },
	        error : function(e) {
	        	showErrorNotification("Viga serveriga ühendumisel");
	        }
	    });
	});
	
	/*
	 * changes the regular data row in table
	 */
	var changeRegularDataRow = function(object,index){
		var row = $("#statisticsTable").children("tbody").children("tr").eq(index-1);

		row.children(".statisticsAmountTd").html(parseFloat(object.amount).toFixed(2));
		row.children(".statisticsTotalPriceTd").html(parseFloat(object.totalPrice).toFixed(2));
	};
	
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
			localStorage.setItem("statisticsClientID", $("#clientIDDiv").html());
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
	
	/*
	 * deleteable objects json
	 */
	var makeDeleteStatisticsJSON = function(){
		var objects = [];
		
		$(".statisticsDeleteCheckbox").each(function(){

			if($(this).is(":checked")){

				var object = {};
				object.ID = $(this).closest(".statisticsRow").attr("id").replace("statisticsRow","");
				
				objects.push(object);
			}
			
		});

		if(objects.length == 0){ // there were no objects selected, no need to post
			return;
		}

		return objects;
	};
	
	/*
	 * make changed statsitics json
	 */
	var makeChangedStatisticsJSON = function(){
		
		var id = $("#statisticDetailIDDiv").html();
		var totalPrice = $("#statisticDetailPriceInput").val();
		var amount = $("#statisticDetailAmountInput").val();
		
		/*
		 * check for invalid input
		 */
		if(checkForInvalidNumberCharacters(new Array(
				new Array(amount,"statisticDetailAmountInput"),
				new Array(totalPrice,"statisticDetailPriceInput")
				))){
			return;
		}
		
		var changedStatisticsJSON = {};
		
		changedStatisticsJSON.ID = id;
		changedStatisticsJSON.totalPrice = totalPrice;
		changedStatisticsJSON.amount = amount;
		
		return changedStatisticsJSON;
	};
	
	/*
	 * load the client names on page load
	 */
	$("input:radio[name='statisticsClientGroup']").each(function(){
		if($(this).is(":checked")){
			$(this).click();
			return false;
		}
	});
	
	/*
	 * load statistics on page open if setting is set true
	 */
	if(loadStatisticsOnOpen){
		$("#searchForStatistics").trigger("click");
	}
});

function addNewStatisticsTableWithData(statisticsJSON){
	
	var hasExtraCells = false;
	if(statisticsJSON.statisticsType == "separate"){
		hasExtraCells = true;
	}
	
	var table = document.createElement("table");
	table.setAttribute("id","statisticsTable");
	table.createTHead();
	table.createTBody();

	var headerRow = table.getElementsByTagName("thead")[0].insertRow(0);
	headerRow.className = "tableHeaderRow";
	
	var headerCell1 = document.createElement("th");
	var headerCell2 = document.createElement("th");
	var headerCell3 = document.createElement("th");
	var headerCell4 = document.createElement("th");
	var headerCell5 = document.createElement("th");
	
	headerRow.appendChild(headerCell1);
	headerRow.appendChild(headerCell2);
	headerRow.appendChild(headerCell3);
	headerRow.appendChild(headerCell4);
	headerRow.appendChild(headerCell5);
	
	headerCell1.innerHTML = "Kood";
	headerCell1.className = "statisticsCodeTd tableBorderRight";
	
	headerCell2.innerHTML = "Nimetus";
	headerCell2.className = "statisticsNameTd tableBorderRight";
	
	headerCell3.innerHTML = "Ühik";
	headerCell3.className = "statisticsUnitTd tableBorderRight";
	
	headerCell4.innerHTML = "Kogus";
	headerCell4.className = "statisticsAmountTd tableBorderRight numberColumn";
	
	headerCell5.innerHTML = "Summa";
	headerCell5.className = "statisticsTotalPriceTd numberColumn";

	if(hasExtraCells){
		headerCell5.className += " tableBorderRight";
		
		var headerCell6 = document.createElement("th");
		var headerCell7 = document.createElement("th");
		var headerCell8 = document.createElement("th");
		headerRow.appendChild(headerCell6);
		headerRow.appendChild(headerCell7);
		headerRow.appendChild(headerCell8);
		
		headerCell6.innerHTML = "Aeg";
		headerCell6.className = "statisticsDateTd tableBorderRight dateColumn";
		
		headerCell7.innerHTML = "Klient";
		headerCell7.className = "statisticsClientNameTd tableBorderRight";
		
		if(allowedDeleteStatistics == "true"){
			headerCell8.innerHTML = "<input type='button' id='deleteSelectedStatistics' value='Kustuta' class='defaultButton' />";
		}
		headerCell8.className = "statisticsDeleteTd";
	}
	
	for(var i = 0; i < statisticsJSON.statisticsObjects.length; i++){
		
		var statObj = statisticsJSON.statisticsObjects[i];
		var statRow = table.getElementsByTagName("tbody")[0].insertRow(0);
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
		
		cell4.innerHTML = parseFloat(statObj.amount).toFixed(2);
		cell4.className = "statisticsAmountTd tableBorderRight alignRightTd";
		
		cell5.innerHTML = parseFloat(statObj.totalPrice).toFixed(2);
		cell5.className = "statisticsTotalPriceTd alignRightTd";
		
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
	
	var saveButton = "";
	
	if(allowedChangeStatistics == "true"){
		saveButton = "<input type='button' class='saveStatisticDetailDataButton defaultButton' value='Salvesta' />";
	}
	
	cell.innerHTML = "<div class='statisticDetailedDataDiv'>" +

		"<div id='statisticDetailIDDiv' class='hidden'>"+statObjJSON.ID+"</div>"+

		"<div class='statisticDetailDataPieceDiv'>" +
			"<span class='statisticDetailAmountDiv statisticsDetailedNameDiv'>Kogus</span>" +
			"<input type='text' id='statisticDetailAmountInput' class='statisticsDetailedInput' value='"+parseFloat(statObjJSON.amount).toFixed(2)+"' />" +
		"</div>" +

		"<div class='statisticDetailDataPieceDiv'>" +
			"<span class='statisticDetailTotalPriceDiv statisticsDetailedNameDiv'>Summa</span>" +
			"<input type='text' id='statisticDetailPriceInput' class='statisticsDetailedInput' value='"+parseFloat(statObjJSON.totalPrice).toFixed(2)+"' />" +
		"</div>" +

		saveButton +
		"<input type='button' class='closeDetailDiv defaultButton' value='Sulge' />"+
		
	"</div>";
			
	cell.colSpan = "8";
	cell.className = "statisticsDetailedDataRow";
	
	row.className = "detailedTr";
	
}