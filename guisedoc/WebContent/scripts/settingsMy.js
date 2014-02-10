$(document).ready(function(){
	
	/*
	 * save a setting on click
	 */
	$(document).on("change",".settingsInput",function(){
		
		showLoadingDiv();
		
		var key = $(this).attr("id").replace("Setting","");
		var value = $(this).is(":checked");
		var whine = $.cookie("whine");
		if(whine == undefined){
			whine = "";
		}

		$.ajax({
			type : "POST",
			url : contextPath+"/settingsMy/save",
			data : {key:key, value:value,whine:whine},
			success : function(response){
	        	if(response.split(";")[0] =="success"){
	        		showSuccessNotification(response.split(";")[1]);
	        	}
	        	else{
	        		showErrorNotification(response.split(";")[1]);
	        	}
			},
			error : function(err){
				showErrorNotification("Viga serveriga ühendumisel");
			}
		});
	});

});