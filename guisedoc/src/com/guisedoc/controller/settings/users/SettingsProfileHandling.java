package com.guisedoc.controller.settings.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.SettingsMessages;
import com.guisedoc.object.Profile;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/settingsUsers")
public class SettingsProfileHandling {

	@RequestMapping(value="/profile/detail", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getProfileDetail(@RequestParam("ID")long ID, Model model){
		
		Profile p = new Profile();
		p.setName("asdas");
		
		JsonObject jsonObject = new JsonObject();
		
		JsonObject profileObject = new JsonObject();
		profileObject.addElement("ID",p.getID());
		profileObject.addElement("name",p.getName());
		profileObject.addElement("rules", p.getRulesJsonArray());
		
		jsonObject.addElement("profile", profileObject);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.PROFILE_GET_DETAIL_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/save/rule", method = RequestMethod.POST, params = {"ID","value","ruleKey"})
	@ResponseBody
	public String saveProfileRule(@RequestParam("ID")long ID,@RequestParam("value")boolean value,
			@RequestParam("ruleKey")String ruleKey, Model model){
		
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.PROFILE_SAVE_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/save/name", method = RequestMethod.POST, params = {"ID","name"})
	@ResponseBody
	public String saveProfileName(@RequestParam("ID")long ID,@RequestParam("name")String name, Model model){
		
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.PROFILE_SAVE_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/delete", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String deleteProfile(@RequestParam("ID")long ID, Model model){
		
		
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.PROFILE_DELETE_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/add", method = RequestMethod.POST, params = {"name"})
	@ResponseBody
	public String addProfile(@RequestParam("name")String name, Model model){
		Profile p = new Profile();
		p.setName(name);
		
		JsonObject jsonObject = new JsonObject();
		
		JsonObject profileObject = new JsonObject();
		
		profileObject.addElement("name", p.getName());
		profileObject.addElement("ID", p.getID());
		profileObject.addElement("usersCount", p.getUsersCount());
		profileObject.addElement("allowedActionsCount", p.getAllowedActionsCount());
		
		jsonObject.addElement("profile",profileObject);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", SettingsMessages.PROFILE_DELETE_SUCCESS);
		
		return jsonObject.getJsonString();
	}
}
