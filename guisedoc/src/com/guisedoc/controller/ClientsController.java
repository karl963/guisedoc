package com.guisedoc.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.object.Client;
import com.guisedoc.object.Product;

@Controller
public class ClientsController {
	
	public static String CLIENT_DATA_SAVE_SUCCESS = "Kliendi andmed edukalt salvestatud";
	public static String CLIENT_SEARCH_SUCCESS = "Leitud kliente";
	
	List<Client> clients = new ArrayList<Client>();
	
	@RequestMapping(value="/clients", method = RequestMethod.GET)
	public String clientsView(Model model){

		if(clients.size() == 0){
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
				
				clients.add(c1);
			}
		}
		
		model.addAttribute("clients",clients);
		model.addAttribute("pageRequest","clients");
		
		return "clients";
	}
	
	@RequestMapping(value="/clients", method = RequestMethod.POST, params={"id"})
	@ResponseBody
	public String getClientDetailedData(@RequestParam("id")String id){

		int ID = Integer.parseInt(id);
		
		String clientJSON = "{";
		
		clientJSON += "'response':'success','message':'Kliendi andmed edukalt kuvatud',"
				+ "'name':'"+clients.get(ID).getName()+"',"
				+ "'phone':'"+clients.get(ID).getPhone()+"',"
				+ "'email':'"+clients.get(ID).getEmail()+"',"
				+ "'contactPerson':'"+clients.get(ID).getContactPerson()+"',"
				+ "'address':'"+clients.get(ID).getAddress()+"',"
				+ "'lastDealNR':'"+clients.get(ID).getLastDealNR()+"',"
				+ "'lastDealDate':'"+clients.get(ID).getLastDealDateString()+"',"
				+ "'totalDeals':'"+clients.get(ID).getTotalDeals()+"'";

		
		clientJSON += "}";
		clientJSON = clientJSON.replaceAll("'", "\""); // javascript parseJSON doesn't accept ' , and needs to have "

		return clientJSON;
	}
	
	@RequestMapping(value="/clients", method = RequestMethod.POST, params={"clientJSONString"})
	@ResponseBody
	public String saveClientDetailedData(@RequestParam("clientJSONString")String clientJSONString){


		return "success;"+ClientsController.CLIENT_DATA_SAVE_SUCCESS;
	}
	
	@RequestMapping(value="/clients", method = RequestMethod.POST, params={"searchJSON"})
	@ResponseBody
	public String searchForClients(@RequestParam("searchJSON")String searchJSON){

		Gson gson = new Gson();
    	Map<String,Object> map = gson.fromJson(searchJSON, HashMap.class);

		
		int totalClients = 0;
		
		List<Client> foundClients = new ArrayList<Client>();
		
		for(int i = 0; i<100;i++){
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
		
		String clientJSON = "{'response':'success','message':'"+ClientsController.CLIENT_SEARCH_SUCCESS+" "+foundClients.size()+" (kokku "+totalClients+")',"
				+ "'clients':[";

		boolean wasClientsBefore = false;
		
		for(Client c : foundClients){
			
			if(wasClientsBefore){
				clientJSON += ",";
			}
			clientJSON +=
					"{'id':"+c.getID()+","
					+ "'name':'"+c.getName()+"',"
					+ "'contactPerson':'"+c.getContactPerson()+"',"
					+ "'totalBoughtFor':'"+c.getTotalBoughtFor()+"'}";
			
			wasClientsBefore = true;
		}

		clientJSON += "]}";
		clientJSON = clientJSON.replaceAll("'", "\""); // javascript parseJSON doesn't accept ' , and needs to have "

		return clientJSON;
	}

}
