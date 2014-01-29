package com.guisedoc.controller.user;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.firm.FirmImpl;
import com.guisedoc.database.implement.user.UserImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.UserMessages;
import com.guisedoc.object.Firm;
import com.guisedoc.object.User;

@Controller
@RequestMapping("/user-firm/save")
public class UserHandling {
	
	@RequestMapping(value="/user", method = RequestMethod.POST, params = {"dataJSON"})
	@ResponseBody
	public String saveUserData(HttpSession session,
			@RequestParam("dataJSON")String userDataJSONString){
		
		Gson gson = new Gson();
		
	    User newUser = gson.fromJson(userDataJSONString, User.class);
	    
	    String returnable = null;

	    // if user changed the password
	    if(!(newUser.getPassword().equals(""))){
	    	
	    	newUser.setUserName(((User)session.getAttribute("user")).getUserName());

	    	//update the current user password
	    	User user = (User)session.getAttribute("user");
	    	user.setPassword(newUser.getPassword());
	    	session.setAttribute("user", user);
	    	
	    	ErrorType response = (ErrorType)new UserImpl((new Connector("main")).getDatasource()).saveUserPassword(newUser);
		    
		    if(response != ErrorType.SUCCESS){
		    	returnable =  "failure;"+ErrorMessages.getMessage(response);
		    }
	    }
	    
	    // if we already had an error, can't update others
	    if(returnable == null){
		    ErrorType response = (ErrorType)new UserImpl(((Connector)session.getAttribute("connector")).getDatasource()).saveUserData(newUser,true,false,false);
		    
		    if(response == ErrorType.SUCCESS){
		    	
		    	// update the current user data
		    	User user = (User)session.getAttribute("user");
		    	user.setEmail(newUser.getEmail());
		    	user.setName(newUser.getEmail());
		    	user.setPhone(newUser.getPhone());
		    	user.setSkype(newUser.getSkype());
		    	session.setAttribute("user", user);
		    	
		    	returnable =  "success;"+UserMessages.USER_DATA_SAVE_SUCCESS;
		    }
		    else{
		    	returnable =  "failure;"+ErrorMessages.getMessage(response);
		    }
	    }
	    
	    return returnable;
	}
	
	@RequestMapping(value="/firm", method = RequestMethod.POST, params = {"dataJSON"})
	@ResponseBody
	public String saveFirmData(HttpSession session,
			@RequestParam("dataJSON")String userDataJSONString){
		
		Gson gson = new Gson();
		
	    Firm firm = gson.fromJson(userDataJSONString, Firm.class);

	    ErrorType response = (ErrorType)new FirmImpl(((Connector)session.getAttribute("connector")).getDatasource()).saveFirm(firm);
	    
	    String returnable = null;
	    
	    if(response == ErrorType.SUCCESS){
	    	returnable =  "success;"+UserMessages.FIRM_DATA_SAVE_SUCCESS;
	    }
	    else{
	    	returnable =  "failure;"+ErrorMessages.getMessage(response);
	    }
	    
		return returnable;
	}
	
	@RequestMapping(value="/prefixes", method = RequestMethod.POST, params = {"dataJSON"})
	@ResponseBody
	public String savePrefixes(HttpSession session,
			@RequestParam("dataJSON")String userDataJSONString){
		
		Gson gson = new Gson();
		
	    HashMap<String,String> prefixes = gson.fromJson(userDataJSONString, HashMap.class);
	    
	    ErrorType response = (ErrorType)new FirmImpl(((Connector)session.getAttribute("connector")).getDatasource()).savePrefixes(prefixes);
	    
	    String returnable = null;
	    
	    if(response == ErrorType.SUCCESS){
	    	returnable =  "success;"+UserMessages.PREFIXES_SAVE_SUCCESS;
	    }
	    else{
	    	returnable =  "failure;"+ErrorMessages.getMessage(response);
	    }
	    
		return returnable;
	}

}
