package com.guisedoc.controller.document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.enums.DocumentType;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.document.DocumentBuilder;

@Controller
@RequestMapping("/documents")
public class DocumentHandling {
	
	@RequestMapping(value="/pdf",method = RequestMethod.GET, params={"ID"})
	@ResponseBody
	public HttpEntity<byte[]> getPDF(@RequestParam("ID")long id, HttpServletRequest request, HttpServletResponse response){
		
        Document dd = new Document();
        
        dd.setPrefix("AA");
        dd.setNumber(2222);
        dd.setType(DocumentType.ADVANCE_INVOICE);
        
        List<Product> products = new ArrayList<Product>();
        for(int i = 0; i < 60; i++){
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
        

		byte[] bytes = DocumentBuilder.build(dd);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "pdf"));
	    header.set("Content-Disposition",
	    		"attachment; filename=" + dd.getFullNumber().replace(" ", "_"));
	    header.setContentLength(bytes.length);

	    response.addCookie(new Cookie("pdfDownload","starting"));
	    
		return new HttpEntity<byte[]>(bytes, header);
	}

	@RequestMapping(value="/add", method = RequestMethod.POST, params={"newDocumentType"})
	@ResponseBody
	public String makeNewDocument(@RequestParam("newDocumentType")String type){

		Document d = new Document();
		d.setNumber(DocumentGet.documents.size());
		d.setID(DocumentGet.documents.size());
		d.setPrefix(type);
		d.setType(type);

		DocumentGet.documents.add(d);
		
		return "success;"+DocumentMessages.DOCUMENT_SELECT_SUCCESS+";"+d.getFullNumber()+";"+DocumentGet.documents.size()+";"+d.getID();
	}
	
	@RequestMapping(value="/close", method = RequestMethod.POST, params={"closeDocumentID"})
	@ResponseBody
	public String closeDocument(@RequestParam("closeDocumentID")long id){
		
		for(Document d : DocumentGet.documents){
			if(d.getID() == id){
				DocumentGet.documents.remove(d);
				break;
			}
		}
		
		return "success;"+DocumentMessages.DOCUMENT_SELECT_SUCCESS;
	}
	
	@RequestMapping(value="/save", method = RequestMethod.POST, params={"changedDocumentID"})
	@ResponseBody
	public String saveChangedDocumentData(@RequestParam("changedDocumentID")long documentID,@RequestParam("attributeName")String attributeName,
			@RequestParam("value")Object value){
		String error = "";
		
		Document d = new Document();
		
		if(attributeName.equals("Number")){
			d.setFullNumber((String)value);
		}
		else if(attributeName.equals("ValidDue")){
			d.setValidDue(Long.parseLong(String.valueOf(value).replace(",", ".")));
		}
		else if(attributeName.equals("Advance")){
			d.setAdvance(Double.parseDouble(String.valueOf(value).replace(",", "."))); 
		}
		else if(attributeName.equals("PaymentRequirement")){
			d.setPaymentRequirement(Integer.parseInt(String.valueOf(value))); 
		}
		else if(attributeName.equals("ShipmentTime")){
			d.setShipmentTime((String) value);
		}
		else if(attributeName.equals("ShipmentAddress")){
			d.setShipmentAddress((String) value); 
		}
		else if(attributeName.equals("ShipmentPlace")){
			d.setShipmentPlace((String) value); 
		}
		else if(attributeName.equals("OrderNumber")){
			d.setOrderNR((String) value);
		}
		else if(attributeName.equals("ClientName")){
			d.getClient().setName((String) value);
		}
		else if(attributeName.equals("ContactPerson")){
			d.getClient().setContactPerson((String) value);
		}
		else if(attributeName.equals("ClientAddress")){
			d.getClient().setAddress((String) value);
		}
		else if(attributeName.equals("ClientAdditionalAddress")){
			d.getClient().setAdditionalAddress((String) value);
		}
		else if(attributeName.equals("ClientPhone")){
			d.getClient().setPhone((String) value);
		}
		else if(attributeName.equals("Email")){
			d.getClient().setEmail((String) value);
		}
		else if(attributeName.equals("DocumentDate")){
			try {
				d.setDate(Document.DATE_FORMAT.parse((String) value));
			} catch (Exception e) {
				d.setDate(new Date());
			}
		}
		else if(attributeName.equals("AddToStatistics")){
			d.setAddToStatistics(Boolean.parseBoolean((String) value));
		}
		else if(attributeName.equals("ShowDiscount")){
			d.setShowDiscount(Boolean.parseBoolean((String) value));
		}
		else if(attributeName.equals("PaydInCash")){
			d.setPaydInCash(Boolean.parseBoolean((String) value));
		}
		else if(attributeName.equals("ShowCE")){
			d.setShowCE(Boolean.parseBoolean((String) value));
		}
	    
		return "success;"+DocumentMessages.DOCUMENT_SAVE_SUCCESS;
	}
}
