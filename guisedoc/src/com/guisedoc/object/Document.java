package com.guisedoc.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.guisedoc.enums.DocumentType;
import com.guisedoc.workshop.document.DocumentBuilder;
import com.guisedoc.workshop.document.settings.Language;

public class Document {
	
	public static String DEFAULT_STRING = "";
	public static Double DEFAULT_DOUBLE = 0.0;
	public static long DEFAULT_LONG = 0L;
	public static int DEFAULT_INT = 0;
	public static boolean DEFAULT_BOOLEAN = false;
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	public static SimpleDateFormat HTML5_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@SerializedName("ID")
	private long ID;
	@SerializedName("number")
	private long number;
	
	@SerializedName("prefix")
	private String prefix;
	@SerializedName("orderNR")
	private String orderNR;
	@SerializedName("shipmentTime")
	private String shipmentTime;
	@SerializedName("shipmentAddress")
	private String shipmentAddress;
	@SerializedName("shipmentPlace")
	private String shipmentPlace;
	@SerializedName("CeSpecification")
	private String CeSpecification;
	@SerializedName("paymentRequirement")
	private String paymentRequirement;

	@SerializedName("validDue")
	private long validDue;
	@SerializedName("advance")
	private Double advance;
	
	@SerializedName("paydInCash")
	private boolean paydInCash;
	@SerializedName("showDiscount")
	private boolean showDiscount;
	@SerializedName("addToStatistics")
	private boolean addToStatistics;
	@SerializedName("showCE")
	private boolean showCE;

	@SerializedName("date")
	private Date date;
	
	@SerializedName("client")
	private Client client;

	private List<Product> products;
	private DocumentType type;
	private String fullNumber;
	private Language language;
	
	/*
	 * Constructors
	 */
	
	public Document(){
		this.number = Document.DEFAULT_LONG;
		this.prefix = Document.DEFAULT_STRING;
		this.orderNR = Document.DEFAULT_STRING;
		this.shipmentTime = Document.DEFAULT_STRING;
		this.shipmentAddress = Document.DEFAULT_STRING;
		this.shipmentPlace = Document.DEFAULT_STRING;
		this.validDue = Document.DEFAULT_LONG;
		this.advance = Document.DEFAULT_DOUBLE;
		this.paymentRequirement = Document.DEFAULT_STRING;
		this.paydInCash = Document.DEFAULT_BOOLEAN;
		this.showDiscount = Document.DEFAULT_BOOLEAN;
		this.addToStatistics = Document.DEFAULT_BOOLEAN;
		this.date = new Date();
		this.setProducts(new ArrayList<Product>());
		this.type = DocumentType.QUOTATION;
		this.client = new Client();
		this.fullNumber = null;
		this.setLanguage(new Language("EST"));
		this.showCE = !Document.DEFAULT_BOOLEAN;
		this.CeSpecification = Document.DEFAULT_STRING;;
	}
	
	/*
	 * Methods
	 */
	
	public String getFullNumber(){
		if(fullNumber != null){
			return fullNumber;
		}
		return prefix+number;
	}
	
	public Double getTotalSum(){
		Double sum = 0.0;
		for(Product p : products){
			sum += p.getTotalSum();
		}
		return sum;
	}
	
	public String getFormatedDate(){
		return Document.DATE_FORMAT.format(this.date);
	}
	
	public String getHtml5FormatedDate(){
		return Document.HTML5_DATE_FORMAT.format(this.date);
	}
	
	public String getTypeString(){
		return this.type.toString().toLowerCase();
	}
	
	public void setType(String typeString){
		typeString = typeString.toLowerCase();
		
		if(typeString.equals("invoice")){
			type = DocumentType.INVOICE;
		}
		else if(typeString.equals("advance_invoice")){
			type = DocumentType.ADVANCE_INVOICE;
		}
		else if(typeString.equals("delivery_note")){
			type = DocumentType.DELIVERY_NOTE;
		}
		else if(typeString.equals("quotation")){
			type = DocumentType.QUOTATION;
		}
		else if(typeString.equals("order")){
			type = DocumentType.ORDER;
		}
		else if(typeString.equals("order_confirmation")){
			type = DocumentType.ORDER_CONFIRMATION;
		}
	}
	
	public byte[] getDocumentInBytes(Firm firm, User user){
		return DocumentBuilder.build(this,firm,user);
	}
	
	/*
	 * getters and setters
	 */
	
	public DocumentType getType() {
		return type;
	}

	public void setType(DocumentType type) {
		this.type = type;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getOrderNR() {
		return orderNR;
	}

	public void setOrderNR(String orderNR) {
		this.orderNR = orderNR;
	}

	public String getShipmentTime() {
		return shipmentTime;
	}

	public void setShipmentTime(String shipmentTime) {
		this.shipmentTime = shipmentTime;
	}

	public String getShipmentAddress() {
		return shipmentAddress;
	}

	public void setShipmentAddress(String shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
	}

	public String getShipmentPlace() {
		return shipmentPlace;
	}

	public void setShipmentPlace(String shipmentPlace) {
		this.shipmentPlace = shipmentPlace;
	}
	
	public Long getValidDue() {
		return validDue;
	}

	public void setValidDue(long validDue) {
		this.validDue = validDue;
	}

	public Double getAdvance() {
		return advance;
	}

	public void setAdvance(Double advance) {
		this.advance = advance;
	}

	public String getPaymentRequirement() {
		return paymentRequirement;
	}

	public void setPaymentRequirement(String paymentRequirement) {
		this.paymentRequirement = paymentRequirement;
	}

	public boolean isPaydInCash() {
		return paydInCash;
	}

	public void setPaydInCash(boolean paydInCash) {
		this.paydInCash = paydInCash;
	}

	public boolean isShowDiscount() {
		return showDiscount;
	}

	public void setShowDiscount(boolean showDiscount) {
		this.showDiscount = showDiscount;
	}

	public boolean isAddToStatistics() {
		return addToStatistics;
	}

	public void setAddToStatistics(boolean addToStatistics) {
		this.addToStatistics = addToStatistics;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	public void setFullNumber(String fullNumber){
		this.fullNumber = fullNumber;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	public boolean isShowCE() {
		return showCE;
	}

	public void setShowCE(boolean showCE) {
		this.showCE = showCE;
	}
	public String getCeSpecification() {
		return CeSpecification;
	}

	public void setCeSpecification(String ceSpecification) {
		CeSpecification = ceSpecification;
	}
}
