package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.client.ClientImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.User;

@Controller
public class ClientGet {

	@RequestMapping(value="/clients", method = RequestMethod.GET)
	public Object getClientsView(HttpSession session, RedirectAttributes redirectAttributes,
			Model model){
		
		session.setAttribute("requestedPage", "clients");
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		String noteMessage = null;
		List<Client> clients = new ArrayList<Client>();

		model.addAttribute("noteMessage",noteMessage);
		model.addAttribute("clients",clients);
		model.addAttribute("user",session.getAttribute("user"));
		
		return "clients";
	}
}
