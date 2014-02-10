package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.ClientDocument;

@SuppressWarnings("hiding")
public class ClientDocumentRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			ClientDocument clientDocument = new ClientDocument();

			clientDocument.setDate(rs.getTimestamp("documentDate"));
			clientDocument.setTotalSum(rs.getDouble("totalSum"));
			clientDocument.setFullNumber(rs.getString("fullNumber"));
			
			return (Object) clientDocument;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
