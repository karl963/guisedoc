
$(document).ready(function() {

	/*
	 * Redirecting the user to the selected link
	 */
	$(document).on("click",".mainMenuLink", function() {
		localStorage.setItem("activeLinkId",$(this).closest(".topMenu").attr("id"));
		window.location.href = contextPath+"/"+$(this).attr("id");
	});
	$(document).on("click",".mainMenuBigLink", function() {
		localStorage.setItem("activeLinkId",$(this).attr("id"));
		window.location.href = contextPath+"/"+$(this).children("a").attr("id");
	});
	
	if(localStorage.getItem("activeLinkId") != null){
		$(".topMenu").removeClass("selectedMenuLink");
		$(".topMenu").addClass("defaultMenuLink");
		
		$("#"+localStorage.getItem("activeLinkId")).removeClass("defaultMenuLink");
		$("#"+localStorage.getItem("activeLinkId")).addClass("selectedMenuLink");
	}
	
});