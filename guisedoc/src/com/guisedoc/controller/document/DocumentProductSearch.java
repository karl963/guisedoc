package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.database.implement.product.ProductImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/product")
public class DocumentProductSearch {

	@RequestMapping(value="/search", method = RequestMethod.POST, params={"code","name","price","unit","isEstonian","includesPrice"})
	@ResponseBody
	public String searchForProducts(HttpSession session,
			@RequestParam("code")String code,@RequestParam("name")String name,
			@RequestParam("price")Double price,@RequestParam("unit")String unit,
			@RequestParam("isEstonian")boolean isEstonian, @RequestParam("includesPrice")boolean includesPrice){

		JsonObject jsonObject = new JsonObject();
		JsonArray productArray = new JsonArray();
		String message = null;
		String response = null;
		
		// make the searchable product
		Product searchProduct = new Product();
		searchProduct.setCode(code);
		if(isEstonian){
			searchProduct.setName(name);
			searchProduct.setUnit(unit);
			searchProduct.setPrice(price);
		}
		else{
			searchProduct.setE_name(name);
			searchProduct.setE_unit(unit);
			searchProduct.setO_price(price);
		}
	    
		
		Object responseObject = new ProductImpl(session)
				.searchForProductsAdvanced(searchProduct,includesPrice,7);

		if(responseObject instanceof List){
			
			for(Object obj : (List<Object>)responseObject){
				JsonObject productObj = new JsonObject();
				Product product = (Product)obj;
				
		    	String productName,productUnit;
		    	Double productPrice;
		    	
		    	if(isEstonian){
		    		productName = product.getName();
		    		productUnit = product.getUnit();
		    		productPrice = product.getPrice();
		    	}
		    	else{
		    		productName = product.getE_name();
		    		productUnit = product.getE_unit();
		    		productPrice = product.getO_price();
		    	}
		    	
				productObj.addElement("ID", product.getID());
				productObj.addElement("code", product.getCode());
				productObj.addElement("name", productName);
				productObj.addElement("unit", productUnit);
				productObj.addElement("price", productPrice);
				productObj.addElement("storage", product.getStorage());
				
				productArray.addElement(productObj);
			}
		
			response = "success";
			message = DocumentMessages.PRODUCT_SEARCH_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}

	    jsonObject.addElement("products", productArray);
	    jsonObject.addElement("response", response);
	    jsonObject.addElement("message", message);
	    
		return jsonObject.getJsonString();
	}

	@RequestMapping(value="/add", method = RequestMethod.POST, params={"productID","documentType","amount","documentID"})
	@ResponseBody
	public String addProductFromSearch(HttpSession session,
			@RequestParam("productID")long productID,
			@RequestParam("documentType")String documentType,@RequestParam("amount")Double amount,
			@RequestParam("documentID")long documentID){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject productObj = new JsonObject();
		String message = null;
		String response = null;
		
		Object responseObject = new DocumentImpl(session)
				.addProductToDocument(productID,amount,documentID,documentType);
		
		if(responseObject instanceof Product){
			
			Product product = (Product)responseObject;
			
			productObj.addElement("unitID", product.getUnitID());
			productObj.addElement("code", product.getCode());
			productObj.addElement("name", product.getName());
			productObj.addElement("e_name", product.getE_name());
			productObj.addElement("unit", product.getUnit());
			productObj.addElement("e_unit", product.getE_unit());
			productObj.addElement("price", product.getPrice());
			productObj.addElement("o_price", product.getO_price());
			productObj.addElement("discount", product.getDiscount());
			productObj.addElement("calculateSum", product.isCalculateSum());
			productObj.addElement("totalSum", product.getTotalSum());
			productObj.addElement("amount", amount);
			
			response = "success";
			message = DocumentMessages.PRODUCT_ADD_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObject.addElement("product", productObj);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
}
