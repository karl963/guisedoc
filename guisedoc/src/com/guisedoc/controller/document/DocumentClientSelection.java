package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.object.Client;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/client")
public class DocumentClientSelection {
	
	@RequestMapping(value="/search", method = RequestMethod.POST, params={"clientType","clientName"})
	@ResponseBody
	public String getAllClientsByType(@RequestParam("clientType")String type,
			@RequestParam("clientName")String name){

		List<Client> clients = new ArrayList<Client>();
		
		for(int i = 0; i< 500 ; i++){
			Client c = new Client();
			c.setName("name"+i);
			c.setTotalBoughtFor(i*1.0);
			c.setContactPerson("contasf"+i);
			c.setTotalDeals(i);
			clients.add(c);
		}
		
		JsonObject jsonObject = new JsonObject();
		JsonArray clientsArray = new JsonArray();
		
		for(Client c : clients){

			JsonObject client = new JsonObject();

			client.addElement("ID",c.getID());
			client.addElement("contactPerson",c.getContactPerson());
			client.addElement("name",c.getName());
			client.addElement("totalDeals",c.getTotalDeals());
			client.addElement("totalSum",c.getTotalBoughtFor());
			
			clientsArray.addElement(client);
	    }
		
		jsonObject.addElement("clients",clientsArray);
		jsonObject.addElement("message",DocumentMessages.CLIENT_SEARCH_SUCCESS);
		jsonObject.addElement("response","success");
	    
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/select", method = RequestMethod.POST, params={"selectedClientType","selectedClientID","currentDocumentID"})
	@ResponseBody
	public String getSelectedClientData(@RequestParam("selectedClientType")String type,
			@RequestParam("selectedClientID")long id,@RequestParam("currentDocumentID")long documentID){
	
		Client c = new Client();
		c.setName("name");
		c.setAddress("aadress");
		c.setContactPerson("contakt");
		c.setAdditionalAddress("lisaaadress");
		c.setEmail("email");
		c.setPhone("phone");
		
		JsonObject jsonObject = new JsonObject();
		JsonObject client = new JsonObject();
		
		client.addElement("ID",c.getID());
		client.addElement("contactPerson",c.getContactPerson());
		client.addElement("name",c.getName());
		client.addElement("address",c.getAddress());
		client.addElement("additionalAddress",c.getAdditionalAddress());
		client.addElement("phone",c.getPhone());
		client.addElement("email",c.getEmail());
		
		jsonObject.addElement("client",client);
		jsonObject.addElement("message",DocumentMessages.CLIENT_SELECT_SUCCESS);
		jsonObject.addElement("response","success");
		
		return jsonObject.getJsonString();
	}

}
