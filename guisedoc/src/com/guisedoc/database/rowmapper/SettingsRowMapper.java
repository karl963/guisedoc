package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Settings;

@SuppressWarnings("hiding")
public class SettingsRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			Settings settings = new Settings();

			ResultSetMetaData metaData = rs.getMetaData();
			
			for(int i = 2; i <= metaData.getColumnCount(); i++){ // skip 1 (ID)
				settings.updateValue(metaData.getColumnName(i), rs.getBoolean(i));
			}
	
			return (Object)settings;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
