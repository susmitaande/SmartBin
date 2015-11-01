package com.testng.smartbin.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.smartbin.data.Bin;
import com.smartbin.data.BinHandler;
import com.smartbin.dblayer.MongoDBService;
import com.smartbin.service.ServiceMgmt;

public class TestBinHandler {
	
	MongoDBService dbservice = null;
	BinHandler binHandler = null;
	Bin bin = null;
	ServiceMgmt ser = null;
	@BeforeTest
	public void SetUp()
	{
		System.out.println("in Setup for TestBinHandler");
		ser = new ServiceMgmt();
		dbservice = ser.getDbService();
		binHandler =  new BinHandler();	
	}
	
	@AfterTest
	public void tearDown()
	{
		ser = null;
		dbservice = null;
		binHandler = null;
	}

	@Test
	public void TestCreateBin()
	{
		System.out.println("TestCreateBin -> ENTER");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("binId", 1);
		map.put("fillLevel", Bin.EMPTY);
		map.put("latitude", 1.1f);
		map.put("longitude", 1.2f);
		map.put("temperature", 24);
		map.put("humidity", 24);
		bin = binHandler.createBin(map);
		if(bin != null)
			assert true;
		else
			assert false;
		System.out.println("TestCreateBin -> EXIT");
	}
	
	@Test (dependsOnMethods={"TestCreateBin"})
	public void TestInsertBin()
	{
		assert(binHandler.insertBin("BinStore", bin));	
		
	}
	
	@Test (dependsOnMethods={"TestInsertBin"})
	public void TestUpdateBin()
	{
		bin.setFillLevel(Bin.FULL);
		binHandler.updateBin("BinStore", bin);	
		Bin bin1 = binHandler.fetchBin("BinStore", bin.getBinId());
		if(bin1.getFillLevel() == Bin.FULL)
			assert true;
		else
			assert false;
		
	}
	
	@Test (dependsOnMethods={"TestUpdateBin"})
	public void TestDeleteBin()
	{
		binHandler.deleteBin("BinStore", bin.getBinId());	
		Bin bin1 = binHandler.fetchBin("BinStore", bin.getBinId());
		if(bin1 == null)
			assert true;
		else
			assert false;
		
	}
	
	@Test (dependsOnMethods={"TestInsertBin"}, enabled=false)
	public void TestGetAllBins()
	{
		ArrayList<Bin> list = binHandler.getAllBins();
		System.out.println("Total no of bins ->" + list.size());
		if(list.size() != 0)
		{
			assert true;
		}
		else
			assert false;		
		
	}
	
	@Test (dependsOnMethods={"TestInsertBin"}, enabled=false)
	public void TestGetAllLocations()
	{
		ArrayList<String> list = binHandler.getAllLocations();
		System.out.println("Total no of bins ->" + list.size());
		if(list.size() != 0)
		{
			assert true;
		}
		else
			assert false;		
		
	}
	
	@Test (dependsOnMethods={"TestInsertBin"})
	public void TestGetLocalityBins()
	{
		/* test data: lat, long for Miyapur, raidus is 10 km */
		ArrayList<Bin> list = binHandler.getLocalityBins(17.4968, 78.3614, 40);
		System.out.println("Total no of Locality bins ->" + list.size());
		if(list.size() != 0)
		{
			assert true;
		}
		else
			assert false;		
		
	}
}
