package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/documents/product")
public class DocumentProductSearch {

	@RequestMapping(value="/search", method = RequestMethod.POST, params={"code","name","price","unit","isEstonian","includesPrice"})
	@ResponseBody
	public String searchForProducts(@RequestParam("code")String code,@RequestParam("name")String name,
			@RequestParam("price")Double price,@RequestParam("unit")String unit,
			@RequestParam("isEstonian")boolean isEstonian, @RequestParam("includesPrice")boolean includesPrice){

		List<Product> resultProducts = new ArrayList<Product>();

		Double searchOptimalLowPrice = null,searchOptimalHighPrice = null;
		if(price == null){
			price = 0.0;
		}
		if(includesPrice){
			searchOptimalLowPrice = price - price * 0.1;
			searchOptimalHighPrice = price + price * 0.1;
		}
		
		for(Product p : DocumentGet.allProducts){
			if(isEstonian){
				if(p.getName().contains(name) && p.getUnit().contains(unit)
						&& p.getCode().contains(code) ){
					if(includesPrice){
						if(searchOptimalLowPrice < p.getPrice() && searchOptimalHighPrice > p.getPrice()){
							resultProducts.add(p);
						}
					}
					else{
						resultProducts.add(p);
					}
				}
			}
			else{
				if(p.getE_name().contains(name) && p.getE_unit().contains(unit)
						&& p.getCode().contains(code) ){
					if(includesPrice){
						if(searchOptimalLowPrice < p.getO_price() && searchOptimalHighPrice > p.getO_price()){
							resultProducts.add(p);
						}
					}
					else{
						resultProducts.add(p);
					}
				}
			
			}
			
			if(resultProducts.size() == 7){
				break;
			}
		}
		
		
		
		JsonObject jsonObject = new JsonObject();
		JsonArray productArray = new JsonArray();
		
	    for(Product p : resultProducts){
	    	
	    	JsonObject product = new JsonObject();
	    	
	    	String productName,productUnit;
	    	Double productPrice;
	    	
	    	if(isEstonian){
	    		productName = p.getE_name();
	    		productUnit = p.getE_unit();
	    		productPrice = p.getO_price();
	    	}
	    	else{
	    		productName = p.getE_name();
	    		productUnit = p.getE_unit();
	    		productPrice = p.getO_price();
	    	}
	    	
	    	product.addElement("ID", p.getID());
	    	product.addElement("unitID", p.getUnitID());
	    	product.addElement("name", productName);
	    	product.addElement("code", p.getCode());
	    	product.addElement("price", productPrice);
	    	product.addElement("storage", p.getStorage());
	    	product.addElement("unit", productUnit);
	    	
	    	productArray.addElement(product);
	    }

	    jsonObject.addElement("products", productArray);
	    
	    jsonObject.addElement("response", "success");
	    jsonObject.addElement("message", DocumentMessages.PRODUCT_SEARCH_SUCCESS);
	    
		return jsonObject.getJsonString();
	}

	@RequestMapping(value="/add", method = RequestMethod.POST, params={"productID","isEstonian","amount"})
	@ResponseBody
	public String addProductFromSearch(@RequestParam("productID")long productID,
			@RequestParam("isEstonian")boolean isEstonian,@RequestParam("amount")Double amount){

		Product pro = new Product();
		
		for(Product p : DocumentGet.allProducts){
			if(p.getID() == productID){
				pro = p;
				p.setAmount(amount);
				break;
			}
		}
		
		JsonObject jsonObject = new JsonObject();
		JsonObject product = new JsonObject();

		product.addElement("ID", pro.getID());
		product.addElement("unitID", pro.getUnitID());
		product.addElement("name", pro.getName());
		product.addElement("e_name", pro.getE_name());
		product.addElement("unit", pro.getUnit());
		product.addElement("e_unit", pro.getE_unit());
		product.addElement("price", pro.getPrice());
		product.addElement("o_price", pro.getO_price());
		product.addElement("discount", pro.getDiscount());
		product.addElement("code", pro.getCode());
		product.addElement("calculateSum", pro.isCalculateSum());
		product.addElement("amount", amount);
		
	    jsonObject.addElement("product", product);
	    
	    jsonObject.addElement("response", "success");
	    jsonObject.addElement("message", DocumentMessages.PRODUCT_ADD_SUCCESS);
	    
		return jsonObject.getJsonString();
	}
}
