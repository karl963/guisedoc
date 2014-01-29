package com.guisedoc.controller.statistics;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guisedoc.messages.StatisticsMessages;
import com.guisedoc.object.Client;
import com.guisedoc.object.Product;
import com.guisedoc.object.StatisticsObject;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/statistics")
public class StatisticsHandling {
	
	@RequestMapping(value ="/delete", method = RequestMethod.POST, params = {"forDeleteStatisticsJSON"})
	@ResponseBody
	public String deleteSelectedStatisticsObjects(@RequestParam("forDeleteStatisticsJSON")String forDeleteStatisticsJSON){
		
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		com.google.gson.JsonArray objects = parser.parse(forDeleteStatisticsJSON).getAsJsonArray();
	    for(JsonElement objectElement : objects){
	    	StatisticsObject object = gson.fromJson(objectElement, StatisticsObject.class);
	    }
		
		return "success;"+StatisticsMessages.STATISTICS_DELETE_SUCCESS;
	}
	
	@RequestMapping(value ="/detail", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getDetailedStatisticData(@RequestParam("ID")String ID){
		

		StatisticsObject statObject = new StatisticsObject();
		
		JsonObject jsonObject = new JsonObject();
		JsonObject statObj = new JsonObject();
		
		//statObj.addElement("clientName", statObject.getClientName());
		//statObj.addElement("code", statObject.getCode());
		statObj.addElement("id", statObject.getID());
		statObj.addElement("amount", statObject.getAmount());
		statObj.addElement("totalPrice", statObject.getTotalPrice());

		jsonObject.addElement("statisticObject", statObj);
		jsonObject.addElement("message", StatisticsMessages.STATISTICS_DETAILED_DATA_GET_SUCCESS);
		jsonObject.addElement("response", "success");
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value ="/save", method = RequestMethod.POST, params = {"changedStatisticsJSON"})
	@ResponseBody
	public String changeDetailStatisticsObject(@RequestParam("changedStatisticsJSON")String changedStatisticsJSONString){
		
		Gson gson = new Gson();
		HashMap<String,Integer> map = gson.fromJson(changedStatisticsJSONString, HashMap.class);

		
		
		return "success;"+StatisticsMessages.STATISTICS_DETAILED_DATA_SAVE_SUCCESS;
	}

}
