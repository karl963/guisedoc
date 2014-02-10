package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.database.implement.product.ProductImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/product")
public class DocumentProductHandling {
	
	@RequestMapping(value="/detail", method = RequestMethod.POST, params={"detailedProductID"})
	@ResponseBody
	public String getProductDetailedData(HttpSession session,
			@RequestParam("detailedProductID")long productID){
		
		JsonObject jsonObject = new JsonObject();
		JsonObject productObj = new JsonObject();
		String message = null;
		String response = null;
		
		Object responseObject = new DocumentImpl(session)
				.getDocumentProductByID(productID);
		
		if(responseObject instanceof Product){
			
			Product product = (Product)responseObject;
			
			productObj.addElement("ID", product.getID());
			productObj.addElement("unitID", product.getUnitID());
			productObj.addElement("code", product.getCode());
			productObj.addElement("name", product.getName());
			productObj.addElement("e_name", product.getE_name());
			productObj.addElement("unit", product.getUnit());
			productObj.addElement("e_unit", product.getE_unit());
			productObj.addElement("price", product.getPrice());
			productObj.addElement("o_price", product.getO_price());
			productObj.addElement("discount", product.getDiscount());
			productObj.addElement("comments", product.getComments());
			productObj.addElement("additional_info", product.getAdditional_Info());
			productObj.addElement("storage", product.getStorage());
			productObj.addElement("calculateSum", product.isCalculateSum());
			productObj.addElement("amount", product.getAmount());
			productObj.addElement("totalSum", product.getTotalSum());
			
			response = "success";
			message = DocumentMessages.PRODUCT_DETAILED_SUCCESS;
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
	
	@RequestMapping(value="/addInput", method = RequestMethod.POST, params={"productJSON"})
	@ResponseBody
	public String addProductFromInput(HttpSession session,
			@RequestParam("productJSON")String productJSON, @RequestParam("documentID")long documentID){
		
		JsonObject jsonObject = new JsonObject();
		long ID = 0;
		String message = null;
		String response = null;
		
		Product product = new Gson().fromJson(productJSON, Product.class);
		
		// firstly, we add a new product to products
		Object productResponse = new ProductImpl(session)
				.addNewProduct(product);
		
		if(productResponse instanceof Long){
			
			product.setID((Long)productResponse);
			
			// now add the document product to document
			Object responseObject = new DocumentImpl(session)
					.addProductToDocument(product,documentID);
			
			if(responseObject instanceof Long){
				ID = (Long)responseObject;
				response = "success";
				message = DocumentMessages.PRODUCT_ADD_SUCCESS;
			}
			else{
				response = "failure";
				message = ErrorMessages.getMessage((ErrorType)responseObject);
			}
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)productResponse);
		}

		jsonObject.addElement("ID", ID);
		jsonObject.addElement("message", message);
		jsonObject.addElement("response", response);
		
		return jsonObject.getJsonString();
		
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST, params={"forDeleteJSON"})
	@ResponseBody
	public String deleteProductsFromDocument(HttpSession session,
			@RequestParam("forDeleteJSON")String forDeleteJSON){

		String message = null;
		String response = null;
		
	    Map<String, List<Product>> map = new Gson().fromJson(forDeleteJSON, new TypeToken<HashMap<String, List<Product>>>(){}.getType());
	    
		ErrorType responseObject = new DocumentImpl(session)
				.deleteProductsFromDocument(map.get("products"));
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = DocumentMessages.PRODUCT_DELETE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}

		return response+";"+message;
	}

	
	@RequestMapping(value="/save", method = RequestMethod.POST, params={"savedProductJSON"})
	@ResponseBody
	public String saveProductDetailedDataInDocument(HttpSession session,
			@RequestParam("savedProductJSON")String productJSON){
		
		String message = null;
		String response = null;
		
		Product product = new Gson().fromJson(productJSON, Product.class);
	    
		ErrorType responseObject = new DocumentImpl(session)
				.saveDocumentProduct(product);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = DocumentMessages.PRODUCT_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}

		return response+";"+message;
	}
	
	@RequestMapping(value="/queue", method = RequestMethod.POST, params={"productsQueueNumbers","documentID"})
	@ResponseBody
	public String saveProductsQueueNumberInDocument(HttpSession session,
			@RequestParam("productsQueueNumbers")String productsJSON,
			@RequestParam("documentID")long documentID){

		String message = null;
		String response = null;
		
		List<Product> products = new Gson().fromJson(productsJSON, new TypeToken<ArrayList<Product>>(){}.getType());
	    
		ErrorType responseObject = new DocumentImpl(session)
				.updateQueueNumbers(products,documentID);
		
		if(responseObject == ErrorType.SUCCESS){
			response = "success";
			message = DocumentMessages.PRODUCT_SAVE_SUCCESS;
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage(responseObject);
		}

		return response+";"+message;
	}

}
