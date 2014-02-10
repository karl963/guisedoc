package com.guisedoc.controller.statistics;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.object.User;

@Controller
public class StatisticsGet {
	
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	public Object getStatisticsView(HttpSession session, RedirectAttributes redirectAttributes, 
			Model model){
		
		session.setAttribute("requestedPage", "statistics");
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}

		model.addAttribute("user",session.getAttribute("user"));
		
		return "statistics";
	}

}
