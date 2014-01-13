package com.guisedoc.controller.products;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.guisedoc.object.Product;

@Controller
public class ProductsGet {
	
	public static List<Product> products = new ArrayList<Product>();
	
	@RequestMapping(value="/manage-products", method = RequestMethod.GET)
	public String getProductManageView(Model model){
		
		model.addAttribute("pageRequest","manage-products");
		
		return "manage-products";
	}

}
