package com.guisedoc.controller.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginGet {

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public Object getLoginView(HttpSession session,
			Model model){
		
		if(session.getAttribute("user") != null){
			return "documents";
		}
		
		model.addAttribute("pageRequest","login");
		
		return "login";
	}
	
}
