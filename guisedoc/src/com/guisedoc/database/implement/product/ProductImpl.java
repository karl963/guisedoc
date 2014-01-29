package com.guisedoc.database.implement.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.guisedoc.database.rowmapper.ProductRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Product;
import com.guisedoc.object.User;

public class ProductImpl extends JdbcTemplate {
	
	public ProductImpl(DataSource ds){
		super(ds);
	}
	
	public Object getAllProducts(){
		try{
			String query = "SELECT * FROM products";
			
			List<Object> products = query(query, new ProductRowMapper<Object>());
			
			if(products.size() > 0 && products.get(0) instanceof ErrorType){
				return ErrorType.DATABASE_QUERY;
			}
			
			return products;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getProductByID(long id){
		try{
			String query = "SELECT * FROM products WHERE ID="+id;

			List<Object> products = query(query, new ProductRowMapper<Object>());
			
			if(products.get(0) instanceof Product){
				return (Product)products.get(0);
			}
			else{
				return ErrorType.DATABASE_QUERY;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object addNewProduct(final Product product){
		try{
			
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO products ("
							+ "code, name, e_name, unit, e_unit,"
							+ " price, o_price, storage"
							+ ") VALUES (?,?,?,?,?,?,?,?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, product.getCode());
					stmt.setString(2, product.getName());
					stmt.setString(3, product.getE_name());
					stmt.setString(4, product.getUnit());
					stmt.setString(5, product.getE_unit());
					stmt.setDouble(6, product.getPrice());
					stmt.setDouble(7, product.getO_price());
					stmt.setDouble(8, product.getStorage());

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
	
	public ErrorType deleteProducts(List<Product> products){
		try{
			String query = "DELETE FROM products WHERE ID IN (";
			
			boolean wasBefore = false;
			for(Product p: products){
				if(wasBefore){
					query += ",";
				}
				
				query+= p.getID();
				
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
	
	public ErrorType saveProduct(Product product){
		try{
			String query = "UPDATE products SET code = ?, name = ?, e_name = ?,"
					+ "unit = ?, e_unit = ?, price = ?, o_price = ?, storage = ?"
					+ " WHERE ID=?";
			
			Object[] objects = new Object[]{
					product.getCode(),
					product.getName(),
					product.getE_name(),
					product.getUnit(),
					product.getE_unit(),
					product.getPrice(),
					product.getO_price(),
					product.getStorage(),
					product.getID(),

			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.VARCHAR,
					Types.DOUBLE,
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
	
	public Object searchForProducts(Product product){
		try{
			String query = "UPDATE products SET code = ?, name = ?, e_name = ?,"
					+ "unit = ?, e_unit = ?, price = ?, o_price = ?, storage = ?"
					+ " WHERE ID=?";
			
			// get the procuts
			List<Product> products = query(query,new ProductRowMapper<Product>());
			
			// get the total products
			long totalProducts = queryForObject("SELECT COUNT(ID) FROM products",Long.class);
			
			Object[] returnable = new Object[]{totalProducts,products};
			return returnable;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
}
