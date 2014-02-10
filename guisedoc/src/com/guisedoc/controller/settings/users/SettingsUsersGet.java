package com.guisedoc.controller.settings.users;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.firm.FirmImpl;
import com.guisedoc.database.implement.profile.ProfileImpl;
import com.guisedoc.database.implement.user.UserImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Profile;
import com.guisedoc.object.User;

@Controller
public class SettingsUsersGet {

	@RequestMapping(value="/settingsUsers", method = RequestMethod.GET)
	public Object getSettingsUsersView(HttpSession session, RedirectAttributes redirectAttributes,
			Model model){
		
		session.setAttribute("requestedPage", "settingsUsers");
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		String noteMessage = null;
		List<User> users = new ArrayList<User>();
		List<Profile> profiles = new ArrayList<Profile>();
		
		Object usersResponse = new UserImpl(session)
				.getAllUsers();
		if(!(usersResponse instanceof List)){
			noteMessage = "Kasutajad: "+ErrorMessages.getMessage((ErrorType)usersResponse);
			usersResponse = null;
		}
		else{
			users = (List<User>)usersResponse;
		}
		
		Object profilesResponse = new ProfileImpl(session)
				.getAllProfiles();
		if(!(profilesResponse instanceof List)){
			noteMessage = "Profiilid: "+ErrorMessages.getMessage((ErrorType)profilesResponse);
			profilesResponse = null;
		}
		else{
			profiles = (List<Profile>)profilesResponse;
		}
		
		model.addAttribute("noteMessage",noteMessage);
		model.addAttribute("user",session.getAttribute("user"));
		model.addAttribute("users",users);
		model.addAttribute("profiles",profiles);
		
		return "settingsUsers";
	}
}
