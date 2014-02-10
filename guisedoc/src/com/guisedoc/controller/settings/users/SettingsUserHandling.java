package com.guisedoc.controller.settings.users;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.profile.ProfileImpl;
import com.guisedoc.database.implement.user.UserImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.SettingsMessages;
import com.guisedoc.object.Profile;
import com.guisedoc.object.User;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/settingsUsers")
public class SettingsUserHandling {
	
	@RequestMapping(value="/user/detail", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getUserDetail(HttpSession session,
			@RequestParam("ID")long ID, Model model){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject userObject = new JsonObject();
		JsonObject userProfile = new JsonObject();
		JsonArray profilesArray = new JsonArray();
		
		String message = null;
		String response = null;
		

		Object responseObject = new UserImpl(
				session)
				.getUserAndAllProfiles(ID);
		
		if(responseObject instanceof Object[]){
			
			User user = (User)((Object[])responseObject)[0];
			List<Profile> profiles = (List<Profile>)((Object[])responseObject)[1];
			
			userProfile.addElement("name", user.getProfile().getName());
			userObject.addElement("ID", user.getID());
			userObject.addElement("profile", userProfile);

			for(Profile p : profiles){
				JsonObject profileObject = new JsonObject();
				profileObject.addElement("name",p.getName());
				profileObject.addElement("ID", p.getID());
				
				profilesArray.addElement(profileObject);
			}
			
			response = "success";
			message = SettingsMessages.USER_GET_DETAIL_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("profiles", profilesArray);
		jsonObject.addElement("user", userObject);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/user/delete", method = RequestMethod.POST, params = {"ID","username"})
	@ResponseBody
	public String deleteUser(HttpSession session,
			@RequestParam("ID")long ID,@RequestParam("username")String username, Model model){

		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		
		ErrorType checkResponse = new UserImpl(new Connector("main"))
				.deleteUser(username);
		
		if(checkResponse == ErrorType.SUCCESS){
			ErrorType responseObject = new UserImpl(
					session)
					.deleteUser(ID);
			
			if(responseObject == ErrorType.SUCCESS){
				response = "success";
				message = SettingsMessages.USER_DELETE_SUCCESS;
			}
			else{
				response = "failure";
				message = ErrorMessages.getMessage((ErrorType)responseObject);
			}
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(checkResponse);
		}
		
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}

	@RequestMapping(value="/user/save/profile", method = RequestMethod.POST, params = {"userID","profileID"})
	@ResponseBody
	public String saveUserProfile(HttpSession session,
			@RequestParam("userID")long userID,@RequestParam("profileID")long profileID, Model model){

		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		
		ErrorType responseObject = new UserImpl(
				session)
				.changeUserProfile(userID,profileID);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = SettingsMessages.USER_PROFILE_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/user/add", method = RequestMethod.POST, params = {"name","password"})
	@ResponseBody
	public String addUser(HttpSession session,
			@RequestParam("name")String name,@RequestParam("password")String password, Model model){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject userObject = new JsonObject();
		JsonObject userProfile = new JsonObject();
		String response = null;
		String message = null;
		
		User addUser = new User();
		addUser.setUserName(name);
		addUser.setPassword(password);
		
		ErrorType checkAdd = new UserImpl(new Connector("main"))
				.checkExistingUserAndAdd(addUser,(String)session.getAttribute("schema"));
		
		// check if the user doesn't exits
		if(checkAdd == ErrorType.SUCCESS){

			Object responseObject = new UserImpl(session)
					.addNewUser(addUser);
			
			if(responseObject instanceof User){
				
				User user = (User)responseObject;
				
				userObject.addElement("ID", user.getID());
				userObject.addElement("totalDeals", user.getTotalDeals());
				userObject.addElement("lastOnlineString", user.getLastOnlineString());
				userObject.addElement("userName", name);
				
				userProfile.addElement("name", user.getProfile().getName());
				userObject.addElement("profile", userProfile);
				
				response = "success";
				message = SettingsMessages.USER_PROFILE_SAVE_SUCCESS;
			}
			else{
				response = "failure";
				message = ErrorMessages.getMessage((ErrorType)responseObject);
			}
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(checkAdd);
		}
		
		jsonObject.addElement("user", userObject);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
}
