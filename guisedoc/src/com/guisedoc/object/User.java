package com.guisedoc.object;

import com.google.gson.annotations.SerializedName;

public class User {
	
	public static String DEFAULT_STRING = "";
	public static long DEFAULT_LONG = 0;
	
	@SerializedName("ID")
	private long ID;
	@SerializedName("userName")
	private String userName;
	@SerializedName("password")
	private String password;
	@SerializedName("name")
	private String name;
	@SerializedName("phone")
	private String phone;
	@SerializedName("email")
	private String email;
	@SerializedName("skype")
	private String skype;
	
	/*
	 * Constructors
	 */
	
	public User(){
		ID = User.DEFAULT_LONG;
		userName = User.DEFAULT_STRING;
		password = User.DEFAULT_STRING;
		name = User.DEFAULT_STRING;
		phone = User.DEFAULT_STRING;
		email = User.DEFAULT_STRING;
		skype = User.DEFAULT_STRING;
	}
	
	/*
	 * Methods
	 */

	
	/*
	 * Getters and setters
	 */

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}
	
	
	
}
