$(document).ready(function(){
	
	/*
	 * Changing shown language fields
	 */
	$("#productsInEnglish").click(function() {
		showLoadingDiv();
		
		$(".productEstonianDiv").hide();
		$(".productEnglishDiv").show();
		
		try{
			if(getCurrentDocType() == "order"){
				$(".regularPriceDiv").hide();
				$(".orderPriceDiv").show();
			}
			else{
				$(".regularPriceDiv").show();
				$(".orderPriceDiv").hide();
			}
		}catch(err){}
		
		$("#productsInEnglish").attr("class","selectedLanguageButton");
		$("#productsInEstonian").attr("class","defaultButton");
		
		isEstonian = false;
		
		hideLoadingDiv();
	});
	
	$("#productsInEstonian").click(function() {
		showLoadingDiv();
		
		$(".productEnglishDiv").hide();
		$(".productEstonianDiv").show();
		
		try{
			if(getCurrentDocType() == "order"){
				$(".regularPriceDiv").hide();
				$(".orderPriceDiv").show();
			}
			else{
				$(".regularPriceDiv").show();
				$(".orderPriceDiv").hide();
			}
		}catch(err){}
		
		$("#productsInEstonian").attr("class","selectedLanguageButton");
		$("#productsInEnglish").attr("class","defaultButton");
		
		isEstonian = true;
		
		hideLoadingDiv();
	});

	/*
	 * Put default value for each input field
	 */
	$('.defaultInputField, input[type="password"]').each(function(i, obj) {
		$(this).data("default_val", $(this).val());
	});
	
	/*
	 * On click, if there was an error in input field, it's invalid class notifier would be removed
	 */
	$(document).on("focus","input, textarea", function(){
		if($(this).hasClass("invalidInputCharacter")){
			$(this).removeClass("invalidInputCharacter");
		}
	});
	
	/*
	 * NOTIFICATION dissapear on load and trigger and then hide, for use of objects behind it
	 */
	$("#notificationDiv").ready(function() {
		  $(this).trigger("hideMe");
	});
	
	$(document).on("hideMe", "#notificationDiv", function() {
		$("#notificationDiv").stop();
		$("#notificationDiv").show( function(){
			$("#notificationDiv").animate({opacity: 0.0,}, 7000 , function(){
				$("#notificationDiv").hide();
			});
		});
	});
	
	/*
	 * AUTOCHANGE on click and revert back to default on null input
	 */
    $(document).on("focus", '.searchInputField', function() {
        if($(this).val() == $(this).data('default_val') || !$(this).data('default_val')) {
            $(this).data('default_val', $(this).val());
            $(this).val('');
            $(this).removeClass("defaultInputField");
        }
    });
    
    $(document).on("blur", '.searchInputField', function() {
        if ($(this).val() == ''){
        	$(this).val($(this).data('default_val'));
        	$(this).addClass("defaultInputField");
        }
    });
});

/*
 * closing the detailed data div
 */
var closeDetailedDataDiv = function(tableID, callback, rowName){
	if(rowName == undefined){
		rowName = "detailedTr";
	}
	$('#'+tableID+' > tbody > tr.'+rowName)
 	.find("td").eq(0)
 	.wrapInner('<div style="display: block;" />')
 	.parent()
 	.find('td > div').eq(0)
 	.slideUp(200, function(){
 		if(callback != undefined){ // if we added a function to execute after
 			callback();
 		}
 	});
};

/*
 * open the detailed data div
 */
var openDetailedDataDiv = function(tableID, callback,rowName){
	if(rowName == undefined){
		rowName = "detailedTr";
	}
	$('#'+tableID+' > tbody > tr.'+rowName)
	.find('td').eq(0)
	 .wrapInner('<div style="display: none;" />')
	 .parent()
	 .find('td > div').eq(0)
	 .slideDown(600, function(){
 		if(callback != undefined){ // if we added a function to execute after
 			callback();
 		}
 	});
};

/*
 * Loading div
 */

function hideLoadingDiv(){
	$("#fullPageLoadingDiv").hide();
}

function showLoadingDiv(){
	$("#fullPageLoadingDiv").show();
}

/*
* SHOW NOTIFICATION
*/

function showErrorNotification(message){
	document.getElementById("notificationDiv").style.display = "block";
	document.getElementById("notificationDiv").innerHTML = message;
	document.getElementById("notificationDiv").className = "negativeNotification";
	document.getElementById("notificationDiv").style.opacity = "1.0";
	document.getElementById("notificationDiv").style.filter  = 'alpha(opacity=100)';
	hideLoadingDiv();
	$("#notificationDiv").trigger("hideMe");
}

function showSuccessNotification(message){
	document.getElementById("notificationDiv").style.display = "block";
	document.getElementById("notificationDiv").innerHTML = message;
	document.getElementById("notificationDiv").className = "positiveNotification";
	document.getElementById("notificationDiv").style.opacity = 1.0;
	document.getElementById("notificationDiv").style.filter  = 'alpha(opacity=100)';
	hideLoadingDiv();
	$("#notificationDiv").trigger("hideMe");
}

/*
 * Valid characters for string and numbers
 */
var validStringCharacters = "qwertyuiopüõäölkjhgfdsazxcvbnm" +
							"QWERTYUIOPÜÕÄÖLKJHGFDSAMNBVCXZ" +
							" ,.-;:_-<>!?/1234567890+'*@()";
var validNumberCharacters = "1234567890.,";

/*
* Invalid characters check, return true if there are any
* for false character, change the given ID field class
* INPUT: array consists of arrays {string,fieldID}
*/
function checkForInvalidStringCharacters(array){
	
	var hadInvalidCharacter = false;
	
	for(var i = 0; i < array.length ; i++){
		
		var string = array[i][0];
		var elementId = array[i][1];
		
		for(var j = 0; j < validStringCharacters.length ; j++){
			
			var char = string.charAt(j);

			if(validStringCharacters.indexOf(char) < 0 && char != "\n"){ // doesn't find a character in valid list nad isn't newLine
				hadInvalidCharacter = true;
				
				giveInvalidInputMessage(elementId,"Sisendväli sisaldab keelatud või üleliigset karakterit: "+char);

				break;break;
			}
		}
		
	}

	return hadInvalidCharacter;
}

/*
* Invalid characters check, return true if there are any
* for false character, change the given ID field class
* INPUT: array consists of arrays {string,fieldID}
*/
function checkForInvalidNumberCharacters(array){
	
	var hadInvalidCharacter = false;
	
	for(var i = 0; i < array.length ; i++){
		
		var string = array[i][0];
		var elementId = array[i][1];
		
		if(!isNaN(string)){
			continue;
		}
		
		for(var j = 0; j < validNumberCharacters.length ; j++){
			
			var char = string.charAt(j);

			if(validNumberCharacters.indexOf(char) < 0){ // doesn't find a character in valid list

				hadInvalidCharacter = true;
				
				giveInvalidInputMessage(elementId,"Sisendväli sisaldab keelatud või üleliigset karakterit: "+char);
				
				hideLoadingDiv();
				break;break;
			}
		}
	}
	
	return hadInvalidCharacter;
}

function giveInvalidInputMessage(elementID, message){
	if(!((' ' + document.getElementById(elementID).className + ' ').indexOf(' ' + "invalidInputCharacter" + ' ') > -1)){// if it doesn't already have the class
		document.getElementById(elementID).className += " invalidInputCharacter";
	}
	showErrorNotification(message);
	
	hideLoadingDiv();
}

/*
* SORTING TABLES
*/
var makeSortable = function(tableID){
	$("#"+tableID+" > thead > tr > th").each(function(){
		if($(this).children("input").length == 0){
			$(this).addClass("headerSortable");
		}
	});
};

$(document).ready(function(){
	
	var sortableIndexes = [];
	var sortableValues = [];
	var rowsArray = [];
	var sortUp = false;
	
	// sort up
	$(document).on("click",".headerSortDown, .headerSortable",function(){
		sortUp = false;

		if($(this).closest("[class^='detailed']").length == 0){ // if we didn't have the click in the detailed div
			$("[class^='detailed']").remove();
		}
		var tableID = $(this).closest("table").attr("id");
		var columnIndex = $(this).index();

		makeAllHeadersSortable(tableID);

		$(this).removeClass("headerSortable");
		$(this).addClass("headerSortUp");
		
		// check for the column type
		if($(this).hasClass("dateColumn")){
			makeIndexesAndValuesDates(tableID,columnIndex);
		}
		else if($(this).hasClass("numberColumn")){
			makeIndexesAndValuesNumbers(tableID,columnIndex);
		}
		else{
			makeIndexesAndValues(tableID,columnIndex);
		}

		addSortedToTable(tableID,mergeSort(sortableIndexes));
	});
	
	// sort down
	$(document).on("click",".headerSortUp",function(){
		sortUp = true;

		if($(this).closest("[class^='detailed']").length == 0){ // if we didn't have the click in the detailed div
			$("[class^='detailed']").remove();
		}
		var tableID = $(this).closest("table").attr("id");
		var columnIndex = $(this).index();

		makeAllHeadersSortable(tableID);
		
		$(this).removeClass("headerSortable");
		$(this).addClass("headerSortDown");
		
		// check for the column type
		if($(this).hasClass("dateColumn")){
			makeIndexesAndValuesDates(tableID,columnIndex);
		}
		else if($(this).hasClass("numberColumn")){
			makeIndexesAndValuesNumbers(tableID,columnIndex);
		}
		else{
			makeIndexesAndValues(tableID,columnIndex);
		}
		
		addSortedToTable(tableID,mergeSort(sortableIndexes));
	});
	
	/*
	 * changes the classes of headers to headerSortable
	 */
	var makeAllHeadersSortable = function(tableID){
		$("#"+tableID+" > thead > tr > th").each(function(){
			$(this).removeClass("headerSortUp");
			$(this).removeClass("headerSortDown");
			if($(this).find("input").length == 0){
				$(this).addClass("headerSortable");
			}
		});
	};
	
	/*
	 * finds the first visible text in the TD
	 */
	var findFirstVisibleString = function(tableID,rowIndex,columnIndex){
		if($("#"+tableID+" > tbody > tr").eq(rowIndex).find("td").eq(columnIndex).find("div").length > 0){
			return $("#"+tableID+" > tbody > tr").eq(rowIndex).find("td").eq(columnIndex).find("div").filter(":visible").eq(0).html().trim();
		}
		
		return $("#"+tableID+" > tbody > tr").eq(rowIndex).find("td").eq(columnIndex).html().trim();
	};
	
	/*
	 * cleans arrays and adds the table data
	 */
	var makeIndexesAndValues = function(tableID,columnIndex){
		var sortIndex = 0;
		$("#"+tableID+" > tbody > tr").each(function(){
			rowsArray.push($(this).clone());
			sortableValues.push(findFirstVisibleString(tableID,sortIndex,columnIndex));
			
			sortableIndexes.push(sortIndex);
			sortIndex++;
		});
		
		$("#"+tableID+" > tbody > tr").remove();
	};
	
	var makeIndexesAndValuesDates = function(tableID,columnIndex){
		var sortIndex = 0;
		$("#"+tableID+" > tbody > tr").each(function(){
			rowsArray.push($(this).clone());
			sortableValues.push(parseDate(findFirstVisibleString(tableID,sortIndex,columnIndex)));
			
			sortableIndexes.push(sortIndex);
			sortIndex++;
		});
		
		$("#"+tableID+" > tbody > tr").remove();
	};
	
	var makeIndexesAndValuesNumbers = function(tableID,columnIndex){
		var sortIndex = 0;
		$("#"+tableID+" > tbody > tr").each(function(){
			rowsArray.push($(this).clone());
			sortableValues.push(parseFloat(findFirstVisibleString(tableID,sortIndex,columnIndex)));
			
			sortableIndexes.push(sortIndex);
			sortIndex++;
		});
		
		$("#"+tableID+" > tbody > tr").remove();
	};
	
	/*
	 * adds the sorted data back to the table
	 */
	var addSortedToTable = function(tableID,indexes){
		while(indexes.length > 0){
			var row = rowsArray[indexes.shift()];
			$("#"+tableID+" tbody").append(row);
		}
		
		// clean all arrays
		sortableIndexes.length = 0;
		sortableValues.length = 0;
		rowsArray.length = 0;
	};
	
	/**************************************
	**** MERGE SORT
	**************************************/

	var mergeSort = function(array){
	    if (array.length < 2) return array;
	 
	    var middle = parseInt(array.length / 2);
	    var left = array.slice(0, middle);
	    var right = array.slice(middle, array.length);

	    return merge(mergeSort(left), mergeSort(right));
	};
	 
	var merge = function(left, right){

	    var result = [];

	    while (left.length && right.length) {
	    	if(sortUp){
		        if(sortableValues[left[0]] <= sortableValues[right[0]]) { // <=
		            result.push(left.shift());
		        }
		        else{
		            result.push(right.shift());
		        }
	    	}
	    	else{
		        if(sortableValues[left[0]] > sortableValues[right[0]]) { // >
		            result.push(left.shift());
		        }
		        else{
		            result.push(right.shift());
		        }
	    	}
	    }
	 
	    while (left.length) result.push(left.shift());
	 
	    while (right.length) result.push(right.shift());
	 
	    return result;
	};
});

/*
 * checks if the input is a number
 */
function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

/*
* takes input string date and gives the date output
* inut as day.month.year, output new date
*/
function parseDate(input) {
	var parts = input.split('.');
	return new Date(parts[2], parts[1]-1, parts[0]);
}

/*
* shows the confirmation dialog to the user
*/
var showConfirmationDialog = function(message, callback, attribute){
	$("#confirmationYesButton").off("click");
	$("#confirmationNoButton").off("click");
	
	$("#confirmationYesButton").on("click",function(){
		$("#confirmationDiv").hide();
		callback(attribute);
	});
	$("#confirmationNoButton").on("click",function(){
		$("#confirmationDiv").hide();
		return false;
	});
	
	$("#confirmationTextDiv").html(message);
	$("#confirmationDiv").show();
};