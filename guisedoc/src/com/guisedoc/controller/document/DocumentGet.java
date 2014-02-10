package com.guisedoc.controller.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.guisedoc.controller.UserValidator;
import com.guisedoc.database.implement.document.DocumentImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;
import com.guisedoc.workshop.document.settings.Language;

@Controller
public class DocumentGet {

	@RequestMapping(value="/documents", method = RequestMethod.GET)
	public Object getDocumentsView(HttpSession session, Model model,
			RedirectAttributes redirectAttributes){
		
		session.setAttribute("requestedPage", "documents");
		
		if(UserValidator.validateLoggedUser(session)){
			return UserValidator.directToLogin(session.getServletContext().getContextPath(),redirectAttributes);
		}
		
		List<Document> documents = new ArrayList<Document>();
		String noteMessage = null;
		
		Object response = new DocumentImpl(session)
				.getAllOpenedDocuments((User)session.getAttribute("user"));
		
		if(response instanceof List){
			documents = (List<Document>)response;
		}
		else{
			noteMessage = ErrorMessages.getMessage((ErrorType)response);
		}
		
		model.addAttribute("noteMessage",noteMessage);
		model.addAttribute("user",session.getAttribute("user"));
		model.addAttribute("documents",documents);
		model.addAttribute("howManyDocuments",documents.size());
		
		return "documents";
	}

}
