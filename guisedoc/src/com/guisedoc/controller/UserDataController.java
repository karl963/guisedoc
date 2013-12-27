package com.guisedoc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

@Controller
public class UserDataController {
	
	public static String USER_DATA_SAVE_SUCCESS = "Kasutaja andmed edukalt salvestatud";
	
	@RequestMapping(value="manage-user-data", method = RequestMethod.GET)
	public String userDataView(Model model){
		
		model.addAttribute("firm",new Firm());
		model.addAttribute("user",new User());
		model.addAttribute("pageRequest","manage-user-data");
		
		return "manage-user-data";
	}
	
	@RequestMapping(value="manage-user-data", method = RequestMethod.POST, params = {"dataJSON"})
	@ResponseBody
	public String saveUserData(@RequestParam("dataJSON")String userDataJSONString){
		
		Gson gson = new Gson();
	    JsonParser parser = new JsonParser();
	    JsonArray JsonProductarray = parser.parse(userDataJSONString).getAsJsonArray();

	    Firm firm = gson.fromJson(JsonProductarray.get(0), Firm.class);
	    User user = gson.fromJson(JsonProductarray.get(1), User.class);
	    
		return "success;"+UserDataController.USER_DATA_SAVE_SUCCESS;
	}

}
