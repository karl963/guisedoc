package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Product;

@SuppressWarnings("hiding")
public class ProductRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			Product product = new Product();
			
			try{product.setID(rs.getLong("ID"));
			}catch(Exception x){}
			try{product.setCode(rs.getString("code"));
			}catch(Exception x){}
			try{product.setName(rs.getString("name"));
			}catch(Exception x){}
			try{product.setE_name(rs.getString("e_name"));
			}catch(Exception x){}
			try{product.setUnit(rs.getString("unit"));
			}catch(Exception x){}
			try{product.setE_unit(rs.getString("e_unit"));
			}catch(Exception x){}
			try{product.setPrice(rs.getDouble("price"));
			}catch(Exception x){}
			try{product.setO_price(rs.getDouble("o_price"));
			}catch(Exception x){}
			try{product.setStorage(rs.getDouble("storage"));
			}catch(Exception x){}
			
			try{product.setUnitID(rs.getLong("unitID"));
			}catch(Exception x){}
			try{product.setAmount(rs.getDouble("amount"));
			}catch(Exception x){}
			try{product.setDiscount(rs.getDouble("discount"));
			}catch(Exception x){}
			try{product.setTotalSum(rs.getDouble("totalSum"));
			}catch(Exception x){}
			try{product.setQueueNumber(rs.getLong("queueNumber"));
			}catch(Exception x){}
			try{product.setCalculateSum(rs.getBoolean("calculateSum"));
			}catch(Exception x){}
			
			try{product.setComments(rs.getString("comments"));
			}catch(Exception x){}
			try{product.setAdditional_Info(rs.getString("additional_info"));
			}catch(Exception x){}
			
			return (Object) product;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
