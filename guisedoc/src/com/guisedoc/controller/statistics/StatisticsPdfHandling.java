package com.guisedoc.controller.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.firm.FirmImpl;
import com.guisedoc.database.implement.statistics.StatisticsImpl;
import com.guisedoc.enums.DocumentType;
import com.guisedoc.object.Document;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Product;
import com.guisedoc.object.StatisticsObject;
import com.guisedoc.object.StatisticsSummary;
import com.guisedoc.object.User;
import com.guisedoc.workshop.document.DocumentBuilder;

@Controller
@RequestMapping("/statistics/pdf")
public class StatisticsPdfHandling {
	
	@RequestMapping(value="/download",method = RequestMethod.GET, params={"statisticsJSON"})
	@ResponseBody
	public Object downloadPDF(HttpSession session,RedirectAttributes redirectAttributes,
			 HttpServletResponse response,
			 @RequestParam("statisticsJSON")String statisticsJSON){

		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext()
					.getContextPath(),redirectAttributes);
		}
		
		HashMap<String,String> map = new Gson().fromJson(statisticsJSON, HashMap.class);
		
		try{
			
			Object responseObject = new StatisticsImpl(session)
					.getStatisticsSummary(map);

			if(responseObject instanceof StatisticsSummary){

				Firm firm = (Firm) new FirmImpl(session).getFirmData();
				byte[] bytes = DocumentBuilder.build((StatisticsSummary)responseObject,firm,(User)session.getAttribute("user"));
		
				HttpHeaders header = new HttpHeaders();
				header.setContentType(new MediaType("application", "pdf"));
			    header.set("Content-Disposition",
			    		"attachment; filename=" + ((StatisticsSummary)responseObject).getSummaryName().replace(" ", "_")+".pdf");

			    header.setContentLength(bytes.length);
		
			    response.addCookie(new Cookie("statisticsDownload","true"));
			    
			    return new HttpEntity<byte[]>(bytes, header);
			}
			else{
				return UserValidator.directToLogin(session.getServletContext()
						.getContextPath(),redirectAttributes);
			}
		}
		catch(Exception x){
			return UserValidator.directToLogin(session.getServletContext()
					.getContextPath(),redirectAttributes);
		}
	}
	
	@RequestMapping(value="/view",method = RequestMethod.GET, params = {"statisticsJSON"})
	@ResponseBody
	public Object viewPDF(HttpSession session, RedirectAttributes redirectAttributes,
			HttpServletResponse response,
			@RequestParam("statisticsJSON")String statisticsJSON){

		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}

		HashMap<String,String> map = new Gson().fromJson(statisticsJSON, HashMap.class);
		
        try {
			Object responseObject = new StatisticsImpl(session)
					.getStatisticsSummary(map);

			if(responseObject instanceof StatisticsSummary){
				session.setAttribute("summary", (StatisticsSummary)responseObject);
				response.sendRedirect("preview/"+((StatisticsSummary)responseObject).getSummaryName().replace(" ", "_")+".pdf");
			}
			else{
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return null;
	}
	
	@RequestMapping(value="/preview/**",method = RequestMethod.GET)
	@ResponseBody
	public Object preViewPDF(HttpSession session, RedirectAttributes redirectAttributes,
			HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
        try{

        	Firm firm = (Firm) new FirmImpl(session).getFirmData();
    		byte[] bytes = DocumentBuilder.build((StatisticsSummary)session.getAttribute("summary"),firm,(User)session.getAttribute("user"));
	
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "pdf"));
			header.setContentLength(bytes.length);
		    header.set("Content-Disposition",
		    		"inline; filename=" + ((StatisticsSummary)session.getAttribute("summary")).getSummaryName().replace(" ", "_"));
		    
		    session.removeAttribute("summary");
		    
			response.getOutputStream().write(bytes);
	    
		} catch (IOException e) {
			session.removeAttribute("summary");
			e.printStackTrace();
		}

		return null;
	}
	
}
