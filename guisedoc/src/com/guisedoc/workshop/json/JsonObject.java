package com.guisedoc.workshop.json;

import java.util.HashMap;
import java.util.Map;

public class JsonObject {
	
	String elements;
	
	public JsonObject(){
		elements = "{";
	}
	
	/**
	 * Applicable object types:
	 * <ul>
	 * <li>JsonObject</li>
	 * <li>JsonArray</li>
	 * <li>String, long, int, double, boolean</li>
	 * </ul>
	 */
	public void addElement(String key, Object value){
		if(elements.length() != 1){ // not the first element added, add a comma
			elements += ",";
		}
		
		elements += "'"+key+"':";
		
		try{
			addObject(value);
		}
		catch(NotApplicableObjectException e){
			try {
				addObject("");
			} catch (NotApplicableObjectException e1) {}
		}
	}
	
	private void addObject(Object value) throws NotApplicableObjectException{
       
		if(value instanceof String){
        	
        	elements += "'"+value+"'";
        	
        }
        else if(value instanceof Long || value instanceof Double
        		|| value instanceof Integer || value instanceof Boolean){
        	
        	elements += value;
        	
        }
        else if(value instanceof JsonArray){
        	
        	elements += "[";
        	
        	boolean firstObject = true;
        	for(Object o : ((JsonArray)value).getElements()){
        		
        		if(!firstObject){
        			elements += ",";
        		}
        		
        		addObject(o);
        		
        		firstObject = false;
        	}
        	
        	elements += "]";
        }
        else if(value instanceof JsonObject){
        	
        	elements += ((JsonObject)value).getJsonString();
        	
        }
        else{
        	
        	throw new NotApplicableObjectException("Object type not supported for adding to the object");
        	
        }
	}
	
	public String getJsonString(){
		return (elements + "}").replace("'", "\"");
	}

}
