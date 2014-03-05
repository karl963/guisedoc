package com.guisedoc.controller.document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.implement.client.ClientImpl;
import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.enums.DocumentType;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;
import com.guisedoc.workshop.document.DocumentBuilder;
import com.guisedoc.workshop.document.settings.DateFormats;

@Controller
@RequestMapping("/documents")
public class DocumentHandling {

	@RequestMapping(value="/add", method = RequestMethod.POST, params={"newDocumentType"})
	@ResponseBody
	public String makeNewDocument(HttpSession session,
			@RequestParam("newDocumentType")String type){

		String message = null;
		String response = null;
		
		DocumentImpl documentImpl = new DocumentImpl(session);
		
		Object responseObject = documentImpl
				.addNewDocument((User)session.getAttribute("user"),type);
		
		if(responseObject instanceof Object[]){
			response = "success";
			message = DocumentMessages.DOCUMENT_SELECT_SUCCESS;
			
			message += ";"+((Object[])responseObject)[0];
			message += ";"+((Object[])responseObject)[1];
			message += ";"+((Object[])responseObject)[2];
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		return response+";"+message;
	}
	
	@RequestMapping(value="/close", method = RequestMethod.POST, params={"closeDocumentID"})
	@ResponseBody
	public String closeDocument(HttpSession session,
			@RequestParam("closeDocumentID")long documentID){
		
		String message = null;
		String response = null;
		
		ErrorType responseObject = new DocumentImpl(session)
				.checkAndCloseDocument(((User)session.getAttribute("user")).getID(),documentID);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = DocumentMessages.DOCUMENT_SELECT_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}

		return response+";"+message;
	}
	
	@RequestMapping(value="/save", method = RequestMethod.POST, params={"changedDocumentID","attributeName","value","valueType","clientID"})
	@ResponseBody
	public String saveChangedDocumentData(HttpSession session,
			@RequestParam("changedDocumentID")long documentID,@RequestParam("attributeName")String attributeName,
			@RequestParam("value")Object value,@RequestParam("valueType")String valueType,
			@RequestParam("clientID")long clientID){

		String message = null;
		String response = null;

		if(valueType.equals("string")){
			value = String.valueOf(value);
		}
		else if(valueType.equals("number")){
			value = String.valueOf(value).replace(",", ".");
		}
		else if(valueType.equals("date")){
			try {
				value = new Timestamp(DateFormats.HTML5_DATE_FORMAT().parse((String) value).getTime()).toString();
			} catch (ParseException e) {
				e.printStackTrace();
				value = new Timestamp(new Date().getTime()).toString();
			}
		}
		else if(valueType.equals("boolean")){
			value = Boolean.parseBoolean(String.valueOf(value));
		}
		
		// update accordingly, document or it's client
		ErrorType responseObject;
		if(clientID == 0){
			responseObject = new DocumentImpl(session)
				.saveDocumentAttribute(attributeName, value, documentID);
		}
		else{ // we update client date
			responseObject = new ClientImpl(session)
				.saveClientAttribute(attributeName, value, clientID);
		}
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = DocumentMessages.DOCUMENT_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}
	    
		return response+";"+message;
	}
}
