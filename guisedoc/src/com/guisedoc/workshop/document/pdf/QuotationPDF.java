package com.guisedoc.workshop.document.pdf;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

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
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class QuotationPDF {
	
	Document DOC;
	
	public QuotationPDF(Document d){
		this.DOC = d;
	}
	
	public byte[] make(Firm firm, User user){
		
		/*
		 * make metadata for the document
		 */
		Language language = DOC.getLanguage();
		
		GregorianCalendar gregCalendar = new GregorianCalendar();
		gregCalendar.setTime(DOC.getDate());
		
		SimpleDateFormat sdf = DateFormats.DOT_DATE_FORMAT();
		DecimalFormat COMMA_3 = NumberFormats.THREE_AFTER_COMMA();
		DecimalFormat COMMA_2 = NumberFormats.TWO_AFTER_COMMA();
		DecimalFormat DISCOUNT = NumberFormats.DISCOUNT_FORMAT();
		
		Font TR_12_B = DocumentFonts.TIMES_ROMAN_12_BOLD();
		Font TR_12 = DocumentFonts.TIMES_ROMAN_12();
		Font TR_10_B = DocumentFonts.TIMES_ROMAN_10_BOLD();
		Font TR_10 = DocumentFonts.TIMES_ROMAN_10();
		Font TR_10_I = DocumentFonts.TIMES_ROMAN_10_ITALIC();
		Font TR_10_U_B = DocumentFonts.TIMES_ROMAN_10_UNDERLINE_BOLD();
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
			headerTable.setWidths(new int[]{170,300}); // table column sizes
			headerTable.setWidthPercentage(100);

			// Type cell
			headerTable.addCell(new PdfPCell(new Phrase(language.get("quotation")+":  "+DOC.getFullNumber(), TR_12_B)));
			
			// language client
			PdfPCell languageClient = new PdfPCell(new Phrase(language.get("capitalClient")+" :",TR_10_U_B));
			languageClient.setHorizontalAlignment(Element.ALIGN_RIGHT);
			languageClient.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(languageClient);
		
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

			PdfPTable headerTableSecond = new PdfPTable(2); // table with 2 columns
			headerTableSecond.setWidths(new int[]{170,300}); // table column sizes
			headerTableSecond.setWidthPercentage(100);
			
			// Date cell
			PdfPCell dateCell = new PdfPCell(new Phrase(DOC.getFormatedDate(),TR_10));
			dateCell.setBorder(Rectangle.NO_BORDER);
			headerTableSecond.addCell(dateCell);
			
			// client name cell
			String fullClientName;
			if(DOC.getClient().getSelectedContactPerson().getID() == 0){ // contact person is not selected
				fullClientName = DOC.getClient().getName();
			}
			else{
				fullClientName = DOC.getClient().getName()+", "+DOC.getClient().getSelectedContactPerson().getName();
			}
			PdfPCell clientNameCell = new PdfPCell(new Phrase(fullClientName,TR_10_B));
			clientNameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			clientNameCell.setBorder(Rectangle.NO_BORDER);
			headerTableSecond.addCell(clientNameCell);
			
			// empty cell
			headerTableSecond.addCell(emptyCell);

			// client phone cell
			PdfPCell clientPhoneCell = new PdfPCell(new Phrase(DOC.getClient().getPhone(),TR_10_I));
			clientPhoneCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			clientPhoneCell.setBorder(Rectangle.NO_BORDER);
			headerTableSecond.addCell(clientPhoneCell);
			
			// empty cell
			headerTableSecond.addCell(emptyCell);

			// client email cell
			PdfPCell clientEmailCell = new PdfPCell(new Phrase(DOC.getClient().getEmail(),TR_10_I));
			clientEmailCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			clientEmailCell.setBorder(Rectangle.NO_BORDER);
			headerTableSecond.addCell(clientEmailCell);
			
			// done with second header table
			pageStartParagraph.add(headerTableSecond);

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

			// thank the customer and offer
			firstParagraph.add(new Phrase(language.get("thanksAndOffer"),TR_10_I));
			
			/*
			 * Products header table
			 */
			PdfPTable productsHeaderRow;
			int[] productsTableWidths;
			
			if(DOC.isShowDiscount()){
				productsHeaderRow = new PdfPTable(7);
				productsTableWidths = new int[]{8,93,17,12,20,15,25};
			}
			else{
				productsHeaderRow = new PdfPTable(6);
				productsTableWidths = new int[]{8,110,17,12,20,23};
			}
			productsHeaderRow.setWidthPercentage(100);
			productsHeaderRow.setWidths(productsTableWidths);
			
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
			
			if(DOC.isShowDiscount()){
				PdfPCell headerDiscountCell = new PdfPCell(new Phrase(language.get("discount"),TR_12));
				headerDiscountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				productsHeaderRow.addCell(headerDiscountCell);
			}

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
				productRow.setWidths(productsTableWidths);
				
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

				// discount cell
				PdfPCell discountCell = null;
				if(DOC.isShowDiscount()){
					discountCell = new PdfPCell(new Phrase(DISCOUNT.format(product.getDiscount()),TR_10));
					discountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					discountCell.setBorderColor(BaseColor.LIGHT_GRAY);
				}
				
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
				if(DOC.isShowDiscount()){
					productRow.addCell(discountCell);
				}
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
					if(DOC.isShowDiscount()){
						discountCell.setUseVariableBorders(true);
					}
					sumCell.setUseVariableBorders(true);
					
					NRCEll.setBorderColorTop(BaseColor.BLACK);
					nameCell.setBorderColorTop(BaseColor.BLACK);
					amountCell.setBorderColorTop(BaseColor.BLACK);
					unitCell.setBorderColorTop(BaseColor.BLACK);
					priceCell.setBorderColorTop(BaseColor.BLACK);
					if(DOC.isShowDiscount()){
						discountCell.setBorderColorTop(BaseColor.BLACK);
					}
					sumCell.setBorderColorTop(BaseColor.BLACK);
					
					/*
					 * this is needed, because SOMEWHY color changing also changes the width
					 */
					NRCEll.setBorderWidth(borderWidth/2);
					nameCell.setBorderWidth(borderWidth/2);
					amountCell.setBorderWidth(borderWidth/2);
					unitCell.setBorderWidth(borderWidth/2);
					priceCell.setBorderWidth(borderWidth/2);
					if(DOC.isShowDiscount()){
						discountCell.setBorderWidth(borderWidth/2);
					}
					sumCell.setBorderWidth(borderWidth/2);
					
					productRow.addCell(NRCEll);
					productRow.addCell(nameCell);
					productRow.addCell(amountCell);
					productRow.addCell(unitCell);
					productRow.addCell(priceCell);
					if(DOC.isShowDiscount()){
						productRow.addCell(discountCell);
					}
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
			
			/*
			 * information table
			 */
			PdfPTable infoTable = new PdfPTable(3);
			infoTable.setWidthPercentage(100);
			infoTable.setWidths(new int[]{159,65,32});
			
			Paragraph infoParagraph = new Paragraph();

			 // valid due date
			Phrase validDuePhrase = new Phrase();
			if(DOC.getValidDue() == 0){ // user has NOT set the validDue for the document
				validDuePhrase.add(new Phrase(language.get("quotation"),TR_10_B));
				validDuePhrase.add(new Phrase(" "+language.get("notAsDeal"),TR_10));
			}
			else{
				validDuePhrase.add(new Phrase(language.get("validUntil")+"  ",TR_10_B));
				validDuePhrase.add(new Phrase(sdf.format(new Date(gregCalendar.getTimeInMillis()+(DOC.getValidDue()*24*60*60*1000L)))+
						"  "+
						language.get("and")+
						" "+
						language.get("notAsDeal"),TR_10));
			}
			
			infoParagraph.add(validDuePhrase);
			infoParagraph.add(Chunk.NEWLINE);
            
            PdfPCell infoCell = new PdfPCell(infoParagraph);
            infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            infoTable.addCell(infoCell);
			
			// the money info
            Paragraph languageParagraph = new Paragraph();
            
            // language sum
            languageParagraph.add(new Phrase(language.get("sumTotalEuro")+"    :",TR_10));
            languageParagraph.add(Chunk.NEWLINE);
            
            // language VAT
            languageParagraph.add(new Phrase(language.get("vat")+"    :",TR_10));
            languageParagraph.add(Chunk.NEWLINE);
            
            // language total
            languageParagraph.add(new Phrase(language.get("totalPay")+"    :",TR_10_B));
            
            PdfPCell languageCell = new PdfPCell(languageParagraph);
            languageCell.setUseVariableBorders(true);
            languageCell.setBorderColorRight(BaseColor.WHITE);
            languageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoTable.addCell(languageCell);

            // numbers paragraph
            Paragraph numbersParagaph = new Paragraph();
            
            // sum
            numbersParagaph.add(new Phrase(""+COMMA_2.format(totalSum),TR_10));
            numbersParagaph.add(Chunk.NEWLINE);
            
            // VAT 20%
            numbersParagaph.add(new Phrase(""+COMMA_2.format(totalSum*0.2),TR_10));
            numbersParagaph.add(Chunk.NEWLINE);
            
            // total
            numbersParagaph.add(new Phrase(""+COMMA_2.format(totalSum+(totalSum*0.2)),TR_10_B));
            
            PdfPCell numbersCell = new PdfPCell(numbersParagaph);
            numbersCell.setUseVariableBorders(true);
            numbersCell.setBorderColorLeft(BaseColor.WHITE);
            numbersCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoTable.addCell(numbersCell);
            
            lastParagraph.add(infoTable);
            
            int pageNumberAfterProducts = writer.getPageNumber();

            /*
             * shipment data
             */
            PdfPTable shipmentTable = new PdfPTable(2);
            shipmentTable.setWidthPercentage(100);
            shipmentTable.setWidths(new int[]{30,140});
            
            Paragraph shipLangParagraph = new Paragraph();
            
            // time
            shipLangParagraph.add(new Phrase(language.get("shipmentTime")+":",TR_10_B));
            shipLangParagraph.add(Chunk.NEWLINE);
            
            // address
            shipLangParagraph.add(new Phrase(language.get("shipmentAddress")+":",TR_10_B));
            shipLangParagraph.add(Chunk.NEWLINE);
            
            // payment requirement
            shipLangParagraph.add(new Phrase(language.get("paymentRequirement")+":",TR_10_B));
            
            // add to the table
            PdfPCell shipLangCell = new PdfPCell(shipLangParagraph);
            shipLangCell.setBorder(Rectangle.NO_BORDER);
            shipmentTable.addCell(shipLangCell);
            
            // document shipment data

            Paragraph shipmentParagraph = new Paragraph();

            // time
            shipmentParagraph.add(new Phrase(DOC.getShipmentTime(),TR_10));
            shipmentParagraph.add(Chunk.NEWLINE);
            
            // place
            shipmentParagraph.add(new Phrase(DOC.getShipmentAddress(),TR_10));
            shipmentParagraph.add(Chunk.NEWLINE);
            
            // address
            shipmentParagraph.add(new Phrase(DOC.getPaymentRequirement(),TR_10));
            
            // add to the table
            PdfPCell shipmentCell = new PdfPCell(shipmentParagraph);
            shipmentCell.setBorder(Rectangle.NO_BORDER);
            
            shipmentTable.addCell(shipmentCell);
            
            lastParagraph.add(shipmentTable);
            
			/*
			 * changeable data for user 
			 */

            lastParagraph.add(new Paragraph(language.get("quotationSpecification"),TR_10_I));
			
			/*
			 * creator data
			 */
			
			Paragraph creatorParagraph = new Paragraph();

			creatorParagraph.add(new Phrase(language.get("orderCreator")+":  ",TR_10_B));
		
			if(!user.getName().equals("")){
				creatorParagraph.add(new Phrase(user.getName(),TR_10));
			}
			if(!user.getPhone().equals("")){
				creatorParagraph.add(new Phrase(";  tel  "+user.getPhone(),TR_10));
			}
			if(!user.getEmail().equals("")){
				creatorParagraph.add(new Phrase(";  e-mail:  "+user.getEmail(),TR_10));
			}
			if(!user.getSkype().equals("")){
				creatorParagraph.add(new Phrase(";  skype:  "+user.getSkype(),TR_10));
			}
			
			lastParagraph.add(creatorParagraph);

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
