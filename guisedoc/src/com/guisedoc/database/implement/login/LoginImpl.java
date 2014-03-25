package com.guisedoc.database.implement.login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.guisedoc.database.Connector;
import com.guisedoc.database.implement.settings.MySettingsImpl;
import com.guisedoc.database.implement.user.UserImpl;
import com.guisedoc.enums.ErrorType;
import com.guisedoc.messages.ErrorMessages;
import com.guisedoc.object.User;

public class LoginImpl extends JdbcTemplate{
	
	public LoginImpl(HttpSession session){
		super(((Connector)session.getAttribute("connector")).getDatasource());
	}
	
	public LoginImpl(Connector connector){
		super(connector.getDatasource());
	}

	public Object checkUserValid(String username, String password, boolean autoLogin){
		try{
			
			String query = "SELECT dbSchema FROM users WHERE userName='"+username+"'";
			if(!autoLogin){
				query+= " AND password=PASSWORD('"+password+"')";
			}
			
			try{
				String response = queryForObject(query,String.class);
				
				if(response != null){
					return response;
				}
				else{
					return ErrorType.INVALID_LOGIN;
				}
			}
			catch(EmptyResultDataAccessException e){ // there was no such user
				return ErrorType.INVALID_LOGIN;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public ErrorType checkForAutologinAndLogin(String secret,HttpSession session){
		try{
			if(secret != null && !secret.equals("")){
				String query = "SELECT username, password FROM "
						+ " users WHERE secret='"+secret+"' LIMIT 1";
	
				try{
					Map<String, Object> response = queryForMap(query);
					
					if(response != null && response.get("username") != null){
						
						String[] loginResponse = tryLogin((String)response.get("username")
								,"",session,true);
						
						if(loginResponse[0].equals("success")){
							return ErrorType.SUCCESS;
						}
						else{
							return ErrorType.FAILURE;
						}
	
					}
					else{
						return ErrorType.NONE_FOUND;
					}
				}
				catch(IncorrectResultSizeDataAccessException e){
					return ErrorType.NONE_FOUND;
				}
			}
			else{
				return ErrorType.FAILURE;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}
	
	public String[] tryLogin(String username, String password, HttpSession session, boolean autoLogin){
		String message = null;
		String response = null;

		// get the schema in response
		Object validationResponse = checkUserValid(username, password, autoLogin);
		if(validationResponse instanceof String){
			
			Connector connector = new Connector((String)validationResponse);

			Object dataResponse = new UserImpl(connector)
					.getUser(username,true,true,true,false);
			if(dataResponse instanceof User){
				
				session.setAttribute("user", (User)dataResponse);
				session.setAttribute("connector", connector);
				session.setAttribute("schema", (String)validationResponse);

				response = "success";
				if(session.getAttribute("requestedPage") == null){
					message = "documents";
				}
				else{
					message = (String)session.getAttribute("requestedPage");
				}
			}
			else{
				response = "failure";
				message = ErrorMessages.getMessage((ErrorType)dataResponse);
			}
		}
		else{
			response = "failure";
			message = ErrorMessages.getMessage((ErrorType)validationResponse);
		}
		
		return new String[]{response,message};
	}

	public void removeAutologin(User user,HttpSession session){
		try{
			ErrorType response = new MySettingsImpl(session).changeOneSetting(user.getID(), "autoLogin", 0);
			
			if(response == ErrorType.SUCCESS){
				new LoginImpl(new Connector("main")).removeWhine(user);
			}
		}
		catch(Exception x){
			x.printStackTrace();
		}
	}

	public void removeWhine(User user){
		try{
			setSecret(user,"");
		}
		catch(Exception x){
			x.printStackTrace();
		}
	}
	
	public ErrorType setSecret(User user, String secret){
		try{
			String query = "UPDATE users SET "
					+ "secret = '"+secret+"' WHERE username = '"+user.getUserName()+"'";

			int response = update(query);
			
			if(response == 1){
				return ErrorType.SUCCESS;
			}
			else{
				return ErrorType.NOTHING_TO_UPDATE;
			}
		}
		catch(Exception x){
			x.printStackTrace();
			return ErrorType.DATABASE_QUERY;
		}
	}

}
