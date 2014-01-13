package com.guisedoc.workshop.document.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

public class Language {
	
	public static String TYPE_ESTONIAN = "EST";
	public static String TYPE_ENGLISH = "ENG";
	
	private String type;
	private Map<String,String> elements;

	/*
	 * Constructor
	 */
	public Language(String type){
		elements = new HashMap<String,String>();
		this.setType(type);
	}
	
	/*
	 * methods
	 */
	private void loadDataFromFile(String type){

		Properties prop = new Properties();
		
		try {
			prop.load(new InputStreamReader(new ClassPathResource("/languages/"+type+".properties").getInputStream(),"UTF-8"));
	 	
			for(Object o : prop.keySet()){
				elements.put(String.valueOf(o), prop.getProperty(String.valueOf(o)));
			}
		}catch (Exception ex) {
	 		ex.printStackTrace();
	    }
	}
	
	public String get(String key){
		return elements.get(key);
	}

	/*
	 * getters, setters
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		loadDataFromFile(type);
	}

}
