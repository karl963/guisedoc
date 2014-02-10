package com.guisedoc.controller.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.login.LoginImpl;
import com.guisedoc.enums.ErrorType;

@Controller
public class LoginGet {

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public Object getLoginView(HttpSession session, HttpServletRequest request,
			RedirectAttributes redirectAttributes, Model model){

		// user is already logged in
		if(session.getAttribute("user") != null){
			return "redirect:documents";
		}

		// user has autologin enabled, then we log in
		for(Cookie cookie : request.getCookies()){
			if(cookie.getName().equals("whine")){
				
				String ip = request.getHeader("X-FORWARDER-FOR");
				if(ip == null){
					ip = request.getRemoteAddr();
				}
				
				String secret = cookie.getValue()+"-"+ip;
				
				// do autologin
				ErrorType responseObject = new LoginImpl(new Connector("main"))
						.checkForAutologinAndLogin(secret,session);
				if(responseObject == ErrorType.SUCCESS){
					if(session.getAttribute("requestPage") != null){
						return session.getAttribute("requestPage");
					}
					
					return "redirect:documents";
				}
				
				break;
			}
		}

		// registration message
		String registerResponse = "";
		String registerMessage = null;
		
		if(session.getAttribute("registerResponseGood") != null){
			registerResponse = "registerResponseGood";
			registerMessage = (String)session.getAttribute("registerResponseGood");
	       	session.removeAttribute("registerResponseGood");
		}
		else if(session.getAttribute("registerResponseBad") != null){
			registerResponse = "registerResponseBad";
			registerMessage = (String)session.getAttribute("registerResponseBad");
	    	session.removeAttribute("registerResponseBad");
		}
		
		model.addAttribute(registerResponse,registerMessage);
		model.addAttribute("pageRequest","login");
		
		return "login";
	}
	
}
