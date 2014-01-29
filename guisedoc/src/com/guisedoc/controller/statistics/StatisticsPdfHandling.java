package com.guisedoc.controller.statistics;

import java.io.IOException;
import java.util.ArrayList;
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

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.firm.FirmImpl;
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
	
	private StatisticsSummary summary;
		
	@RequestMapping(value="/download",method = RequestMethod.GET)
	@ResponseBody
	public Object downloadPDF(RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response){

		if(UserValidator.validateLoggedUser(request.getSession())){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
		try{
			summary = new StatisticsSummary();
			for(int i = 0;i< 78 ; i++){
				StatisticsObject statObject = new StatisticsObject();
				statObject.setAmount(i*1.0);
				statObject.setCode("FGH22334");
				statObject.setName("name"+i);
				statObject.setTotalPrice(i*1.0);
				statObject.setUnit("unit");
				
				summary.getStatObjects().add(statObject);
			}
			
			summary.setClientName("Balex Metal OÜ");
			summary.setClientType("Müüja");
			summary.setCode("FGH22334");
			
			Firm firm = (Firm) new FirmImpl(((Connector)request.getSession().getAttribute("connector")).getDatasource()).getFirmData();
			byte[] bytes = DocumentBuilder.build(summary,firm,(User)request.getSession().getAttribute("user"));
	
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "pdf"));
		    header.set("Content-Disposition",
		    		"attachment; filename=" + summary.getSummaryName().replace(" ", "_")+".pdf");
		    header.setContentLength(bytes.length);
	
		    response.addCookie(new Cookie("statisticsDownload","true"));
		    
		    return new HttpEntity<byte[]>(bytes, header);
		}
		catch(Exception x){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
	}
	
	@RequestMapping(value="/view",method = RequestMethod.GET)
	@ResponseBody
	public Object viewPDF(HttpSession session, RedirectAttributes redirectAttributes,
			HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		summary = new StatisticsSummary();
		
		for(int i = 0;i< 78 ; i++){
			StatisticsObject statObject = new StatisticsObject();
			statObject.setAmount(i*1.0);
			statObject.setCode("FGH22334");
			statObject.setName("name"+i);
			statObject.setTotalPrice(i*1.0);
			statObject.setUnit("unit");
			
			summary.getStatObjects().add(statObject);
		}
		
		summary.setClientName("Balex Metal OÜ");
		summary.setClientType("Müüja");
		summary.setCode("FGH22334");
        
        try {
			response.sendRedirect("preview/"+summary.getSummaryName().replace(" ", "_")+".pdf");
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

        	Firm firm = (Firm) new FirmImpl(((Connector)session.getAttribute("connector")).getDatasource()).getFirmData();
    		byte[] bytes = DocumentBuilder.build(summary,firm,(User)session.getAttribute("user"));
	
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "pdf"));
			header.setContentLength(bytes.length);
		    header.set("Content-Disposition",
		    		"inline; filename=" + summary.getSummaryName().replace(" ", "_"));
			response.getOutputStream().write(bytes);
	    
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		return null;
	}
	
}
