package com.guisedoc.object;

import com.google.gson.annotations.SerializedName;

public class Firm {
	
	public static String DEFAULT_STRING = "";
	public static long DEFAULT_LONG = 0;
	
	@SerializedName("ID")
	private long ID;
	@SerializedName("name")
	private String name;
	@SerializedName("address")
	private String address;
	@SerializedName("regNR")
	private String regNR;
	@SerializedName("kmkr")
	private String kmkr;
	@SerializedName("phone")
	private String phone;
	@SerializedName("email")
	private String email;
	@SerializedName("bank")
	private String bank;
	@SerializedName("bankAccountNR")
	private String bankAccountNR;
	@SerializedName("fax")
	private String fax;

	/*
	 * CONSTRUCTORS
	 */
	
	public String getKmkr() {
		return kmkr;
	}

	public void setKmkr(String kmkr) {
		this.kmkr = kmkr;
	}

	public Firm(){
		ID = Firm.DEFAULT_LONG;
		name = Firm.DEFAULT_STRING;
		address = Firm.DEFAULT_STRING;
		regNR = Firm.DEFAULT_STRING;
		kmkr = Firm.DEFAULT_STRING;
		phone = Firm.DEFAULT_STRING;
		email = Firm.DEFAULT_STRING;
		bank = Firm.DEFAULT_STRING;
		bankAccountNR = Firm.DEFAULT_STRING;
		fax = Firm.DEFAULT_STRING;
	}
	
	/*
	 * METHODS
	 */
	
	
	/*
	 * GETTERS AND SETTERS
	 */

	
	public String getName() {
		return name;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRegNR() {
		return regNR;
	}

	public void setRegNR(String regNR) {
		this.regNR = regNR;
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

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankAccountNR() {
		return bankAccountNR;
	}

	public void setBankAccountNR(String bankAccountNR) {
		this.bankAccountNR = bankAccountNR;
	}
	
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

}
