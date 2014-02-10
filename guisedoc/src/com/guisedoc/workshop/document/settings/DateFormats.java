package com.guisedoc.workshop.document.settings;

import java.text.SimpleDateFormat;

public class DateFormats {
	
	public static SimpleDateFormat DOT_DATE_FORMAT(){
		return new SimpleDateFormat("dd.MM.yyyy");
	}
	
	public static SimpleDateFormat HTML5_DATE_FORMAT(){
		return new SimpleDateFormat("yyyy-MM-dd");
	}
	
}
