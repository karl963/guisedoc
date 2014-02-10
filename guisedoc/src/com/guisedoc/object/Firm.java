package com.guisedoc.object;

import com.google.gson.annotations.SerializedName;

public class Firm {
	
	public static String DEFAULT_STRING = "";
	public static long DEFAULT_LONG = 0;
	
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
	@SerializedName("iban")
	private String iban;
	@SerializedName("swift")
	private String swift;
	@SerializedName("fax")
	private String fax;
	
	@SerializedName("logoURL")
	private String logoURL;
	@SerializedName("logoWidth")
	private int logoWidth;
	@SerializedName("logoHeight")
	private int logoHeight;
	
	/*
	 * CONSTRUCTORS
	 */

	public Firm(){
		name = Firm.DEFAULT_STRING;
		address = Firm.DEFAULT_STRING;
		regNR = Firm.DEFAULT_STRING;
		kmkr = Firm.DEFAULT_STRING;
		phone = Firm.DEFAULT_STRING;
		email = Firm.DEFAULT_STRING;
		bank = Firm.DEFAULT_STRING;
		iban = Firm.DEFAULT_STRING;
		swift = Firm.DEFAULT_STRING;
		fax = Firm.DEFAULT_STRING;
		logoURL = "http://www.bestroof.ee/g/logo.jpg";//Firm.DEFAULT_STRING;
		logoWidth = 85;
		logoHeight = 60;
	}
	
	/*
	 * METHODS
	 */
	
	
	/*
	 * GETTERS AND SETTERS
	 */
	
	public int getLogoWidth() {
		return logoWidth;
	}

	public void setLogoWidth(int logoWidth) {
		this.logoWidth = logoWidth;
	}

	public int getLogoHeight() {
		return logoHeight;
	}

	public void setLogoHeight(int logoHeight) {
		this.logoHeight = logoHeight;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getKmkr() {
		return kmkr;
	}

	public void setKmkr(String kmkr) {
		this.kmkr = kmkr;
	}
	
	public String getName() {
		return name;
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
	
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getSwift() {
		return swift;
	}

	public void setSwift(String swift) {
		this.swift = swift;
	}

}
