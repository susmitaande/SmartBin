package com.smartbin.data;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Bin {

	public static int FULL=2;
	public static int HALF=1;
	public static int EMPTY=0;
	private static final String GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/json";
	int binId;
	Float longitude;
	Float latitude;
	int fillLevel;	
	int temperature;
	int humidity;
	String city = null;
	String locality = null;
	String sublocality = null;
	
	public String getCity() {
		return city;
	}

	public void setCity(String _city) {
		this.city = _city;
	}
	
	public String getLocality() {
		return locality;
	}

	public void setLocality(String _locality) {
		this.locality = _locality;
	}
	
	public String getSublocality() {
		return sublocality;
	}

	public void setSubLocality(String _sublocality) {
		this.sublocality = _sublocality;
	}
	public int getBinId() {
		return binId;
	}
	public void setBinId(int binId) {
		this.binId = binId;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public int getFillLevel() {
		return fillLevel;
	}
	public void setFillLevel(int fillLevel) {
		this.fillLevel = fillLevel;
	}
	public int getTemperature() {
		return temperature;
	}
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public void convertLatLongToLocation(String latLong)
	{
		String location = null;
		InputStream in = null;
		try {
			//	URL url = new URL(GEOCODE_URL + "?latlng="
			//	+ URLEncoder.encode(latLong, "UTF-8") + "&sensor=false");
			URL url = new URL(GEOCODE_URL + "?latlng=" + latLong + "&sensor=false");
			//System.out.println("URL is->" + url.toString());
			// Open the Connection
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			String result = convertInputStreamToString(conn.getInputStream());
			System.out.println("RESULT of GEOCODING  is->" + result);
			JSONObject json = new JSONObject(result);
			JSONArray resultsArray = json.getJSONArray("results");
			for(int i = 0; i < resultsArray.length(); i++)
			{
				JSONObject jsonObj = resultsArray.getJSONObject(i);
				JSONArray address_components = jsonObj.getJSONArray("address_components");
				for(int j = 0; j < address_components.length(); j++)
				{
					JSONObject address = address_components.getJSONObject(j);
					JSONArray types = address.getJSONArray("types");
					if(types.getString(0).equalsIgnoreCase("locality"))
					{
						city = address.getString("short_name");
					}
					if(types.getString(0).equalsIgnoreCase("sublocality_level_1"))
					{
						locality = address.getString("short_name");
					}
					if(types.getString(0).equalsIgnoreCase("sublocality_level_2"))
					{
						sublocality = address.getString("short_name");
					}				
				}
			}
			if(city == null)
				city = "";
			if(locality == null)
				locality = "";
			if(sublocality == null)
				sublocality = "";
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		catch (JSONException e) {
			System.out.println("This is not a valid address\n" + e.getMessage());
		} 
		finally {
			try {
				if(in != null)
					in.close();		
			} catch (IOException ioe)
			{

			}
		}
	}
	
	private static String convertInputStreamToString(InputStream inputStream) {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		try {
			while((line = bufferedReader.readLine()) != null)
				result += line;

		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally {
			try {
				inputStream.close();
			} catch (IOException ioe)
			{
				System.out.println("Exception while closing the resource stream\n" + ioe.getMessage());
			}
		}
	    return result;

	}

    public String toString()
    {
    	String str = "{binId="+getBinId()+", filllevel=" + getFillLevel() + ", longitude=" + getLongitude()
    			+ ",latitude=" + getLatitude() + ", temperature=" + getTemperature() + ", humidity=" + getHumidity()
    			+ ", location=" + getSublocality() + "," + getLocality() + "," + getCity() +"}\n";
    	return str;
    }
}
