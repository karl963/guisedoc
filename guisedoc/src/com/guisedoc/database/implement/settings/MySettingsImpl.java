package com.guisedoc.database.implement.settings;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.guisedoc.database.Connector;
import com.guisedoc.enums.ErrorType;

public class MySettingsImpl extends JdbcTemplate{
	
	public MySettingsImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public ErrorType changeOneSetting(long userID, String key, int value){
		try{
			String query = "UPDATE settings SET "+key+" = "+value+" WHERE ID = "+userID;

			int result = update(query);
			
			if(result == 0){
				return ErrorType.NOTHING_TO_UPDATE;
			}
			else{
				return ErrorType.SUCCESS;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
}