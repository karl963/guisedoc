package com.guisedoc.database.implement.user;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.guisedoc.database.rowmapper.ProfileRowMapper;
import com.guisedoc.database.rowmapper.SettingsRowMapper;
import com.guisedoc.database.rowmapper.UserRowMapper;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Firm;
import com.guisedoc.object.Profile;
import com.guisedoc.object.Settings;
import com.guisedoc.object.User;

public class UserImpl extends JdbcTemplate{

	public UserImpl(DataSource ds){
		super(ds);
	}

	public Object getUser(String username,boolean userData,
			boolean profileData,boolean settingsData, boolean additionalData){

		try{
			User user = new User();
			
			/*
			 * user data
			 */
			if(userData){
				String query = "SELECT * FROM users WHERE userName='"+username+"'";
				List<Object> result = query(query, new UserRowMapper<Object>());
				
				if(result.get(0) instanceof User){
					user = (User)result.get(0);
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			
			user.setUserName(username);
			
			/*
			 * user profile
			 */
			if(profileData){
				String query = "SELECT * FROM profiles WHERE ID="+user.getProfileID();
				List<Object> result = query(query, new ProfileRowMapper<Object>());
				
				if(result.get(0) instanceof Profile){
					user.setProfile((Profile)result.get(0));
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			
			/*
			 * user personal settings
			 */
			if(settingsData){
				String query = "SELECT * FROM settings WHERE ID="+user.getSettingsID();
				List<Object> result = query(query, new SettingsRowMapper<Object>());
				
				if(result.get(0) instanceof Settings){
					user.setSettings((Settings)result.get(0));
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			/*
			 * additionalData about the user
			 */
			if(additionalData){
				String query = "SELECT lastOnline, COUNT(documents.ID) AS totalDeals FROM users WHERE "
						+ "userName='"+username+"' AND documents.user_ID = user.ID";

				Map<String,Object> map = queryForMap(query);

				user.setLastOnline((Timestamp)map.get("lastOnline"));
				user.setTotalDeals((Long)map.get("totalDeals"));
			}
			
			return user;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

	public ErrorType saveUserData(User user,boolean userData,
			boolean profileData,boolean settingsData) {
		
		try{
			/*
			 * user data
			 */
			if(userData){
				String query = "UPDATE users SET name = ?, email = ?, phone = ?,"
						+ "skype = ? WHERE ID = ?";
				
				Object[] objects = new Object[]{
						user.getName(),
						user.getEmail(),
						user.getPhone(),
						user.getSkype(),
						user.getID()
				};
				int[] types = new int[]{
						Types.VARCHAR,
						Types.VARCHAR,
						Types.VARCHAR,
						Types.VARCHAR,
						Types.BIGINT
				};
				
				update(query,objects,types);
			}
			
			/*
			 * user profile
			 */
			if(profileData){
				String query = "UPDATE users SET profile_ID = ? WHERE ID = ?";
				
				Object[] objects = new Object[]{
						user.getProfileID(),
						user.getID()
				};
				int[] types = new int[]{
						Types.BIGINT,
						Types.BIGINT
				};
				
				update(query,objects,types);
			}
			
			/*
			 * user personal settings
			 */
			if(settingsData){
				String query = "UPDATE users SET settings_ID = ? WHERE ID = ?";
				
				Object[] objects = new Object[]{
						user.getSettingsID(),
						user.getID()
				};
				int[] types = new int[]{
						Types.BIGINT,
						Types.BIGINT
				};
				
				update(query,objects,types);
			}
			
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType saveUserPassword(User user){
		try{
			String query = "UPDATE users SET password = ? WHERE userName = ?";
			
			Object[] objects = new Object[]{
					user.getPassword(),
					user.getUserName()
			};
			int[] types = new int[]{
					Types.VARCHAR,
					Types.VARCHAR
			};
			
			update(query,objects,types);
			
			return ErrorType.SUCCESS;
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

}
