package com.guisedoc.database;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class Connector {
	
	DataSource datasource = null;
	Connection connection = null;
	String schema;
	
	public Connector(String schema){
		this.schema = schema;
	}
	
	/*
	 * makes the datasource
	 */
	private void makeDatasource(){
        try {
        	Context initContext = new InitialContext();
        	Context envContext  = (Context)initContext.lookup("java:/comp/env");
        	datasource = (DataSource)envContext.lookup("jdbc/" + schema);
        } catch (Exception x) {
            x.printStackTrace();
        }
	}
	
	/*
	 * returns connection 
	 */
	public Connection getConnection(){
		
		// if the connection isn't null yet
		if(connection != null){
			return connection;
		}
		
		// remake the datasource, if it's null
		if(datasource == null){
			makeDatasource();
		}
		
		try{
			connection = datasource.getConnection();
		}
		catch(Exception x){
			x.printStackTrace();
		}
		
		return connection;
	}
	
	/*
	 * return the datasource
	 */
	public DataSource getDatasource(){
		System.out.println("-----");
		System.out.println("datasrc is: "+datasource);
		// remake the datasource, if it's null
		if(datasource == null){
			System.out.println("making new");
			makeDatasource();
		}
		System.out.println("datasrc is: "+datasource);
		return datasource;
	}
}
