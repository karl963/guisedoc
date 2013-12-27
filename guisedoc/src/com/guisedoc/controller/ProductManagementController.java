package com.guisedoc.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.guisedoc.object.Product;

@Controller
public class ProductManagementController {
	
	public static String PRODUCT_ADD_SUCCESS = "Toode edukalt lisatud";
	public static String PRODUCTS_SAVE_SUCCESS = "Tooted edukalt salvestatud";
	public static String NUMBER_FORMAT_EXCEPTION = "Ei saa salvestada, numbri sisendiks pole number";
	public static String PRODUCTS_SEARCH_SUCCESS = "Leituid tooteid: ";
	public static String PRODUCT_DETAIL_DATA_SUCCESS = "Toote andmete kuvamine õnnestus";

	List<Product> products = new ArrayList<Product>();
	int i = 0;
	
	@RequestMapping(value="manage-products", method = RequestMethod.GET)
	public String productManageView(Model model){
		
		model.addAttribute("pageRequest","manage-products");
		
		return "manage-products";
	}
	
	@RequestMapping(value = "manage-products", method = RequestMethod.POST, params = {"addProductJSON"})
	@ResponseBody
	public String addProduct(@RequestParam("addProductJSON")String addProductJSON){
		
		Gson gson = new Gson();
		Product newProduct = gson.fromJson(addProductJSON, Product.class);
		
		newProduct.setID(i++);
		products.add(newProduct);
		
		return "success"+";"+ProductManagementController.PRODUCT_ADD_SUCCESS+";"+"id";
	}
	
	@RequestMapping(value = "manage-products", method = RequestMethod.POST, params = {"productJSONString"})
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
    		return "error"+";"+ProductManagementController.NUMBER_FORMAT_EXCEPTION;
    	}
	    
    	for(Product p: products){
    		System.out.println(product.getID()+ " - "+p.getID());
    		if(product.getID() == p.getID()){
    			System.out.println("leidis");
    			p = product;
    			break;
    		}
    	}
	    
		return "success"+";"+ProductManagementController.PRODUCTS_SAVE_SUCCESS;
	}
	
	@RequestMapping(value = "manage-products", method = RequestMethod.POST, params = {"searchProductJSON","inEstonian"})
	@ResponseBody
	public String searchForProducts(@RequestParam("searchProductJSON")String searchProductJSON,
			@RequestParam("inEstonian")boolean inEstonian){
		
		Gson gson = new Gson();
		Product searchProduct = gson.fromJson(searchProductJSON, Product.class);
		
		//List<Product> searchResultProducts = Product.searchForProductsInDatabase(code,name,price,unit,inEstonian);;

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
		
		int totalProducts = 30;
		
		String productsJSON = "{'response':'success','message':'"+
		ProductManagementController.PRODUCTS_SEARCH_SUCCESS+" "+searchResultProducts.size()+" (tooteid kokku: "+totalProducts+")'"
				+ ",'products':[";
		
		
		// put all products in json
		boolean thereWasAProductBeforeMe = false;
		
		for(Product product : products){
			
			if(thereWasAProductBeforeMe){
				productsJSON += ",";
			}
			productsJSON += "{'code':'"+product.getCode()+"','name':'"+product.getName()+"','e_name':'"+product.getE_name()
					+"','price':"+product.getPrice()+",'o_price':"+product.getO_price()+",'unit':'"+product.getUnit()+
					"','e_unit':'"+product.getE_unit()+"','ID':"+product.getID()+",'storage':"+product.getStorage()+"}";
			thereWasAProductBeforeMe = true;
			
		}
		
		productsJSON += "]}";
		
		productsJSON = productsJSON.replaceAll("'", "\""); // javascript parseJSON doesn't accept ' , and needs to have "
		
		return productsJSON;
	}
	
	@RequestMapping(value = "manage-products", method = RequestMethod.POST, params = {"id"})
	@ResponseBody
	public String getProductDetailedInfo(@RequestParam("id")int id){

		String productJSON = "{'response':'success','message':'"+ProductManagementController.PRODUCT_DETAIL_DATA_SUCCESS+"',";

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
		
		Product product = new Product();
		for(Product p : products){
			if(p.getID()==id){
				product = p;
				break;
			}
		}
		
		productJSON += "'code':'"+product.getCode()+
				"','name':'"+product.getName()+
				"','e_name':'"+product.getE_name()+
				"','price':"+product.getPrice()+
				",'o_price':"+product.getO_price()+
				",'unit':'"+product.getUnit()+
				"','e_unit':'"+product.getE_unit()+
				"','ID':"+product.getID()+
				",'storage':"+product.getStorage();
			
		productJSON += "}";
		
		productJSON = productJSON.replaceAll("'", "\""); // javascript parseJSON doesn't accept ' , and needs to have "

		return productJSON;
	}
	
}
