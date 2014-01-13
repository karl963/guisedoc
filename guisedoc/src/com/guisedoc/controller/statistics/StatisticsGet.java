package com.guisedoc.controller.statistics;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StatisticsGet {
	
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	public String getStatisticsView(Model model){
		
		model.addAttribute("pageRequest","statistics");
		
		return "statistics";
	}

}
