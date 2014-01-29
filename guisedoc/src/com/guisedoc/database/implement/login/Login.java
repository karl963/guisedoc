package com.guisedoc.database.implement.login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.guisedoc.database.Connector;
import com.guisedoc.enums.ErrorType;

public class Login {
	
	public static Connector mainConnector = new Connector("main");
	
	public static Object checkUserValid(String username, String password){
		
		Connection con = mainConnector.getConnection();
		
		if(con == null){
			return ErrorType.DATABASE_CONNECTION;
		}
		
		try{
			Statement stmt = con.createStatement();
			String query = "SELECT dbSchema FROM users WHERE userName='"+username+"' AND password='"+password+"'";
			ResultSet rs = stmt.executeQuery(query);
			
			if(rs.next()){
				return rs.getString("dbSchema");
			}
			else{
				return ErrorType.INVALID_LOGIN;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

}
