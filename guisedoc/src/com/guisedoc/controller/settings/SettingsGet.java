package com.guisedoc.controller.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SettingsGet {

	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public String getSettingsView(Model model){
		model.addAttribute("pageRequest","settings");
		
		return "settings";
	}
}
