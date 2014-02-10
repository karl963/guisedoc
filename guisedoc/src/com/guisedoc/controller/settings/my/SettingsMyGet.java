package com.guisedoc.controller.settings.my;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.object.Profile;
import com.guisedoc.object.User;

@Controller
public class SettingsMyGet {
	
	@RequestMapping(value="/settingsMy", method = RequestMethod.GET)
	public Object getSettingsMyView(HttpSession session, RedirectAttributes redirectAttributes,
			Model model){
		
		session.setAttribute("requestedPage", "settingsMy");
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		model.addAttribute("user",session.getAttribute("user"));
		
		return "settingsMy";
	}
}
