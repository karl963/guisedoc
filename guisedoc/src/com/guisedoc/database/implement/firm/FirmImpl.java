package com.guisedoc.database.implement.firm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Firm;

public class FirmImpl extends JdbcTemplate{
	
	public FirmImpl(DataSource ds){
		super(ds);
	}
	
	public Object getFirmData(){
		try{
			Firm firm = new Firm();

			String query = "SELECT * FROM firm";
			Map<String,Object> map = queryForMap(query);
			
			firm.setAddress((String) map.get("address"));
			firm.setBank((String) map.get("bank"));
			firm.setBankAccountNR((String) map.get("bankAccountNR"));
			firm.setEmail((String) map.get("email"));
			firm.setFax((String) map.get("fax"));
			firm.setKmkr((String) map.get("kmkr"));
			firm.setName((String) map.get("name"));
			firm.setPhone((String) map.get("phone"));
			firm.setRegNR((String) map.get("regNR"));
			
			firm.setLogoURL((String) map.get("logoURL"));
			firm.setLogoWidth((Integer) map.get("logoWidth"));
			firm.setLogoHeight((Integer) map.get("logoHeight"));
			
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
					+ "kmkr = ?, phone = ?, email = ?, bank = ?, bankAccountNR = ?,"
					+ "fax = ?, logoURL = ?, logoWidth = ?, logoHeight = ?";
			
			Object[] objects = new Object[]{
					firm.getName(),
					firm.getAddress(),
					firm.getRegNR(),
					firm.getKmkr(),
					firm.getPhone(),
					firm.getEmail(),
					firm.getBank(),
					firm.getBankAccountNR(),
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
					Types.VARCHAR
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
