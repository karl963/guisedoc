package com.guisedoc.controller.products;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.product.ProductImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

@Controller
public class ProductsGet {
	
	public static List<Product> products = new ArrayList<Product>();
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/products", method = RequestMethod.GET)
	public Object getProductManageView(HttpSession session, RedirectAttributes redirectAttributes,
			Model model){
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		String noteMessage = null;
		List<Product> products = null;
		
		// if the user wants the procuts to be loaded on page open
		if(((User)session.getAttribute("user")).getSettings().getSettingValue("loadAllProductsOnOpen")){
			
			Object response = new ProductImpl(
					((Connector)session.getAttribute("connector")).getDatasource()
					).getAllProducts();
			
			if(response instanceof List){
				products = (List<Product>)response;
			}
			else{
				noteMessage = ErrorMessages.getMessage((ErrorType)response);
			}
		}
		
		model.addAttribute("products",products);
		model.addAttribute("noteMessage",noteMessage);
		model.addAttribute("user",session.getAttribute("user"));
		
		return "products";
	}

}
