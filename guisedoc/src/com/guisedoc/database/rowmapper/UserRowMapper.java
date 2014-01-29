package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.User;

@SuppressWarnings("hiding")
public class UserRowMapper<Object> implements RowMapper<Object>{

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			User user = new User();
			
			user.setID(rs.getLong("ID"));
			user.setUserName(rs.getString("userName"));
			user.setEmail(rs.getString("email"));
			user.setName(rs.getString("name"));
			user.setPhone(rs.getString("phone"));
			user.setSkype(rs.getString("skype"));
			user.setID(rs.getLong("ID"));
			
			user.setSettingsID(rs.getLong("settings_ID"));
			user.setProfileID(rs.getLong("profile_ID"));
			
			return (Object) user;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
