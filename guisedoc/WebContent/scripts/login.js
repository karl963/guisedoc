$(document).ready(function(){
	
	/*
	 * user logs in
	 */
	$(document).on("click","#loginButton",function(){
		
		showLoadingDiv();
		
		var username = $("#inputUsername").val();
		var password = $("#inputPassword").val();
		
		if(username == $("#inputUsername").data("default_val")){
			giveInvalidInputMessage("inputUsername","Palun sisestage kasutajanimi !");
			return;
		}
		if(password == $("#inputPassword").data("default_val")){
			giveInvalidInputMessage("inputPassword","Palun sisestage parool !");
			return;
		}
		
		$.ajax({
			type : "POST",
			url : contextPath+"/login/doLogin",
			data : {username:username,password:password},
			success : function(response){

				if(response.split(";")[0]=="success"){
					window.location.href = contextPath+"/documents";
				}
				else if(response.split(";")[0]=="wrong"){
					
					showErrorNotification(response.split(";")[1]);
				}
				else{
					showErrorNotification(response.split(";")[1]);
				}
			},
			error : function(e){
	        	showErrorNotification("Viga serveriga ühendumisel");
			}
		});
	});
	
});