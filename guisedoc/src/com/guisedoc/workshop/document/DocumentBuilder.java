package com.guisedoc.workshop.document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import com.guisedoc.enums.DocumentType;
import com.guisedoc.object.Document;
import com.guisedoc.workshop.document.pdf.AdvanceInvoicePDF;
import com.guisedoc.workshop.document.pdf.DeliveryNotePDF;
import com.guisedoc.workshop.document.pdf.InvoicePDF;
import com.guisedoc.workshop.document.pdf.OrderConfirmationPDF;
import com.guisedoc.workshop.document.pdf.OrderPDF;
import com.guisedoc.workshop.document.pdf.QuotationPDF;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

public class DocumentBuilder {
	
	public static byte[] build(Document document){
		
		byte[] bytes = null;
		
		if(document.getType() == DocumentType.INVOICE){
			bytes = new InvoicePDF(document).make();
		}
		else if(document.getType() == DocumentType.ADVANCE_INVOICE){
			bytes = new AdvanceInvoicePDF(document).make();
		}
		else if(document.getType() == DocumentType.ORDER){
			bytes = new OrderPDF(document).make();
		}
		else if(document.getType() == DocumentType.DELIVERY_NOTE){
			bytes = new DeliveryNotePDF(document).make();
		}
		else if(document.getType() == DocumentType.QUOTATION){
			bytes = new QuotationPDF(document).make();
		}
		else if(document.getType() == DocumentType.ORDER_CONFIRMATION){
			bytes = new OrderConfirmationPDF(document).make();
		}
		
		return bytes;
	}

}
