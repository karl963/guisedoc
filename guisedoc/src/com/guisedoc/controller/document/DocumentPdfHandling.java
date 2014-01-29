package com.guisedoc.controller.document;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	
	private Document doc;
	
	@RequestMapping(value="/download",method = RequestMethod.GET, params={"ID"})
	@ResponseBody
	public Object downloadPDF(@RequestParam("ID")long id, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(request.getSession())){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
		try{
	        Document dd = new Document();
	        
	        dd.setPrefix("AA");
	        dd.setNumber(2222);
	        dd.setType(DocumentType.QUOTATION);
	        dd.setCeSpecification("dddsada as");
	        dd.setShowCE(true);
	        
	        List<Product> products = new ArrayList<Product>();
	        for(int i = 0; i < 4; i++){
	        	Product p = new Product();
	        	p.setCode("K"+i*11111);
	        	if(i == 4 || i == 6 || i == 10){
	        		p.setAdditional_Info("a\n"
	        				+ "b\n"
	        				+ "c\n"
	        				+ "asd\n"
	        				+ "asd\n"
	        				+ "dd\n"
	        				+ "ass\n"
	        				+ "asdsada");
	        	}
	        	p.setName("name"+i);
	        	p.setPrice(i*1.0);
	        	p.setDiscount(10.0);
	        	p.setUnit("unit");
	        	p.setAmount(i*1.0);
	        	p.setTotalSum(i*1000.0);
	
	        	products.add(p);
	        }
	        
	        dd.setProducts(products);
	        
	        Firm firm = (Firm) new FirmImpl(((Connector)request.getSession().getAttribute("connector")).getDatasource()).getFirmData();
			byte[] bytes = DocumentBuilder.build(dd,firm,(User)request.getSession().getAttribute("user"));
	
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "pdf"));
		    header.set("Content-Disposition",
		    		"attachment; filename=" + dd.getFullNumber().replace(" ", "_"));
		    header.setContentLength(bytes.length);
	
		    response.addCookie(new Cookie("documentsDownload","true"));
		    
		    return new HttpEntity<byte[]>(bytes, header);
		}
		catch(Exception x){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
	}
	
	@RequestMapping(value="/view",method = RequestMethod.GET, params={"ID"})
	@ResponseBody
	public Object viewPDF(@RequestParam("ID")long id, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(request.getSession())){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
        doc = new Document();
        
        doc.setPrefix("AA");
        doc.setNumber(2222);
        doc.setType(DocumentType.QUOTATION);
        doc.setCeSpecification("dddsada as");
        doc.setShowCE(true);
        
        List<Product> products = new ArrayList<Product>();
        for(int i = 0; i < 4; i++){
        	Product p = new Product();
        	p.setCode("K"+i*11111);
        	if(i == 4 || i == 6 || i == 10){
        		p.setAdditional_Info("a\n"
        				+ "b\n"
        				+ "c\n"
        				+ "asd\n"
        				+ "asd\n"
        				+ "dd\n"
        				+ "ass\n"
        				+ "asdsada");
        	}
        	p.setName("name"+i);
        	p.setPrice(i*1.0);
        	p.setDiscount(10.0);
        	p.setUnit("unit");
        	p.setAmount(i*1.0);
        	p.setTotalSum(i*1000.0);

        	products.add(p);
        }
        
        doc.setProducts(products);

        try {
			response.sendRedirect("preview/"+doc.getFullNumber().replace(" ", "_")+".pdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return null;
	}
	
	@RequestMapping(value="/preview/**",method = RequestMethod.GET)
	@ResponseBody
	public Object preViewPDF(RedirectAttributes redirectAttributes,HttpServletRequest request,
			HttpServletResponse response){
		
		if(UserValidator.validateLoggedUser(request.getSession())){
			return UserValidator.directToLogin(request.getContextPath(),redirectAttributes);
		}
		
        try{
        	
        	Firm firm = (Firm) new FirmImpl(((Connector)request.getSession().getAttribute("connector")).getDatasource()).getFirmData();
        	byte[] bytes = DocumentBuilder.build(doc,firm,(User)request.getSession().getAttribute("user"));
        	
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "pdf"));
			header.setContentLength(bytes.length);
			header.set("Content-Disposition",
		    		"inline; filename=" + doc.getFullNumber().replace(" ", "_"));
			response.getOutputStream().write(bytes);
	    
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return null;
	}

}
