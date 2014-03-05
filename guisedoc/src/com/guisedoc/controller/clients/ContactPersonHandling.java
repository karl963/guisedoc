package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guisedoc.database.implement.client.ClientImpl;
import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ClientMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.ContactPerson;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/clients/contacts")
public class ContactPersonHandling {
	
	@RequestMapping(value="/delete", method = RequestMethod.POST, params={"forDeleteJSON"})
	@ResponseBody
	public String deleteContacts(HttpSession session,
			@RequestParam("forDeleteJSON")String forDeleteJSON){
		
		List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
		String response = null;
		String message = null;
		
		Gson gson = new Gson();

		com.google.gson.JsonArray productArray = new JsonParser().parse(forDeleteJSON).getAsJsonArray();
	    for(JsonElement contactElement : productArray){
	    	contactPersons.add(gson.fromJson(contactElement, ContactPerson.class));
	    }
	    
		ErrorType responseObject = new ClientImpl(session)
				.deleteContactPersons(contactPersons);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = ClientMessages.CONTACT_PERSON_DELETE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}

		return response+";"+message;
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST, params={"name","clientID"})
	@ResponseBody
	public String addContactPerson(HttpSession session,
			@RequestParam("name")String name,@RequestParam("clientID")long clientID){
		
		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		long ID = 0;
		
		Object responseObject = new ClientImpl(session)
				.addNewContactPerson(name,clientID);
		
		if(responseObject instanceof Long){
			ID = (Long)responseObject;
			
			response = "success";
			message = ClientMessages.CONTACT_PERSON_ADD_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("ID", ID);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/change/document", method = RequestMethod.POST, params={"documentID","contactID"})
	@ResponseBody
	public String changeContactPerson(HttpSession session,
			@RequestParam("documentID")long documentID,@RequestParam("contactID")long contactID){
		
		JsonObject jsonObject = new JsonObject();
		String message = null;
		String response = null;
		
		ErrorType responseObject = new DocumentImpl(session)
				.changeDocumentContactPerson(documentID,contactID);
		
		if(responseObject == ErrorType.SUCCESS){
			
			response = "success";
			message = ClientMessages.CONTACT_PERSON_CHANGE_SUCCESS;
		}
		else{
		    response = "failure";
		    message = ErrorMessages.getMessage(responseObject);
		}
		
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/change/name", method = RequestMethod.POST, params={"contactID","name"})
	@ResponseBody
	public String changeContactPersonsName(HttpSession session,
			@RequestParam("contactID")long contactID,@RequestParam("name")String name){
		
		JsonObject jsonObject = new JsonObject();
		String message = null;
		String response = null;
		
		ErrorType responseObject = new ClientImpl(session)
				.changeContactPersonName(contactID,name);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = ClientMessages.CONTACT_PERSON_NAME_CHANGE_SUCCESS;
		}
		else{
		    response = "failure";
		    message = ErrorMessages.getMessage(responseObject);
		}
		
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
}
