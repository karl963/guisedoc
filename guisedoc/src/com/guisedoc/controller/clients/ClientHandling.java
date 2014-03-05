package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.Date;
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
import com.guisedoc.database.Connector;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ClientMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.ClientDocument;
import com.guisedoc.object.ContactPerson;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.document.settings.DateFormats;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/clients")
public class ClientHandling {
	
	@RequestMapping(value="/detailed", method = RequestMethod.POST, params={"id"})
	@ResponseBody
	public String getClientDetailedData(HttpSession session,
			@RequestParam("id")long id){
		
		JsonObject jsonObject = new JsonObject();
		JsonArray documents = new JsonArray();
		String response = null;
		String message = null;
		
		Object responseObject = new ClientImpl(session)
				.getClientByID(id);

		if(responseObject instanceof Object[]){
			
			Client client = (Client)((Object[])responseObject)[0];
			
			jsonObject.addElement("ID", client.getID());
			jsonObject.addElement("name", client.getName());
			jsonObject.addElement("phone", client.getPhone());
			jsonObject.addElement("email", client.getEmail());
			jsonObject.addElement("address", client.getAddress());
			jsonObject.addElement("lastDealNR", client.getLastDealNR());
			jsonObject.addElement("totalDeals", client.getTotalDeals());
			jsonObject.addElement("lastDealDate", client.getLastDealDateString());
			
			JsonArray contactPersons = new JsonArray();
			for(ContactPerson contactPerson : client.getContactPersons()){
				JsonObject contPers = new JsonObject();
				
				contPers.addElement("ID", contactPerson.getID());
				contPers.addElement("name", contactPerson.getName());
				
				contactPersons.addElement(contPers);
			}
			jsonObject.addElement("contactPersons", contactPersons);
			
			List<ClientDocument> clientDocuments = 
					(List<ClientDocument>)((Object[])responseObject)[1];
			
			for(ClientDocument cd : clientDocuments){
				JsonObject document = new JsonObject();
				document.addElement("fullNumber", cd.getFullNumber());
				document.addElement("totalSum", cd.getTotalSum());
				document.addElement("formatedDate", cd.getFormatedDate());
				
				documents.addElement(document);
			}
			
			message = ClientMessages.CLIENT_DETAIL_DATA_SUCCESS;
			response = "success";
		}
		else{
			message = ErrorMessages.getMessage((ErrorType)responseObject);
			response = "failure";
		}
		
		jsonObject.addElement("documents",documents);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/save", method = RequestMethod.POST, params={"clientJSONString"})
	@ResponseBody
	public String saveClientDetailedData(HttpSession session,
			@RequestParam("clientJSONString")String clientJSONString){

		String response = null;
		String message = null;
		
		Client client = new Gson().fromJson(clientJSONString, Client.class);
		
		ErrorType responseObject = new ClientImpl(session)
				.saveClient(client);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = ClientMessages.CLIENT_DATA_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}
			
		return response+";"+message;
	}
	
	@RequestMapping(value="/searchDocument", method = RequestMethod.POST, params={"documentNumber","clientID"})
	@ResponseBody
	public String getDetailedClientDocuments(HttpSession session,
			@RequestParam("documentNumber")String number,@RequestParam("clientID")long id){
		
		JsonObject jsonObject = new JsonObject();
		JsonArray documentsArray = new JsonArray();
		String response = null;
		String message = null;
		
		Object responseObject = new ClientImpl(session)
				.getClientDocuments(id, number);
		
		if(responseObject instanceof List){
			
			List<ClientDocument> documents = (List<ClientDocument>)responseObject;
			
			for(ClientDocument cd : documents){
				JsonObject docObj = new JsonObject();
				
				docObj.addElement("fullNumber", cd.getFullNumber());
				docObj.addElement("totalSum", cd.getTotalSum());
				docObj.addElement("formatedDate", cd.getFormatedDate());
					
				documentsArray.addElement(docObj);
			}
			
			response = "success";
			message = ClientMessages.CLIENT_DETAIL_DATA_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("documents", documentsArray);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST, params={"clientJSON"})
	@ResponseBody
	public String addClient(HttpSession session,
			@RequestParam("clientJSON")String clientJSON){
		
		JsonObject jsonObject = new JsonObject();
		String response = null;
		String message = null;
		long ID = 0;
		
		Client client = new Gson().fromJson(clientJSON, Client.class);

		Object responseObject = new ClientImpl(session)
				.addNewClient(client);
		System.out.println(client.getSelectedContactPerson().getName());
		if(responseObject instanceof Long){
			ID = (Long)responseObject;
			
			response = "success";
			message = ClientMessages.CLIENT_ADD_SUCCESS;
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
	
	@RequestMapping(value="/delete", method = RequestMethod.POST, params={"forDeleteJSON"})
	@ResponseBody
	public String deleteClients(HttpSession session,
			@RequestParam("forDeleteJSON")String forDeleteJSON){
		
		List<Client> clients = new ArrayList<Client>();
		String response = null;
		String message = null;
		
		Gson gson = new Gson();

		com.google.gson.JsonArray productArray = new JsonParser().parse(forDeleteJSON).getAsJsonArray();
	    for(JsonElement clientElement : productArray){
	    	clients.add(gson.fromJson(clientElement, Client.class));
	    }
	    
		ErrorType responseObject = new ClientImpl(session)
				.deleteClients(clients);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = ClientMessages.CLIENT_DELETE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}

		return response+";"+message;
	}
}
