package com.guisedoc.controller.settings.my;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.login.LoginImpl;
import com.guisedoc.database.implement.settings.MySettingsImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.SettingsMessages;
import com.guisedoc.object.User;

@Controller
@RequestMapping("/settingsMy")
public class SettingsMyHandling {
	
	@RequestMapping(value="/save", method = RequestMethod.POST, params = {"key","value","whine"})
	@ResponseBody
	public String saveSetting(HttpSession session,HttpServletRequest request,
			@RequestParam("key")String key,@RequestParam("value")boolean value,
			@RequestParam("whine")String whine){
		
		String message = null;
		String response = null;
		
		User user = ((User)session.getAttribute("user"));
		
		// set the value in int
		int intValue = 0;
		if(value){
			intValue = 1;
		}
		
		ErrorType responseObject = null;
		if(key.equals("autoLogin")){
			
			String secret = "";
			
			// if the user wants to have autologin, we set secret
			if(value){
				String ip = request.getHeader("X-FORWARDER-FOR");
				if(ip == null){
					ip = request.getRemoteAddr();
				}
				
				secret = whine+"-"+ip;
			}
			
			ErrorType secretResponse = new LoginImpl(new Connector("main"))
				.setSecret(user, secret);
			
			if(secretResponse == ErrorType.SUCCESS){
				responseObject = new MySettingsImpl(session)
					.changeOneSetting(user.getID(), key, intValue);
			}
			else{
				responseObject = ErrorType.FAILURE;
			}
			
		}
		else{
			responseObject = new MySettingsImpl(session)
				.changeOneSetting(user.getID(), key, intValue);
		}
		
		
		if(responseObject == ErrorType.SUCCESS){
			
			// update the current user
			user.getSettings().updateValue(key, value);	
			session.setAttribute("user", user);
			
			response = "success";
			message = SettingsMessages.SETTING_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}
		
		return response+";"+message;
	}
	
}
