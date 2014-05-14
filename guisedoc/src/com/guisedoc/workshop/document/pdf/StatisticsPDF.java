package com.guisedoc.workshop.document.pdf;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import com.guisedoc.object.Firm;
import com.guisedoc.object.StatisticsObject;
import com.guisedoc.object.StatisticsSummary;
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

public class StatisticsPDF {
	
	StatisticsSummary SUMMARY;
	
	public StatisticsPDF(StatisticsSummary s){
		this.SUMMARY = s;
	}
	
	public byte[] make(Firm firm, User user){
		
		/*
		 * make metadata for the document
		 */
		Language language = SUMMARY.getLanguage();
		DecimalFormat COMMA_2 = NumberFormats.TWO_AFTER_COMMA();
		
		Font TR_12_B = DocumentFonts.TIMES_ROMAN_12_BOLD();
		Font TR_12 = DocumentFonts.TIMES_ROMAN_12();
		Font TR_10_B = DocumentFonts.TIMES_ROMAN_10_BOLD();
		Font TR_10 = DocumentFonts.TIMES_ROMAN_10();
		
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
			headerTable.setWidthPercentage(100);
			
			// Type cell
			headerTable.addCell(new PdfPCell(new Phrase(language.get("statisticsSummary")+":  "+SUMMARY.getFormatedDatePeriod(), TR_12_B)));
			
			// Date cell
			PdfPCell dateCell = new PdfPCell(new Phrase(DateFormats.DOT_DATE_FORMAT().format(new Date()),TR_10));
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(dateCell);
			
			// Client type cell
			PdfPCell clientTypeCell = new PdfPCell(new Phrase(language.get("clientType")+": "+SUMMARY.getClientType(),TR_12_B));
			clientTypeCell.setBorder(Rectangle.NO_BORDER);
			headerTable.addCell(clientTypeCell);

			headerTable.addCell(emptyCell);
			
			// Client name cell
			if(!SUMMARY.getClientName().equals("")){
				PdfPCell clientCell = new PdfPCell(new Phrase(language.get("client")+": "+SUMMARY.getClientName(),TR_12_B));
				clientCell.setBorder(Rectangle.NO_BORDER);
				headerTable.addCell(clientCell);

				headerTable.addCell(emptyCell);
			}
			
			// code cell
			if(!SUMMARY.getCode().equals("")){
				PdfPCell codeCell = new PdfPCell(new Phrase(language.get("queryCode")+": "+SUMMARY.getCode(),TR_12_B));
				codeCell.setBorder(Rectangle.NO_BORDER);
				headerTable.addCell(codeCell);

				headerTable.addCell(emptyCell);
			}
			
			// done with header table
			pageStartParagraph.add(headerTable);

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
			PdfPTable productsHeaderRow = new PdfPTable(6);
			productsHeaderRow.setWidthPercentage(100);
			productsHeaderRow.setWidths(new int[]{8,30,96,22,12,20});
			
			
			// products table header row
			
			PdfPCell headerNRCell = new PdfPCell(new Phrase(language.get("nr"),TR_12));
			headerNRCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerNRCell);
			
			PdfPCell headerCodeCell = new PdfPCell(new Phrase(language.get("code"),TR_12));
			headerCodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerCodeCell);
			
			productsHeaderRow.addCell(new PdfPCell(new Phrase(language.get("specification"),TR_12)));
			
			PdfPCell headerAmountCell = new PdfPCell(new Phrase(language.get("amount"),TR_12));
			headerAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerAmountCell);
			
			PdfPCell headerUnitCell = new PdfPCell(new Phrase(language.get("unit"),TR_12));
			headerUnitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerUnitCell);

			PdfPCell headerSumCell = new PdfPCell(new Phrase(language.get("sum"),TR_12));
			headerSumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			productsHeaderRow.addCell(headerSumCell);
			
			// add all the products
			
			double totalSum = 0.0, totalAmount = 0.0;
			
			int lastPageNumber = 0;
			boolean firstProductInPage = true;
			boolean updateBlackBoard = false;

			for(int i = 0;i<SUMMARY.getStatObjects().size();i++){
				
				StatisticsObject statObject = SUMMARY.getStatObjects().get(i);
				
				// each products has a separate table with the same size as header
				PdfPTable productRow = new PdfPTable(productsHeaderRow.getNumberOfColumns());
				productRow.setWidthPercentage(100);
				productRow.setWidths(new int[]{8,30,96,22,12,20});
				
				// nr cell
				PdfPCell NRCEll = new PdfPCell(new Phrase(""+(i+1),TR_10));
				NRCEll.setHorizontalAlignment(Element.ALIGN_CENTER);
				NRCEll.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// code cell
				PdfPCell codeCell = new PdfPCell(new Phrase(statObject.getCode(),TR_10));
				codeCell.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// name cell
				PdfPCell nameCell = new PdfPCell(new Phrase(statObject.getName(),TR_10));
				nameCell.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// amount cell
				PdfPCell amountCell = new PdfPCell(new Phrase(statObject.getAmount()+"",TR_10));
				amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				amountCell.setBorderColor(BaseColor.LIGHT_GRAY);

				// unit cell
				PdfPCell unitCell = new PdfPCell(new Phrase(statObject.getUnit(),TR_10));
				unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				unitCell.setBorderColor(BaseColor.LIGHT_GRAY);
				
				// sum cell
				PdfPCell sumCell = new PdfPCell(new Phrase(COMMA_2.format(statObject.getTotalPrice()),TR_10));
				sumCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				sumCell.setBorderColor(BaseColor.LIGHT_GRAY);

				// add all the cells
				productRow.addCell(NRCEll);
				productRow.addCell(codeCell);
				productRow.addCell(nameCell);
				productRow.addCell(amountCell);
				productRow.addCell(unitCell);
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
					codeCell.setUseVariableBorders(true);
					nameCell.setUseVariableBorders(true);
					amountCell.setUseVariableBorders(true);
					unitCell.setUseVariableBorders(true);
					sumCell.setUseVariableBorders(true);
					
					NRCEll.setBorderColorTop(BaseColor.BLACK);
					codeCell.setBorderColorTop(BaseColor.BLACK);
					nameCell.setBorderColorTop(BaseColor.BLACK);
					amountCell.setBorderColorTop(BaseColor.BLACK);
					unitCell.setBorderColorTop(BaseColor.BLACK);
					sumCell.setBorderColorTop(BaseColor.BLACK);
					
					/*
					 * this is needed, because SOMEWHY color changing also changes the width
					 */
					NRCEll.setBorderWidth(borderWidth/2);
					codeCell.setBorderWidth(borderWidth/2);
					nameCell.setBorderWidth(borderWidth/2);
					amountCell.setBorderWidth(borderWidth/2);
					unitCell.setBorderWidth(borderWidth/2);
					sumCell.setBorderWidth(borderWidth/2);
					
					productRow.addCell(NRCEll);
					productRow.addCell(codeCell);
					productRow.addCell(nameCell);
					productRow.addCell(amountCell);
					productRow.addCell(unitCell);
					productRow.addCell(sumCell);

				}
				
				totalSum += statObject.getTotalPrice();
				totalAmount += statObject.getAmount();
				
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
			PdfPTable infoTable = new PdfPTable(1);
			infoTable.setWidthPercentage(100);

			PdfPCell cinfo = new PdfPCell(new Paragraph(new Phrase(
            		"Kogus kokku:  "+COMMA_2.format(totalAmount)+"    "+
            		"Summa kokku:  "+COMMA_2.format(totalSum)
            		,TR_10_B)));
        	cinfo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        	infoTable.addCell(cinfo);
		
            // add the info table to document
            lastParagraph.add(infoTable);
            
            int pageNumberAfterProducts = writer.getPageNumber();

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
				if(SUMMARY.getStatObjects().size()==0){ // we have no objects, just to make it look nicer, add a blank
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
