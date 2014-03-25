package com.guisedoc.controller.document;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.implement.client.ClientImpl;
import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ClientMessages;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.ContactPerson;
import com.guisedoc.workshop.document.settings.DateFormats;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/client")
public class DocumentClientHandling {
	
	@RequestMapping(value="/search", method = RequestMethod.POST, params={"clientType","clientName"})
	@ResponseBody
	public String getAllClientsByType(HttpSession session,
			@RequestParam("clientType")String type,
			@RequestParam("clientName")String name){
		
		JsonObject jsonObject = new JsonObject();
		JsonArray clientsArray = new JsonArray();
		String message = null;
		String response = null;
		
		// make the type
		boolean nonBuyer = false;
		boolean realBuyer = false;
		boolean seller = false;
		if(type.equals("nonBuyer")){
			nonBuyer = true;
		}
		else if(type.equals("realBuyer")){
			realBuyer = true;
		}
		else if(type.equals("seller")){
			seller = true;
		}

		// make the client
		Client searchClient = new Client();
		searchClient.setName(name);
		
		// get clients
		Object responseObject = new ClientImpl(session)
				.searchForTypeClients(searchClient,nonBuyer,realBuyer,seller,false);
		
		if(responseObject instanceof Object[]){
			
			for(Object obj : (List<Object>)((Object[])responseObject)[1]){
				Client client = (Client)obj;
				
				JsonObject clientObj = new JsonObject();
				clientObj.addElement("ID",client.getID());

				JsonArray contactPersons = new JsonArray();
				for(ContactPerson contactPerson : client.getContactPersons()){
					JsonObject contPers = new JsonObject();
					contPers.addElement("name", contactPerson.getName());
					
					contactPersons.addElement(contPers);
				}
				clientObj.addElement("contactPersons", contactPersons);
				clientObj.addElement("name",client.getName());
				clientObj.addElement("totalDeals",client.getTotalDeals());
				clientObj.addElement("totalSum",client.getTotalBoughtFor());
				
				clientsArray.addElement(clientObj);
			}
			
		    response = "success";
		    message = DocumentMessages.CLIENT_SEARCH_SUCCESS;
		}
		else{
		    response = "failure";
		    message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("clients", clientsArray);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/select", method = RequestMethod.POST, params={"selectedClientType","selectedClientID","currentDocumentID"})
	@ResponseBody
	public String getSelectedClientData(HttpSession session,
			@RequestParam("selectedClientType")String type,
			@RequestParam("selectedClientID")long id,@RequestParam("currentDocumentID")long documentID){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject clientObj = new JsonObject();
		String message = null;
		String response = null;
		
		Object responseObject = new ClientImpl(session)
				.getClientByID(id);
		
		if(responseObject instanceof Object[]){
			
			Client client = (Client)((Object[])responseObject)[0];
			
			ErrorType addResponse = new DocumentImpl(session)
					.addClientToDocument(documentID,client.getID());
			
			if(addResponse == ErrorType.SUCCESS){
				clientObj.addElement("ID",client.getID());

				JsonArray contactPersons = new JsonArray();
				for(ContactPerson contactPerson : client.getContactPersons()){
					JsonObject contPers = new JsonObject();
					contPers.addElement("name", contactPerson.getName());
					contPers.addElement("ID", contactPerson.getID());
					
					contactPersons.addElement(contPers);
				}
				clientObj.addElement("contactPersons", contactPersons);
				
				clientObj.addElement("name",client.getName());
				clientObj.addElement("address",client.getAddress());
				clientObj.addElement("additionalAddress",client.getAdditionalAddress());
				clientObj.addElement("phone",client.getPhone());
				clientObj.addElement("email",client.getEmail());
	
			    response = "success";
			    message = DocumentMessages.CLIENT_SELECT_SUCCESS;
			}
			else{
			    response = "failure";
			    message = ErrorMessages.getMessage(addResponse);
			}
		}
		else{
		    response = "failure";
		    message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("client", clientObj);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST, params={"documentID"})
	@ResponseBody
	public String getSelectedClientData(HttpSession session,
			@RequestParam("documentID")long documentID){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject clientObj = new JsonObject();
		String message = null;
		String response = null;
		
		Object responseObject = new ClientImpl(session)
				.addNewClient(new Client());
		
		if(responseObject instanceof Long){
			
			ErrorType clientToDocument = new DocumentImpl(session)
					.saveDocumentAttribute("client_ID", (Long)responseObject, documentID);
			if(clientToDocument == ErrorType.SUCCESS){
				clientObj.addElement("ID", (Long)responseObject);
				clientObj.addElement("name", "");
				clientObj.addElement("address", "");
				clientObj.addElement("additionalAddress", "");
				clientObj.addElement("phone", "");
				clientObj.addElement("email", "");
	
			    response = "success";
			    message = ClientMessages.CLIENT_ADD_SUCCESS;
			}
			else{
			    response = "failure";
			    message = ErrorMessages.getMessage(clientToDocument);
			}
		}
		else{
		    response = "failure";
		    message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("client", clientObj);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
}
