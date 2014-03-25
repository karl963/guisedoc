package com.guisedoc.database.implement.firm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.guisedoc.database.Connector;
import com.guisedoc.database.rowmapper.FirmRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Firm;

public class FirmImpl extends JdbcTemplate{
	
	public FirmImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public Object getFirmData(){
		try{
			Firm firm = new Firm();

			String query = "SELECT * FROM firm";
			List<Object> response = query(query, new FirmRowMapper<Object>());
			
			if(response.size() > 0){
				if(response.get(0) instanceof Firm){
					firm = (Firm)response.get(0);
				}
				else{
					return (ErrorType)response.get(0);
				}
			}
			else{
				return ErrorType.NONE_FOUND;
			}
			
			return firm;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

	public Object getPrefixesArray(){
		try{
			String[] prefixes = new String[6];
			
			String query = "SELECT * FROM prefixes";
			Map<String,Object> map = queryForMap(query);

			prefixes[0] = (String) map.get("invoice");
			prefixes[1] = (String) map.get("advance_invoice");
			prefixes[2] = (String) map.get("quotation");
			prefixes[3] = (String) map.get("order_confirmation");
			prefixes[4] = (String) map.get("order_prefix");
			prefixes[5] = (String) map.get("delivery_note");
			
			return prefixes;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType saveFirm(Firm firm){

		try{
			String query = "UPDATE firm SET name = ?, address = ?, regNR = ?,"
					+ "kmkr = ?, phone = ?, email = ?, bank = ?, iban = ?,"
					+ "swift = ?, fax = ?, logoURL = ?, logoWidth = ?, logoHeight = ?";
			
			Object[] objects = new Object[]{
					firm.getName(),
					firm.getAddress(),
					firm.getRegNR(),
					firm.getKmkr(),
					firm.getPhone(),
					firm.getEmail(),
					firm.getBank(),
					firm.getIban(),
					firm.getSwift(),
					firm.getFax(),
					firm.getLogoURL(),
					firm.getLogoWidth(),
					firm.getLogoHeight()
			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.INTEGER,
					Types.INTEGER
			};
			
			update(query,objects,types);
			
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType savePrefixes(Map<String,String> map){
		try{
			String query = "UPDATE prefixes SET quotation = ?, invoice = ?, advance_invoice = ?,"
					+ "delivery_note = ?, order_prefix = ?, order_confirmation = ?";
			
			Object[] objects = new Object[]{
					map.get("quotation"),
					map.get("invoice"),
					map.get("advanceInvoice"),
					map.get("deliveryNote"),
					map.get("order"),
					map.get("orderConfirmation"),
			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
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
