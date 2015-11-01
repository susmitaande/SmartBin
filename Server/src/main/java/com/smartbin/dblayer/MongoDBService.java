package com.smartbin.dblayer;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoDBService {

	MongoDatabase db = null;

	MongoClient mongoClient = null;
	public static int COLLECTION = 1;
	public static int DOCUMENT = 2;
	private String mongoURI = null;
	public void connect(String hostname, int portNum, String dbName, String userName,
			String password) {

		try {
			//Connect to the mongodb server
			mongoClient = new MongoClient( hostname , portNum );
			
			//Connect to the mongodb database
			db = mongoClient.getDatabase(dbName);
			setMongoURI(hostname, portNum, dbName);
			//Authenticate - TODO
		} catch (Exception e)
		{
			System.out.println("Could not connect to the Database\n" + e.getMessage());
		}

	}

	public void disconnect()
	{
		try {
			mongoClient.close();
			db.drop();
		} catch (Exception e)
		{
			System.out.println("There was some exception while disconnectiong from the database\n" + e.getMessage());
		}
	}


	/**
	 * Each collection will have a unique name.
	 * 
	 */
	public boolean createCollection(String name)  {
		//System.out.println("Inside create");
		try {
			MongoIterable<String> itr = db.listCollectionNames();
			System.out.println(itr.first());
			MongoCursor<String> cursor = itr.iterator();
			while(cursor.hasNext())
			{
				String cur = cursor.next();
				//System.out.println("cursor is pointing at " + cur);
				if(cur.equals(name))
					return true;
				
			}
			db.createCollection(name);
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
			
		}
	}


	public MongoCollection<Document> fetchCollection(String name)  {
		try
		{
			//System.out.println("Inside fetch");
			MongoIterable<String> itr = db.listCollectionNames();
			MongoCursor<String> cursor = itr.iterator();
			while(cursor.hasNext())
			{
				String cur = cursor.next();
				if(cur.equals(name))
					return db.getCollection(name);			
			}
			return null;
		} catch (Exception e)
		{
			System.out.println("Could not fetch a Collection object from Mongo database\n" + e.getMessage());
			return null;
		}
	}


	public void deleteCollection(String name) {

		try {
			MongoCollection<Document> coll = db.getCollection(name);
			//System.out.println("In delete" + coll.toString());
			coll.drop();
		} catch (Exception e)
		{
			System.out.println("Could not delete the Mongo database\n" + e.getMessage());
		}
	}


	public MongoDatabase getDb() {
		return db;
	}

	public void setDb(MongoDatabase db) {
		this.db = db;
	}

	public void insert(String collName, Document doc, String uniqueKey)
	{
		//System.out.println("=====================Inside INSERT method=====================");
		MongoCollection<Document> coll = fetchCollection(collName);  
		if(coll != null && doc !=null)
		{
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{				
				Document cur = cursor.next();
				cur.toString();
				if(cur.containsKey(uniqueKey))
				{
					if(cur.get(uniqueKey).equals(doc.get(uniqueKey)))
						return;		
				}
			}
			coll.insertOne(doc);
			
		}
	}
	
	public void insertOrUpdate(String collName, Document doc, String uniqueKey)
	{
		//System.out.println("=====================Inside INSERTOrUpdate method=====================");
		MongoCollection<Document> coll = fetchCollection(collName); 
		if(coll != null && doc !=null)
		{
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			//System.out.println("fetched iterator");
			while(cursor.hasNext())
			{				
				Document cur = cursor.next();
				//System.out.println("fetching cursor.next()");
				cur.toString();
				if(cur.containsKey(uniqueKey))
				{
					//System.out.println("binId key exists");
					if(cur.get(uniqueKey).equals(doc.get(uniqueKey)))
					{
						coll.findOneAndReplace(cur, doc);	
						return;
					}
				}
			}
			coll.insertOne(doc);
		}
	}
	public void delete(String collName, String uniqueKey, int value)
	{
		MongoCollection<Document> coll = fetchCollection(collName); 
		if(coll != null && uniqueKey !=null)
		{
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{				
				Document cur = cursor.next();
				//System.out.println("fetching cursor.next()");
				cur.toString();
				if(cur.containsKey(uniqueKey))
				{
					//System.out.println("binId key exists");
					if(cur.get(uniqueKey).equals(value))
						coll.deleteOne(cur);	
				}
			}

		}
	}

	public void update(String collName, Document doc, String uniqueKey)
	{
		MongoCollection<Document> coll = fetchCollection(collName); 
		if(coll != null && doc !=null && uniqueKey != null)
		{
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{				
				Document cur = cursor.next();
				cur.toString();
				if(cur.containsKey(uniqueKey))
				{
					if(cur.get(uniqueKey).equals(doc.get(uniqueKey)))
						coll.findOneAndReplace(cur, doc);
				}
			}

		}
	}
	
	public Document fetch(String collName, String uniqueKey, int value)
	{
		MongoCollection<Document> coll = fetchCollection(collName); 
		if(coll != null && uniqueKey !=null)
		{
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{				
				Document cur = cursor.next();
				cur.toString();
				if(cur.containsKey(uniqueKey))
				{
					if(cur.get(uniqueKey).equals(value))
						return cur;	
				}
			}

		}
		return null;
	}
	
	private void setMongoURI(String hostname, int portNum, String dbName)
	{
		mongoURI = "mongodb://" + hostname + ":" + portNum + "/" + dbName;
	}
	
	public String getMongURI()
	{
		return mongoURI;
	}
}
