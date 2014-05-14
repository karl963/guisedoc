package com.guisedoc.database.implement.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.guisedoc.database.Connector;
import com.guisedoc.database.rowmapper.ProductRowMapper;
import com.guisedoc.database.rowmapper.StatObjectRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Product;
import com.guisedoc.object.StatisticsObject;
import com.guisedoc.object.StatisticsSummary;
import com.guisedoc.workshop.document.settings.DateFormats;

public class StatisticsImpl extends JdbcTemplate{
	
	public StatisticsImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public Object searchForStatistics(Map<String,String> map){
		try{
			
			long clientID = Long.parseLong(map.get("clientID"));
			String code = map.get("code");
			
			// make the document type
			String type = "";
			if(map.get("dataGroup").equals("nonBuyer")){
				type = "quotation";
			}
			else if(map.get("dataGroup").equals("seller")){
				type = "order";
			}
			else if(map.get("dataGroup").equals("realBuyer")){
				type = "order_confirmation";
			}
			
			// make the dates
			Object afterDate = map.get("startDate");
			if((afterDate).equals("")){
				afterDate = null;
			}
			else{  // after date is already 00:00:00
				afterDate = new Timestamp(
						DateFormats.HTML5_DATE_FORMAT().parse((String)afterDate)
						.getTime());
			}
			Object beforeDate = map.get("endDate");
			if((beforeDate).equals("")){
				beforeDate = null;
			}
			else{ // before date needs to have time at 23:59:59
				Calendar c = Calendar.getInstance();
				c.setTime(DateFormats.HTML5_DATE_FORMAT().parse((String)beforeDate));
				c.set(Calendar.HOUR_OF_DAY, 23);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND, 59);
				beforeDate = new Timestamp(c.getTime().getTime());
			}
			
			boolean groupResults = false;
			if(((String)map.get("statisticsType")).equals("sumAll")){
				groupResults = true;
			}

			// make the query accordingly
			String query = "SELECT document_products.*, products.code, "
					+ "products.name, products.unit, documents.documentDate, "
					+ "COALESCE( "
					+ "(SELECT name FROM clients, documents WHERE clients.ID = client_ID AND documents.ID = document_products.document_ID) " 
					+ ",'') AS clientName "
					+ "FROM document_products, products, documents "
					+ "WHERE product_ID = products.ID AND "
					+ "document_ID = documents.ID AND "
					+ "addToStatistics = 1 AND "
					+ "documentType = '"+type+"' AND "
					+ "code LIKE '%"+code+"%'";
			
			// filtering as the user requested
			if(clientID != 0){
				query += " AND COALESCE( "
					+ "(SELECT clients.ID FROM clients, documents WHERE clients.ID = client_ID AND documents.ID = document_products.document_ID) " 
					+ ",0) = "+clientID;
			}
			if(!code.equals("")){
				query += " AND code LIKE '%"+code+"%'";
			}
			if(beforeDate != null){
				query += " AND documentDate <= '"+beforeDate+"'";
			}
			if(afterDate != null){
				query += " AND documentDate >= '"+afterDate+"'";
			}
			
			if(groupResults){
				query = "SELECT code, name, unit, "
					+ "SUM(amount) AS amount, SUM(totalSum) AS totalSum "
					+ "FROM ("+query+") resultTable " // result table needs an alias
					+ "GROUP BY code, name, unit";
			}
			
			// get the statistics
			List<Object> statObjects = query(query,new StatObjectRowMapper<Object>());

			if(statObjects.size() > 0){
				if(statObjects.get(0) instanceof StatisticsObject){
					return statObjects;
				}
				else{
					return ErrorType.DATABASE_QUERY;
				}
			}
			else{
				return statObjects;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getStatisticsSummary(Map<String,String> map){
		try{
			StatisticsSummary summary = new StatisticsSummary();
			// get the statistics
			Object statObjects = searchForStatistics(map);

			if(statObjects instanceof List){
				
				summary.setStatObjects((List<StatisticsObject>)statObjects);

				// add the data
				summary.setClientName(map.get("clientName"));
				summary.setCode(map.get("code"));
				
				if(map.get("dataGroup").equals("seller")){
					summary.setClientType("Müüja");
				}
				else if(map.get("dataGroup").equals("nonBuyer")){
					summary.setClientType("Hinnapärija");
				}
				else if(map.get("dataGroup").equals("realBuyer")){
					summary.setClientType("Ostja");
				}
				
				// make the dates
				if(!(map.get("startDate")).equals("")){
					summary.setStartDate(DateFormats.HTML5_DATE_FORMAT().parse(map.get("startDate")));
				}
				else{
					summary.setStartDate(new Date(Long.MIN_VALUE));
				}
				if(!(map.get("endDate")).equals("")){
					summary.setEndDate(DateFormats.HTML5_DATE_FORMAT().parse(map.get("endDate")));
				}
				else{
					summary.setEndDate(new Date(Long.MAX_VALUE));
				}
				
				return summary;
			}
			else{
				return (ErrorType)statObjects;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getStatObjectByID(long documentProductID){
		try{
			String query = "SELECT ID, amount, totalSum "
					+ "FROM document_products "
					+ "WHERE ID = "+documentProductID;

			List<Object> statObjects = query(query, new StatObjectRowMapper<Object>());
			
			if(statObjects.size() > 0){
				if(statObjects.get(0) instanceof StatisticsObject){
					return (StatisticsObject)statObjects.get(0);
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
	
	public ErrorType deleteStatisticsObjects(List<StatisticsObject> statObjects){
		try{
			String query = "DELETE FROM document_products WHERE ID IN (";
			
			boolean wasBefore = false;
			for(StatisticsObject ststObj: statObjects){
				if(wasBefore){
					query += ",";
				}
				
				query+= ststObj.getID();
				
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
	
	public ErrorType saveStatisticsObject(StatisticsObject statObj){
		try{
			String query = "UPDATE document_products SET amount = ?, totalSum = ?, "
					+ "calculateSum = 0"
					+ " WHERE ID=?";
			
			Object[] objects = new Object[]{
					statObj.getAmount(),
					statObj.getTotalPrice(),
					statObj.getID()
			};
			int[] types = new int[]{
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
}