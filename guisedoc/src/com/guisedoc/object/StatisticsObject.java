package com.guisedoc.object;

import java.util.Date;

import com.guisedoc.workshop.document.settings.DateFormats;

public class StatisticsObject {
	
	public static String DEFAULT_STRING = "";
	public static Double DEFAULT_DOUBLE = 0.0;
	public static long DEFAULT_LONG = 0;
	
	private String code,name,clientName,unit;
	private Double amount,totalPrice;
	private Date date;
	
	private long ID;
	private long product_ID;
	private long document_ID;
	
	/*
	 * constructors
	 */
	
	public StatisticsObject(){
		this.setID(StatisticsObject.DEFAULT_LONG);
		this.code = StatisticsObject.DEFAULT_STRING;
		this.name = StatisticsObject.DEFAULT_STRING;
		this.clientName = StatisticsObject.DEFAULT_STRING;
		this.date = new Date();
		this.amount = StatisticsObject.DEFAULT_DOUBLE;
		this.totalPrice = StatisticsObject.DEFAULT_DOUBLE;
		this.setUnit(StatisticsObject.DEFAULT_STRING);
		this.setProduct_ID(StatisticsObject.DEFAULT_LONG);
		this.setDocument_ID(StatisticsObject.DEFAULT_LONG);
	}
	
	public String getFormatedDate(){
		if(date == null) return "";
		return DateFormats.DOT_DATE_FORMAT().format(date);
	}
	
	/*
	 * getters and setters
	 */
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getDocument_ID() {
		return document_ID;
	}

	public void setDocument_ID(long document_ID) {
		this.document_ID = document_ID;
	}

	public long getProduct_ID() {
		return product_ID;
	}

	public void setProduct_ID(long product_ID) {
		this.product_ID = product_ID;
	}
}
