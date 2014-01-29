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
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.product.ProductImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.messages.ProductMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/products")
public class ProductSearch {
	
	@RequestMapping(value = "/search", method = RequestMethod.POST, params = {"searchProductJSON","inEstonian"})
	@ResponseBody
	public String searchForProducts(HttpSession session,
			@RequestParam("searchProductJSON")String searchProductJSON,
			@RequestParam("inEstonian")boolean inEstonian){
		
		JsonObject jsonObj = new JsonObject();
		JsonArray jsonArray = new JsonArray();
		String response = null;
		String message = null;
		
		Gson gson = new Gson();
		Product searchProduct = gson.fromJson(searchProductJSON, Product.class);

		Object responseObject = new ProductImpl(
				((Connector)session.getAttribute("connector")).getDatasource())
				.searchForProducts(searchProduct);
		
		if(responseObject instanceof Object[]){
			
			long totalProducts = (Long)((Object[])responseObject)[0];
			List<Product> searchResultProducts = (List<Product>)((Object[])responseObject)[1];
			
			// put all products in json
			for(int i = 1; i < searchResultProducts.size() ;i++){
				Product product = searchResultProducts.get(i);
				
				JsonObject job = new JsonObject();
				job.addElement("code",product.getCode());
				job.addElement("name",product.getName());
				job.addElement("e_name",product.getE_name());
				job.addElement("price",product.getPrice());
				job.addElement("o_price",product.getO_price());
				job.addElement("ID",product.getID());
				job.addElement("e_unit",product.getE_unit());
				job.addElement("unit",product.getUnit());
				job.addElement("storage",product.getStorage());
				
				jsonArray.addElement(job);
			}
			
			response = "success";
			message = ProductMessages.PRODUCTS_SEARCH_SUCCESS+" "+searchResultProducts.size()+" (tooteid kokku: "+totalProducts+")";
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)responseObject);
		}
		
		jsonObj.addElement("products", jsonArray);
		jsonObj.addElement("response", response);
		jsonObj.addElement("message", message);
		
		return jsonObj.getJsonString();
	}

}
