package com.guisedoc.messages;

import java.util.HashMap;
import java.util.Map;

import com.guisedoc.enums.ErrorType;

public class ErrorMessages {
	
	public static Map<ErrorType,String> errors = new HashMap<ErrorType,String>();
	
	public static String DATABASE_CONNECTION = "Viga andmebaasiga �hendumisel !";
	public static String DATABASE_QUERY = "Viga andmebaasist p�rimisel  !";
	public static String INVALID_LOGIN = "Valed login andmed  !";
	public static String SUCCESS = "Edukas p�ring!";
	
	static{
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_QUERY, DATABASE_QUERY);
		errors.put(ErrorType.INVALID_LOGIN, INVALID_LOGIN);
		errors.put(ErrorType.SUCCESS, SUCCESS);
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
	}
	
	public synchronized static String getMessage(ErrorType error){
		return errors.get(error);
	}
	
}
