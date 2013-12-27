package com.guisedoc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.guisedoc.object.StatisticsObject;

@Controller
public class StatisticsController {
	
	public static String STATISTICS_GET_SUCCESS = "Statistika edukalt kuvatud";
	public static String STATISTICS_DELETE_SUCCESS = "Statistika objektide kustutamine õnnestus";
	public static String STATISTICS_DETAILED_DATA_GET_SUCCESS = "Statistika objekti detailid edukalt kuvatud";
	public static String STATISTICS_DETAILED_DATA_SAVE_SUCCESS = "Statistika objekti salvestamine õnnestus";
	
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	public String statisticsView(Model model){
		
		model.addAttribute("pageRequest","statistics");
		
		return "statistics";
	}
	
	@RequestMapping(value ="/statistics", method = RequestMethod.POST, params = {"statisticsJSONString"})
	@ResponseBody
	public String getStatisticsSearchData(@RequestParam("statisticsJSONString")String statJSONSTring){
		
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
		
		
		String statJSONReturnString = "{'response':'success','statisticsType':'"+map.get("statisticsType")+"',"+
				"'message':'"+StatisticsController.STATISTICS_GET_SUCCESS+"','statisticsObjects':[";
		
		boolean wasObjectBefore = false;
		for(StatisticsObject statObj : statisticObjects){
			
			if(wasObjectBefore){
				statJSONReturnString += ",";
			}
			
			statJSONReturnString += "{"+
					"'ID':"+statObj.getID()+","+
					"'amount':"+statObj.getAmount()+","+
					"'clientName':'"+statObj.getClientName()+"',"+
					"'code':'"+statObj.getCode()+"',"+
					"'name':'"+statObj.getName()+"',"+
					"'totalPrice':"+statObj.getTotalPrice()+","+
					"'unit':'"+statObj.getUnit()+"',"+
					"'date':'"+statObj.getDate()+"'"+
			"}";
					
			wasObjectBefore = true;
		}
		
		statJSONReturnString += "]}";
		
		statJSONReturnString = statJSONReturnString.replaceAll("'", "\""); // javascript parse json doesn't allow '
		
		return statJSONReturnString;
	}
	
	@RequestMapping(value ="/statistics", method = RequestMethod.POST, params = {"forDeleteStatisticsJSON"})
	@ResponseBody
	public String deleteSelectedStatisticsObjects(@RequestParam("forDeleteStatisticsJSON")String forDeleteStatisticsJSON){
		
		Gson gson = new Gson();
		HashMap<String,Integer> map = gson.fromJson(forDeleteStatisticsJSON, HashMap.class);

		
		
		return "success;"+StatisticsController.STATISTICS_DELETE_SUCCESS;
	}
	
	@RequestMapping(value ="/statistics", method = RequestMethod.POST, params = {"ID"})
	@ResponseBody
	public String getDetailedStatisticData(@RequestParam("ID")String ID){
		
		String statisticJSON = "{'response':'success','message':'"+StatisticsController.STATISTICS_DETAILED_DATA_GET_SUCCESS+
				"','statisticObject':{";
		
		StatisticsObject statObj = new StatisticsObject();
		
		statisticJSON += 
				"'clientName':'"+statObj.getClientName()+"',"+
				"'code':'"+statObj.getCode()+"',"+
				"'id':"+statObj.getID()+","+
				"'amount':"+statObj.getAmount()+","+
				"'totalPrice':"+statObj.getTotalPrice();
						
		statisticJSON += "}}";
		
		statisticJSON = statisticJSON.replaceAll("'", "\""); // javascript json parse doesn't allow '
		
		return statisticJSON;
	}
	
	@RequestMapping(value ="/statistics", method = RequestMethod.POST, params = {"changedStatisticsJSON"})
	@ResponseBody
	public String changeDetailStatisticsObject(@RequestParam("changedStatisticsJSON")String changedStatisticsJSONString){
		
		Gson gson = new Gson();
		HashMap<String,Integer> map = gson.fromJson(changedStatisticsJSONString, HashMap.class);

		
		
		return "success;"+StatisticsController.STATISTICS_DETAILED_DATA_SAVE_SUCCESS;
	}
	
}
