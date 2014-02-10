package com.guisedoc.database.implement.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.guisedoc.database.Connector;
import com.guisedoc.database.rowmapper.ClientDocumentRowMapper;
import com.guisedoc.database.rowmapper.ClientRowMapper;
import com.guisedoc.database.rowmapper.ProductRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Client;
import com.guisedoc.object.ClientDocument;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

public class ClientImpl extends JdbcTemplate {
	
	public ClientImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public Object getClientByID(long id){
		try{
			String query = "SELECT * , "
					+ "COALESCE( "
					+ "(SELECT SUM(totalSum) FROM document_products, documents WHERE document_ID = documents.ID AND client_ID = clients.ID) "
					+ ",0) AS totalBoughtFor, "
					+ "COALESCE("
					+ "(SELECT COUNT(*) FROM documents WHERE client_ID = clients.ID) "
					+ ",0) AS totalDeals, "
					+ "COALESCE( "
					+ "(SELECT documentDate FROM documents WHERE "
					+ "client_ID = clients.ID ORDER BY documentDate DESC LIMIT 1) "
					+ ",'') AS lastDealDate, "
					+ "COALESCE( "
					+ "(SELECT fullNumber FROM (SELECT fullNumber,documentDate, client_ID FROM documents) result WHERE client_ID = clients.ID ORDER BY documentDate DESC LIMIT 1) "
					+ ",'') AS fullNumber "
					+ "FROM clients WHERE clients.ID="+id;

			List<Object> clients = query(query, new ClientRowMapper<Object>());

			if(clients.size() > 0){
				if(clients.get(0) instanceof Client){
					
					Object documents = getClientDocuments(id,"");
					
					if(documents instanceof List){
						return new Object[]{
								clients.get(0),
								documents
						};
					}
					else{
						return ErrorType.DATABASE_QUERY;
					}
				}
				else{
					return ErrorType.DATABASE_QUERY;
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
	
	public Object getClientDocuments(long id,String fullNumber){
		try{
			String query = "SELECT COALESCE((SELECT SUM(totalSum) FROM "
					+ "document_products WHERE document_ID = documents.ID),0) "
					+ "AS totalSum,"
					+ "documentDate, "
					+ "fullNumber, ID "
					+ "FROM documents "
					+ "WHERE client_ID = "+id+" AND "
					+ "fullNumber LIKE '%"+fullNumber+"%'";

			List<ClientDocument> documents = 
					query(query, new ClientDocumentRowMapper<ClientDocument>());
			
			if(documents.size() > 0){
				if(documents.get(0) instanceof ClientDocument){
					return documents;
				}
				else{
					return ErrorType.DATABASE_QUERY;
				}
			}
			else{
				return new ArrayList<ClientDocument>();
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object addNewClient(final Client client){
		try{
			
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO clients ("
							+ "name, contactPerson, address, additionalAddress,"
							+ " email, phone"
							+ ") VALUES (?,?,?,?,?,?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, client.getName());
					stmt.setString(2, client.getContactPerson());
					stmt.setString(3, client.getAddress());
					stmt.setString(4, client.getAdditionalAddress());
					stmt.setString(5, client.getEmail());
					stmt.setString(6, client.getPhone());

					return stmt;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType deleteClients(List<Client> clients){
		try{
			String query = "DELETE FROM clients WHERE ID IN (";
			
			boolean wasBefore = false;
			for(Client c: clients){
				if(wasBefore){
					query += ",";
				}
				
				query+= c.getID();
				
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
	
	public ErrorType saveClient(Client client){
		try{
			String query = "UPDATE clients SET name = ?, phone = ?, contactPerson = ?,"
					+ "address = ?, additionalAddress = ?, email = ?"
					+ " WHERE ID=?";
			
			Object[] objects = new Object[]{
					client.getName(),
					client.getPhone(),
					client.getContactPerson(),
					client.getAddress(),
					client.getAdditionalAddress(),
					client.getEmail(),
					client.getID(),
			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
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
	
	public Object searchForTypeClients(Client client,
			boolean nonBuyers,boolean realBuyers, boolean sellers){
		try{
			// make the query accordingly
			
			String typesQuery = "";
			if(nonBuyers || realBuyers || sellers){
				typesQuery += " AND (";
				boolean wasBefore = false;
				if(nonBuyers){
					typesQuery += "documentType = 'quotation'";
					wasBefore = true;
				}
				if(realBuyers){
					if(wasBefore){
						typesQuery +=" OR ";
					}
					typesQuery += "documentType = 'order_confirmation'";
					wasBefore = true;
				}
				if(sellers){
					if(wasBefore){
						typesQuery +=" OR ";
					}
					typesQuery += "documentType = 'order'";
				}
				typesQuery += ")";
			}
			
			String query = "SELECT *,"
					+ "COALESCE("
					+ "(SELECT SUM(totalSum) FROM document_products, documents WHERE document_ID = documents.ID AND client_ID = clients.ID "+typesQuery+")"
					+ ",0) AS totalBoughtFor,"
					+ "COALESCE("
					+ "(SELECT COUNT(*) FROM documents WHERE client_ID = clients.ID "+typesQuery+")"
					+ ",0) AS totalDeals "
					+ "FROM clients WHERE "
					+ "name LIKE '%"+client.getName()+"%' AND "
					+ "contactPerson LIKE '%"+client.getContactPerson()+"%'";

			// get the clients
			List<Client> clients = query(query,new ClientRowMapper<Client>());
			
			// get the total products
			long totalClients = queryForObject("SELECT COUNT(*) FROM clients",Long.class);
			
			Object[] returnable = new Object[]{totalClients,clients};
			return returnable;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType saveClientAttribute(String key, Object value, long clientID){
		try{
			
			String valueQuery = String.valueOf(value);
			if(value instanceof String){
				valueQuery = "'"+valueQuery+"'";
			}
			
			String query = "UPDATE clients SET "+key+" = "+valueQuery+" "
					+ "WHERE ID = "+clientID;
			
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
}