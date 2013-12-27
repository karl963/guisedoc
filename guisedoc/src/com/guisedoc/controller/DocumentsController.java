package com.guisedoc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

@Controller
public class DocumentsController {
	
	public static String PRODUCT_ADD_SUCCESS = "Toote lisamine õnnestus";
	public static String PRODUCT_DELETE_SUCCESS = "Toodete eemaldamine õnnestus";
	public static String PRODUCT_SEARCH_SUCCESS = "Toodete otsimine õnnestus";
	public static String PRODUCT_DETAILED_SUCCESS = "Toote täpsemate andmete kuvamine õnnestus";
	
	List<Product> mainProducts = new ArrayList<Product>();
	
	@RequestMapping(value="/documents", method = RequestMethod.GET)
	public String documentsView(Model model){
		
		model.addAttribute("pageRequest","documents");
		
		if(mainProducts.size()==0){
			for(int i = 0; i < 4000 ; i++){
				
				Product p = new Product();
				
				p.setCode("code"+i);
				p.setName("name"+i);
				p.setPrice(i+0.0);
				p.setStorage(i+0.0);
				p.setUnit("unit"+i);
				p.setID(i);
				
				p.setE_name("E_name"+i);
				p.setO_price(i+0.0);
				p.setE_unit("E_unit"+i);
				
				mainProducts.add(p);
			}
		}
		
		return "documents";
	}
	
	@RequestMapping(value="/documents", method = RequestMethod.POST, params={"productJSON"})
	@ResponseBody
	public String addProductFromInput(@RequestParam("productJSON")String productJSON){
		
		Gson gson = new Gson();
		Product p = gson.fromJson(productJSON, Product.class);
		
		long id = p.getID();
		
		return "success;"+DocumentsController.PRODUCT_ADD_SUCCESS+";"+id;
	}
	
	@RequestMapping(value="/documents", method = RequestMethod.POST, params={"productID","isEstonian","amount"})
	@ResponseBody
	public String addProductFromSearch(@RequestParam("productID")long productID,
			@RequestParam("isEstonian")boolean isEstonian,@RequestParam("amount")Double amount){

		String resultJSON = "{'message':'"+DocumentsController.PRODUCT_ADD_SUCCESS+"',"+
				"'response':'success','product':{";
		
		Product pro = new Product();
		
		for(Product p : mainProducts){
			if(p.getID() == productID){
				pro = p;
				p.setAmount(amount);
				break;
			}
		}
		
		resultJSON += 
				"'ID':"+pro.getID()+","+
				"'name':'"+pro.getName()+"',"+
				"'e_name':'"+pro.getE_name()+"',"+
				"'unit':'"+pro.getUnit()+"',"+
				"'e_unit':'"+pro.getE_unit()+"',"+
				"'price':"+pro.getPrice()+","+
				"'o_price':"+pro.getO_price()+","+
				"'discount':"+pro.getDiscount()+","+
				"'code':'"+pro.getCode()+"',"+
				"'amount':"+amount+
		"}}";
		
		resultJSON = resultJSON.replaceAll("'","\"");
	    
		return resultJSON;
	}
	
	@RequestMapping(value="/documents", method = RequestMethod.POST, params={"detailedProductID"})
	@ResponseBody
	public String getProductDetailedData(@RequestParam("detailedProductID")long productID){
		
		String resultJSON = "{'response':'success','message':'"+DocumentsController.PRODUCT_DETAILED_SUCCESS+"','product':{";
		
		Product p = new Product();
		
		for(Product pr : mainProducts){
			if(pr.getID() == productID){
				p = pr;
				break;
			}
		}
		
		resultJSON += ""+
			"'ID':"+p.getID()+","+
			"'code':'"+p.getCode()+"',"+
			"'name':'"+p.getName()+"',"+
			"'e_name':'"+p.getE_name()+"',"+
			"'unit':'"+p.getUnit()+"',"+
			"'e_unit':'"+p.getE_unit()+"',"+
			"'price':"+p.getPrice()+","+
			"'o_price':"+p.getO_price()+","+
			"'discount':"+p.getDiscount()+","+
			"'comment':'"+p.getComment()+"',"+
			"'additionalInfo':'"+p.getAdditionalInfo()+"',"+
			"'storage':"+p.getStorage()+","+
			"'amount':"+p.getAmount()+
		"}}";
		
		resultJSON = resultJSON.replaceAll("'","\"");
		
		return resultJSON;
	}
	
	@RequestMapping(value="/documents", method = RequestMethod.POST, params={"forDeleteJSON"})
	@ResponseBody
	public String deleteProducts(@RequestParam("forDeleteJSON")String forDeleteJSON){

		Gson gson = new Gson();
	    
	    Map<String, List<Product>> map = gson.fromJson(forDeleteJSON, new TypeToken<HashMap<String, List<Product>>>(){}.getType());
	    
	    for(Product p : map.get("products")){
	    	
	    }
	    
		return "success;"+DocumentsController.PRODUCT_DELETE_SUCCESS;
	}
	
	@RequestMapping(value="/documents", method = RequestMethod.POST, params={"code","name","price","unit","isEstonian","includesPrice"})
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
		
		for(Product p : mainProducts){
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
		
		
		
	    String productsJSON = "{'products':[";
	    boolean wasProduct = false;
	    
	    for(Product p : resultProducts){
	    	if(wasProduct){
	    		productsJSON += ",";
	    	}
	    	
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
	    	
	    	productsJSON += "{"+
	    		"'ID':"+p.getID()+","+
		    	"'name':'"+productName+"',"+
		    	"'code':'"+p.getCode()+"',"+
		    	"'price':"+productPrice+","+
		    	"'storage':"+p.getStorage()+","+
		    	"'unit':'"+productUnit+"'"+
		    "}";

	    	wasProduct = true;
	    }
	    
	    productsJSON += "],'message':'"+DocumentsController.PRODUCT_SEARCH_SUCCESS+"',"
	    		+ "'response':'success'}";
	    
	    productsJSON = productsJSON.replaceAll("'", "\"");
	    
		return productsJSON;
	}
	
}
