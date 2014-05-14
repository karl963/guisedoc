package com.guisedoc.messages;

import java.util.HashMap;
import java.util.Map;

import com.guisedoc.enums.ErrorType;

public class ErrorMessages {
	
	public static Map<ErrorType,String> errors = new HashMap<ErrorType,String>();
	
	public static String DATABASE_CONNECTION = "Viga andmebaasiga ühendumisel !";
	public static String DATABASE_QUERY = "Viga andmebaasist pärimisel !";
	public static String INVALID_LOGIN = "Valed sisse logimise andmed !";
	public static String SUCCESS = "Edukas päring !";
	public static String NONE_FOUND = "Ühtegi objekti ei leitud !";
	public static String USER_NOT_EXISTING = "Kasutaja ei eksisteeri !";
	public static String USER_ALREADY_EXISTS = "Selle nimega kasutaja juba eksisteerib !";
	public static String NOTHING_TO_DELETE = "Pole midagi kustutada !";
	public static String NOTHING_TO_UPDATE = "Pole midagi uuendada !";
	public static String FAILURE = "Tegevus ebaõnnestus !";
	public static String DOCUMENT_ALREADY_OPENED = "Dokument on juba avatud !";

	static{
		errors.put(ErrorType.DATABASE_CONNECTION, DATABASE_CONNECTION);
		errors.put(ErrorType.DATABASE_QUERY, DATABASE_QUERY);
		errors.put(ErrorType.INVALID_LOGIN, INVALID_LOGIN);
		errors.put(ErrorType.SUCCESS, SUCCESS);
		errors.put(ErrorType.NONE_FOUND, NONE_FOUND);
		errors.put(ErrorType.USER_NOT_EXISTING, USER_NOT_EXISTING);
		errors.put(ErrorType.NOTHING_TO_DELETE, NOTHING_TO_DELETE);
		errors.put(ErrorType.NOTHING_TO_UPDATE, NOTHING_TO_UPDATE);
		errors.put(ErrorType.USER_ALREADY_EXISTS, USER_ALREADY_EXISTS);
		errors.put(ErrorType.FAILURE, FAILURE);
		errors.put(ErrorType.DOCUMENT_ALREADY_OPENED, DOCUMENT_ALREADY_OPENED);
	}
	
	public synchronized static String getMessage(ErrorType error){
		return errors.get(error);
	}
	
}
