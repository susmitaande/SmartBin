package com.smartbin.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.smartbin.dblayer.MongoDBService;

public class ServiceMgmt {

	static MongoDBService dbService;
	
	public ServiceMgmt() {
		
		/* Read properties from serviceconfig.properties file */
		Properties prop = new Properties();
		loadConfigFile(prop);
		
		try {
			createMongoDBService(prop.getProperty("DB_SERVER"), Integer.parseInt(prop.getProperty("DB_PORT")), prop.getProperty("DB_NAME"));
			dbService.createCollection(prop.getProperty("COLLECTION_BIN"));
			dbService.createCollection(prop.getProperty("COLLECTION_BIN_HISTORY"));
		} catch (NumberFormatException nfe)
		{
			System.out.println("Exception thrown while parsing the value for key DB_PORT\n" + nfe.getMessage());
		}
	}
	/**
	 * Initialize the db layer service
	 */
	
	private static void createMongoDBService(String hostname, int portNum, String dbName)
	{
		dbService = new MongoDBService();
		dbService.connect(hostname, portNum, dbName, null, null);
	}
	
	private void loadConfigFile(Properties prop)
	{
		String propFile = "serviceconfig.properties";
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFile);
		try{
			if(inputStream != null)
				prop.load(inputStream);
			else {
				System.out.println("Property file " + propFile + "not found");
			}
		} catch(IOException ioe) {
			System.out.println("Could not load the properties file " + propFile + "\n" + ioe.getMessage());
		}
		finally {
			try {
				if(inputStream != null)
					inputStream.close();
			} catch (IOException ioe)
			{
				System.out.println("Exception while closing the input stream for " + prop + "\n" + ioe.getMessage());
			}
		}
	}
	
	public static MongoDBService getDbService(String hostname, int portNum, String dbName) {
	
		if(dbService == null)
			createMongoDBService(hostname, portNum, dbName);
		return dbService;
	}
	
	public static MongoDBService getDbService() {
		return dbService;
	}
	
	
	
}
