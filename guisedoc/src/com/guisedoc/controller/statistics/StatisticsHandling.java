package com.guisedoc.controller.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.statistics.StatisticsImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
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
	public String deleteSelectedStatisticsObjects(HttpSession session,
			@RequestParam("forDeleteStatisticsJSON")String forDeleteStatisticsJSON){
		
		List<StatisticsObject> statObjects = new ArrayList<StatisticsObject>();
		Gson gson = new Gson();
		String message = null;
		String response = null;
		
		com.google.gson.JsonArray objects = new JsonParser().parse(forDeleteStatisticsJSON).getAsJsonArray();
	    for(JsonElement objectElement : objects){
	    	statObjects.add(gson.fromJson(objectElement, StatisticsObject.class));
	    }
	    
		ErrorType responseObject = new StatisticsImpl(session)
				.deleteStatisticsObjects(statObjects);
	    
	    if(responseObject == ErrorType.SUCCESS){
	    	response = "success";
	    	message = StatisticsMessages.STATISTICS_DELETE_SUCCESS;
	    }
	    else{
	    	response = "failure";
	    	message = ErrorMessages.getMessage(responseObject);
	    }
		
		return response+";"+message;
	}
	
	@RequestMapping(value ="/detail", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getDetailedStatisticData(HttpSession session,
			@RequestParam("ID")long ID){
	
		JsonObject jsonObject = new JsonObject();
		JsonObject statObj = new JsonObject();
		String message = null;
		String response = null;
		
		Object responseObject = new StatisticsImpl(session)
				.getStatObjectByID(ID);
		
		if(responseObject instanceof StatisticsObject){
			
			StatisticsObject statObject = (StatisticsObject)responseObject;
			
			statObj.addElement("ID"
					+ "", statObject.getID());
			statObj.addElement("amount", statObject.getAmount());
			statObj.addElement("totalPrice", statObject.getTotalPrice());
		
			response = "success";
			message = StatisticsMessages.STATISTICS_DETAILED_DATA_GET_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("statisticObject", statObj);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value ="/save", method = RequestMethod.POST, params = {"changedStatisticsJSON"})
	@ResponseBody
	public String changeDetailStatisticsObject(HttpSession session,
			@RequestParam("changedStatisticsJSON")String changedStatisticsJSONString){

		String message = null;
		String response = null;
		
		StatisticsObject statObject = new Gson().fromJson(changedStatisticsJSONString, StatisticsObject.class);
	    
		ErrorType responseObject = new StatisticsImpl(session)
				.saveStatisticsObject(statObject);
	    
	    if(responseObject == ErrorType.SUCCESS){
	    	response = "success";
	    	message = StatisticsMessages.STATISTICS_DETAILED_DATA_SAVE_SUCCESS;
	    }
	    else{
	    	response = "failure";
	    	message = ErrorMessages.getMessage(responseObject);
	    }
		
		return response+";"+message;
	}

}
