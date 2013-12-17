package com.guisedoc.object;

public class StatisticsObject {
	
	public static String DEFAULT_STRING = "";
	public static Double DEFAULT_DOUBLE = 0.0;
	public static long DEFAULT_LONG = 0;
	public static String DEFAULT_DATE_STRING = "01.01.2014";
	
	private String code,name,clientName,date,unit;
	private Double amount,totalPrice;
	private long ID;
	
	/*
	 * constructors
	 */
	
	public StatisticsObject(){
		this.setID(StatisticsObject.DEFAULT_LONG);
		this.code = StatisticsObject.DEFAULT_STRING;
		this.name = StatisticsObject.DEFAULT_STRING;
		this.clientName = StatisticsObject.DEFAULT_STRING;
		this.date = StatisticsObject.DEFAULT_DATE_STRING;
		this.amount = StatisticsObject.DEFAULT_DOUBLE;
		this.totalPrice = StatisticsObject.DEFAULT_DOUBLE;
		this.setUnit(StatisticsObject.DEFAULT_STRING);
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
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
	
	

}
