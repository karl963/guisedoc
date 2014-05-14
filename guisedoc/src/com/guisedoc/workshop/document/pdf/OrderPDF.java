package com.guisedoc.workshop.document.pdf;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.guisedoc.object.Document;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;
import com.guisedoc.workshop.document.settings.DateFormats;
import com.guisedoc.workshop.document.settings.DocumentFonts;
import com.guisedoc.workshop.document.settings.HeaderFooter;
import com.guisedoc.workshop.document.settings.Language;
import com.guisedoc.workshop.document.settings.NumberFormats;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class OrderPDF {
	
	Document DOC;
	
	public OrderPDF(Document d){
		this.DOC = d;
	}

	public byte[] make(Firm firm, User user){
		/*
		 * make metadata for the document
		 */
		Language language = DOC.getLanguage();
		DecimalFormat COMMA_3 = NumberFormats.THREE_AFTER_COMMA();
		DecimalFormat COMMA_2 = NumberFormats.TWO_AFTER_COMMA();
		
		Font TR_12_B = DocumentFonts.TIMES_ROMAN_12_BOLD();
		Font TR_12 = DocumentFonts.TIMES_ROMAN_12();
		Font TR_10_B = DocumentFonts.TIMES_ROMAN_10_BOLD();
		Font TR_10 = DocumentFonts.TIMES_ROMAN_10();
		Font TR_10_I = DocumentFonts.TIMES_ROMAN_10_ITALIC();
		Font TR_8_I = DocumentFonts.TIMES_ROMAN_8_ITALIC();
		
		com.itextpdf.text.Document blackBoard = null;
		com.itextpdf.text.Document finalDocument = null;
		
		ByteArrayOutputStream byteOutputStream = null;
		ByteArrayOutputStream temporaryByteStream = null;
		PdfReader reader;
		
		HeaderFooter pageEvent = new HeaderFooter(firm,false,true);
		
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
			headerTable.setWidths(new int[]{150,320}); // table column sizes
			headerTable.setWidthPercentage(100);
			
			// Seller (the one who we buy from) name
			PdfPCell sellerName = new PdfPCell(new Phrase(DOC.getClient().getName(),TR_12_B));
			sellerName.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(sellerName);
			
			// Date cells
			PdfPCell dateCell = new PdfPCell(new Phrase(DOC.getFormatedDate(),TR_10));
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(dateCell);
			
			// empty row
			headerTable.addCell(emptyCell);
			headerTable.addCell(emptyCell);
			
			// Type cell
			PdfPCell documentTypeCell = new PdfPCell(new Phrase(language.get("order")+":  "+DOC.getFullNumber(), TR_12_B));
			headerTable.addCell(documentTypeCell);
			headerTable.addCell(emptyCell);
			
			// table + 2 empty rows
			pageStartParagraph.add(headerTable);
			
			pageStartParagraph.add(emptyParagraph);
			pageStartParagraph.add(emptyParagraph);
			
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
			
			// changeable info for the seller
			firstParagraph.add(new Phrase(language.get("weOrder")+":",TR_10_I));
			
			/*
			 * Products header table
			 */
			PdfPTable productsHeaderRow;
			productsHeaderRow = new PdfPTable(6);
			productsHeaderRow.setWidthPercentage(100);
			productsHeaderRow.setWidths(new int[]{8,110,17,12,20,23});
			
			// products table header row
			
			PdfPCell headerNRCell = new PdfPCell(new Phrase(language.get("nr"),TR_12));
			headerNRCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerNRCell);
			
			productsHeaderRow.addCell(new PdfPCell(new Phrase(language.get("specification"),TR_12)));
			
			PdfPCell headerAmountCell = new PdfPCell(new Phrase(language.get("amount"),TR_12));
			headerAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerAmountCell);
			
			PdfPCell headerUnitCell = new PdfPCell(new Phrase(language.get("unit"),TR_12));
			headerUnitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerUnitCell);
			
			PdfPCell headerPriceCell = new PdfPCell(new Phrase(language.get("price"),TR_12));
			headerPriceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerPriceCell);
			
			PdfPCell headerSumCell = new PdfPCell(new Phrase(language.get("sum"),TR_12));
			headerSumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerSumCell);
			
			// add all the products
			
			double totalSum = 0;
			
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
				productRow.setWidths(new int[]{8,110,17,12,20,23});
				
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
				
				// price cell
				PdfPCell priceCell = new PdfPCell(new Phrase(COMMA_3.format(product.getPrice()),TR_10));
				priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				priceCell.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// sum cell
				PdfPCell sumCell = new PdfPCell(new Phrase(COMMA_2.format(product.getTotalSum()),TR_10));
				sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				sumCell.setBorderColor(BaseColor.LIGHT_GRAY);

				// add all the cells
				productRow.addCell(NRCEll);
				productRow.addCell(nameCell);
				productRow.addCell(amountCell);
				productRow.addCell(unitCell);
				productRow.addCell(priceCell);
				productRow.addCell(sumCell);
				
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
					priceCell.setUseVariableBorders(true);
					sumCell.setUseVariableBorders(true);
					
					NRCEll.setBorderColorTop(BaseColor.BLACK);
					nameCell.setBorderColorTop(BaseColor.BLACK);
					amountCell.setBorderColorTop(BaseColor.BLACK);
					unitCell.setBorderColorTop(BaseColor.BLACK);
					priceCell.setBorderColorTop(BaseColor.BLACK);
					sumCell.setBorderColorTop(BaseColor.BLACK);
					
					/*
					 * this is needed, because SOMEWHY color changing also changes the width
					 */
					NRCEll.setBorderWidth(borderWidth/2);
					nameCell.setBorderWidth(borderWidth/2);
					amountCell.setBorderWidth(borderWidth/2);
					unitCell.setBorderWidth(borderWidth/2);
					priceCell.setBorderWidth(borderWidth/2);
					sumCell.setBorderWidth(borderWidth/2);
					
					productRow.addCell(NRCEll);
					productRow.addCell(nameCell);
					productRow.addCell(amountCell);
					productRow.addCell(unitCell);
					productRow.addCell(priceCell);
					productRow.addCell(sumCell);
				}
				
				totalSum+=product.getTotalSum();
				
				firstParagraph.add(productRow);
				
				/*
				 *  update the blackBoard, because we added another page to the document, update the page number
				 */
				if(updateBlackBoard){
					
					blackBoard.close();
					blackBoard = new com.itextpdf.text.Document();

					try{temporaryByteStream.close();}catch(Exception x){} // close right after done for memory release
					temporaryByteStream = new ByteArrayOutputStream();
					writer = PdfWriter.getInstance(blackBoard,temporaryByteStream);
					pageEvent.resetPagesTotal();
					writer.setPageEvent(pageEvent);
					
					blackBoard.open();
					blackBoard.add(firstParagraph);
					updateBlackBoard = false;
				}
				
			}
			
			/*
			 * last paragraph, includes page end information
			 */
			
			Paragraph lastParagraph = new Paragraph();
			
			// total sum
			
			PdfPTable sumTable = new PdfPTable(1);
			sumTable.setWidthPercentage(100);
			
			PdfPCell sumCell = new PdfPCell(new Phrase(language.get("totalEuro")+":  "+COMMA_2.format(totalSum),TR_10_B));
			sumCell.setBorder(Rectangle.NO_BORDER);
			sumCell.setHorizontalAlignment(Chunk.ALIGN_RIGHT);
			sumTable.addCell(sumCell);
            lastParagraph.add(sumTable);
            
            int pageNumberAfterProducts = writer.getPageNumber();
            
            /*
             * shipment data
             */
            PdfPTable shipmentTable = new PdfPTable(2);
            shipmentTable.setWidthPercentage(100);
            shipmentTable.setWidths(new int[]{30,140});
            
            Paragraph languageParagraph = new Paragraph();
            
            // time
            languageParagraph.add(new Phrase(language.get("shipmentTime")+":",TR_10_B));
            languageParagraph.add(Chunk.NEWLINE);
            
            // place
            languageParagraph.add(new Phrase(language.get("shipmentPlace")+":",TR_10_B));
            languageParagraph.add(Chunk.NEWLINE);
            
            // address
            languageParagraph.add(new Phrase(language.get("shipmentAddress")+":",TR_10_B));
            
            // add to the table
            PdfPCell languageCell = new PdfPCell(languageParagraph);
            languageCell.setBorder(Rectangle.NO_BORDER);
            shipmentTable.addCell(languageCell);
            
            // document shipment data
            
            Paragraph shipmentParagraph = new Paragraph();

            // time
            shipmentParagraph.add(new Phrase(DOC.getShipmentTime(),TR_10));
            shipmentParagraph.add(Chunk.NEWLINE);
            
            // place
            shipmentParagraph.add(new Phrase(DOC.getShipmentPlace(),TR_10));
            shipmentParagraph.add(Chunk.NEWLINE);
            
            // address
            shipmentParagraph.add(new Phrase(DOC.getShipmentAddress(),TR_10));
            
            // add to the table
            PdfPCell shipmentCell = new PdfPCell(shipmentParagraph);
            shipmentCell.setBorder(Rectangle.NO_BORDER);
            
            shipmentTable.addCell(shipmentCell);
            
            lastParagraph.add(shipmentTable);
            
            // changeable data
            
            lastParagraph.add(emptyParagraph);
            lastParagraph.add(new Paragraph(language.get("regards")+",",TR_8_I));
            lastParagraph.add(emptyParagraph);

            // add the document creator data
            
           	Paragraph creatorParagraph = new Paragraph();
            
           	// check for info about the user and add it
           	if(!user.getName().equals("")){
           		creatorParagraph.add(new Phrase(user.getName(),TR_8_I));
           	}
           	if(!user.getPhone().equals("")){
           		creatorParagraph.add(new Phrase(";  tel  "+user.getPhone(),TR_8_I));
           	}
           	if(!user.getEmail().equals("")){
           		creatorParagraph.add(new Phrase(";  e-mail:  "+user.getEmail(),TR_8_I));
           	}
           	if(!user.getSkype().equals("")){
           		creatorParagraph.add(new Phrase(";  skype:  "+user.getSkype(),TR_8_I));
           	}
            
           	lastParagraph.add(creatorParagraph);
            
           /*
            * finally add all the info to the document
            */
            
            blackBoard.add(lastParagraph);

            try{blackBoard.close();}catch(Exception x){}
    		reader = new PdfReader(temporaryByteStream.toByteArray());
    		try{temporaryByteStream.close();}catch(Exception x){} // close right after done for memory release
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
				finalDocument.add(emptyParagraph);
				finalDocument.add(lastParagraph);
			}
			else{
				finalDocument.add(firstParagraph);
				if(DOC.getProducts().size()==0){ // we have no products, just to make it look nicer, add a blank
					finalDocument.add(emptyParagraph);
				}
				finalDocument.add(lastParagraph);
			}
			
		}catch(Exception x){
			x.printStackTrace();
			wasError = true;
		}
		finally{
			try{temporaryByteStream.close();}catch(Exception x){}
			try{byteOutputStream.close();}catch(Exception x){}
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
