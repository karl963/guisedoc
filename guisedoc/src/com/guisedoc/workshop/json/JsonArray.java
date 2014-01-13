package com.guisedoc.workshop.json;

import java.util.ArrayList;
import java.util.Map;

public class JsonArray {
	
	ArrayList<Object> elements;
	
	public JsonArray(){
		elements = new ArrayList<Object>();
	}
	
	public void addElement(Object value){
		elements.add(value);
	}
	
	public ArrayList<Object> getElements(){
		return elements;
	}

}
