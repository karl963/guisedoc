package com.guisedoc.object;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	
	public static String DEFAULT_STRING = "";
	public static long DEFAULT_LONG = 0;
	
	private Map<String,Object[]> settings;
	
	/*
	 * Constructors
	 */
	public Settings(){

		settings = new HashMap<String, Object[]>();
		
		settings.put("autoSetDocNumber",new Object[]{true,"Lisa uue dokumendi number automaatselt (eesliide+id)"});
		
		settings.put("saveSearchResultType",new Object[]{true,"Salvesta otsingu tüübid"});
		settings.put("saveSearchResultInput",new Object[]{true,"Salvesta otsingu sisendid"});
		
		settings.put("loadAllProductsOnOpen",new Object[]{false,"Näita kõiki tooteid lehe avamisel"});
		settings.put("loadAllClientsOnOpen",new Object[]{false,"Näita kõiki kliente lehe avamisel"});
		
	}
	
	/*
	 * methods
	 */
	public boolean getSettingValue(String key){
		if(settings.get(key) == null) return false;
		else return Boolean.parseBoolean(String.valueOf(settings.get(key)[0]));
	}
	
	public String getSettingDescription(String key){
		if(settings.get(key) == null) return "-Unknown setting-";
		else return String.valueOf(settings.get(key)[1]);
	}
	
	/*
	 * updates the value of existing object only
	 */
	public void updateValue(String key, boolean value){
		settings.put(key, new Object[]{value,settings.get(key)[1]});
	}
	
	/*
	 * getters and setters
	 */

}
