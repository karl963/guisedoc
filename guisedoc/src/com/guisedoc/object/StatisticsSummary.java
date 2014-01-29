package com.guisedoc.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.guisedoc.workshop.document.settings.DateFormats;
import com.guisedoc.workshop.document.settings.Language;

public class StatisticsSummary {
	
	private static String DEFAULT_STRING = "";
	
	private List<StatisticsObject> statObjects;
	
	private String clientName;
	private String code;
	private String clientType;
	private Date startDate,endDate;
	
	private Language language;
	
	/*
	 * constructors
	 */
	public StatisticsSummary(){
		clientName = StatisticsSummary.DEFAULT_STRING;
		code =  StatisticsSummary.DEFAULT_STRING;
		clientType = StatisticsSummary.DEFAULT_STRING;
		statObjects = new ArrayList<StatisticsObject>();
		startDate = new Date();
		endDate = new Date();
		language = new Language("EST");
	}
	
	/*
	 * methods
	 */

	public String getSummaryName(){
		String name = "Kokkuvõte "+
				DateFormats.DOT_DATE_FORMAT().format(startDate)+
				"-"+
				DateFormats.DOT_DATE_FORMAT().format(endDate);

		if(!clientName.equals("")){
			name += "("+clientName+")";
		}
		
		return name;
	}
	
	public String getFormatedDatePeriod(){
		return DateFormats.DOT_DATE_FORMAT().format(startDate)+
				" - "+
				DateFormats.DOT_DATE_FORMAT().format(endDate);
	}
	
	/*
	 * gettters and setters
	 */
	public List<StatisticsObject> getStatObjects() {
		return statObjects;
	}

	public void setStatObjects(List<StatisticsObject> statObjects) {
		this.statObjects = statObjects;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
}
