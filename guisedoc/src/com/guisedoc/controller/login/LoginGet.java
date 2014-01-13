package com.guisedoc.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginGet {

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String loginView(Model model){
		
		model.addAttribute("pageRequest","login");
		
		return "login";
	}
	
}
