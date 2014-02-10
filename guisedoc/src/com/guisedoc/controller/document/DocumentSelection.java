package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.enums.DocumentType;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Client;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/import")
public class DocumentSelection {

	@RequestMapping(value="/search", method = RequestMethod.POST, params={"importDocumentType","importDocumentNumber"})
	@ResponseBody
	public String getAllDocumentsByType(HttpSession session,
			@RequestParam("importDocumentType")String type,
			@RequestParam("importDocumentNumber")String fullNumber){
		
		JsonObject jsonObject = new JsonObject();
		JsonArray documentArray = new JsonArray();
		String message = null;
		String response = null;
		
		Object responseObject = new DocumentImpl(session)
				.searchForDocuments(type, fullNumber);
		
		if(responseObject instanceof List){
			
		    for(Object obj : (List<Object>)responseObject){
		    	
		    	Document document = (Document)obj;
		    	
		    	JsonObject documentObj = new JsonObject();
		    	
		    	documentObj.addElement("ID", document.getID());
		    	documentObj.addElement("totalSum", document.getTotalSum());
		    	documentObj.addElement("date", document.getFormatedDate());
		    	documentObj.addElement("fullNumber", document.getFullNumber());
		    	
		    	JsonObject clientObj = new JsonObject();
		    	clientObj.addElement("name", document.getClient().getName());
		    	documentObj.addElement("client", clientObj);
		    	
		    	documentArray.addElement(documentObj);
		    }
		    
		    response = "success";
		    message = DocumentMessages.DOCUMENT_SEARCH_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
	    jsonObject.addElement("documents", documentArray);
	    jsonObject.addElement("response", response);
	    jsonObject.addElement("message", message);
	    
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/select", method = RequestMethod.POST, params={"selectedDocumentType","selectedDocumentID","isEstonian","currentDocumentID"})
	@ResponseBody
	public String getSelectedDocumentData(HttpSession session,
			@RequestParam("selectedDocumentType")String type,
			@RequestParam("selectedDocumentID")long selectedDocumentID,@RequestParam("isEstonian")boolean isEstonian,
			@RequestParam("currentDocumentID")long openedDocumentID){

		JsonObject jsonObject = new JsonObject();
		JsonObject documentObj = new JsonObject();
		String message = null;
		String response = null;

		Object responseObject = null;
		
		// we didn't import, we selected it's tab or selected an existing one
		if(openedDocumentID == 0){
			responseObject = new DocumentImpl(session)
					.getDocumentByIDAndOpen(selectedDocumentID
							,((User)session.getAttribute("user")).getID());
		}
		else{
			responseObject = new DocumentImpl(session)
					.importDocument(openedDocumentID,selectedDocumentID);
		}
		
		if(responseObject instanceof Document){
			
			Document document = (Document)responseObject;
			
	    	documentObj.addElement("ID", document.getID());
	    	documentObj.addElement("type", document.getTypeString());
	    	documentObj.addElement("fullNumber", document.getFullNumber());
	    	documentObj.addElement("validDue", document.getValidDue());
	    	documentObj.addElement("advance", document.getAdvance());
	    	documentObj.addElement("payment_requirement", document.getPaymentRequirement());
	    	documentObj.addElement("shipmentAddress", document.getShipmentAddress());
	    	documentObj.addElement("shipmentPlace", document.getShipmentPlace());
	    	documentObj.addElement("shipmentTime", document.getShipmentTime());
	    	documentObj.addElement("orderNumber", document.getOrderNR());
	    	documentObj.addElement("html5FormatedDate", document.getHtml5FormatedDate());
	    	documentObj.addElement("addToStatistics", document.isAddToStatistics());
	    	documentObj.addElement("showDiscount", document.isShowDiscount());
	    	documentObj.addElement("paydInCash", document.isPaydInCash());
	    	documentObj.addElement("showCE", document.isShowCE());
	    	
			Client c = document.getClient();
			JsonObject client = new JsonObject();
			
			client.addElement("ID", c.getID());
			client.addElement("contactPerson", c.getContactPerson());
			client.addElement("name", c.getName());
			client.addElement("address", c.getAddress());
			client.addElement("additionalAddress", c.getAdditionalAddress());
			client.addElement("phone", c.getPhone());
			client.addElement("email", c.getEmail());
			
			documentObj.addElement("client", client);
			 
			JsonArray productArray = new JsonArray();
		    for(Product pro : document.getProducts()){
	
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
		    	product.addElement("totalSum", pro.getTotalSum());
		    	
		    	productArray.addElement(product);
		    }
		    
		    documentObj.addElement("products", productArray);
		    
		    response = "success";
		    message = DocumentMessages.DOCUMENT_SELECT_SUCCESS;
		}
		else{
		    response = "failure";
		    message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
	    jsonObject.addElement("document", documentObj);
	    jsonObject.addElement("response", response);
	    jsonObject.addElement("message", message);
	    
		return jsonObject.getJsonString();
	}
}
