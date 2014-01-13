package com.guisedoc.controller.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.messages.StatisticsMessages;
import com.guisedoc.object.StatisticsObject;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/statistics")
public class StatisticsSearch {
	
	@RequestMapping(value ="/search", method = RequestMethod.POST, params = {"statisticsJSONString"})
	@ResponseBody
	public String searchForStatistics(@RequestParam("statisticsJSONString")String statJSONSTring){
		
		Gson gson = new Gson();
		HashMap<String,String> map = gson.fromJson(statJSONSTring, HashMap.class);
		
		List<StatisticsObject> statisticObjects = new ArrayList<StatisticsObject>();
		
		for(int i = 0; i<100; i++){
			StatisticsObject statObj = new StatisticsObject();
			
			statObj.setID(i);
			statObj.setAmount(i+0.0);
			statObj.setClientName("client"+i);
			statObj.setCode("code"+i);
			statObj.setName("name"+i);
			statObj.setTotalPrice(i*22.0);
			statObj.setUnit("unit"+i);
			
			statisticObjects.add(statObj);
		}
		
		JsonObject jsonObject = new JsonObject();
		JsonArray statObjArray = new JsonArray();
		
		for(StatisticsObject statObject : statisticObjects){
			JsonObject statObj = new JsonObject();
			
			statObj.addElement("ID", statObject.getID());
			statObj.addElement("amount", statObject.getAmount());
			statObj.addElement("clientName", statObject.getClientName());
			statObj.addElement("code", statObject.getCode());
			statObj.addElement("name", statObject.getName());
			statObj.addElement("totalPrice", statObject.getTotalPrice());
			statObj.addElement("unit", statObject.getUnit());
			statObj.addElement("date", statObject.getDate());
			
			statObjArray.addElement(statObj);
		}
		
		jsonObject.addElement("statisticsObjects", statObjArray);
		
		jsonObject.addElement("statisticsType",map.get("statisticsType"));
		jsonObject.addElement("message", StatisticsMessages.STATISTICS_DETAILED_DATA_GET_SUCCESS);
		jsonObject.addElement("response", "success");
		
		return jsonObject.getJsonString();
	}

}
