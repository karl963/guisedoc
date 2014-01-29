package com.guisedoc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

public class UserValidator {
	
	public static boolean validateLoggedUser(HttpSession session){
		if(session.getAttribute("user") == null){
			return true;
		}
		
		return false;
	}
	
	public static Object directToLogin(String contextPath, RedirectAttributes redirectAttributes){
		redirectAttributes.addFlashAttribute("errorMessage", "Palun logige sisse");
		return new RedirectView(contextPath+"/login");
	}

}
