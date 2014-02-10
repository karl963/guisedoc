package com.guisedoc.controller.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.statistics.StatisticsImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.StatisticsMessages;
import com.guisedoc.object.StatisticsObject;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/statistics")
public class StatisticsSearch {
	
	@RequestMapping(value ="/search", method = RequestMethod.POST, params = {"statisticsJSONString"})
	@ResponseBody
	public String searchForStatistics(HttpSession session,
			@RequestParam("statisticsJSONString")String statJSONSTring){
		
		JsonObject jsonObject = new JsonObject();
		JsonArray statObjArray = new JsonArray();
		String message = null;
		String response = null;
		
		HashMap<String,String> map = new Gson().fromJson(statJSONSTring, HashMap.class);
		
		Object responseObject = new StatisticsImpl(session)
				.searchForStatistics(map);

		if(responseObject instanceof List){
			List<StatisticsObject> statisticObjects = (List<StatisticsObject>)responseObject;
			
			for(StatisticsObject statObject : statisticObjects){
				JsonObject statObj = new JsonObject();
				
				statObj.addElement("ID", statObject.getID());
				statObj.addElement("amount", statObject.getAmount());
				statObj.addElement("clientName", statObject.getClientName());
				statObj.addElement("code", statObject.getCode());
				statObj.addElement("name", statObject.getName());
				statObj.addElement("totalPrice", statObject.getTotalPrice());
				statObj.addElement("unit", statObject.getUnit());
				statObj.addElement("date", statObject.getFormatedDate());
				
				statObjArray.addElement(statObj);
			}
			
			response = "success";
			message = StatisticsMessages.STATISTICS_GET_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("statisticsObjects", statObjArray);
		jsonObject.addElement("statisticsType",map.get("statisticsType"));
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}

}
