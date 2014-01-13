package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.enums.DocumentType;
import com.guisedoc.object.Client;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/import")
public class DocumentSelection {

	@RequestMapping(value="/search", method = RequestMethod.POST, params={"importDocumentType","importDocumentNumber"})
	@ResponseBody
	public String getAllDocumentsByType(@RequestParam("importDocumentType")String type,
			@RequestParam("importDocumentNumber")String numer){

		List<Document> documents = new ArrayList<Document>();
		
		for(int i = 0; i< 500 ; i++){
			Document d = new Document();
			d.setNumber(i);
			d.setType(DocumentType.INVOICE);
			documents.add(d);
			
			Client c = new Client();
			c.setName("client name"+i);
			d.setClient(c);
		}
		
		JsonObject jsonObject = new JsonObject();
		JsonArray documentArray = new JsonArray();
		
	    for(Document d : documents){
	    	
	    	JsonObject document = new JsonObject();
	    	
	    	document.addElement("ID", d.getID());
	    	document.addElement("totalSum", d.getTotalSum());
	    	document.addElement("date", d.getFormatedDate());
	    	document.addElement("fullNumber", d.getFullNumber());
	    	
	    	JsonObject client = new JsonObject();
	    	client.addElement("name", d.getClient().getName());
	    	document.addElement("client", client);
	    	
	    	documentArray.addElement(document);
	    }

	    jsonObject.addElement("documents", documentArray);
	    
	    jsonObject.addElement("response", "success");
	    jsonObject.addElement("message", DocumentMessages.DOCUMENT_SEARCH_SUCCESS);
	    
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/select", method = RequestMethod.POST, params={"selectedDocumentType","selectedDocumentID","isEstonian","currentDocumentID"})
	@ResponseBody
	public String getSelectedDocumentData(@RequestParam("selectedDocumentType")String type,
			@RequestParam("selectedDocumentID")long newDocumentID,@RequestParam("isEstonian")boolean isEstonian,
			@RequestParam("currentDocumentID")long openedDocumentID){
		
		if(type.equals("tab")){ // we didn't import, we selected it's tab
			
		}
		
		String documentJSON = "{";

		Document d = new Document();
		
		for(Document dd : DocumentGet.documents){
			if(dd.getID() == newDocumentID){
				d = dd;
				break;
			}
		}
		
		JsonObject jsonObject = new JsonObject();
		JsonObject document = new JsonObject();
		
    	document.addElement("ID", d.getID());
    	document.addElement("type", d.getTypeString());
    	document.addElement("fullNumber", d.getFullNumber());
    	document.addElement("validDue", d.getValidDue());
    	document.addElement("advance", d.getAdvance());
    	document.addElement("payment_requirement", d.getPaymentRequirement());
    	document.addElement("shipmentAddress", d.getShipmentAddress());
    	document.addElement("shipmentPlace", d.getShipmentPlace());
    	document.addElement("shipmentTime", d.getShipmentTime());
    	document.addElement("orderNumber", d.getOrderNR());
    	document.addElement("html5FormatedDate", d.getHtml5FormatedDate());
    	document.addElement("addToStatistics", d.isAddToStatistics());
    	document.addElement("showDiscount", d.isShowDiscount());
    	document.addElement("paydInCash", d.isPaydInCash());
    	document.addElement("showCE", d.isShowCE());
    	
		Client c = new Client();
		c.setName("name");
		c.setAddress("aadress");
		c.setContactPerson("contakt");
		c.setAdditionalAddress("lisaaadress");
		c.setEmail("email");
		c.setPhone("phone");
		
		JsonObject client = new JsonObject();
		
		client.addElement("ID", c.getID());
		client.addElement("contactPerson", c.getContactPerson());
		client.addElement("name", c.getName());
		client.addElement("address", c.getAddress());
		client.addElement("additionalAddress", c.getAdditionalAddress());
		client.addElement("phone", c.getPhone());
		client.addElement("email", c.getEmail());
		
		document.addElement("client", client);
		 
		JsonArray productArray = new JsonArray();
		

	    for(Product pro : DocumentGet.allProducts){

	    	JsonObject product = new JsonObject();
	    	
	    	product.addElement("ID", pro.getID());
	    	product.addElement("unitID", pro.getUnitID());
	    	product.addElement("name", pro.getName());
	    	product.addElement("e_name", pro.getE_name());
	    	product.addElement("unit", pro.getUnit());
	    	product.addElement("e_unit", pro.getE_unit());
	    	product.addElement("price", pro.getPrice());
	    	product.addElement("o_price", pro.getO_price());
	    	product.addElement("discount", pro.getDiscount());
	    	product.addElement("code", pro.getCode());
	    	product.addElement("amount", pro.getAmount());
	    	product.addElement("calculateSum", pro.isCalculateSum());
	    	
	    	productArray.addElement(product);
	    }
	    
	    document.addElement("products", productArray);
	    
	    jsonObject.addElement("document", document);
	    jsonObject.addElement("response", "success");
	    jsonObject.addElement("message", DocumentMessages.DOCUMENT_SELECT_SUCCESS);
	    
		return jsonObject.getJsonString();
	}
}
