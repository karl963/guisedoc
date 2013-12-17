package com.guisedoc.object;

import com.google.gson.annotations.SerializedName;

public class Product {
	
	public static String DEFAULT_STRING = "";
	public static Double DEFAULT_DOUBLE = 0.0;
	public static long DEFAULT_LONG = 0;
	
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
	@SerializedName("comment")
	private String comment;
	@SerializedName("additionalInfo")
	private String additionalInfo;

	@SerializedName("price")
	private Double price;
	@SerializedName("o_price")
	private Double o_price;
	@SerializedName("storage")
	private Double storage;

	@SerializedName("ID")
	private long ID;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public Product(){
		this.code = Product.DEFAULT_STRING;
		this.name = Product.DEFAULT_STRING;
		this.e_name = Product.DEFAULT_STRING;
		this.unit = Product.DEFAULT_STRING;
		this.e_unit = Product.DEFAULT_STRING;
		this.comment = Product.DEFAULT_STRING;
		this.additionalInfo = Product.DEFAULT_STRING;
		
		this.price = Product.DEFAULT_DOUBLE;
		this.o_price = Product.DEFAULT_DOUBLE;
		this.storage = Product.DEFAULT_DOUBLE;
		
		this.ID = Product.DEFAULT_LONG;
	}
	
	public Product(String code, String name, String e_name, String unit, String e_unit, Double price, Double o_price,Double storage, int ID){
		this.code = code;
		this.name = name;
		this.e_name = e_name;
		this.unit = unit;
		this.e_unit = e_unit;
		
		this.price =price;
		this.o_price = o_price;
		this.storage = storage;
		
		this.ID = ID;
	}
	
	/*
	 * methods
	 */
	
	
	
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
		ID = iD;
	}
	
	public Double getStorage() {
		return storage;
	}

	public void setStorage(Double storage) {
		this.storage = storage;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
