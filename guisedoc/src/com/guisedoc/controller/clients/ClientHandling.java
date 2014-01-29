package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guisedoc.messages.ClientMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.document.settings.DateFormats;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/clients")
public class ClientHandling {
	
	@RequestMapping(value="/detailed", method = RequestMethod.POST, params={"id"})
	@ResponseBody
	public String getClientDetailedData(@RequestParam("id")String id){

		int ID = Integer.parseInt(id);
		
		JsonObject jsonObject = new JsonObject();

		jsonObject.addElement("ID", ClientGet.clients.get(ID).getID());
		jsonObject.addElement("name", ClientGet.clients.get(ID).getName());
		jsonObject.addElement("phone", ClientGet.clients.get(ID).getPhone());
		jsonObject.addElement("email", ClientGet.clients.get(ID).getEmail());
		jsonObject.addElement("address", ClientGet.clients.get(ID).getAddress());
		jsonObject.addElement("contactPerson", ClientGet.clients.get(ID).getContactPerson());
		jsonObject.addElement("lastDealNR", ClientGet.clients.get(ID).getLastDealNR());
		jsonObject.addElement("totalDeals", ClientGet.clients.get(ID).getTotalDeals());
		jsonObject.addElement("lastDealDate", ClientGet.clients.get(ID).getLastDealDateString());
		
		JsonArray documents = new JsonArray();
		
		for(int i = 0;i < 25 ;i++){
			JsonObject document = new JsonObject();
			document.addElement("fullNumber", "documentnr"+i);
			document.addElement("totalSum", i*240.5);
			document.addElement("formatedDate", "10.01.2014");
			
			documents.addElement(document);
		}
		
		jsonObject.addElement("documents",documents);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", ClientMessages.CLIENT_DETAIL_DATA_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/save", method = RequestMethod.POST, params={"clientJSONString"})
	@ResponseBody
	public String saveClientDetailedData(@RequestParam("clientJSONString")String clientJSONString){

		Client c = new Gson().fromJson(clientJSONString, Client.class);
		
		return "success;"+ClientMessages.CLIENT_DATA_SAVE_SUCCESS;
	}
	
	@RequestMapping(value="/searchDocument", method = RequestMethod.POST, params={"documentNumber"})
	@ResponseBody
	public String getDetailedClientDocuments(@RequestParam("documentNumber")String number){
		
		JsonObject jsonObject = new JsonObject();

		JsonArray documents = new JsonArray();
		
		for(int i = 0;i < 25 ;i++){
			if(("documentnr"+i).contains(number)){
				JsonObject document = new JsonObject();
				document.addElement("fullNumber", "documentnr"+i);
				document.addElement("totalSum", i*240.5);
				document.addElement("formatedDate", DateFormats.DOT_DATE_FORMAT().format(new Date().getTime()+1000*60*60*24*i));
				
				documents.addElement(document);
			}
		}
		
		jsonObject.addElement("documents",documents);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", ClientMessages.CLIENT_DETAIL_DATA_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST, params={"clientJSON"})
	@ResponseBody
	public String addClient(@RequestParam("clientJSON")String clientJSON){

		Client c = new Gson().fromJson(clientJSON, Client.class);
		
		c.setID(123);
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addElement("ID", c.getID());
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", ClientMessages.CLIENT_ADD_SUCCESS);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST, params={"forDeleteJSON"})
	@ResponseBody
	public String deleteClients(@RequestParam("forDeleteJSON")String forDeleteJSON){
		
		List<Product> products = new ArrayList<Product>();
		
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		com.google.gson.JsonArray productArray = parser.parse(forDeleteJSON).getAsJsonArray();
	    for(JsonElement productElement : productArray){
	    	products.add(gson.fromJson(productElement, Product.class));
	    }

		return "success;"+ClientMessages.CLIENT_DELETE_SUCCESS;
	}
}
