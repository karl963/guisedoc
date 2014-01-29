package com.guisedoc.controller.user;

import java.sql.Connection;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.firm.FirmImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Firm;
import com.guisedoc.object.User;

@Controller
public class UserGet {
	
	@RequestMapping(value="/user-firm", method = RequestMethod.GET)
	public Object userDataView(HttpSession session, RedirectAttributes redirectAttributes, 
			Model model){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		String noteMessage = null;

		Object prefixes = new FirmImpl(((Connector)session.getAttribute("connector")).getDatasource()).getPrefixesArray();
		if(!(prefixes instanceof String[])){
			noteMessage = "Eesliited: "+ErrorMessages.getMessage((ErrorType)prefixes);
			prefixes = null;
		}
		
		Object firm = new FirmImpl(((Connector)session.getAttribute("connector")).getDatasource()).getFirmData();
		if(!(firm instanceof Firm)){
			noteMessage = "Firma: "+ErrorMessages.getMessage((ErrorType)firm);
			firm = null;
		}

		model.addAttribute("noteMessage",noteMessage);
		model.addAttribute("firm",(Firm)firm);
		model.addAttribute("user",session.getAttribute("user"));
		model.addAttribute("prefixes",(String[])prefixes);
		
		return "user-firm";
	}

}
