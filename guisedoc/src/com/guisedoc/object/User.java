package com.guisedoc.object;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.guisedoc.workshop.document.settings.DateFormats;

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
	
	private Date lastOnline;
	private long totalDeals;
	
	private Profile profile;
	private Settings settings;
	
	// for queries from database
	private long profileID;
	private long settingsID;
	
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
		profile = new Profile();
		lastOnline = new Date();
		totalDeals = User.DEFAULT_LONG;
		setSettings(new Settings());
		
		setProfileID(User.DEFAULT_LONG);
		setSettingsID(User.DEFAULT_LONG);
	}
	
	/*
	 * Methods
	 */

	public String getLastOnlineString(){
		return DateFormats.DOT_DATE_FORMAT().format(lastOnline);
	}
	
	/*
	 * Getters and setters
	 */

	public Date getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(Date lastOnline) {
		this.lastOnline = lastOnline;
	}

	public long getTotalDeals() {
		return totalDeals;
	}

	public void setTotalDeals(long totalDeals) {
		this.totalDeals = totalDeals;
	}

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
	
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public long getProfileID() {
		return profileID;
	}

	public void setProfileID(long profileID) {
		this.profileID = profileID;
	}

	public long getSettingsID() {
		return settingsID;
	}

	public void setSettingsID(long settingsID) {
		this.settingsID = settingsID;
	}
	
}
