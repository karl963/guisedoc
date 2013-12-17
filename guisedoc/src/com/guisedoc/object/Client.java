package com.guisedoc.object;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Client {
	
	public static String DEFAULT_STRING = "";
	public static long DEFAULT_LONG = 0;
	public static Double DEFAULT_DOUBLE = 0.0;
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	@SerializedName("ID")
	private long ID;
	@SerializedName("name")
	private String name;
	@SerializedName("phone")
	private String phone;
	@SerializedName("contactPerson")
	private String contactPerson;
	@SerializedName("address")
	private String address;
	@SerializedName("email")
	private String email;
	@SerializedName("totalDeals")
	private long totalDeals;
	@SerializedName("totalBoughtFor")
	private Double totalBoughtFor;
	
	private String lastDealNR;
	private Date lastDealDate;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public Client(){
		ID = Client.DEFAULT_LONG; 
		name = Client.DEFAULT_STRING;
		phone = Client.DEFAULT_STRING;
		contactPerson = Client.DEFAULT_STRING;
		address = Client.DEFAULT_STRING;
		email = Client.DEFAULT_STRING;
		totalDeals = Client.DEFAULT_LONG;
		totalBoughtFor = Client.DEFAULT_DOUBLE;
		lastDealNR = Client.DEFAULT_STRING;
		lastDealDate = new Date();
	}
	
	/*
	 * METHODS
	 */

	public String getLastDealDateString(){
		return Client.DATE_FORMAT.format(this.lastDealDate);
	}
	
	/*
	 * getters and setters
	 */

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getTotalDeals() {
		return totalDeals;
	}

	public void setTotalDeals(long totalDeals) {
		this.totalDeals = totalDeals;
	}

	public Double getTotalBoughtFor() {
		return totalBoughtFor;
	}

	public void setTotalBoughtFor(Double totalBoughtFor) {
		this.totalBoughtFor = totalBoughtFor;
	}

	public String getLastDealNR() {
		return lastDealNR;
	}

	public void setLastDealNR(String lastDealNR) {
		this.lastDealNR = lastDealNR;
	}
	
	public Date getLastDealDate() {
		return lastDealDate;
	}

	public void setLastDealDate(Date lastDealDate) {
		this.lastDealDate = lastDealDate;
	}
}
