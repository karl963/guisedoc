package com.guisedoc.workshop.document.settings;

import javax.swing.ImageIcon;

import com.guisedoc.object.Firm;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooter extends PdfPageEventHelper{
	
	private int howManyPagesTotal = -1;
	private Image firmLogo;
	private Paragraph pageFirstParagraph;
	private boolean insertFirmData,insertLogo,stopCounting;
	private Firm firm;
	
	public HeaderFooter(Firm firm, boolean insertFirmData, boolean insertLogo){
		this.insertFirmData=insertFirmData;
		this.insertLogo = insertLogo;
		this.firm = firm;
		this.stopCounting = false;
	}

    public void onEndPage(PdfWriter writer, Document document) {
    	
    	Font TR_8 = DocumentFonts.TIMES_ROMAN_8();
    	Phrase firstPhrase,secondPhrase,thirdPhrase;
    	
    	if(insertFirmData){
    		
    		/*
        	 * first phrase
        	 */
        	firstPhrase = new Phrase("",TR_8);
        	
        	boolean firstAdded = true;
        	
        	if(!firm.getName().equals("")){
        		firstPhrase.add(new Phrase(firm.getName()));
        		firstAdded = false;
        	}
        	if(!firm.getAddress().equals("")){
        		if(!firstAdded){
        			firstPhrase.add("; ");
        		}
        		firstPhrase.add(firm.getAddress());
        		firstAdded = false;
        	}
        	if(!firm.getRegNR().equals("")){
        		if(!firstAdded){
        			firstPhrase.add("; ");
        		}
        		firstPhrase.add("Reg nr : "+firm.getRegNR());
        		firstAdded = false;
        	}
        	if(!firm.getKmkr().equals("")){
        		if(!firstAdded){
        			firstPhrase.add("; ");
        		}
        		firstPhrase.add("KMKR nr : "+firm.getKmkr());
        	}
        	
        	/*
        	 * second phrase
        	 */
        	secondPhrase = new Phrase("",TR_8);
        	
        	firstAdded = true;
        	
        	if(!firm.getPhone().equals("")){
        		if(firm.getPhone().equals(firm.getFax())){
        			secondPhrase.add("Tel/fax : "+firm.getPhone());
        		}
        		else{
        			secondPhrase.add("Tel : "+firm.getPhone());
        		}
        		firstAdded = false;
        	}
        	if(!firm.getEmail().equals("")){
        		if(!firstAdded){
        			secondPhrase.add("; ");
        		}
        		secondPhrase.add("e-post : "+firm.getEmail());
        	}
            
        	/*
        	 * third phrase
        	 */
            thirdPhrase = new Phrase("",TR_8);

        	firstAdded = true;
        	
        	if(!firm.getBank().equals("")){
        		thirdPhrase.add(firm.getBank());
        	}
        	if(!firm.getIban().equals("")){
        		if(!firstAdded){
        			thirdPhrase.add(" ");
        		}
        		thirdPhrase.add("IBAN "+firm.getIban());
        		firstAdded = false;
        	}
        	if(!firm.getSwift().equals("")){
        		if(!firstAdded){
        			thirdPhrase.add(" ");
        		}
        		thirdPhrase.add("SWIFT "+firm.getSwift());
        		firstAdded = false;
        	}

        	/*
        	 * add all phrases to the end page
        	 */
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, firstPhrase,document.right()/2, Rectangle.BOTTOM + 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, secondPhrase,document.right()/2, Rectangle.BOTTOM + 21, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, thirdPhrase,document.right()/2, Rectangle.BOTTOM + 12, 0);
    	}
        Phrase p4 = new Phrase(String.format("%d", writer.getPageNumber())+"/"+howManyPagesTotal,TR_8);
        
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, p4,document.right()/2, Rectangle.BOTTOM + 5, 0);
        
    }
    
    public void onStartPage(PdfWriter writer, Document document){
    	
    	if(!stopCounting){
    		howManyPagesTotal++;
    	}
		
    	if(insertLogo){
			try {
				
				firmLogo = Image.getInstance(firm.getLogoURL());
				firmLogo.scaleToFit(firm.getLogoWidth(), firm.getLogoHeight());
				firmLogo.setAlignment(Element.ALIGN_CENTER);
				
				document.add(firmLogo);
				document.add(new Paragraph(" "));
				
			} catch (Exception e) {

			}
    	}
    	
		try {
			
			document.add(pageFirstParagraph);
			
		} catch (Exception e) {}
    }
    
    public void resetPagesTotal(){
    	howManyPagesTotal = -1;
    }
    
    public void setPageCount(int count, boolean stopCounting){
    	this.howManyPagesTotal = count;
    	this.stopCounting = stopCounting;
    }
    
    public int getPageCount(){
    	return howManyPagesTotal;
    }
    
	public void setPageStartParagraph(Paragraph pageFirstParagraph){
		this.pageFirstParagraph = pageFirstParagraph;
	}
}
