package com.guisedoc.workshop.document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import com.guisedoc.enums.DocumentType;
import com.guisedoc.object.Document;
import com.guisedoc.object.Firm;
import com.guisedoc.object.StatisticsSummary;
import com.guisedoc.object.User;
import com.guisedoc.workshop.document.pdf.AdvanceInvoicePDF;
import com.guisedoc.workshop.document.pdf.DeliveryNotePDF;
import com.guisedoc.workshop.document.pdf.InvoicePDF;
import com.guisedoc.workshop.document.pdf.OrderConfirmationPDF;
import com.guisedoc.workshop.document.pdf.OrderPDF;
import com.guisedoc.workshop.document.pdf.QuotationPDF;
import com.guisedoc.workshop.document.pdf.StatisticsPDF;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

public class DocumentBuilder {
	
	public static byte[] build(Document document, Firm firm, User user){

		byte[] bytes = null;
		
		if(document.getType() == DocumentType.INVOICE){
			bytes = new InvoicePDF(document).make(firm, user);
		}
		else if(document.getType() == DocumentType.ADVANCE_INVOICE){
			bytes = new AdvanceInvoicePDF(document).make(firm, user);
		}
		else if(document.getType() == DocumentType.DELIVERY_NOTE){
			bytes = new DeliveryNotePDF(document).make(firm, user);
		}
		else if(document.getType() == DocumentType.ORDER){
			bytes = new OrderPDF(document).make(firm, user);
		}
		else if(document.getType() == DocumentType.QUOTATION){
			bytes = new QuotationPDF(document).make(firm, user);
		}
		else if(document.getType() == DocumentType.ORDER_CONFIRMATION){
			bytes = new OrderConfirmationPDF(document).make(firm, user);
		}
		
		return bytes;
	}
	
	public static byte[] build(StatisticsSummary summary,Firm firm, User user){
		
		byte[] bytes = new StatisticsPDF(summary).make(firm, user);
		
		return bytes;
	}

}
