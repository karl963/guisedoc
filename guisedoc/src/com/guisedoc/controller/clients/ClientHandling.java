package com.guisedoc.controller.clients;

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
public class ClientHandling {
	
	@RequestMapping(value="/detailed", method = RequestMethod.POST, params={"id"})
	@ResponseBody
	public String getClientDetailedData(@RequestParam("id")String id){

		int ID = Integer.parseInt(id);
		
		JsonObject jsonObject = new JsonObject();

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
		
		System.out.println(c.getName());

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
				document.addElement("formatedDate", "10.01.2014");
				
				documents.addElement(document);
			}
		}
		
		jsonObject.addElement("documents",documents);
		
		jsonObject.addElement("response", "success");
		jsonObject.addElement("message", ClientMessages.CLIENT_DETAIL_DATA_SUCCESS);
		
		return jsonObject.getJsonString();
	}

}
