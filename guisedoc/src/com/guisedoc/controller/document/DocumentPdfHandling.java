package com.guisedoc.controller.document;

import java.io.IOException;
import java.net.HttpCookie;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.document.DocumentImpl;
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
@RequestMapping("/documents/pdf")
public class DocumentPdfHandling {
	
	@RequestMapping(value="/download",method = RequestMethod.GET, params={"ID"})
	@ResponseBody
	public Object downloadPDF(HttpSession session,@RequestParam("ID")long ID, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(request.getSession())){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
		try{
			
			Object responseObject = new DocumentImpl(session)
					.getDocumentByID(ID);

			if(responseObject instanceof Document){
				Document document = (Document)responseObject;
				
		        Firm firm = (Firm) new FirmImpl(session)
		        		.getFirmData();
		        
				byte[] bytes = DocumentBuilder.build(document,
						firm,(User)request.getSession().getAttribute("user"));
		
				HttpHeaders header = new HttpHeaders();
				header.setContentType(new MediaType("application", "pdf"));
			    header.set("Content-Disposition",
			    		"attachment; filename=" + document.getFullNumber().replace(" ", "_"));

			    header.setContentLength(bytes.length);
		
			    response.addCookie(new Cookie("documentsDownload","true"));

			    return new HttpEntity<byte[]>(bytes, header);
			}
			else{
				return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
			}
		}
		catch(Exception x){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
	}
	
	@RequestMapping(value="/view",method = RequestMethod.GET, params={"ID"})
	@ResponseBody
	public Object viewPDF(HttpSession session,
			@RequestParam("ID")long ID, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
        try {
			
			Object responseObject = new DocumentImpl(session)
					.getDocumentByID(ID);

			if(responseObject instanceof Document){
				session.setAttribute("document",(Document)responseObject);
				response.sendRedirect("preview/"+((Document)responseObject).getFullNumber().replace(" ", "_")+".pdf");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return null;
	}
	
	@RequestMapping(value="/preview/**",method = RequestMethod.GET)
	@ResponseBody
	public Object preViewPDF(HttpSession session,
			RedirectAttributes redirectAttributes,HttpServletResponse response,
			HttpServletRequest request){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
        try{
        	Firm firm = (Firm) new FirmImpl(session)
        			.getFirmData();
        	byte[] bytes = DocumentBuilder.build((Document)session.getAttribute("document"),firm,(User)session.getAttribute("user"));
        	
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "pdf"));
			header.setContentLength(bytes.length);
			header.set("Content-Disposition",
		    		"inline; filename=" + ((Document)session.getAttribute("document")).getFullNumber().replace(" ", "_"));
			session.removeAttribute("document");
			response.getOutputStream().write(bytes);
		
		} catch (IOException e) {
			session.removeAttribute("document");
			e.printStackTrace();
		}
        
        return null;
	}

}
