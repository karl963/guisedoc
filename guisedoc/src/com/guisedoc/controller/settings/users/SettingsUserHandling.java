package com.guisedoc.controller.settings.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.SettingsMessages;
import com.guisedoc.object.User;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/settingsUsers")
public class SettingsUserHandling {
	
	@RequestMapping(value="/user/detail", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getUserDetail(@RequestParam("ID")long ID, Model model){
		
		JsonObject jsonObject = new JsonObject();
		
		JsonObject userObject = new JsonObject();
		JsonObject userProfile = new JsonObject();
		userProfile.addElement("name", "profile6");
		userObject.addElement("ID", 123);
		userObject.addElement("profile", userProfile);
		
		JsonArray profilesArray = new JsonArray();

		for(int i = 0; i< 10 ; i++){
			JsonObject profileObject = new JsonObject();
			profileObject.addElement("name","profile"+i);
			profileObject.addElement("ID", i);
			profilesArray.addElement(profileObject);
		}
		
		jsonObject.addElement("profiles", profilesArray);
		
		jsonObject.addElement("user", userObject);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.USER_GET_DETAIL_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/user/delete", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String deleteUser(@RequestParam("ID")long ID, Model model){
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.USER_DELETE_SUCCESS);
		
		return jsonObject.getJsonString();
	}

	@RequestMapping(value="/user/save/profile", method = RequestMethod.POST, params = {"userID","profileID"})
	@ResponseBody
	public String saveUserProfile(@RequestParam("userID")long userID,@RequestParam("profileID")long profileID, Model model){
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.USER_PROFILE_SAVE_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/user/add", method = RequestMethod.POST, params = {"name","password"})
	@ResponseBody
	public String addUser(@RequestParam("name")String name,@RequestParam("password")String password, Model model){
		
		JsonObject jsonObject = new JsonObject();
		
		JsonObject userObject = new JsonObject();
		JsonObject userProfile = new JsonObject();
		userProfile.addElement("name", "profile6");
		userObject.addElement("ID", 123);
		userObject.addElement("profile", userProfile);
		userObject.addElement("totalDeals", 444);
		userObject.addElement("lastOnlineString", new User().getLastOnlineString());
		userObject.addElement("userName", name);
		
		jsonObject.addElement("user", userObject);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.USER_ADD_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
}
