package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Profile;

@SuppressWarnings("hiding")
public class ProfileRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			Profile profile = new Profile();

			ResultSetMetaData metaData = rs.getMetaData();
			
			profile.setName(rs.getString("name"));
			profile.setID(rs.getLong("ID"));
			
			for(int i = 3; i <= metaData.getColumnCount(); i++){ // skip 1 and 2 (ID and name)
				profile.updateValue(metaData.getColumnName(i), rs.getBoolean(i));
			}

			return (Object) profile;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
