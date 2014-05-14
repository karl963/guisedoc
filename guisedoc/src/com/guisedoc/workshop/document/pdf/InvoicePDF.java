package com.guisedoc.workshop.document.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

public class InvoicePDF {
	
	Document DOC;
	
	public InvoicePDF(Document d){
		this.DOC = d;
	}
	
	public byte[] make(Firm firm, User user){
		
		/*
		 * make metadata for the document
		 */
		Language language = DOC.getLanguage();
		SimpleDateFormat sdf = DateFormats.DOT_DATE_FORMAT();
		DecimalFormat COMMA_3 = NumberFormats.THREE_AFTER_COMMA();
		DecimalFormat COMMA_2 = NumberFormats.TWO_AFTER_COMMA();
		DecimalFormat DISCOUNT = NumberFormats.DISCOUNT_FORMAT();
		
		Font TR_12_B = DocumentFonts.TIMES_ROMAN_12_BOLD();
		Font TR_12 = DocumentFonts.TIMES_ROMAN_12();
		Font TR_10_B = DocumentFonts.TIMES_ROMAN_10_BOLD();
		Font TR_10 = DocumentFonts.TIMES_ROMAN_10();
		Font TR_10_B_I = DocumentFonts.TIMES_ROMAN_10_BOLD_ITALIC();
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
			PdfPTable headerTable = new PdfPTable(3); // table with 3 columns
			headerTable.setWidths(new int[]{170,230,70}); // table column sizes
			headerTable.setWidthPercentage(100);
			
			// Type cell
			headerTable.addCell(new PdfPCell(new Phrase(language.get("invoice")+":  "+DOC.getFullNumber(), TR_12_B)));
			
			// Date cells
			PdfPCell languageDateCell = new PdfPCell(new Phrase(language.get("date")+":",TR_10_B));
			languageDateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			languageDateCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(languageDateCell);
			
			PdfPCell dateCell = new PdfPCell(new Phrase(DOC.getFormatedDate(),TR_10));
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(dateCell);
			
			/// Payd in cash cell
			
			if(DOC.isPaydInCash()){
				PdfPCell paydInCashCell = new PdfPCell(new Phrase(language.get("paydInCash"),TR_10));
				paydInCashCell.setBorder(Rectangle.NO_BORDER);
				headerTable.addCell(paydInCashCell);
			}
			else{
				headerTable.addCell(emptyCell);
			}
			
			// payment requirement cells
			
			PdfPCell languagePayReqCell = new PdfPCell(new Phrase(language.get("paymentRequirement")+":",TR_10));
			languagePayReqCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			languagePayReqCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(languagePayReqCell);
			
			PdfPCell payReqCell = new PdfPCell(new Phrase(DOC.getValidDue()+" "+language.get("days"),TR_10));
			payReqCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			payReqCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(payReqCell);
			
			// empty cell
			headerTable.addCell(emptyCell);
			
			// payment due time cells
			PdfPCell languagePayDueCell = new PdfPCell(new Phrase(language.get("paymentTime")+":",TR_10_B));
			languagePayDueCell.setBorder(Rectangle.NO_BORDER);
			languagePayDueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			headerTable.addCell(languagePayDueCell);

			Date paymentDueDate = new Date(DOC.getDate().getTime()+(DOC.getValidDue()*24*60*60*1000L));

			PdfPCell payDueCell = new PdfPCell(new Phrase(sdf.format(paymentDueDate),TR_10));
			payDueCell.setBorder(Rectangle.NO_BORDER);
			payDueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			headerTable.addCell(payDueCell);
			
			// done with header table
			pageStartParagraph.add(headerTable);
			pageStartParagraph.add(emptyParagraph);

			/*
			 * client data
			 */
			
			// buyer and firm data table
			PdfPTable buyerAndFirmTable = new PdfPTable(2); // table with 2 columns
			buyerAndFirmTable.setWidthPercentage(100);
			buyerAndFirmTable.setWidths(new int[]{200,200}); // set table column sizes

			// Buyer data
			Paragraph buyerParagraph = new Paragraph();

			buyerParagraph.add(new Phrase(language.get("buyer")+":",TR_10_B));
			buyerParagraph.add(Chunk.NEWLINE);

			buyerParagraph.add(new Phrase(DOC.getClient().getName(),TR_10));
			buyerParagraph.add(Chunk.NEWLINE);

			buyerParagraph.add(new Phrase(DOC.getClient().getAddress(),TR_10));
			buyerParagraph.add(Chunk.NEWLINE);

			buyerParagraph.add(new Phrase(DOC.getClient().getAdditionalAddress(),TR_10));
			buyerParagraph.add(Chunk.NEWLINE);
			
			Phrase contactPersonPhrase = new Phrase("",TR_10);
			contactPersonPhrase.add(new Phrase(language.get("contactPerson")+":",TR_10_B));
			contactPersonPhrase.add(new Phrase(" "+DOC.getClient().getSelectedContactPerson().getName(),TR_10));
			buyerParagraph.add(contactPersonPhrase);
			buyerParagraph.add(Chunk.NEWLINE);
			
			buyerParagraph.add(new Phrase(language.get("orderNR")+": "+DOC.getOrderNR(),TR_10));	
			
			// firm data
			Paragraph firmParagraph = new Paragraph();
			
			firmParagraph.add(new Phrase(language.get("seller")+":",TR_10_B));
			firmParagraph.add(Chunk.NEWLINE);
			
			firmParagraph.add(new Phrase(firm.getName(),TR_10));
			firmParagraph.add(Chunk.NEWLINE);
			
			firmParagraph.add(new Phrase(firm.getAddress(),TR_10));
			firmParagraph.add(Chunk.NEWLINE);
			
			firmParagraph.add(new Phrase("Reg nr     "+firm.getRegNR(),TR_10));
			firmParagraph.add(Chunk.NEWLINE);
			
			firmParagraph.add(new Phrase("KMKR nr  "+firm.getKmkr(),TR_10));
			firmParagraph.add(Chunk.NEWLINE);

			firmParagraph.add(new Phrase(firm.getBank()+" IBAN "+firm.getIban()+" SWIFT "+firm.getSwift(),TR_10));
			
			buyerAndFirmTable.addCell(new PdfPCell(buyerParagraph));
			buyerAndFirmTable.addCell(new PdfPCell(firmParagraph));
			
			// done with buyer and firm data
			pageStartParagraph.add(buyerAndFirmTable);
			
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
			
			/*
			 * information table
			 */
			PdfPTable infoTable = new PdfPTable(3);
			infoTable.setWidthPercentage(100);
			infoTable.setWidths(new int[]{159,65,32});
			
			// info that the user chose
			Paragraph chosenInfo = new Paragraph();
			
			chosenInfo.add(new Phrase(language.get("please")+" ",TR_10));
			chosenInfo.add(new Phrase(DOC.getFullNumber(),TR_10_B));
			chosenInfo.add(Chunk.NEWLINE);
			chosenInfo.add(new Phrase(language.get("overdueCharge"),TR_10_B_I));
			chosenInfo.add(Chunk.NEWLINE);
			chosenInfo.add(new Phrase(language.get("belongToSeller"),TR_10));
			chosenInfo.add(Chunk.NEWLINE);
			chosenInfo.add(Chunk.NEWLINE);
            Paragraph subInfo = new Paragraph(new Phrase(language.get("thanksDueDate"),TR_10_B));
            subInfo.setAlignment(Chunk.ALIGN_CENTER);
            chosenInfo.add(subInfo);
            
            PdfPCell chosenInfoCell = new PdfPCell(chosenInfo);
            chosenInfoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			infoTable.addCell(chosenInfoCell);

			// all the sums
            Paragraph languageSumParagraph = new Paragraph();

            /*
             * add language
             */
			Phrase languageSum = new Phrase(language.get("sumEuro")+"    :",TR_10);
			languageSumParagraph.add(languageSum);
			languageSumParagraph.add(Chunk.NEWLINE);
			
			Phrase languageVAT = new Phrase(language.get("vat")+"    :",TR_10);
			languageSumParagraph.add(languageVAT);
			languageSumParagraph.add(Chunk.NEWLINE);
			    
			Phrase languageTotal = new Phrase(language.get("totalEuro")+"    :",TR_10);
			languageSumParagraph.add(languageTotal);
			languageSumParagraph.add(Chunk.NEWLINE);
			
			Phrase languageAdvance = new Phrase(language.get("advance")+"    :",TR_10);
			languageSumParagraph.add(languageAdvance);
			languageSumParagraph.add(Chunk.NEWLINE);
			    
			Phrase languageFinal = new Phrase(language.get("totalPay")+"    :",TR_10_B);
			languageSumParagraph.add(languageFinal);
			    
			// add the paragraph to table
            PdfPCell totalSumCell = new PdfPCell(languageSumParagraph);
            totalSumCell.setUseVariableBorders(true);
            totalSumCell.setBorderColorRight(BaseColor.WHITE);
            totalSumCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoTable.addCell(totalSumCell);
            
            /*
             * all the numbers
             */
            Paragraph numbersParagraph = new Paragraph();

            // total sum
            numbersParagraph.add(new Phrase(COMMA_2.format(totalSum),TR_10));
            numbersParagraph.add(Chunk.NEWLINE);
            
            // VAT 20%
            double VAT = totalSum*0.2;

            numbersParagraph.add(new Phrase(COMMA_2.format(VAT),TR_10));
            numbersParagraph.add(Chunk.NEWLINE);
            
            // subsum with total+VAT
            double subSum = totalSum+VAT;

            numbersParagraph.add(new Phrase(""+COMMA_2.format(subSum),TR_10));
            numbersParagraph.add(Chunk.NEWLINE);
            
            // advance
            numbersParagraph.add(new Phrase("-"+COMMA_2.format(DOC.getAdvance()),TR_10));
            numbersParagraph.add(Chunk.NEWLINE);
            
            // final sum
            double finalSum = subSum-DOC.getAdvance();
            numbersParagraph.add(new Phrase(COMMA_2.format(finalSum),TR_10_B));
            
            
            // add the paragraph to table
            PdfPCell numbersCell = new PdfPCell(numbersParagraph);
            numbersCell.setUseVariableBorders(true);
            numbersCell.setBorderColorLeft(BaseColor.WHITE);
            numbersCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoTable.addCell(numbersCell);
            
            // add the info table to document
            lastParagraph.add(infoTable);
            
            int pageNumberAfterProducts = writer.getPageNumber();
            
            /*
             * document creator and receiver info fields
             */
            PdfPTable signaturesTable = new PdfPTable(2);
            signaturesTable.setWidthPercentage(100);
            signaturesTable.setWidths(new int[]{200,200});
            
            // creator
            Paragraph creatorParagraph = new Paragraph();
            
            creatorParagraph.add(new Phrase(language.get("creator")+":",TR_10));
            creatorParagraph.add(Chunk.NEWLINE);
            creatorParagraph.add(new Phrase(user.getName()+", "+user.getPhone(),TR_10));

            PdfPCell creatorCell = new PdfPCell(creatorParagraph);
            creatorCell.setBorder(Rectangle.NO_BORDER);
            signaturesTable.addCell(creatorCell);
            
            // receiver
            Paragraph receiverParagraph = new Paragraph();
            
            receiverParagraph.add(new Phrase(language.get("receiver")+":",TR_10));

            PdfPCell receiverCell = new PdfPCell(receiverParagraph);
            receiverCell.setBorder(Rectangle.NO_BORDER);
            signaturesTable.addCell(receiverCell);
            
            // add them to the table
            lastParagraph.add(signaturesTable);
            
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
