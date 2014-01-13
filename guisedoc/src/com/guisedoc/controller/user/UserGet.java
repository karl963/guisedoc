package com.guisedoc.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.guisedoc.object.Firm;
import com.guisedoc.object.User;

@Controller
public class UserGet {
	
	@RequestMapping(value="/manage-user-data", method = RequestMethod.GET)
	public String userDataView(Model model){
		
		model.addAttribute("firm",new Firm());
		model.addAttribute("user",new User());
		model.addAttribute("pageRequest","manage-user-data");
		
		return "manage-user-data";
	}

}
