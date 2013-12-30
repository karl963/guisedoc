
$(document).ready(function() {

	/*
	 * Redirecting the user to the selected link
	 */
	$(document).on("click",".mainMenuLink", function() {
		window.location.href = contextPath+"/"+$(this).attr("id");
	});
	$(document).on("click",".mainMenuBigLink", function() {
		window.location.href = contextPath+"/"+$(this).children("a").attr("id");
	});
	
	/*
	 * making the selected link active
	 */
	if(activeLink != null){
		
		var linkID = "";
		
		if(activeLink == "documents"){
			linkID = "menuBigLink1";
		}
		else if(activeLink == "manage-products" || activeLink == "clients" ||
				activeLink == "manage-user-data"){
			linkID = "menuBigLink2";
		}
		else if(activeLink == "statistics"){
			linkID = "menuBigLink3";
		}
		
		$(".topMenu").removeClass("selectedMenuLink");
		$(".topMenu").addClass("defaultMenuLink");
		
		$("#"+linkID).removeClass("defaultMenuLink");
		$("#"+linkID).addClass("selectedMenuLink");
	}
	
});