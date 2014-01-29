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

			product.setID(rs.getLong("ID"));
			product.setCode(rs.getString("code"));
			product.setName(rs.getString("name"));
			product.setE_name(rs.getString("e_name"));
			product.setUnit(rs.getString("unit"));
			product.setE_unit(rs.getString("e_unit"));
			product.setPrice(rs.getDouble("price"));
			product.setO_price(rs.getDouble("o_price"));
			product.setStorage(rs.getDouble("storage"));
			
			return (Object) product;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
