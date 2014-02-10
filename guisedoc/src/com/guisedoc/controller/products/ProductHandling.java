package com.guisedoc.controller.products;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.product.ProductImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.ProductMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/products")
public class ProductHandling {

	@RequestMapping(value = "/add", method = RequestMethod.POST, params = {"addProductJSON"})
	@ResponseBody
	public String addProduct(HttpSession session,
			@RequestParam("addProductJSON")String addProductJSON){
		
		String message = null;
		String response = null;
		
		Product newProduct = new Gson().fromJson(addProductJSON, Product.class);
		
		Object responseObject = new ProductImpl(session)
				.addNewProduct(newProduct);

		long ID = 0;
		if(responseObject instanceof Long){
			ID = (Long)responseObject;
			response = "success";
			message = ProductMessages.PRODUCT_ADD_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		return response+";"+message+";"+ID;
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST, params = {"productJSONString"})
	@ResponseBody
	public String saveChangedProduct(HttpSession session,
			@RequestParam("productJSONString")String productJSONString){

		String response = null;
		String message = null;
		
		// make the product
		Product product;
    	try{
    		product = new Gson().fromJson(productJSONString, Product.class);
    	}catch(NumberFormatException x){
    		return "error"+";"+ProductMessages.NUMBER_FORMAT_EXCEPTION;
    	}
    	
    	ErrorType responseObject = new ProductImpl(session)
    			.saveProduct(product);
    			
    	if(responseObject == ErrorType.SUCCESS){
    		response = "success";
    		message = ProductMessages.PRODUCTS_SAVE_SUCCESS;
    	}
    	else{
    		response = "failure";
    		message = ErrorMessages.getMessage(responseObject);
    	}
	    
		return response+";"+message;
	}
	
	
	
	@RequestMapping(value = "/detail", method = RequestMethod.POST, params = {"id"})
	@ResponseBody
	public String getProductDetailedInfo(HttpSession session,@RequestParam("id")int id){
		
		JsonObject jsonObject = new JsonObject();
		String message = null;
		String response = null;
		
		Object responseObject = new ProductImpl(session)
				.getProductByID(id);

		// make the product json
		if(responseObject instanceof Product){
			Product pro = (Product) responseObject;
			
			JsonObject product = new JsonObject();
			
			product.addElement("code", pro.getCode());
			product.addElement("name", pro.getName());
			product.addElement("e_name", pro.getE_name());
			product.addElement("price", pro.getPrice());
			product.addElement("o_price", pro.getO_price());
			product.addElement("unit", pro.getUnit());
			product.addElement("e_unit", pro.getE_unit());
			product.addElement("ID", pro.getID());
			product.addElement("storage", pro.getStorage());

			jsonObject.addElement("product", product);
			message = ProductMessages.PRODUCT_DETAIL_DATA_SUCCESS;
			response = "success";
		}
		else{
			message = ErrorMessages.getMessage((ErrorType)responseObject);
			response = "failure";
		}

		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST, params = {"forDeleteJSON"})
	@ResponseBody
	public String deleteProducts(HttpSession session,
			@RequestParam("forDeleteJSON")String forDeleteJSON){
		
		String message = null;
		String response = null;
		List<Product> products = new ArrayList<Product>();
		
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		
		// make a list of products
		JsonArray productsArray = parser.parse(forDeleteJSON).getAsJsonArray();
	    for(JsonElement productElement : productsArray){
	    	products.add(gson.fromJson(productElement, Product.class));
	    }
	    
	    ErrorType responseObject = new ProductImpl(session)
	    		.deleteProducts(products);
	    
	    if(responseObject == ErrorType.SUCCESS){
	    	response = "success";
	    	message = ProductMessages.PRODUCTS_DELETE_SUCCESS;
	    }
	    else{
	    	response = "failure";
	    	message = ErrorMessages.getMessage(responseObject);
	    }
		return response+";"+message;
	}
}
