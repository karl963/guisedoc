package com.guisedoc.workshop.document.settings;

import com.itextpdf.text.Font;

public class DocumentFonts {
	
	public static Font TIMES_ROMAN_10_BOLD(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
	}
	
	public static Font TIMES_ROMAN_12_BOLD(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	}
	
	public static Font TIMES_ROMAN_10(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10);
	}
	
	public static Font TIMES_ROMAN_12(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12);
	}
	
	public static Font TIMES_ROMAN_8_ITALIC(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 8, Font.ITALIC);
	}
	
	public static Font TIMES_ROMAN_8(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 8);
	}
	
	public static Font TIMES_ROMAN_10_BOLD_ITALIC(){
		return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,com.itextpdf.text.Font.BOLD | com.itextpdf.text.Font.ITALIC);
	}
	
	public static Font TIMES_ROMAN_10_UNDERLINE_ITALIC(){
		return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,com.itextpdf.text.Font.UNDERLINE | com.itextpdf.text.Font.ITALIC);
	}
	
	public static Font TIMES_ROMAN_10_UNDERLINE_BOLD(){
		return new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,com.itextpdf.text.Font.UNDERLINE | com.itextpdf.text.Font.BOLD);
	}
	
	public static Font TIMES_ROMAN_10_ITALIC(){
		return new Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 10,com.itextpdf.text.Font.ITALIC);
	}
	
}
