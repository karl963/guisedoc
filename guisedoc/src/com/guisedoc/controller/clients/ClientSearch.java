package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.client.ClientImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ClientMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.ContactPerson;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/clients")
public class ClientSearch {
	
	@RequestMapping(value="/search", method = RequestMethod.POST, params={"searchJSON"})
	@ResponseBody
	public String searchForClients(HttpSession session,
			@RequestParam("searchJSON")String searchJSON){

		JsonObject jsonObject = new JsonObject();
		JsonArray clients = new JsonArray();
		String message = null;
		String response = null;
		
		// make search client and attributes
    	Map<String,Object> map = new Gson().fromJson(searchJSON, HashMap.class);

    	Client searchClient = new Client();
    	searchClient.setName((String)map.get("name"));
    	searchClient.setSelectedContactPerson(new ContactPerson((String)map.get("contactPerson")));
		
    	boolean sellers = (Boolean)map.get("sellers");
    	boolean nonBuyers = (Boolean)map.get("nonBuyers");
    	boolean realBuyers = (Boolean)map.get("realBuyers");
    	
    	// find the clients
		Object responseObject = new ClientImpl(session)
				.searchForTypeClients(searchClient,nonBuyers,realBuyers,sellers,true);
		
		if(responseObject instanceof Object[]){
			
			List<Client> foundClients = (List<Client>)
					((Object[])responseObject)[1];
			
			for(Client c : foundClients){
				
				JsonObject clientObj = new JsonObject();

				clientObj.addElement("ID", c.getID());
				clientObj.addElement("name", c.getName());
				
				// add the contactpersons
				JsonArray contactPersons = new JsonArray();
				for(ContactPerson contactPerson : c.getContactPersons()){
					JsonObject contPers = new JsonObject();
					contPers.addElement("name", contactPerson.getName());
					
					contactPersons.addElement(contPers);
				}
				clientObj.addElement("contactPersons", contactPersons);
				
				clientObj.addElement("totalBoughtFor", c.getTotalBoughtFor());
				
				clients.addElement(clientObj);
			}
			
			long totalClients = (Long)((Object[])responseObject)[0];
			
			response = "success";
			message = ClientMessages.CLIENT_SEARCH_SUCCESS+" "+
					foundClients.size()+" (kokku "+totalClients+")";
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("clients", clients);
		jsonObject.addElement("response", response);
		jsonObject.addElement("message", message);
		
		return jsonObject.getJsonString();
	}

}
