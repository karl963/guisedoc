package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Profile;
import com.guisedoc.object.StatisticsObject;

@SuppressWarnings("hiding")
public class StatObjectRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			
			StatisticsObject statObj = new StatisticsObject();

			try{statObj.setName(rs.getString("name"));
			}catch(Exception x){}
			try{statObj.setCode(rs.getString("code"));
			}catch(Exception x){}
			try{statObj.setUnit(rs.getString("unit"));
			}catch(Exception x){}
			try{statObj.setAmount(rs.getDouble("amount"));
			}catch(Exception x){}
			try{statObj.setTotalPrice(rs.getDouble("totalSum"));
			}catch(Exception x){}
			
			try{statObj.setDocument_ID(rs.getLong("document_ID"));
			}catch(Exception x){}
			try{statObj.setProduct_ID(rs.getLong("product_ID"));
			}catch(Exception x){}
			try{statObj.setID(rs.getLong("ID"));
			}catch(Exception x){}
			try{statObj.setClientName(rs.getString("clientName"));
			}catch(Exception x){}
			try{statObj.setDate(rs.getTimestamp("documentDate"));
			}catch(Exception x){}
			
			return (Object) statObj;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}