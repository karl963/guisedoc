package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.document.settings.Language;

@Controller
public class DocumentGet {
	
	public static List<Product> allProducts = new ArrayList<Product>();
	public static List<Document> documents = new ArrayList<Document>();
	
	@RequestMapping(value="/documents", method = RequestMethod.GET)
	public String getDocumentsView(Model model){
		new Language(Language.TYPE_ESTONIAN);
		model.addAttribute("pageRequest","documents");
		
		if(allProducts.size()==0){
			for(int i = 0; i < 10 ; i++){
				
				Product p = new Product();
				
				p.setCode("code"+i);
				p.setName("name"+i);
				p.setPrice(i+0.0);
				p.setStorage(i+0.0);
				p.setUnit("unit"+i);
				p.setID(i);
				p.setUnitID(i);
				
				p.setE_name("E_name"+i);
				p.setO_price(i+0.0);
				p.setE_unit("E_unit"+i);
				
				allProducts.add(p);
			}
		}
		
		model.addAttribute("documents",documents);
		model.addAttribute("howManyDocuments",documents.size());
		
		return "documents";
	}

}
