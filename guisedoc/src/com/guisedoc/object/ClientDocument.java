package com.guisedoc.object;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.guisedoc.workshop.document.settings.DateFormats;

public class ClientDocument {
	
	private Date date;
	private String fullNumber;
	double totalSum;
	
	/*
	 * Constructors
	 */
	public ClientDocument(){
		this.date = new Date();
		this.fullNumber = Document.DEFAULT_STRING;
		this.totalSum = Document.DEFAULT_DOUBLE;
	}
	
	/*
	 * Methods
	 */
	
	public String getFullNumber(){
		return fullNumber;
	}
	
	public String getFormatedDate(){
		if(date == null) return "";
		return DateFormats.DOT_DATE_FORMAT().format(this.date);
	}
	
	public String getHtml5FormatedDate(){
		if(date == null) return "";
		return DateFormats.HTML5_DATE_FORMAT().format(this.date);
	}
	
	/*
	 * getters and setters
	 */
	
	public double getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(double totalSum) {
		this.totalSum = totalSum;
	}

	public Date getDate() {
		if(date == null) return new Date();
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setFullNumber(String fullNumber){
		this.fullNumber = fullNumber;
	}

}
