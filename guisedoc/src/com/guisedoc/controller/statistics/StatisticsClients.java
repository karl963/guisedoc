package com.guisedoc.controller.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.client.ClientImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
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
	public String getClientNamesByType(HttpSession session,
			@RequestParam("clientType")String clientType){

		JsonObject jsonObject = new JsonObject();
		JsonArray clientArray = new JsonArray();
		String response = null;
		String message = null;
		boolean nonBuyers = false;
		boolean realBuyers = false;
		boolean sellers = false;
		
		if(clientType.equals("seller")){
			sellers = true;
		}
		else if(clientType.equals("nonBuyer")){
			nonBuyers = true;
		}
		else if(clientType.equals("realBuyer")){
			realBuyers = true;
		}
		
		Object responseObject = new ClientImpl(session)
				.searchForTypeClients(new Client(), nonBuyers, realBuyers, sellers);
		
		if(responseObject instanceof Object[]){
			
			List<Client> clients = (List<Client>)((Object[])responseObject)[1];
			
			for(Client client : clients){

				JsonObject clientObj = new JsonObject();
				clientObj.addElement("ID", client.getID());
				clientObj.addElement("name", client.getName());
				clientObj.addElement("contactPerson", client.getContactPerson());
				
				clientArray.addElement(clientObj);
			}
			
			response = "success";
			message = StatisticsMessages.CLIENTS_GET_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}

		jsonObject.addElement("clients", clientArray);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}

}
