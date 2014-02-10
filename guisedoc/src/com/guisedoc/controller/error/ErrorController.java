package com.guisedoc.controller.error;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String customError(HttpServletRequest request, 
			HttpServletResponse response, Model model) {

		  String statusCode = String.valueOf((Integer)request.getAttribute("javax.servlet.error.status_code"));
		  String message = (String) request.getAttribute("javax.servlet.error.message");
		  String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		  Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		
		  String stackTrace = "";
		  String exception = "";
		  
		  if(throwable != null){
			  for(StackTraceElement o : throwable.getStackTrace()){
				  stackTrace += "at "+o.getClassName()+"."+o.getMethodName()+"("
						  +o.getFileName()+":"+o.getLineNumber()+")<br/>";
			  }
			  
			  exception = throwable.getMessage();
		  }
		  model.addAttribute("date", new Date());  
		  model.addAttribute("errorCode", statusCode);  
		  model.addAttribute("errorURI", requestUri);
		  model.addAttribute("errorMessage", message);
		  model.addAttribute("exception", exception);
		  model.addAttribute("stackTrace", stackTrace);
		  
		  if(statusCode.equals("404")){
			  return "documents";
		  }
		  
		  return "error";
	}
}
