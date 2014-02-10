package com.guisedoc.controller.settings.users;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.implement.profile.ProfileImpl;
import com.guisedoc.database.Connector;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.SettingsMessages;
import com.guisedoc.object.Profile;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/settingsUsers")
public class SettingsProfileHandling {

	@RequestMapping(value="/profile/detail", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getProfileDetail(HttpSession session,
			@RequestParam("ID")long ID, Model model){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject profileObject = new JsonObject();
		String response = null;
		String message = null;
		
		Object responseObject = new ProfileImpl(session)
				.getProfileByID(ID);
		
		if(responseObject instanceof Profile){
			
			Profile p = (Profile)responseObject;
			
			profileObject.addElement("ID",p.getID());
			profileObject.addElement("name",p.getName());
			profileObject.addElement("rules", p.getRulesJsonArray());
			
			response = "success";
			message = SettingsMessages.PROFILE_GET_DETAIL_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("profile", profileObject);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/save/rule", method = RequestMethod.POST, params = {"ID","value","ruleKey"})
	@ResponseBody
	public String saveProfileRule(HttpSession session,
			@RequestParam("ID")long ID,@RequestParam("value")boolean value,
			@RequestParam("ruleKey")String ruleKey, Model model){

		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		
		// make the rule value
		int ruleValue = 0;
		if(value){
			ruleValue = 1;
		}
		
		ErrorType responseObject = new ProfileImpl(session)
				.changeProfileRule(ID,ruleKey,ruleValue);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = SettingsMessages.PROFILE_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/save/name", method = RequestMethod.POST, params = {"ID","name"})
	@ResponseBody
	public String saveProfileName(HttpSession session,
			@RequestParam("ID")long ID,@RequestParam("name")String name, Model model){
		
		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		
		ErrorType responseObject = new ProfileImpl(session)
				.changeProfileName(ID,name);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = SettingsMessages.PROFILE_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/delete", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String deleteProfile(HttpSession session,
			@RequestParam("ID")long ID, Model model){
		
		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		
		ErrorType responseObject = new ProfileImpl(session)
				.deleteProfile(ID);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = SettingsMessages.PROFILE_DELETE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/profile/add", method = RequestMethod.POST, params = {"name"})
	@ResponseBody
	public String addProfile(HttpSession session,
			@RequestParam("name")String name, Model model){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject profileObject = new JsonObject();
		String response = null;
		String message = null;
		
		Object responseObject = new ProfileImpl(session)
				.addNewProfile(name);
		
		if(responseObject instanceof Long){

			Profile p = new Profile();
			
			p.setID((Long)responseObject);
			p.setName(name);
			
			profileObject.addElement("name", p.getName());
			profileObject.addElement("ID", p.getID());
			profileObject.addElement("usersCount", p.getUsersCount());
			profileObject.addElement("allowedActionsCount", p.getAllowedActionsCount());
			
			response = "success";
			message = SettingsMessages.PROFILE_ADD_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("profile", profileObject);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
}
