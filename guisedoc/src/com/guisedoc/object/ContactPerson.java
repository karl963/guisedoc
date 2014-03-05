package com.guisedoc.object;

import com.google.gson.annotations.SerializedName;

public class ContactPerson {

	@SerializedName("ID")
	private long ID;
	@SerializedName("name")
	private String name;
	
	public ContactPerson(){
		this.ID = 0;
		this.name = "";
	}
	
	public ContactPerson(String name){
		this.name = name;
		this.ID = 0;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
