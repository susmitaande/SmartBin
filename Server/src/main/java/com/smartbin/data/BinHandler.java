package com.smartbin.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.smartbin.dblayer.MongoDBService;
import com.smartbin.service.ServiceMgmt;

public class BinHandler {

	MongoDBService dbS = ServiceMgmt.getDbService();
	static String COLLECTION_BIN="BinStore";
	static String COLLECTION_BIN_HISTORY="BinHistory";
	public Bin createBin(Map map)
	{
		Bin bin = new Bin();
		bin.setBinId((Integer)map.get("binId"));
		bin.setFillLevel((Integer)map.get("fillLevel"));
		bin.setLatitude(((Number) map.get("latitude")).floatValue());
		bin.setLongitude(((Number) map.get("longitude")).floatValue());
		bin.setTemperature((Integer)map.get("temperature"));
		bin.setHumidity((Integer)map.get("humidity"));
		if(map.get("city") == null && map.get("locality") == null && map.get("sublocality") == null)
			bin.convertLatLongToLocation(bin.getLatitude().toString()+","+bin.getLongitude().toString());
		else
		{
			bin.setCity((String)map.get("city"));
			System.out.println("Setting City as -> " + (String)map.get("city"));
			bin.setLocality((String)map.get("locality"));
			System.out.println("Setting locality as -> " + (String)map.get("locality"));
			bin.setSubLocality((String)map.get("sublocality"));
			System.out.println("Setting sublocality as -> " + (String)map.get("sublocality"));
		}
		
		return bin;

	}

	private Document convertDocument(Bin bin)
	{
		Document doc = null;
		if(bin != null)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("binId", bin.getBinId());
			map.put("fillLevel", bin.getFillLevel());
			map.put("latitude", bin.getLatitude());
			map.put("longitude", bin.getLongitude());
			map.put("temperature", bin.getTemperature());
			map.put("humidity", bin.getHumidity());
			map.put("city", bin.getCity());
			map.put("locality", bin.getLocality());
			map.put("sublocality", bin.getSublocality());
			doc = new Document(map);
		}
		return doc;
	}

	public boolean insertBin(String collName, Bin bin)
	{
		if(collName == null || bin == null)
			return false;
		dbS.insert(collName, convertDocument(bin), "binId");
		/* Insert into the history collection */
		String uniqueKey = getDateBinUniqueKey(bin);
		Document document = convertDateBinDocument(bin, uniqueKey);
		dbS.insert(COLLECTION_BIN_HISTORY, document, "binDate");
		return true;
	}
	
	private String getDateBinUniqueKey(Bin bin)
	{
		if(getCurrentDate() != -1)
		{
			String uniqueKey = getCurrentDate() + "_"+ bin.getBinId();
			//System.out.println("Unique Key is ->" + uniqueKey);
			return uniqueKey;
		} 
		return null;
	}

	/*
	 * This will return the current date excluding the current time.
	 */
	private int getCurrentDate()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(new Date());
		try {
			int todayInt =  Integer.parseInt(today);
			return todayInt;
		} catch (NumberFormatException nfe)
		{
			System.out.println("cannot parse the date ->" + today);
		}		
		return -1;
	}
		
	private Document convertDateBinDocument(Bin bin, String _uniqueKey)
	{
		Document doc = null;
		if(bin != null)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("binId", bin.getBinId());
			map.put("fillLevel", bin.getFillLevel());
			map.put("Date", getCurrentDate()); 
			map.put("binDate", _uniqueKey); 
			doc = new Document(map);
		}
		return doc;
	}
	
	public boolean insertOrUpdateBin(String collName, Bin bin)
	{
		if(collName == null || bin == null)
			return false;
		//System.out.println("bin being inserted or updated is:\n" + bin.toString());
		dbS.insertOrUpdate(collName, convertDocument(bin), "binId");
		/* Insert/Update into the history collection */
		String uniqueKey = getDateBinUniqueKey(bin);
		Document document = convertDateBinDocument(bin, uniqueKey);
		dbS.insertOrUpdate(COLLECTION_BIN_HISTORY, document, "binDate");
		return true;
	}
	
	public boolean deleteBin(String collName, int binId)
	{
		if(collName == null)
			return false;
		dbS.delete(collName, "binId", binId);
		return true;
	}

	public boolean updateBin(String collName, Bin bin)
	{
		if(collName == null || bin == null)
			return false;
		dbS.update(collName, convertDocument(bin), "binId");
		/* Update into the history collection */
		String uniqueKey = getDateBinUniqueKey(bin);
		Document document = convertDateBinDocument(bin, uniqueKey);
		dbS.insertOrUpdate(COLLECTION_BIN_HISTORY, document, "binDate");
		return true;
	}

	public Bin fetchBin(String collName, int binId)
	{
		if(collName == null)
			return null;
		Document doc = dbS.fetch(collName, "binId", binId);
		if(doc == null)
			return null;
		else
			return createBin(dbS.fetch(collName, "binId", binId));
	}
	
	public ArrayList<Bin> getAllBins()
	{
		ArrayList<Bin> resultList = new ArrayList<Bin>();
		MongoCollection<Document> coll = dbS.fetchCollection(COLLECTION_BIN);
		if(coll != null)
		{
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{
				Document cur = cursor.next();
				resultList.add(createBin(cur));
			}
		}

		return resultList;
	}
	
	public ArrayList<String> getAllLocations()
	{
		//System.out.println("Inside getAllLocations");
		ArrayList<String> resultList = new ArrayList<String>();
		MongoCollection<Document> coll = dbS.fetchCollection(COLLECTION_BIN);
		if(coll != null)
		{
			//System.out.println("Collection fetch successful->" + COLLECTION_BIN);
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{

				Document cur = cursor.next();
				//System.out.println("==========Printing the document===========\n" + cur.toString());
				resultList.add(cur.getString("sublocality")+","+cur.getString("locality") + "," +
						cur.getString("city") + "," + cur.getDouble("latitude") + "," + cur.getDouble("longitude"));
				//System.out.println("Bin added to resultList");
			}
		}

		return resultList;
	}
	
	public ArrayList<Bin> getLocalityBins(Double latitude, Double longitude, int radius)
	{
		//System.out.println("Inside getLocalityBins");
		ArrayList<Bin> resultList = new ArrayList<Bin>();
		MongoCollection<Document> coll = dbS.fetchCollection(COLLECTION_BIN);
		if(coll != null)
		{
			//System.out.println("Collection fetch successful->" + COLLECTION_BIN);
			FindIterable<Document> itr = coll.find();
			MongoCursor<Document> cursor = itr.iterator();
			while(cursor.hasNext())
			{

				Document cur = cursor.next();
				System.out.println("==========Printing the document===========\n" + cur.toString());
				Double distance = getHaversineDistance(latitude, longitude, cur.getDouble("latitude"), cur.getDouble("longitude"));
				//System.out.println("Distance is -> " + distance);
				if(distance <= radius )
					resultList.add(createBin(cur));
			}
		}

		return resultList;
	}
	
	/**
	 * This is the implementation of Haversine Distance Algorithm between two places
	 *  R = earth’s radius (mean radius = 6,371km)
	    lat = lat2 minus lat1
	    long = long2 minus long1
	    a = sin²(lat/2) + cos(lat1).cos(lat2).sin²(long/2)
	    c = 2.atan2(sqrt(a), sqrt(1 minus a))
	    d = R.c
	 *
	 */
	public Double getHaversineDistance(Double lat1, Double lon1, Double lat2, Double lon2)
	{
	  final int R = 6371; // Radius of the earth
      Double latDistance = toRad(lat2-lat1);
      Double lonDistance = toRad(lon2-lon1);
      Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                 Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
                 Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
      Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
      Double distance = R * c;
      //System.out.println("The distance between two lat and long is::" + distance);
      return distance;
	}
	
	
	private static Double toRad(Double value) {
		return value * Math.PI / 180;
	}
	
}
