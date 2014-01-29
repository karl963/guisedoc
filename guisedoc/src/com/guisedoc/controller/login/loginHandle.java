package com.guisedoc.controller.login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.login.Login;
import com.guisedoc.database.implement.user.UserImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.User;

@Controller
@RequestMapping("/login")
public class loginHandle {

	@RequestMapping(value="/doLogin", method = RequestMethod.POST, params={"username","password"})
	@ResponseBody
	public String checkLogin(HttpSession session,
			@RequestParam("username")String username,@RequestParam("password")String password){
		
		Object validationResponse = Login.checkUserValid(username, password);
		if(validationResponse instanceof String){
			
			Connector connector = new Connector((String)validationResponse);

			Object dataResponse = new UserImpl(connector.getDatasource())
							.getUser(username,true,true,true,false);
			if(dataResponse instanceof User){
				
				session.setAttribute("user", (User)dataResponse);
				session.setAttribute("connector", connector);
				
				return "success;";
			}
			else{
				return "failure;"+ErrorMessages.getMessage((ErrorType)dataResponse);
			}
		}
		else{
			return "failure;"+ErrorMessages.getMessage((ErrorType)validationResponse);
		}
	}
	
	@RequestMapping(value="/doLogout", method = RequestMethod.GET)
	@ResponseBody
	public View logoutUser(HttpSession session){
		
		session.removeAttribute("user");
		session.removeAttribute("connector");
		
		return new RedirectView("./");
	}
}
