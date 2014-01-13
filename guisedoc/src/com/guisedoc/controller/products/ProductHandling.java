package com.guisedoc.controller.products;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.messages.DocumentMessages;
import com.guisedoc.messages.ProductMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/manage-products")
public class ProductHandling {

	@RequestMapping(value = "/add", method = RequestMethod.POST, params = {"addProductJSON"})
	@ResponseBody
	public String addProduct(@RequestParam("addProductJSON")String addProductJSON){
		
		Gson gson = new Gson();
		Product newProduct = gson.fromJson(addProductJSON, Product.class);
		
		newProduct.setID(ProductsGet.products.size());
		ProductsGet.products.add(newProduct);
		
		return "success"+";"+ProductMessages.PRODUCT_ADD_SUCCESS+";"+"id";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST, params = {"productJSONString"})
	@ResponseBody
	public String saveChangedProduct(@RequestParam("productJSONString")String productJSONString){

		/*
		Gson gson = new Gson();
	    JsonParser parser = new JsonParser();
	    JsonArray JsonProductarray = parser.parse(productJSONString).getAsJsonArray();

	    for(JsonElement productJsonElement : JsonProductarray){
	    	try{
	    		changedProducts.add(gson.fromJson(productJsonElement, Product.class));
	    	}catch(NumberFormatException x){
	    		return "error"+";"+ProductManagementController.NUMBER_FORMAT_EXCEPTION;
	    	}
	    }
	    */
		
		Gson gson = new Gson();
		Product product;
    	try{
    		product = gson.fromJson(productJSONString, Product.class);
    	}catch(NumberFormatException x){
    		return "error"+";"+ProductMessages.NUMBER_FORMAT_EXCEPTION;
    	}
	    
    	for(Product p: ProductsGet.products){
    		if(product.getID() == p.getID()){
    			p = product;
    			break;
    		}
    	}
	    
		return "success"+";"+ProductMessages.PRODUCTS_SAVE_SUCCESS;
	}
	
	
	
	@RequestMapping(value = "/detail", method = RequestMethod.POST, params = {"id"})
	@ResponseBody
	public String getProductDetailedInfo(@RequestParam("id")int id){
		
		List<Product> searchResultProducts = new ArrayList<Product>();
		
		for(int i = 0; i < 30 ; i++){
			Product p = new Product();
			p.setCode("CODEnr "+i);
			p.setName("product with a name nr "+i);
			p.setE_name("english name nr "+i);
			p.setPrice(new Double(i));
			p.setO_price(new Double(i*2));
			p.setUnit("uh"+i);
			p.setE_unit("unit"+i);
			p.setID(i);
			
			searchResultProducts.add(p);
		}
		
		Product pro = new Product();
		for(Product p : ProductsGet.products){
			if(p.getID()==id){
				pro = p;
				break;
			}
		}
		
		JsonObject jsonObject = new JsonObject();
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
		jsonObject.addElement("message", ProductMessages.PRODUCT_DETAIL_DATA_SUCCESS);
		jsonObject.addElement("response", "success");
		
		return jsonObject.getJsonString();
	}
}
