package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Firm;
import com.guisedoc.object.User;

@SuppressWarnings("hiding")
public class FirmRowMapper<Object> implements RowMapper<Object>{

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			Firm firm = new Firm();
			
			firm.setAddress(rs.getString("address"));
			firm.setBank(rs.getString("bank"));
			firm.setIban(rs.getString("iban"));
			firm.setSwift(rs.getString("swift"));
			firm.setEmail(rs.getString("email"));
			firm.setFax(rs.getString("fax"));
			firm.setKmkr(rs.getString("kmkr"));
			firm.setName(rs.getString("name"));
			firm.setPhone(rs.getString("phone"));
			firm.setRegNR(rs.getString("regNR"));
			
			firm.setLogoURL(rs.getString("logoURL"));
			firm.setLogoWidth(rs.getInt("logoWidth"));
			firm.setLogoHeight(rs.getInt("logoHeight"));
			
			return (Object) firm;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
