$(document).ready(function(){
	
	/*
	 * Changing shown language fields
	 */
	$("#productsInEnglish").click(function() {
		showLoadingDiv();
		
		$(".productEstonianDiv").hide();
		$(".productEnglishDiv").show();
		
		$("#productsInEnglish").attr("class","selectedLanguageButton");
		$("#productsInEstonian").attr("class","defaultButton");
		
		isEstonian = false;
		
		hideLoadingDiv();
	});
	
	$("#productsInEstonian").click(function() {
		showLoadingDiv();
		
		$(".productEnglishDiv").hide();
		$(".productEstonianDiv").show();
		
		$("#productsInEstonian").attr("class","selectedLanguageButton");
		$("#productsInEnglish").attr("class","defaultButton");
		
		isEstonian = true;
		
		hideLoadingDiv();
	});

	/*
	 * Put default value for each input field
	 */
	$('.defaultInputField, input[type="password"]').each(function(i, obj) {
		$($(this)).data("default_val", $($(this)).val());
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
var closeDetailedDataDiv = function(tableID, callback){
	$('#'+tableID+' > tbody > tr.detailedTr')
 	.find('td')
 	.wrapInner('<div style="display: block;" />')
 	.parent()
 	.find('td > div')
 	.slideUp(200, function(){
 		if(callback != null){ // if we added a function to execute after
 			callback();
 		}
 	});
};

/*
 * open the detailed data div
 */
var openDetailedDataDiv = function(tableID, callback){
	$('#'+tableID+' > tbody > tr.detailedTr')
	.find('td')
	 .wrapInner('<div style="display: none;" />')
	 .parent()
	 .find('td > div')
	 .slideDown(600, function(){
 		if(callback != null){ // if we added a function to execute after
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
							" ,.-;:_-<>!?/1234567890+'*";
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