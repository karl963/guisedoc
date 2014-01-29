package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.messages.ClientMessages;
import com.guisedoc.object.Client;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/clients")
public class ClientSearch {
	
	@RequestMapping(value="/search", method = RequestMethod.POST, params={"searchJSON"})
	@ResponseBody
	public String searchForClients(@RequestParam("searchJSON")String searchJSON){

		Gson gson = new Gson();
    	Map<String,Object> map = gson.fromJson(searchJSON, HashMap.class);
    	
		int totalClients = 0;
		
		List<Client> foundClients = new ArrayList<Client>();
		
		for(int i = 0; i<10;i++){
			Client c1 = new Client();
			c1.setAddress("aadress "+i);
			c1.setContactPerson("contact "+i);
			c1.setEmail("email "+i);
			c1.setID(i);
			c1.setName("name "+i);
			c1.setPhone("phone "+i);
			c1.setTotalBoughtFor(i*1000.0);
			c1.setTotalDeals(i);
			c1.setLastDealNR("Deal"+i);
			c1.setLastDealDate(new Date());
			
			foundClients.add(c1);
		}
		
		JsonObject jsonObject = new JsonObject();
		JsonArray clients = new JsonArray();
		
		for(Client c : foundClients){
			
			JsonObject client = new JsonObject();

			client.addElement("ID", c.getID());
			client.addElement("name", c.getName());
			client.addElement("contactPerson", c.getContactPerson());
			client.addElement("totalBoughtFor", c.getTotalBoughtFor());
			
			clients.addElement(client);
		}
		
		jsonObject.addElement("clients", clients);
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", ClientMessages.CLIENT_SEARCH_SUCCESS+" "+foundClients.size()+" (kokku "+totalClients+")");
		
		return jsonObject.getJsonString();
	}

}
