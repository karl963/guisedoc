package com.guisedoc.controller.statistics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.ProductMessages;
import com.guisedoc.messages.StatisticsMessages;
import com.guisedoc.object.Client;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/statistics")
public class StatisticsClients {
	
	@RequestMapping(value ="/clients", method = RequestMethod.POST, params = {"clientType"})
	@ResponseBody
	public String getClientNamesByType(@RequestParam("clientType")String type){
		
		List<Client> clients = new ArrayList<Client>();
		
		for(int i = 0; i<100; i++){
			Client client = new Client();
			
			client.setID(i);
			client.setName("Name Shname"+i);
			
			clients.add(client);
		}
		
		
		JsonObject jsonObject = new JsonObject();
		JsonArray clientArray = new JsonArray();
		
		for(Client c : clients){
			
			JsonObject client = new JsonObject();
			client.addElement("ID", c.getID());
			client.addElement("name", c.getName());

			clientArray.addElement(client);
		}

		jsonObject.addElement("clients", clientArray);
		jsonObject.addElement("message", StatisticsMessages.CLIENTS_GET_SUCCESS);
		jsonObject.addElement("response", "success");
		
		return jsonObject.getJsonString();
	}

}
