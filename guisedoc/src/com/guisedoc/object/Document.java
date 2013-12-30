package com.guisedoc.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.guisedoc.enums.DocumentType;

public class Document {
	
	public static String DEFAULT_STRING = "";
	public static Double DEFAULT_DOUBLE = 0.0;
	public static int DEFAULT_LONG = 0;
	public static boolean DEFAULT_BOOLEAN = false;

	@SerializedName("ID")
	private long ID;
	@SerializedName("number")
	private long number;
	@SerializedName("prefix")
	private String prefix;
	@SerializedName("name")
	private String name;
	@SerializedName("address")
	private String address;
	@SerializedName("additionalAddress")
	private String additionalAddress;
	@SerializedName("contactPerson")
	private String contactPerson;
	@SerializedName("orderNR")
	private String orderNR;
	@SerializedName("phone")
	private String phone;
	@SerializedName("email")
	private String email;
	@SerializedName("shipmentTime")
	private String shipmentTime;
	@SerializedName("shipmentAddress")
	private String shipmentAddress;
	@SerializedName("validDue")
	private Double validDue;
	@SerializedName("advance")
	private Double advance;
	@SerializedName("paymentRequirement")
	private int paymentRequirement;
	@SerializedName("paydInCash")
	private boolean paydInCash;
	@SerializedName("showDiscount")
	private boolean showDiscount;
	@SerializedName("addToStatistics")
	private boolean addToStatistics;
	@SerializedName("date")
	private Date date;
	
	private List<Product> products;
	private DocumentType type;
	
	/*
	 * Constructors
	 */
	
	public Document(){
		this.number = Document.DEFAULT_LONG;
		this.prefix = Document.DEFAULT_STRING;
		this.name = Document.DEFAULT_STRING;
		this.address = Document.DEFAULT_STRING;
		this.additionalAddress = Document.DEFAULT_STRING;
		this.contactPerson = Document.DEFAULT_STRING;
		this.orderNR = Document.DEFAULT_STRING;
		this.phone = Document.DEFAULT_STRING;
		this.email = Document.DEFAULT_STRING;
		this.shipmentTime = Document.DEFAULT_STRING;
		this.shipmentAddress = Document.DEFAULT_STRING;
		this.validDue = Document.DEFAULT_DOUBLE;
		this.advance = Document.DEFAULT_DOUBLE;
		this.paymentRequirement = Document.DEFAULT_LONG;
		this.paydInCash = Document.DEFAULT_BOOLEAN;
		this.showDiscount = Document.DEFAULT_BOOLEAN;
		this.addToStatistics = Document.DEFAULT_BOOLEAN;
		this.date = new Date();
		this.setProducts(new ArrayList<Product>());
		this.type = DocumentType.QUOTATION;
	}
	
	/*
	 * Methods
	 */
	
	public String getFullNumber(){
		return prefix+number;
	}
	
	
	/*
	 * getters and setters
	 */
	
	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
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

	public String getAdditionalAddress() {
		return additionalAddress;
	}

	public void setAdditionalAddress(String additionalAddress) {
		this.additionalAddress = additionalAddress;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getOrderNR() {
		return orderNR;
	}

	public void setOrderNR(String orderNR) {
		this.orderNR = orderNR;
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

	public String getShipmentTime() {
		return shipmentTime;
	}

	public void setShipmentTime(String shipmentTime) {
		this.shipmentTime = shipmentTime;
	}

	public String getShipmentAddress() {
		return shipmentAddress;
	}

	public void setShipmentAddress(String shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
	}

	public Double getValidDue() {
		return validDue;
	}

	public void setValidDue(Double validDue) {
		this.validDue = validDue;
	}

	public Double getAdvance() {
		return advance;
	}

	public void setAdvance(Double advance) {
		this.advance = advance;
	}

	public int getPaymentRequirement() {
		return paymentRequirement;
	}

	public void setPaymentRequirement(int paymentRequirement) {
		this.paymentRequirement = paymentRequirement;
	}

	public boolean isPaydInCash() {
		return paydInCash;
	}

	public void setPaydInCash(boolean paydInCash) {
		this.paydInCash = paydInCash;
	}

	public boolean isShowDiscount() {
		return showDiscount;
	}

	public void setShowDiscount(boolean showDiscount) {
		this.showDiscount = showDiscount;
	}

	public boolean isAddToStatistics() {
		return addToStatistics;
	}

	public void setAddToStatistics(boolean addToStatistics) {
		this.addToStatistics = addToStatistics;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
}
