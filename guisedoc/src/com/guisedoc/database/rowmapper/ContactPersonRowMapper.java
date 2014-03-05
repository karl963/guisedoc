package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.ContactPerson;

@SuppressWarnings("hiding")
public class ContactPersonRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			ContactPerson contactPerson = new ContactPerson();

			contactPerson.setID(rs.getLong("ID"));
			contactPerson.setName(rs.getString("name"));
			
			return (Object) contactPerson;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}
