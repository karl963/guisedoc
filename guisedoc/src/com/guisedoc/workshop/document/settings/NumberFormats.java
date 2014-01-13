package com.guisedoc.workshop.document.settings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberFormats {
	
	public static DecimalFormat DISCOUNT_FORMAT(){
		return makeFormatWithPattern("###0.00");
	}
	
	public static DecimalFormat THREE_AFTER_COMMA(){
		return makeFormatWithPattern("###,###,##0.000");
	}
	
	public static DecimalFormat TWO_AFTER_COMMA(){
		return makeFormatWithPattern("###,###,##0.00");
	}
	
	public static DecimalFormat makeFormatWithPattern(String pattern){
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		
		DecimalFormat d = new DecimalFormat(pattern);
		d.setDecimalFormatSymbols(symbols);
		
		return d;
	}

}
