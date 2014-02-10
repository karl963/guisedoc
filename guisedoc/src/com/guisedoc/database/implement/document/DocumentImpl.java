package com.guisedoc.database.implement.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.guisedoc.database.Connector;
import com.guisedoc.database.rowmapper.ClientRowMapper;
import com.guisedoc.database.rowmapper.DocumentRowMapper;
import com.guisedoc.database.rowmapper.ProductRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Client;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

public class DocumentImpl extends JdbcTemplate {
	
	public DocumentImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public Object addNewDocument(final User user,final String type){
		try{
			
			// make the doc number accordingly
			final String fullNumber = makeFullNumber(user,type);
			
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO documents "
							+ "(documentType, user_ID, fullNumber) VALUES (?,?,?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, type);
					stmt.setLong(2, user.getID());
					stmt.setString(3, fullNumber);

					return stmt;
				}
			}, keyHolder);

			Long documentID = keyHolder.getKey().longValue();
			
			// make the document opened
			ErrorType openResponse = openDocument(documentID,user.getID());
			
			if(openResponse == ErrorType.SUCCESS){
				// check how many documents are opened
				String countQuery = "SELECT COUNT(*) FROM opened_documents WHERE user_ID = "+user.getID();
				Long openedCount = queryForObject(countQuery,Long.class);
				
				return new Object[]{fullNumber,openedCount,documentID};
			}
			else{
				return openResponse;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType openDocument(long documentID,long userID){
		try{
			String checkQuery = "SELECT COUNT(*) FROM opened_documents WHERE "
					+ "user_ID = "+userID+" AND document_ID = "+documentID;
			
			Long result = queryForObject(checkQuery,Long.class);
			
			if(result == null || result == 0){
				String openQuery = "INSERT INTO opened_documents (user_ID, document_ID) "
						+ "VALUES("+userID+","+documentID+")";
				
				update(openQuery);
			}

			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getDocumentByID(long documentID){
		try{
			
			String documentQuery = "SELECT * FROM documents WHERE ID = "+documentID;
			
			List<Object> documentResponse = query(documentQuery, new DocumentRowMapper<Object>());;
			
			if(documentResponse.size() > 0){
				if(documentResponse.get(0) instanceof Document){
					Document document = (Document)documentResponse.get(0);
					
					// get the client if exists
					String clientQuery = "SELECT * FROM clients WHERE ID = "+document.getClientID();
					List<Object> clientResponse = query(clientQuery, new ClientRowMapper<Object>());
					
					if(clientResponse.size() > 0){
						if(clientResponse.get(0) instanceof Client){
							document.setClient((Client)clientResponse.get(0));
						}
						else{
							return ErrorType.DATABASE_QUERY;
						}
					}

					// get the products
					String productsQuery = "SELECT document_products.price AS price, "
							+ "document_products.o_price AS o_price,"
							+ " products.*, document_products.*, document_products.ID as unitID "
							+ "FROM products, document_products "
							+ "WHERE product_ID = products.ID "
							+ "AND document_ID = "+documentID;
					List<Object> productsResponse = query(productsQuery, new ProductRowMapper<Object>());
					
					if(productsResponse.size() > 0){
						if(productsResponse.get(0) instanceof Product){
							List<Product> sortedProducts = new ArrayList<Product>();
							
							// sort according to the queueNumber
							while(productsResponse.size() > 0){
								int lowestIndex = 0;
								for(int i = 0; i<productsResponse.size(); i++){
									if(((Product)productsResponse.get(i)).getQueueNumber() 
											<= 
										((Product)productsResponse.get(lowestIndex)).getQueueNumber())
									{
										lowestIndex = i;
									}
								}
								sortedProducts.add((Product)productsResponse.get(lowestIndex));
								productsResponse.remove(lowestIndex);
							}
						
							document.setProducts(sortedProducts);
						}
					}
					
					return document;
				}
				else{
					return ErrorType.DATABASE_QUERY;
				}
			}
			else{
				return (ErrorType)documentResponse.get(0);
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getDocumentByIDAndOpen(long documentID, long userID){
		try{
			
			Object response = getDocumentByID(documentID);
			
			if(response instanceof Document){
				
				ErrorType addResponse = openDocument(documentID,userID);
				
				if(addResponse == ErrorType.SUCCESS){
					return (Document)response;
				}
				else{
					return addResponse;
				}
			}
			else{
				return (ErrorType)response;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType saveDocumentAttribute(String key, Object value, long documentID){
		try{
			
			String valueQuery = String.valueOf(value);
			if(value instanceof String){
				valueQuery = "'"+valueQuery+"'";
			}
			
			String query = "UPDATE documents SET "+key+" = "+valueQuery+" "
					+ "WHERE ID = "+documentID;
			
			int response = update(query);
			
			if(response == 1){
				return ErrorType.SUCCESS;
			}
			else{
				return ErrorType.NOTHING_TO_UPDATE;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object importDocument(long openedID, long importedID){
		try{
			
			Object response  = getDocumentByID(importedID);
			
			if(response instanceof Document){
				
				ErrorType changeResponse = updateDocument((Document)response,openedID);
				
				if(changeResponse == ErrorType.SUCCESS){
					return (Document)response;
				}
				else{
					return changeResponse;
				}
				
			}
			else{
				return (ErrorType)response;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType addClientToDocument(long documentID, long clientID){
		try{
			
			String query = "UPDATE documents SET client_ID = "+clientID
					+" WHERE ID = "+documentID;
			
			int response = update(query);
			
			if(response == 1){
				return ErrorType.SUCCESS;
			}
			else{
				return ErrorType.NOTHING_TO_UPDATE;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType updateDocument(Document oldDocument, long newDocumentID){
		try{
			String query = "UPDATE documents SET orderNR = ?, "
					+ "shipmentTime = ?, shipmentAddress = ?, shipmentPlace = ?, CeSpecification = ?, "
					+ "paymentRequirement = ?, validDue = ?, advance = ?, paydInCash = ?, "
					+ "showDiscount = ?, addToStatistics = ?, showCE = ?, client_ID = ? "
					+ "WHERE ID=?";
			
			Object[] objects = new Object[]{
					oldDocument.getOrderNR(),
					oldDocument.getShipmentTime(),
					oldDocument.getShipmentAddress(),
					oldDocument.getShipmentPlace(),
					oldDocument.getCeSpecification(),
					oldDocument.getPaymentRequirement(),
					oldDocument.getValidDue(),
					oldDocument.getAdvance(),
					oldDocument.isPaydInCash(),
					oldDocument.isShowDiscount(),
					oldDocument.isAddToStatistics(),
					oldDocument.isShowCE(),
					oldDocument.getClient().getID(),
					newDocumentID
			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
					Types.VARCHAR,Types.BIGINT,Types.DOUBLE,Types.BOOLEAN,
					Types.BOOLEAN,Types.BOOLEAN,Types.BOOLEAN,Types.BOOLEAN,
					Types.BIGINT
			};
			
			int rowsAffected = update(query,objects,types);
			
			if(rowsAffected != 0){
				
				// add all the imported products to the new document
				String productQuery = "INSERT INTO document_products "
						+ "(document_ID, queueNumber, product_ID, amount, discount,"
						+ "totalSum, calculateSum, comments, additional_info,"
						+ "price, o_price) "
						+ "SELECT "+newDocumentID+","+99+", product_ID, amount, discount,"
						+ "totalSum, calculateSum, comments, additional_info,"
						+ "price, o_price FROM document_products "
						+ "WHERE document_ID = "+oldDocument.getID();
				
				int productResponse = update(productQuery);
				
				if(productResponse != 0){
					return ErrorType.SUCCESS;
				}
				else{
					return ErrorType.DATABASE_QUERY;
				}
			}
			else{
				return ErrorType.NOTHING_TO_UPDATE;
			}

		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object searchForDocuments(String type, String fullNumber){
		try{
			
			String documentQuery = "SELECT ID, documentDate, "
					+ "COALESCE((SELECT name FROM clients WHERE "
					+ "ID = client_ID),'') AS clientName, "
					+ "fullNumber, "
					+ "COALESCE((SELECT sum(totalSum) FROM "
					+ "document_products where document_ID = documents.ID)"
					+ ",0) AS totalSum "
					+ "FROM documents WHERE "
					+ "documentType = '"+type+"' group by ID";
			
			List<Object> response = query(documentQuery, new DocumentRowMapper<Object>());;

			if(response.size() > 0){
				if(response.get(0) instanceof Document){
					List<Object> documents = new ArrayList<Object>();

					// filter by fullNumber
					for(Object obj : response){
						if(((Document)obj).getFullNumber()
							.contains(fullNumber)){
							documents.add(obj);
						}
					}

					return documents;
				}
				else{
					return (ErrorType)response.get(0);
				}
			}
			else{
				return new ArrayList<Document>();
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public String makeFullNumber(User user,String type){
		try{
			
			// if the user hasn't chosen to make the number
			if(!user.getSettings().getSettingValue("autoSetDocNumber")){
				return "";
			}
			
			/*
			 * there's a little different word for 
			 * 'order' in database (because of mysql syntax)
			 */
			String prefix = type;
			if(type.equals("order")){
				prefix = "order_prefix";
			}

			// get the count
			String queryCount = "SELECT COUNT(*) FROM documents "
					+ "WHERE documentType = '"+type+"'";
			
			int typeCount = queryForObject(queryCount, Integer.class);
			
			// get the prefix
			String queryPrefix = "SELECT "+prefix+" FROM prefixes LIMIT 1";
			String prefixString = queryForObject(queryPrefix, String.class);

			String fullNumber = prefixString
					+new DecimalFormat("0000000").format(typeCount);
					
			return fullNumber;
		}
		catch(Exception x){
			x.printStackTrace();
			return "";
		}
	}
	
	public Object getAllOpenedDocuments(User user){
		try{
			List<Document> documents = new ArrayList<Document>();
			String query = "select document_ID as ID, "
					+ "fullNumber "
					+ "from documents, opened_documents "
					+ "where opened_documents.user_ID = 1 "
					+ "AND opened_documents.document_ID = documents.ID "
					+ "AND opened_documents.user_ID = "+user.getID();
			
			List<Object> result = query(query, new DocumentRowMapper<Object>());
			
			if(result.size() > 0){
				if(result.get(0) instanceof Document){
					
					for(Object obj : result){
						documents.add((Document)obj);
					}

					return documents;
				}
				else{
					return ErrorType.DATABASE_QUERY;
				}
			}
			else{
				return documents;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

	public ErrorType closeDocument(long userID,long documentID){
		try{
			
			String query = "DELETE FROM opened_documents WHERE "
					+ "user_ID = "+userID+" AND document_ID = "+documentID;
			
			int response = update(query);
			
			if(response == 1){
				return ErrorType.SUCCESS;
			}
			else{
				return ErrorType.NONE_FOUND;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getDocumentProductByID(long productID){
		try{
			
			String query = "SELECT document_products.price AS price, "
					+ "document_products.o_price AS o_price,"
					+ " products.*, document_products.*, document_products.ID AS unitID "
					+ "FROM products, document_products WHERE "
					+ "document_products.ID = "+productID+" AND "
					+ "document_products.product_ID = products.ID";

			List<Object> response = query(query,new ProductRowMapper<Object>());
			
			if(response instanceof List){
				if(response.size() > 0){
					if(response.get(0) instanceof Product){
						return (Product)response.get(0);
					}
					else{
						return ErrorType.DATABASE_QUERY;
					}
				}
				else{
					return ErrorType.NONE_FOUND;
				}
			}
			else{
				return ErrorType.DATABASE_QUERY;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object addProductToDocument(final Product product,final long documentID){
		try{
			
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO document_products "
							+ "(product_ID, document_ID, amount, discount,"
							+ "totalSum, calculateSum, comments, additional_info,"
							+ "queueNumber,price,o_price) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setLong(1, product.getID());
					stmt.setLong(2, documentID);
					stmt.setDouble(3, product.getAmount());
					stmt.setDouble(4, product.getDiscount());
					stmt.setDouble(5, product.getTotalSum());
					stmt.setBoolean(6, product.isCalculateSum());
					stmt.setString(7, product.getComments());
					stmt.setString(8, product.getAdditional_Info());
					stmt.setLong(9, product.getQueueNumber());
					stmt.setDouble(10, product.getPrice());
					stmt.setDouble(11, product.getO_price());
					
					return stmt;
				}
			}, keyHolder);

			Long productID = keyHolder.getKey().longValue();
	
			return productID;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object addProductToDocument(final long productID,final Double amount, 
			final long documentID, final String type){
		try{
			// first get the price and order price for calculation
			String existQuery = "SELECT price, o_price FROM products WHERE ID="+productID;
			List<Object> existingProducts = query(existQuery,new ProductRowMapper<Object>());
			
			if(existingProducts.size() > 0){
				
				// calculate the sum for adding
				Product existingProduct = (Product)existingProducts.get(0);
				final Double estonianSum = amount * existingProduct.getPrice();
				final Double orderSum = amount * existingProduct.getO_price();
						
				// insert and get the generated (ID) key back
				KeyHolder keyHolder = new GeneratedKeyHolder();
				update(new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						
						String query = "INSERT INTO document_products "
								+ "(product_ID, document_ID, amount,"
								+ "totalSum, price, o_price) "
								+ "SELECT "+productID+","+documentID+","+amount+",";
						
						if(!type.equals("order")){
							query += estonianSum;
						}
						else{
							query += orderSum;
						}
						
						query += ",price, o_price FROM products WHERE ID = "+productID;
						
						PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});
						
						return stmt;
					}
				}, keyHolder);

				Long newDocumentProductID = keyHolder.getKey().longValue();
				
				
				
				String productQuery = "SELECT document_products.price AS price,"
						+ "document_products.o_price AS o_price,"
						+ " products.*, document_products.*, "
						+ "document_products.ID AS unitID "
						+ "FROM products, document_products "
						+ "WHERE products.ID = product_ID AND "
						+ "document_products.ID = "+newDocumentProductID;
				
				List<Object> products = query(productQuery, new ProductRowMapper<Object>());
		
				if(products.size()>0){
					if(products.get(0) instanceof Product){
						return (Product)products.get(0);
					}
					else{
						return (ErrorType)products.get(0);
					}
				}
				else{
					return ErrorType.NONE_FOUND;
				}
			}
			else{
				return ErrorType.NONE_FOUND;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType deleteProductsFromDocument(List<Product> products){
		try{
			
			String query = "DELETE FROM document_products WHERE "
					+ "ID IN (";
			
			boolean wasBefore = false;
			for(Product p: products){
				if(wasBefore){
					query += ",";
				}
				
				query+= p.getUnitID();
				
				wasBefore = true;
			}
			
			query += ")";

			update(query);
	
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType saveDocumentProduct(Product product){
		try{
			String query = "UPDATE document_products SET amount = ?, discount = ?, totalSum = ?,"
					+ "queueNumber = ?, calculateSum = ?, comments = ?, additional_info = ?,"
					+ "price = ?, o_price = ? "
					+ "WHERE ID=?";
			
			Object[] objects = new Object[]{
					product.getAmount(),
					product.getDiscount(),
					product.getTotalSum(),
					product.getQueueNumber(),
					product.isCalculateSum(),
					product.getComments(),
					product.getAdditional_Info(),
					product.getPrice(),
					product.getO_price(),
					product.getUnitID()
			};
			int[] types = new int[]{
					Types.DOUBLE,
					Types.DOUBLE,
					Types.DOUBLE,
					Types.BIGINT,
					Types.BOOLEAN,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.DOUBLE,
					Types.DOUBLE,
					Types.BIGINT
			};
			
			update(query,objects,types);
			
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType updateQueueNumbers(List<Product> products, long documentID){
		try{
			for(Product product : products){
				String query = "UPDATE document_products SET "
						+ "queueNumber = ? "
						+ "WHERE ID = ? AND document_ID = ?";
				
				Object[] objects = new Object[]{
						product.getQueueNumber(),
						product.getUnitID(),
						documentID
				};
				int[] types = new int[]{
						Types.BIGINT,
						Types.BIGINT,
						Types.BIGINT
				};
				
				update(query,objects,types);
			}
			
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
}
