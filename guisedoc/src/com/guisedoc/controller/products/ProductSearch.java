package com.guisedoc.controller.products;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.guisedoc.messages.ProductMessages;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.json.JsonArray;
import com.guisedoc.workshop.json.JsonObject;

@Controller
@RequestMapping("/manage-products")
public class ProductSearch {
	
	@RequestMapping(value = "/search", method = RequestMethod.POST, params = {"searchProductJSON","inEstonian"})
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
		
		// put all products in json
		
		JsonObject jsonObj = new JsonObject();
		
		jsonObj.addElement("response", "success");
		jsonObj.addElement("message", ProductMessages.PRODUCTS_SEARCH_SUCCESS+" "+searchResultProducts.size()+" (tooteid kokku: "+totalProducts);
		
		
		JsonArray jsonArray = new JsonArray();
		
		for(Product product : searchResultProducts){
			
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
		
		jsonObj.addElement("products", jsonArray);

		return jsonObj.getJsonString();
	}

}
