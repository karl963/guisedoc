package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/product")
public class DocumentProductHandling {
	
	@RequestMapping(value="/detail", method = RequestMethod.POST, params={"detailedProductID"})
	@ResponseBody
	public String getProductDetailedData(@RequestParam("detailedProductID")long productID){
		
		Product p = new Product();
		
		for(Product pr : DocumentGet.allProducts){
			if(pr.getID() == productID){
				p = pr;
				break;
			}
		}
		
		p.setAdditional_Info("asd\ndddddasdasd");

		JsonObject jsonObject = new JsonObject();
		JsonObject product = new JsonObject();
		
		product.addElement("ID", p.getID());
		product.addElement("unitID", p.getUnitID());
		product.addElement("code", p.getCode());
		product.addElement("name", p.getName());
		product.addElement("e_name", p.getE_name());
		product.addElement("unit", p.getUnit());
		product.addElement("e_unit", p.getE_unit());
		product.addElement("price", p.getPrice());
		product.addElement("o_price", p.getO_price());
		product.addElement("discount", p.getDiscount());
		product.addElement("comments", p.getComments());
		product.addElement("additional_info", p.getAdditional_Info());
		product.addElement("storage", p.getStorage());
		product.addElement("calculateSum", p.isCalculateSum());
		product.addElement("amount", p.getAmount());
		
		jsonObject.addElement("product", product);
		jsonObject.addElement("message", DocumentMessages.PRODUCT_DETAILED_SUCCESS);
		jsonObject.addElement("response", "success");
		
		return jsonObject.getJsonString();
	}
	
	@RequestMapping(value="/addInput", method = RequestMethod.POST, params={"productJSON"})
	@ResponseBody
	public String addProductFromInput(@RequestParam("productJSON")String productJSON){
		
		Gson gson = new Gson();
		Product p = gson.fromJson(productJSON, Product.class);
		
		long id = p.getID();
		
		return "success;"+DocumentMessages.PRODUCT_ADD_SUCCESS+";"+id;
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.POST, params={"forDeleteJSON"})
	@ResponseBody
	public String deleteProductsFromDocument(@RequestParam("forDeleteJSON")String forDeleteJSON){

		Gson gson = new Gson();
	    
	    Map<String, List<Product>> map = gson.fromJson(forDeleteJSON, new TypeToken<HashMap<String, List<Product>>>(){}.getType());
	    
	    for(Product p : map.get("products")){
	    	
	    }
	    
		return "success;"+DocumentMessages.PRODUCT_DELETE_SUCCESS;
	}

	
	@RequestMapping(value="/save", method = RequestMethod.POST, params={"savedProductJSON"})
	@ResponseBody
	public String saveProductDetailedDataInDocument(@RequestParam("savedProductJSON")String productJSON){

		Gson gson = new Gson();
	    
		Product product = gson.fromJson(productJSON, Product.class);
	    
		return "success;"+DocumentMessages.PRODUCT_SAVE_SUCCESS;
	}
	
	@RequestMapping(value="/queue", method = RequestMethod.POST, params={"productsQueueNumbers","documentID"})
	@ResponseBody
	public String saveProductsQueueNumberInDocument(@RequestParam("productsQueueNumbers")String productsJSON,
			@RequestParam("documentID")long documentID){

		Gson gson = new Gson();

		List<Product> products = gson.fromJson(productsJSON, new TypeToken<ArrayList<Product>>(){}.getType());

		return "success;"+DocumentMessages.PRODUCT_SAVE_SUCCESS;
	}

}
