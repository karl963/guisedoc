package com.guisedoc.workshop.document.pdf;

import java.io.ByteArrayOutputStream;

import com.guisedoc.object.Document;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;
import com.guisedoc.workshop.document.settings.DocumentFonts;
import com.guisedoc.workshop.document.settings.HeaderFooter;
import com.guisedoc.workshop.document.settings.Language;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class DeliveryNotePDF {
	
	Document DOC;
	
	public DeliveryNotePDF(Document d){
		this.DOC = d;
	}
	
	public byte[] make(Firm firm, User user){
		
		/*
		 * make metadata for the document
		 */
		Language language = DOC.getLanguage();
		
		Font TR_12_B = DocumentFonts.TIMES_ROMAN_12_BOLD();
		Font TR_12 = DocumentFonts.TIMES_ROMAN_12();
		Font TR_10_B = DocumentFonts.TIMES_ROMAN_10_BOLD();
		Font TR_10 = DocumentFonts.TIMES_ROMAN_10();
		Font TR_10_I = DocumentFonts.TIMES_ROMAN_10_ITALIC();
		Font TR_10_B_I = DocumentFonts.TIMES_ROMAN_10_BOLD_ITALIC();
		Font TR_8_I = DocumentFonts.TIMES_ROMAN_8_ITALIC();
		
		com.itextpdf.text.Document blackBoard = null;
		com.itextpdf.text.Document finalDocument = null;
		
		ByteArrayOutputStream byteOutputStream = null;
		ByteArrayOutputStream temporaryByteStream = null;
		PdfReader reader;
		
		HeaderFooter pageEvent = new HeaderFooter(firm,true,true);
		
		boolean wasError = false;
		
		/*
		 * make the document
		 */
		
		try{
			
			// make an empty cell and paragraph, going to need them later multiple times
			PdfPCell emptyCell = new PdfPCell(new Phrase(" "));
			emptyCell.setBorder(Rectangle.NO_BORDER);
			
			Paragraph emptyParagraph = new Paragraph(" ");

			/*
			 * First paragraph of the page, included in every page afterwards
			 */
			Paragraph pageStartParagraph = new Paragraph();
			PdfPTable headerTable = new PdfPTable(2); // table with 2 columns
			headerTable.setWidths(new int[]{270,200}); // table column sizes
			headerTable.setWidthPercentage(100);
			
			// Type cell
			PdfPCell documentTypeCell = new PdfPCell(new Phrase(language.get("deliveryNote")+":  "+DOC.getFullNumber(), TR_12_B));
			headerTable.addCell(documentTypeCell);
			
			// Date
			PdfPCell dateCell = new PdfPCell(new Phrase(DOC.getFormatedDate(),TR_10));
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(dateCell);
			
			pageStartParagraph.add(headerTable);
			
			// CE mark
			if(DOC.isShowCE()){
				PdfPTable CETable = new PdfPTable(2);
				CETable.setWidthPercentage(100);
				CETable.setWidths(new int[]{25,445});
				
				// CE image
				Image ce = null;
				try {
					ce = Image.getInstance(getClass().getResource("/images/ce.png"));
					ce.scaleToFit(24,24);
					ce.setAlignment(Element.ALIGN_CENTER);
				} catch (Exception e) {}
				
				PdfPCell ceCell = new PdfPCell(ce);
				ceCell.setBorder(Rectangle.NO_BORDER);
				CETable.addCell(ceCell);
				
				// CE description
				PdfPCell CEspecCell = new PdfPCell(new Phrase(DOC.getCeSpecification(),TR_10));
				
				CEspecCell.setBorder(Rectangle.NO_BORDER);
				CETable.addCell(CEspecCell);
				
				pageStartParagraph.add(CETable);
			}
			
			/*
			 * Let's make a copy for later page checking
			 */
			blackBoard = new com.itextpdf.text.Document();
			
			temporaryByteStream = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(blackBoard,temporaryByteStream);
			pageEvent.setPageStartParagraph(pageStartParagraph);
			writer.setPageEvent(pageEvent);

			blackBoard.open();
			
			/*
			 * First paragraph
			 */
			Paragraph firstParagraph = new Paragraph();
			
			/*
			 * Client and shipment data
			 */
			Paragraph clientShipmentParagraph = new Paragraph();
			
			PdfPTable clientShipmentTable = new PdfPTable(2);
			clientShipmentTable.setWidths(new int[]{70,230});
			clientShipmentTable.setWidthPercentage(100);

			// client name
			
			PdfPCell languageToWho = new PdfPCell(new Phrase(language.get("toWho")+":",TR_10));
			languageToWho.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(languageToWho);
			
			PdfPCell toWhoCell = new PdfPCell(new Phrase(DOC.getClient().getName(),TR_10));
			toWhoCell.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(toWhoCell);
			
			// client address
			
			PdfPCell languageAddress = new PdfPCell(new Phrase(language.get("address")+":",TR_10));
			languageAddress.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(languageAddress);
			
			PdfPCell addressCell = new PdfPCell(new Phrase(DOC.getClient().getAddress(),TR_10));
			addressCell.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(addressCell);
			
			// orderNR
			
			PdfPCell languageOrderBase = new PdfPCell(new Phrase(language.get("baseOrder")+":",TR_10));
			languageOrderBase.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(languageOrderBase);
			
			PdfPCell orderBaseCell;
			if(DOC.getOrderNR().equals("")){
				orderBaseCell = new PdfPCell(new Phrase("",TR_10));
			}
			else{
				orderBaseCell = new PdfPCell(new Phrase(language.get("order")+" "+DOC.getOrderNR(),TR_10));
			}
			
			orderBaseCell.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(orderBaseCell);
			
			// shipmentaddress
			
			PdfPCell languageShipmentAddress = new PdfPCell(new Phrase(language.get("shipmentAddress")+":",TR_10_B));
			languageShipmentAddress.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(languageShipmentAddress);
			
			PdfPCell shipmentAddressCell = new PdfPCell(new Phrase(DOC.getShipmentAddress(),TR_10_B));
			shipmentAddressCell.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(shipmentAddressCell);
			
			// contactperson
			
			PdfPCell languageContactPerson = new PdfPCell(new Phrase(language.get("contactPerson")+":",TR_10_B));
			languageContactPerson.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(languageContactPerson);
			
			PdfPCell contactPersonCell = new PdfPCell(new Phrase(DOC.getClient().getContactPerson(),TR_10_B));
			contactPersonCell.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(contactPersonCell);
			
			// phone
			
			PdfPCell languagePhone = new PdfPCell(new Phrase(language.get("phone")+":",TR_10));
			languagePhone.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(languagePhone);
			
			PdfPCell phoneCell = new PdfPCell(new Phrase(DOC.getClient().getPhone(),TR_10));
			phoneCell.setBorder(Rectangle.NO_BORDER);
			clientShipmentTable.addCell(phoneCell);
			
			clientShipmentParagraph.add(clientShipmentTable);
			
			// add the created shipment and client data
			firstParagraph.add(emptyParagraph);
			firstParagraph.add(clientShipmentParagraph);
			
			blackBoard.add(emptyParagraph);
			blackBoard.add(clientShipmentParagraph);
			
			/*
			 * Products header table
			 */
			PdfPTable productsHeaderRow = new PdfPTable(5);
			productsHeaderRow.setWidths(new int[]{8,110,17,15,40});
			productsHeaderRow.setWidthPercentage(100);
			
			// products table header row
			
			PdfPCell headerNRCell = new PdfPCell(new Phrase(language.get("nr"),TR_12));
			headerNRCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerNRCell.setUseVariableBorders(true);
			productsHeaderRow.addCell(headerNRCell);
			
			PdfPCell headerSpecificationCell = new PdfPCell(new Phrase(language.get("specification"),TR_12));
			headerSpecificationCell.setUseVariableBorders(true);
			productsHeaderRow.addCell(headerSpecificationCell);
			
			PdfPCell headerAmountCell = new PdfPCell(new Phrase(language.get("amount"),TR_12));
			headerAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerAmountCell.setUseVariableBorders(true);
			productsHeaderRow.addCell(headerAmountCell);
			
			PdfPCell headerUnitCell = new PdfPCell(new Phrase(language.get("unit"),TR_12));
			headerUnitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerUnitCell.setUseVariableBorders(true);
			productsHeaderRow.addCell(headerUnitCell);
			
			PdfPCell headerCommentsCell = new PdfPCell(new Phrase(language.get("comments"),TR_12));
			headerCommentsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCommentsCell.setUseVariableBorders(true);
			productsHeaderRow.addCell(headerCommentsCell);
			
			// add all the products
			
			int lastPageNumber = 0;
			boolean firstProductInPage = true;
			boolean updateBlackBoard = false;

			for(int i = 0;i<DOC.getProducts().size();i++){
				
				Product product = DOC.getProducts().get(i);
				
				// check the language and make string accordingly
				String productName = "",productUnit = "";
				if(language.getType().equals(Language.TYPE_ESTONIAN)){
					productName = product.getName();
					productUnit = product.getUnit();
				}
				else if(language.getType().equals(Language.TYPE_ENGLISH)){
					productName = product.getE_name();
					productUnit = product.getE_unit();
				}
				
				// each products has a separate table with the same size as header
				PdfPTable productRow = new PdfPTable(productsHeaderRow.getNumberOfColumns());
				productRow.setWidthPercentage(100);
				productRow.setWidths(new int[]{8,110,17,15,40});
				
				PdfPCell NRCEll = new PdfPCell(new Phrase(""+(i+1),TR_10));
				NRCEll.setHorizontalAlignment(Element.ALIGN_CENTER);
				NRCEll.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// name cell with additional info
				
				PdfPCell nameCell = null;

				if(product.hasAdditionalInformation()){ // user has added additional information for this product
					
					Paragraph nameAndInfoParagraph = new Paragraph();
					
					nameAndInfoParagraph.add(new Phrase(productName,TR_10));
					nameAndInfoParagraph.add(Chunk.NEWLINE);
					nameAndInfoParagraph.add(new Phrase(product.getAdditional_Info(),TR_8_I));

					nameCell = new PdfPCell(new Phrase(nameAndInfoParagraph));
				}
				else{
					nameCell = new PdfPCell(new Phrase(productName,TR_10));
				}
				nameCell.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// amount cell
				PdfPCell amountCell = new PdfPCell(new Phrase(product.getAmount()+"",TR_10));
				amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				amountCell.setBorderColor(BaseColor.LIGHT_GRAY);

				// unit cell
				PdfPCell unitCell = new PdfPCell(new Phrase(productUnit,TR_10));
				unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				unitCell.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// comments cell
				PdfPCell commentsCell = new PdfPCell(new Phrase(product.getComments(),TR_10));
				commentsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				commentsCell.setBorderColor(BaseColor.LIGHT_GRAY);

				// add all the cells
				productRow.addCell(NRCEll);
				productRow.addCell(nameCell);
				productRow.addCell(amountCell);
				productRow.addCell(unitCell);
				productRow.addCell(commentsCell);
				
				// add the product row to the blackboard
				blackBoard.add(productRow);
				
				/*
				 * check if we changed a page with the last product add,
				 */

				int currentPageNumber = writer.getPageNumber();

				if(currentPageNumber > lastPageNumber){
					
					blackBoard.add(productsHeaderRow); // add the table header again to the new page
					blackBoard.add(emptyParagraph);
					firstProductInPage = true;
					lastPageNumber++;
				}
				
				if(firstProductInPage){  // first product row needs to have black edges on top
					
					productRow.deleteLastRow(); // delete the last row and re-add later with changed color
					
					if(currentPageNumber > 1){ // eliminate the possibility of too-long additional info ending up half paged on old page
						firstParagraph.add(new Chunk(Chunk.NEXTPAGE));
					}
					
					firstParagraph.add(emptyParagraph); // empty space between the page header
					firstParagraph.add(productsHeaderRow);

					firstProductInPage = false;
					updateBlackBoard = true;
					
					float borderWidth = NRCEll.getBorderWidth();
					
					NRCEll.setUseVariableBorders(true);
					nameCell.setUseVariableBorders(true);
					amountCell.setUseVariableBorders(true);
					unitCell.setUseVariableBorders(true);
					commentsCell.setUseVariableBorders(true);
					
					NRCEll.setBorderColorTop(BaseColor.BLACK);
					nameCell.setBorderColorTop(BaseColor.BLACK);
					amountCell.setBorderColorTop(BaseColor.BLACK);
					unitCell.setBorderColorTop(BaseColor.BLACK);
					commentsCell.setBorderColorTop(BaseColor.BLACK);
					
					/*
					 * this is needed, because SOMEWHY color changing also changes the width
					 */
					NRCEll.setBorderWidth(borderWidth/2);
					nameCell.setBorderWidth(borderWidth/2);
					amountCell.setBorderWidth(borderWidth/2);
					unitCell.setBorderWidth(borderWidth/2);
					commentsCell.setBorderWidth(borderWidth/2);
					
					productRow.addCell(NRCEll);
					productRow.addCell(nameCell);
					productRow.addCell(amountCell);
					productRow.addCell(unitCell);
					productRow.addCell(commentsCell);

				}

				firstParagraph.add(productRow);
				
				/*
				 *  update the blackBoard, because we added another page to the document, update the page number
				 */
				if(updateBlackBoard){
					
					blackBoard.close();
					blackBoard = new com.itextpdf.text.Document();

					temporaryByteStream = new ByteArrayOutputStream();
					writer = PdfWriter.getInstance(blackBoard,temporaryByteStream);
					pageEvent.resetPagesTotal();
					writer.setPageEvent(pageEvent);
					
					blackBoard.open();
					blackBoard.add(firstParagraph);
					updateBlackBoard = false;
				}
				
			}
			
            int pageNumberAfterProducts = writer.getPageNumber();
            
			/*
			 * last paragraph, includes page end information
			 */
			
			Paragraph lastParagraph = new Paragraph();
			
			lastParagraph.add(emptyParagraph);
			blackBoard.add(emptyParagraph);
			
			/*
			 * changeable info about the delivery
			 */
			Paragraph changeableInfoParagraph = new Paragraph();
            Phrase changeableInfo = new Phrase(language.get("deliveryNotes"),TR_10_I);
            Phrase website = new Phrase(" www.bestroof.ee",TR_10_B_I);
			
			changeableInfoParagraph.add(changeableInfo);
			changeableInfoParagraph.add(website);
			
            lastParagraph.add(changeableInfoParagraph);
            blackBoard.add(changeableInfoParagraph);
            
			/*
			 * signatures
			 */

            lastParagraph.add(emptyParagraph);
            
        	// seller
            lastParagraph.add(new Paragraph(new Phrase(language.get("giver")+" :",TR_10)));
            lastParagraph.add(emptyParagraph);
        	
        	// receiver
            lastParagraph.add(new Paragraph(new Phrase(language.get("receiver")+" :",TR_10)));
        	
        	Paragraph receiverInputParagraph = new Paragraph();
        	PdfPTable receiverTable = new PdfPTable(3);
            
        	// dots cells
           	PdfPCell punkt = new PdfPCell(new Phrase(language.get("dots"),TR_10_B));
            punkt.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
            punkt.setBorder(Rectangle.NO_BORDER);
            
            receiverTable.addCell(punkt);
            receiverTable.addCell(punkt);
            receiverTable.addCell(punkt);
            
            // date spot
            PdfPCell languageReceiverDate = new PdfPCell(new Phrase(language.get("date"),TR_10));
            languageReceiverDate.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
            languageReceiverDate.setBorder(Rectangle.NO_BORDER);
            
            // name spot
            PdfPCell languageCapitalName = new PdfPCell(new Phrase(language.get("capitalName"),TR_10));
            languageCapitalName.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
            languageCapitalName.setBorder(Rectangle.NO_BORDER);
            
            // signature spot
            PdfPCell languageSignature = new PdfPCell(new Phrase(language.get("signature"),TR_10));
            languageSignature.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
            languageSignature.setBorder(Rectangle.NO_BORDER);
            
            receiverTable.addCell(languageReceiverDate);
            receiverTable.addCell(languageCapitalName);
            receiverTable.addCell(languageSignature);
            
            receiverInputParagraph.add(receiverTable);
            
            // add the data spots
            
            lastParagraph.add(receiverInputParagraph);
        	blackBoard.add(lastParagraph);
            
           /*
            * finally add all the info to the document
            */
            
            blackBoard.add(lastParagraph);

            try{blackBoard.close();}catch(Exception x){}
    		reader = new PdfReader(temporaryByteStream.toByteArray());
    		int pageNumberAfterFinalEnd = reader.getNumberOfPages();

			/*
			 * now we make the final and last document that is ready to be sent to user
			 */
			
            finalDocument = new com.itextpdf.text.Document();
            
            byteOutputStream = new ByteArrayOutputStream();
			writer = PdfWriter.getInstance(finalDocument,byteOutputStream);
			pageEvent.setPageCount(pageEvent.getPageCount(), true);
			writer.setPageEvent(pageEvent);
			
			finalDocument.open();
			
			
			/*
			 * check the last paragraph fit for our document
			 */
			if(pageNumberAfterFinalEnd > pageNumberAfterProducts){ // add the final paragraph to the new page
				finalDocument.add(firstParagraph);
				finalDocument.newPage();
				finalDocument.add(lastParagraph);
			}
			else{
				finalDocument.add(firstParagraph);
				finalDocument.add(lastParagraph);
			}
			
		}catch(Exception x){
			x.printStackTrace();
			wasError = true;
		}
		finally{
			try{temporaryByteStream.close();}catch(Exception x){}
			try{blackBoard.close();}catch(Exception x){}
			try{finalDocument.close();}catch(Exception x){}
		}
		
		if(wasError){
			return null;
		}
		if(byteOutputStream == null){
			return null;
		}

		return byteOutputStream.toByteArray();
	}

}
