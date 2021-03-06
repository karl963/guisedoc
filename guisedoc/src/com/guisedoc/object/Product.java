package com.guisedoc.object;

import com.google.gson.annotations.SerializedName;

public class Product {
	
	public static String DEFAULT_STRING = "";
	public static Double DEFAULT_DOUBLE = 0.0;
	public static long DEFAULT_LONG = 0;
	public static boolean DEFAULT_BOOLEAN = true;
	
	@SerializedName("code")
	private String code;
	@SerializedName("name")
	private String name;
	@SerializedName("e_name")
	private String e_name;
	@SerializedName("unit")
	private String unit;
	@SerializedName("e_unit")
	private String e_unit;
	@SerializedName("comments")
	private String comments;
	@SerializedName("additional_info")
	private String additional_info;

	@SerializedName("price")
	private Double price;
	@SerializedName("o_price")
	private Double o_price;
	@SerializedName("storage")
	private Double storage;
	@SerializedName("discount")
	private Double discount;
	@SerializedName("amount")
	private Double amount;
	@SerializedName("totalSum")
	private Double totalSum;

	@SerializedName("ID")
	private long ID;
	@SerializedName("unitID")
	private long unitID;
	@SerializedName("queueNumber")
	private long queueNumber;
	
	@SerializedName("calculateSum")
	private boolean calculateSum;
	
	/*
	 * CONSTRUCTORS
	 */

	public Product(){
		this.code = Product.DEFAULT_STRING;
		this.name = Product.DEFAULT_STRING;
		this.e_name = Product.DEFAULT_STRING;
		this.unit = Product.DEFAULT_STRING;
		this.e_unit = Product.DEFAULT_STRING;
		this.comments = Product.DEFAULT_STRING;
		this.additional_info = Product.DEFAULT_STRING;
		
		this.price = Product.DEFAULT_DOUBLE;
		this.o_price = Product.DEFAULT_DOUBLE;
		this.storage = Product.DEFAULT_DOUBLE;
		this.discount = Product.DEFAULT_DOUBLE;
		this.amount = Product.DEFAULT_DOUBLE;
		this.totalSum = Product.DEFAULT_DOUBLE;
		this.queueNumber = Product.DEFAULT_LONG;
		
		this.ID = Product.DEFAULT_LONG;
		this.unitID = Product.DEFAULT_LONG;
		this.calculateSum = Product.DEFAULT_BOOLEAN;
	}
	
	public Product(String code, String name, String e_name, 
			String unit, String e_unit, Double price,
			Double o_price,Double storage,Double discount,
			Double amount,Double totalSum, int ID){
		
		this.code = code;
		this.name = name;
		this.e_name = e_name;
		this.unit = unit;
		this.e_unit = e_unit;
		
		this.price =price;
		this.o_price = o_price;
		this.storage = storage;
		this.discount = discount;
		this.amount = amount;
		this.totalSum = totalSum;
		this.queueNumber = Product.DEFAULT_LONG;
		
		this.calculateSum = Product.DEFAULT_BOOLEAN;
		
		this.ID = ID;
		this.unitID = Product.DEFAULT_LONG;
	}
	
	/*
	 * methods
	 */
	
	public boolean isCalculateSum() {
		return calculateSum;
	}

	public void setCalculateSum(boolean calculateSum) {
		this.calculateSum = calculateSum;
	}

	public String toString(){
		return "ID:"+ID+","+
				"code:"+code+","+
				"name:"+name+","+
				"e_name:"+e_name+","+
				"unit:"+unit+","+
				"e_unit:"+e_unit+","+
				"price:"+price+","+
				"o_price:"+o_price+","+
				"amount:"+amount+","+
				"discount:"+discount+","+
				"additional_info:"+additional_info+","+
				"comments:"+comments;
	}
	
	public boolean hasAdditionalInformation(){
		return (!additional_info.replaceAll("\\s","").equals(""));
	}
	
	/*
	 * GETTERS AND SETTERS
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

	public String getE_name() {
		return e_name;
	}

	public void setE_name(String e_name) {
		this.e_name = e_name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getE_unit() {
		return e_unit;
	}

	public void setE_unit(String e_unit) {
		this.e_unit = e_unit;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getO_price() {
		return o_price;
	}

	public void setO_price(Double o_price) {
		this.o_price = o_price;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		this.ID = iD;
	}
	
	public long getUnitID() {
		return unitID;
	}

	public void setUnitID(long unitiD) {
		this.unitID = unitiD;
	}
	
	public Double getStorage() {
		return storage;
	}

	public void setStorage(Double storage) {
		this.storage = storage;
	}
	
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getAdditional_Info() {
		return additional_info;
	}

	public void setAdditional_Info(String additional_info) {
		this.additional_info = additional_info;
	}

	public Double getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(Double totalSum) {
		this.totalSum = totalSum;
	}

	public long getQueueNumber() {
		return queueNumber;
	}

	public void setQueueNumber(long queueNumber) {
		this.queueNumber = queueNumber;
	}
	
}
