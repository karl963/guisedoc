$(document).ready(function(){
	
	/*
	 * change pictures when mouse over list
	 */
	/*
	$(document).on("mouseover",".listElement",function(){
		$(".commercialImage:not(#"
				+$(this).attr("id").replace("img_","")+")").fadeOut(1000);
	});
	$(document).on("mouseover",".listElement",function(){
		$("#"+$(this).attr("id").replace("img_","")).fadeIn(1000);
	});
	*/
	var autoSwapImages = function(){
		var index = 1;
		
		for(var i = 1; i <= 7 ; i++){
			var url = contextPath+"/images/"+i+".png";
			$("#image"+i).css("background", "-webkit-gradient(linear, left top, left bottom, color-stop(100%,rgba(0,0,0,0.65)), color-stop(59%,rgba(0,0,0,0)), color-stop(0%,rgba(0,0,0,0))), url('"+url+"') center center no-repeat"); /* Saf4+, Chrome */
			$("#image"+i).css("background", "-webkit-linear-gradient(to top, rgba(0,0,0,0) 0%,rgba(0,0,0,0) 59%,rgba(0,0,0,0.65) 100%), url('"+url+"') center center no-repeat"); /* Chrome 10+, Saf5.1+ */
			$("#image"+i).css("background", "-moz-linear-gradient(to top, rgba(0,0,0,0) 0%,rgba(0,0,0,0) 59%,rgba(0,0,0,0.65) 100%), url('"+url+"') center center no-repeat"); /* FF3.6+ */
			$("#image"+i).css("background", "-ms-linear-gradient(to top, rgba(0,0,0,0) 0%,rgba(0,0,0,0) 59%,rgba(0,0,0,0.65) 100%), url('"+url+"') center center no-repeat"); /* IE10 */
			$("#image"+i).css("background", "-o-linear-gradient(to top, rgba(0,0,0,0) 0%,rgba(0,0,0,0) 59%,rgba(0,0,0,0.65) 100%), url('"+url+"') center center no-repeat"); /* Opera 11.10+ */
			$("#image"+i).css("background", "linear-gradient(to top, rgba(0,0,0,0) 0%,rgba(0,0,0,0) 59%,rgba(0,0,0,0.65) 100%), url('"+url+"') center center no-repeat"); /* W3C */
		}
		
		window.setInterval(function(){

			$("#image"+index).fadeOut(2000);
			if(index == 7){
				index = 1;
			}
			
			index++;
			$("#image"+index).fadeIn(2500);
		}, 5000);
	};
	autoSwapImages();
	
	/*
	 * show register form
	 */
	$(document).on("click","#showRegister",function(){
		$(this).hide();
		$("#registerResponseDiv").hide();
		$("#formDiv").slideDown(600);
	});
	
	/*
	 * listen on window resize for login div adjusting
	 */
	$(window).resize(function(){
		$("#loginFormDiv").css("top",(
				$("#commercialDiv").height()+20 // default padding is 10, top+bottom=20
			)+"px");
	});
	
	/*
	 * keyboard enter press for login
	 */
	$(document).on("keypress","#inputUsername, #inputPassword",function(e){
		if(e.which == 13){
			$("#loginButton").click();
		}
	});
	
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
					if(response.split(";")[1] != null){
						// set the whine cookie
						$.cookie.raw = true;
						var date = new Date();
						date.setTime(date.getTime() + ( 30*24*60*60*1000)); // 30 days
						$.cookie("whine",makeNewWhine(),{path:contextPath ,expires : date});
						
						window.location.href = contextPath+"/"+response.split(";")[1];
					}
					else{
						window.location.href = contextPath+"/documents";
					}
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
	
	$(window).trigger("resize");
});

var makeNewWhine = function(){
	var pieces = "abcdefghijklmnopqrstuvwxyz";
	var whine = "";
	while(whine.length < 10 ){
		whine += pieces.charAt(Math.floor((Math.random()*25)));
	}
	return whine;
};