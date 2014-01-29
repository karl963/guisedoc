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
import com.guisedoc.object.Profile;
import com.guisedoc.object.User;

@Controller
public class SettingsUsersGet {

	@RequestMapping(value="/settingsUsers", method = RequestMethod.GET)
	public Object getSettingsUsersView(HttpSession session, RedirectAttributes redirectAttributes,
			Model model){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		List<User> users = new ArrayList<User>();
		List<Profile> profiles = new ArrayList<Profile>();
		
		for(int i = 0; i < 30 ; i++){
			User u = new User();
			u.setUserName("username"+i);
			u.setTotalDeals(i);
			
			Profile p = new Profile();
			p.setName("prof"+i);
			p.setUsersCount(i);
			
			u.setProfile(p);
			
			users.add(u);
			profiles.add(p);
		}	
		
		model.addAttribute("user",session.getAttribute("user"));
		model.addAttribute("users",users);
		model.addAttribute("profiles",profiles);
		
		return "settingsUsers";
	}
}
