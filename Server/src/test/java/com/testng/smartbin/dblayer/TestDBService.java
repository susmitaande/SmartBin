package com.testng.smartbin.dblayer;
import org.bson.Document;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.mongodb.client.MongoCollection;
import com.smartbin.dblayer.MongoDBService;

//import com.smartbin.dblayer.MongoDBService;

public class TestDBService {

	MongoDBService dbservice = null;
	@BeforeTest
	public void SetUp()
	{
		dbservice = new MongoDBService();
		dbservice.connect("vwwsu01-w7", 27017, "SmartBin", "", "");
	}

	@AfterTest
	public void tearDown()
	{
		dbservice = null;
	}
	
	@Test(priority=1)
	public void TestCreate()
	{
		System.out.println("TestCreate -> ENTER");
		assert(dbservice.createCollection("Bin"));
		assert(dbservice.createCollection("BinStore"));
		System.out.println("TestCreate -> EXIT");
	}

	@Test (dependsOnMethods={"TestCreate"})
	public void TestFetch()
	{
		System.out.println("TestFetch -> ENTER");
		MongoCollection<Document> coll = dbservice.fetchCollection("Bin");
		if(coll != null)
		{
			assert true;
		}
		else
			assert false;
		System.out.println("TestFetch -> EXIT");
	}

	@Test (dependsOnMethods={"TestFetch"})
	public void TestDelete()
	{
		System.out.println("TestDelete -> ENTER");
		dbservice.deleteCollection("Bin");
		System.out.println("TestDelete -> After delete collection");
		if(dbservice.fetchCollection("Bin") != null)
			assert false;
		else
			assert true;
		System.out.println("TestDelete -> EXIT");

	}
}
