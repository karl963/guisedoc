package com.guisedoc.database.implement.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.guisedoc.object.Firm;
import com.guisedoc.object.Profile;
import com.guisedoc.object.Settings;
import com.guisedoc.object.User;

public class UserImpl extends JdbcTemplate{

	public UserImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	public UserImpl(Connector connector){
		super(connector.getDatasource());
	}
	
	public Object getAllUsers(){
		try{
			String query = "SELECT users.ID AS uID, "
					+ "IFNULL("
					+ "(SELECT profiles.name FROM profiles WHERE profiles.id = profile_ID)"
					+ ",'') AS profileName, "
					+ " users.*, "
					+ "lastOnline, IFNULL((SELECT COUNT(*) FROM documents WHERE "
					+ "documents.user_ID = uID),0) AS totalDeals "
					+ "FROM users";
			
			List<Object> result = query(query, new UserRowMapper<Object>());

			if(result.size() > 0){
				if(result.get(0) instanceof User){
					return (List<Object>)result;
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			else{
				return new ArrayList<User>();
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public Object getUserAndAllProfiles(long ID){
		try{
			User user;
			List<Object> profiles = null;
			
			String query = "SELECT IFNULL("
					+ "(SELECT profiles.name FROM profiles WHERE profiles.id = profile_ID)"
					+ ",'') AS profileName, "
					+ " users.* FROM users "
					+ "WHERE users.ID = "+ID;
			
			List<Object> userResult = query(query, new UserRowMapper<Object>());
			
			if(userResult.size() > 0){
				if(userResult.get(0) instanceof User){
					user = (User)userResult.get(0);
				}
				else{
					return (ErrorType)userResult.get(0);
				}
			}
			else{
				return ErrorType.NONE_FOUND;
			}
			
			String profileQuery = "SELECT * FROM profiles";
			
			List<Object> profilesResult = query(profileQuery, new ProfileRowMapper<Object>());
			if(profilesResult.size() > 0){
				if(profilesResult.get(0) instanceof Profile){
					profiles = (List<Object>)profilesResult;
				}
				else{
					return (ErrorType)profilesResult.get(0);
				}
			}
			else{
				profiles = new ArrayList<Object>();
			}
			
			return new Object[]{user,profiles};
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
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
				
				// if there was a profile or error
				if(result.size() > 0){
					if(result.get(0) instanceof Profile){
						user.setProfile((Profile)result.get(0));
					}
					else{
						return (ErrorType)result.get(0);
					}
				}
			}
			
			/*
			 * user personal settings
			 */
			if(settingsData){
				String query = "SELECT * FROM settings WHERE ID="+user.getSettingsID();
				List<Object> result = query(query, new SettingsRowMapper<Object>());
				
				// if there was a setting or error
				if(result.size() > 0){
					if(result.get(0) instanceof Settings){
						user.setSettings((Settings)result.get(0));
					}
					else{
						return (ErrorType)result.get(0);
					}
				}
			}
			/*
			 * additionalData about the user
			 */
			if(additionalData){
				String query = "SELECT lastOnline, COUNT(documents.ID) AS totalDeals FROM users, documents WHERE "
						+ "userName='"+username+"' AND documents.user_ID = users.ID";

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
	
	public Object getUser(Long id,boolean userData,
			boolean profileData,boolean settingsData, boolean additionalData){

		try{
			User user = new User();
			
			/*
			 * user data
			 */
			if(userData){
				String query = "SELECT * FROM users WHERE ID="+id;
				List<Object> result = query(query, new UserRowMapper<Object>());

				if(result.get(0) instanceof User){
					user = (User)result.get(0);
				}
				else{
					return (ErrorType)result.get(0);
				}
			}
			
			/*
			 * user profile
			 */
			if(profileData){
				String query = "SELECT * FROM profiles WHERE ID="+user.getProfileID();
				List<Object> result = query(query, new ProfileRowMapper<Object>());
				
				// if there was a profile or error
				if(result.size() > 0){
					if(result.get(0) instanceof Profile){
						user.setProfile((Profile)result.get(0));
					}
					else{
						return (ErrorType)result.get(0);
					}
				}
			}
			
			/*
			 * user personal settings
			 */
			if(settingsData){
				String query = "SELECT * FROM settings WHERE ID="+user.getSettingsID();
				List<Object> result = query(query, new SettingsRowMapper<Object>());
				
				// if there was a setting or error
				if(result.size() > 0){
					if(result.get(0) instanceof Settings){
						user.setSettings((Settings)result.get(0));
					}
					else{
						return (ErrorType)result.get(0);
					}
				}
			}
			/*
			 * additionalData about the user
			 */
			if(additionalData){
				String query = "SELECT lastOnline, COUNT(documents.ID) AS totalDeals FROM users, documents WHERE "
						+ "users.ID="+id+" AND documents.user_ID = users.ID";

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
	
	public ErrorType changeUserProfile(long userID, long profileID){
		try{
			String query = "UPDATE users SET profile_ID = ? WHERE ID = ?";
			
			Object[] objects = new Object[]{
					profileID,
					userID
			};
			int[] types = new int[]{
					Types.BIGINT,
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
	
	public ErrorType addNewUserWithPassword(User user,String schema){
		try{
			String query = "INSERT INTO users ("
					+ "userName, password, dbSchema)"
					+ " VALUES ('"+user.getUserName()+"',"
					+ "'"+user.getPassword()+"',"
					+ "'"+schema+"')";
			
			int response = update(query);
					
			if(response == 0){
				return ErrorType.DATABASE_QUERY;
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
	
	public Object addNewUser(final User user){
		try{
			// insert and get the generated (ID) key back
			KeyHolder keyHolder = new GeneratedKeyHolder();
			update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					
					String query = "INSERT INTO users ("
							+ "userName, name, phone, email, skype, lastOnline) "
							+ "VALUES (?,?,?,?,?,NULL)";
					
					PreparedStatement stmt = connection.prepareStatement(query, new String[]{"ID"});

					stmt.setString(1, user.getUserName());
					stmt.setString(2, user.getName());
					stmt.setString(3, user.getPhone());
					stmt.setString(4, user.getEmail());
					stmt.setString(5, user.getSkype());

					return stmt;
				}
			}, keyHolder);

			long ID = keyHolder.getKey().longValue();
			
			// add a profile for the user
			String settingsQuery = "INERT INTO settings (user_ID) VALUES("+ID+")";
			update(settingsQuery);
			
			Object newUser = getUser(ID,true,true,false,true);
			
			if(newUser instanceof User){
				return (User)newUser;
			}
			else{
				return (ErrorType)newUser;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

	public ErrorType checkExistingUserAndAdd(User user,String schema){
		try{
			String query = "SELECT count(*) FROM users WHERE userName = '"+user.getUserName()+"'";

			Long object = queryForObject(query,Long.class);
			
			// the user didn't exist
			if(object == 0L){
				
				// add the user and return response
				return addNewUserWithPassword(user,schema);
			}
			else{
				return ErrorType.USER_ALREADY_EXISTS;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType deleteUser(long id){
		try{
			String query = "DELETE FROM users WHERE ID = "+id;

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
	
	public ErrorType deleteUser(String userName){
		try{
			String query = "DELETE FROM users WHERE userName = '"+userName+"'";

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
}
