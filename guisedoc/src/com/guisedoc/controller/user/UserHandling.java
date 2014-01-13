package com.guisedoc.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.guisedoc.messages.UserMessages;
import com.guisedoc.object.Firm;
import com.guisedoc.object.User;

@Controller
@RequestMapping("/manage-user-data")
public class UserHandling {
	
	@RequestMapping(value="/save", method = RequestMethod.POST, params = {"dataJSON"})
	@ResponseBody
	public String saveUserData(@RequestParam("dataJSON")String userDataJSONString){
		
		Gson gson = new Gson();
	    JsonParser parser = new JsonParser();
	    JsonArray JsonProductarray = parser.parse(userDataJSONString).getAsJsonArray();

	    Firm firm = gson.fromJson(JsonProductarray.get(0), Firm.class);
	    User user = gson.fromJson(JsonProductarray.get(1), User.class);
	    
		return "success;"+UserMessages.USER_DATA_SAVE_SUCCESS;
	}

}
