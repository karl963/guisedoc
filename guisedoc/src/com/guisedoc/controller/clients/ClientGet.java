package com.guisedoc.controller.clients;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.guisedoc.object.Client;

@Controller
public class ClientGet {
	
	public static List<Client> clients = new ArrayList<Client>();

	@RequestMapping(value="/clients", method = RequestMethod.GET)
	public String getClientsView(Model model){

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
}