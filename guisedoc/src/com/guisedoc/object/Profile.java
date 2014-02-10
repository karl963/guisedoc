package com.guisedoc.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

public class Profile {
	
	public static String DEFAULT_STRING = "";
	public static long DEFAULT_LONG = 0;
	
	private Map<String,Object[]> rules;
	
	private long ID;
	private String name;
	private long usersCount;
	
	/*
	 * Constructors
	 */
	public Profile(){
		ID = Profile.DEFAULT_LONG;
		name = Profile.DEFAULT_STRING;
		usersCount = Profile.DEFAULT_LONG;
		
		rules = new HashMap<String, Object[]>();
		addRule("ViewProducts",new Object[]{true,"Toodete vaatamine"});
		addRule("AddProducts",new Object[]{false,"Toodete lisamine"});
		addRule("DeleteProducts",new Object[]{false,"Toodete kustutamine"});
		addRule("ChangeProducts",new Object[]{false,"Toodete muutmine"});
		
		addRule("ViewClients",new Object[]{true,"Klientide vaatamine"});
		addRule("AddClients",new Object[]{false,"Klientide Lisamine"});
		addRule("ChangeClients",new Object[]{false,"Klientide muutmine"});
		addRule("DeleteClients",new Object[]{false,"Klientide kustutamine"});
		
		addRule("ViewFirm",new Object[]{true,"Firma andmete vaatamine"});
		addRule("ChangeFirm",new Object[]{false,"Firma andmete muutmine"});
		
		addRule("ViewStatistics",new Object[]{true,"Statistika vaatamine"});
		addRule("ChangeStatistics",new Object[]{false,"Statistika muutmine"});
		addRule("DeleteStatistics",new Object[]{false,"Statistika kustutamine"});
		addRule("DownloadStatistics",new Object[]{false,"Statistika alla laadimine"});
		
		addRule("ViewDocuments",new Object[]{true,"Dokumentide vaatamine"});
		addRule("CreateDocuments",new Object[]{false,"Dokumentide tekitamine"});
		addRule("ChangeDocuments",new Object[]{false,"Dokumentide muutmine"});
		addRule("DownloadDocuments",new Object[]{false,"Dokumentide alla laadimine"});
		
		addRule("ViewUsers",new Object[]{true,"Kasutajate vaatamine"});
		addRule("AddUsers",new Object[]{false,"Kasutajate lisamine"});
		addRule("DeleteUsers",new Object[]{false,"Kasutajate kustutamine"});
		addRule("ChangeUsers",new Object[]{false,"Kasutajate muutmine"});

		addRule("ViewProfiles",new Object[]{true,"Profiilide vaatamine"});
		addRule("AddProfiles",new Object[]{false,"Profiilide lisamine"});
		addRule("DeleteProfiles",new Object[]{false,"Profiilide kustutamine"});
		addRule("ChangeProfiles",new Object[]{false,"Profiilide muutmine"});
		
		addRule("ViewPrefixes",new Object[]{true,"Dokumendi eesliidete vaatamine"});
		addRule("ChangePrefixes",new Object[]{false,"Dokumendi eesliidete muutmine"});
	}
	
	/*
	 * methods
	 */
	
	public boolean isAllowed(String rule){
		if(rules.get(rule) != null){
			return (boolean)rules.get(rule)[0];
		}
		
		return false;
	}
	
	public void addRule(String key, Object[] objects){
		rules.put(key, objects);
	}
	
	/*
	 * updates the boolean value only on existing objects
	 */
	public void updateValue(String key, boolean value){
		rules.put(key, new Object[]{value,rules.get(key)[1]});
	}
	
	public Object[] getRule(String key){
		if(rules.get(key) == null) return new Object[]{false,"-undefined rule-"};
		else return rules.get(key);
	}
	
	public long getAllowedActionsCount() {
		long count = 0;
		Iterator<Entry<String,Object[]>> it = rules.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Object[]> pairs = it.next();
			if((Boolean)pairs.getValue()[0]){
				count += 1;
			}
		}
		return count;
	}
	
	public JsonArray getRulesJsonArray(){
		JsonArray jsonArray = new JsonArray();
		Iterator<Entry<String,Object[]>> it = rules.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Object[]> pairs = it.next();
			
			JsonObject rule = new JsonObject();
			rule.addElement("key", pairs.getKey());
			rule.addElement("value", pairs.getValue()[0]);
			rule.addElement("description", pairs.getValue()[1]);
			
			String group = "";
			if(pairs.getKey().endsWith("Products")){
				group = "Products";
			}
			else if(pairs.getKey().endsWith("Users")){
				group = "Users";
			}
			else if(pairs.getKey().endsWith("Profiles")){
				group = "Profiles";
			}
			else if(pairs.getKey().endsWith("Documents")){
				group = "Documents";
			}
			else if(pairs.getKey().endsWith("Statistics")){
				group = "Statistics";
			}
			else if(pairs.getKey().endsWith("Clients")){
				group = "Clients";
			}
			else if(pairs.getKey().endsWith("Firm")){
				group = "Firm";
			}
			else if(pairs.getKey().endsWith("Prefixes")){
				group = "Prefix";
			}
			
			rule.addElement("group", group);
			
			jsonArray.addElement(rule);
		}
		return jsonArray;
	}
	
	/*
	 * getters and setters
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(long usersCount) {
		this.usersCount = usersCount;
	}
	
	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}
	
}
