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
import com.guisedoc.database.rowmapper.ContactPersonRowMapper;
import com.guisedoc.database.rowmapper.ProductRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Client;
import com.guisedoc.object.ClientDocument;
import com.guisedoc.object.ContactPerson;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

public class ClientImpl extends JdbcTemplate {
	
	public ClientImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public ClientImpl(DataSource ds){
		super(ds);
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

					// get the contact persons
					String contactQuery = "SELECT * FROM contact_persons WHERE "
							+ "client_ID = "+id;
					
					List<ContactPerson> contactPersons = query(contactQuery,new ContactPersonRowMapper<ContactPerson>());
					((Client) clients.get(0)).setContactPersons(contactPersons);

					// get the documents
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
							+ "name, address, additionalAddress,"
							+ " email, phone"
							+ ") VALUES (?,?,?,?,?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, client.getName());
					stmt.setString(2, client.getAddress());
					stmt.setString(3, client.getAdditionalAddress());
					stmt.setString(4, client.getEmail());
					stmt.setString(5, client.getPhone());

					return stmt;
				}
			}, keyHolder);
			
			long ID = keyHolder.getKey().longValue();
			
			// add the contactperson if it's specified
			if(!client.getSelectedContactPerson().getName().equals("")){
				String contactQuery = "INSERT INTO contact_persons (name,client_ID)"
						+ " VALUES ('"+client.getSelectedContactPerson().getName()+"',"+ID+")";
				update(contactQuery);
			}

			return ID;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object addNewContactPerson(final String name, final long clientID){
		try{
			
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO contact_persons ("
							+ "name, client_ID) "
							+ "VALUES (?,?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, name);
					stmt.setLong(2, clientID);


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
	
	public ErrorType changeContactPersonName(long contactID, String name){
		try{
			String query = "UPDATE contact_persons SET name = ? "
					+ "WHERE ID=?";
			
			Object[] objects = new Object[]{
					name,
					contactID,
			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.BIGINT
			};

			int response = update(query,objects,types);

			if(response > 0){
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
	
	public ErrorType deleteContactPersons(List<ContactPerson> contactPerson){
		try{
			String query = "DELETE FROM contact_persons WHERE ID IN (";
			String documentsQuery = "UPDATE documents SET contact_person_ID = 0 WHERE contact_person_ID IN (";
					
			boolean wasBefore = false;
			for(ContactPerson c: contactPerson){
				if(wasBefore){
					query += ",";
					documentsQuery += ",";
				}
				
				query+= c.getID();
				documentsQuery += c.getID();
				
				wasBefore = true;
			}
			
			query += ")";
			documentsQuery += ")";
			
			update(query);
			update(documentsQuery);
			
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType saveClient(Client client){
		try{
			String query = "UPDATE clients SET name = ?, phone = ?, "
					+ "address = ?, additionalAddress = ?, email = ?"
					+ " WHERE ID=?";
			
			Object[] objects = new Object[]{
					client.getName(),
					client.getPhone(),
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
	
	public Object searchForTypeClients(Client searchClient,
			boolean nonBuyers,boolean realBuyers, boolean sellers,
			boolean filterWithName){
		try{
			
			Object[] returnable = null;
			
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
					+ "name LIKE '%"+searchClient.getName()+"%'";

			// get the clients
			List<Client> clients = query(query,new ClientRowMapper<Client>());
			
			// get the total products
			long totalClients = queryForObject("SELECT COUNT(*) FROM clients",Long.class);
			
			// sort out the clients with contact-persons, if added to search
			String nameFilter = "";
			if(filterWithName){
				nameFilter = "AND name LIKE '%"+searchClient.getSelectedContactPerson().getName()+"%'";
			}
			
			List<Client> resultClients = new ArrayList<Client>();
			
			for(Client client : clients){
				
				String contactQuery = "SELECT * FROM contact_persons WHERE "
						+ "client_ID = "+client.getID()+" "
						+ nameFilter;
				
				List<ContactPerson> contactPersons = query(contactQuery,new ContactPersonRowMapper<ContactPerson>());

				if(contactPersons.size() > 0){
					client.setContactPersons(contactPersons);
					resultClients.add(client);
				}
			}
			
			returnable = new Object[]{totalClients,resultClients};
			
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