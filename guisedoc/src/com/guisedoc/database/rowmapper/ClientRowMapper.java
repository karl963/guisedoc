package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Client;
import com.guisedoc.object.ClientDocument;
import com.guisedoc.object.Product;

@SuppressWarnings("hiding")
public class ClientRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			Client client = new Client();

			client.setID(rs.getLong("ID"));
			client.setName(rs.getString("name"));
			client.setPhone(rs.getString("phone"));
			client.setAddress(rs.getString("address"));
			client.setEmail(rs.getString("email"));
			client.setAdditionalAddress(rs.getString("additionalAddress"));
			
			// additional data, if we had them added to the query
			try{
				client.setTotalBoughtFor(rs.getDouble("totalBoughtFor"));
			}catch(Exception x){}
			try{
				client.setLastDealDate(rs.getTimestamp("lastDealDate"));
			}catch(Exception x){
				client.setLastDealDate(null);
			}
			try{
				client.setTotalDeals(rs.getLong("totalDeals"));
			}catch(Exception x){}
			try{
				client.setLastDealNR(rs.getString("fullNumber"));
			}catch(Exception x){}
			
			return (Object) client;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
