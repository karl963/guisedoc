package com.guisedoc.database.implement.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.guisedoc.database.Connector;
import com.guisedoc.database.rowmapper.ProfileRowMapper;
import com.guisedoc.database.rowmapper.SettingsRowMapper;
import com.guisedoc.database.rowmapper.UserRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Profile;
import com.guisedoc.object.Settings;
import com.guisedoc.object.User;

public class ProfileImpl extends JdbcTemplate{
	
	public ProfileImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public Object getAllProfiles(){
		try{
			String query = "SELECT * FROM profiles";
			List<Object> result = query(query, new ProfileRowMapper<Object>());
			
			// if there was a profile or error
			if(result.size() > 0){
				if(result.get(0) instanceof Profile){
					
					// check the user count for each profile
					int i = 0;
					for(Object obj : result){
						Profile profile = (Profile)obj;
						
						String profileQuery = "SELECT COUNT(*) FROM users "
								+ "WHERE profile_ID = "+profile.getID();
						
						Object usersCount = queryForObject(profileQuery,Long.class);
						
						if(usersCount instanceof Long){
							((Profile)result.get(i)).setUsersCount((Long)usersCount);
						}
						else{
							return ErrorType.DATABASE_QUERY;
						}
						
						i++;
					}
					
					return (List<Object>)result;
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			else{
				return ErrorType.NONE_FOUND;
			}

		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getProfileByID(long id){
		try{
			String query = "SELECT * FROM profiles WHERE ID="+id;
			List<Object> result = query(query, new ProfileRowMapper<Object>());
			
			// if there was a profile or error
			if(result.size() > 0){
				if(result.get(0) instanceof Profile){
					return (Profile)result.get(0);
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			else{
				return ErrorType.NONE_FOUND;
			}

		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object addNewProfile(final String name){
		try{
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO profiles "
							+ "(name) VALUES (?)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, name);
					
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
	
	public ErrorType deleteProfile(long id){
		try{
			String query = "DELETE FROM profiles WHERE ID = "+id;

			int result = update(query);
			
			if(result == 0){
				return ErrorType.NOTHING_TO_DELETE;
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
	
	public ErrorType changeProfileRule(long profileID, String key, int value){
		try{
			String query = "UPDATE profiles SET "+key+" = "+value+" WHERE ID = "+profileID;

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
	
	public ErrorType changeProfileName(long profileID, String name){
		try{
			String query = "UPDATE profiles SET name = '"+name+"' WHERE ID = "+profileID;

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
