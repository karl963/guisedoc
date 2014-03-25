package com.guisedoc.controller.login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.login.LoginImpl;
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
		
		String message = null;
		String response = null;
		
		String[] responseObject = new LoginImpl(new Connector("main"))
				.tryLogin(username, password, session, false);
		
		response = responseObject[0];
		message = responseObject[1];
		
		return response+";"+message;
	}
	
	@RequestMapping(value="/doLogout", method = RequestMethod.GET)
	@ResponseBody
	public View logoutUser(HttpSession session){
		
		new LoginImpl(session)
				.removeAutologin((User)session.getAttribute("user"),session);
		
		session.removeAttribute("user");
		session.removeAttribute("connector");
		
		return new RedirectView("./");
	}
	
	@RequestMapping(value="/register", method = RequestMethod.POST)
	@ResponseBody
	public View register(HttpServletRequest request, HttpSession session,
			@RequestParam("registerName")String name,@RequestParam("registerEmail")String email,
			Model model){
		
		ReCaptchaImpl captcha = new ReCaptchaImpl();
	    captcha.setPrivateKey("6LdSHe4SAAAAACmbZW-MH_QTaIpQu2G_OcqFowd4");
	    
	    String challenge = request.getParameter("recaptcha_challenge_field");
	    String uresponse = request.getParameter("recaptcha_response_field");
	    ReCaptchaResponse reCaptchaResponse =
	            captcha.checkAnswer(request.getRemoteAddr(),
	            challenge, uresponse
	        );

	    if(reCaptchaResponse.isValid()) {
	       	session.setAttribute("registerResponseGood","Teie registreerimise soov on edukalt saadetud !");
	    } else {
	    	session.setAttribute("registerResponseBad","Kahjuks sisestasite vale Captcha koodi, proovige uuesti !");
	    }
	    
	    return new RedirectView("../login");
	}
}
