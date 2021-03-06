package com.guisedoc.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.guisedoc.workshop.document.settings.DateFormats;

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
	@SerializedName("address")
	private String address;
	@SerializedName("additionalAddress")
	private String additionalAddress;
	@SerializedName("email")
	private String email;
	@SerializedName("totalDeals")
	private long totalDeals;
	@SerializedName("totalBoughtFor")
	private Double totalBoughtFor;
	
	private String lastDealNR;
	private Date lastDealDate;
	private List<ContactPerson> contactPersons;
	
	@SerializedName("selectedContactPerson")
	private ContactPerson selectedContactPerson;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public Client(){
		ID = Client.DEFAULT_LONG; 
		name = Client.DEFAULT_STRING;
		phone = Client.DEFAULT_STRING;
		address = Client.DEFAULT_STRING;
		additionalAddress = Client.DEFAULT_STRING;
		email = Client.DEFAULT_STRING;
		totalDeals = Client.DEFAULT_LONG;
		totalBoughtFor = Client.DEFAULT_DOUBLE;
		lastDealNR = Client.DEFAULT_STRING;
		lastDealDate = new Date();
		contactPersons = new ArrayList<ContactPerson>();
		setSelectedContactPerson(new ContactPerson());
	}
	
	/*
	 * METHODS
	 */

	public String getLastDealDateString(){
		if(lastDealDate==null) return "";
		return DateFormats.DOT_DATE_FORMAT().format(this.lastDealDate);
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

	public List<ContactPerson> getContactPersons() {
		return contactPersons;
	}

	public void setContactPersons(List<ContactPerson> contactPersons) {
		this.contactPersons = contactPersons;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAdditionalAddress() {
		return additionalAddress;
	}

	public void setAdditionalAddress(String additionalAddress) {
		this.additionalAddress = additionalAddress;
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

	public ContactPerson getSelectedContactPerson() {
		return selectedContactPerson;
	}

	public void setSelectedContactPerson(ContactPerson selectedContactPerson) {
		this.selectedContactPerson = selectedContactPerson;
	}
}
