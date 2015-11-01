package com.smartbin.rest;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.smartbin.data.Bin;
import com.smartbin.data.BinHandler;
import com.smartbin.dblayer.MongoDBService;
import com.smartbin.reports.JasperReportGenerator;
import com.smartbin.service.ServiceMgmt;


@Path("/SmartBin")
public class RESTBinService {

	MongoDBService dbservice = null;
	BinHandler binHandler = null;
	Bin bin = null;
	JasperReportGenerator repGen = null;
	public RESTBinService()
	{
		new ServiceMgmt();
		dbservice = ServiceMgmt.getDbService();
		binHandler =  new BinHandler();
		repGen = new JasperReportGenerator(dbservice);
	}
	
	@Path("/getAllBins")
	@GET
	@Produces (MediaType.APPLICATION_JSON)
	public Response getAllBins()
	{
		ArrayList<Bin> result = null;
		ResponseBuilder respBuilder = null;
		result = binHandler.getAllBins();
		if(result == null)
			respBuilder = Response.status(Response.Status.NOT_FOUND).entity("List of bins could not be retrieved");
		else
			respBuilder = Response.status(Response.Status.OK).entity(result);		
		return respBuilder.build();
	}
	
	@Path("/getAllLocations")
	@GET
	@Produces (MediaType.APPLICATION_JSON)
	public Response getAllLocations()
	{
		ArrayList<String> result = null;
		ResponseBuilder respBuilder = null;
		result = binHandler.getAllLocations();
		if(result == null)
			respBuilder = Response.status(Response.Status.NOT_FOUND).entity("List of locations could not be retrieved");
		else
			respBuilder = Response.status(Response.Status.OK).entity(result);		
		return respBuilder.build();
	}
	

	@Path("/insertBin")
	@POST
	@Produces (MediaType.APPLICATION_JSON)
	@Consumes (MediaType.APPLICATION_JSON)
	public Response insertBin(Bin bin)
	{
		boolean result = false;
		ResponseBuilder respBuilder = null;
		result = binHandler.insertBin("BinStore", bin);
		if(!result)
			respBuilder = Response.status(Response.Status.GONE).entity("The given document could not be inserted");
		else
			respBuilder = Response.status(Response.Status.OK);		
		return respBuilder.build();
	}
	
	@Path("/updateBin")
	@POST
	@Produces (MediaType.APPLICATION_JSON)
	@Consumes (MediaType.APPLICATION_JSON)
	public Response updateBin(Bin bin)
	{
		boolean result = false;
		ResponseBuilder respBuilder = null;
		result = binHandler.updateBin("BinStore", bin);
		if(!result)
			respBuilder = Response.status(Response.Status.GONE).entity("The given document could not be updated");
		else
			respBuilder = Response.status(Response.Status.OK);		
		return respBuilder.build();
	}
	
	@Path("/deleteBin/{binId}")
	@DELETE
	public Response deleteBin(@PathParam("binId") int binId)
	{
		boolean result = false;
		ResponseBuilder respBuilder = null;
		result = binHandler.deleteBin("BinStore", binId);
		if(!result)
			respBuilder = Response.status(Response.Status.GONE).entity("The given document could not be deleted");
		else
			respBuilder = Response.status(Response.Status.OK);		
		return respBuilder.build();
	}
	
	@Path("/getLocalityBins/{lat}/{lng}/{radius}")
	@GET
	@Produces (MediaType.APPLICATION_JSON)
	public Response getLocalityBins(@PathParam("lat")Double lat,  @PathParam("lng")  Double lng, @PathParam("radius")int radius)
	{
		ArrayList<Bin> result = null;
		ResponseBuilder respBuilder = null;
		result = binHandler.getLocalityBins(lat, lng, radius);
		if(result == null)
			respBuilder = Response.status(Response.Status.NOT_FOUND).entity("List of bins could not be retrieved");
		else
			respBuilder = Response.status(Response.Status.OK).entity(result);		
		return respBuilder.build();
	}
	
	@Path("/getReport/{from}/{to}/{list}")
	@GET
	@Produces (MediaType.APPLICATION_JSON)
	@Consumes (MediaType.APPLICATION_JSON)
	public Response executeFillTrendReport(@PathParam("from")Integer fromDate, @PathParam("to")Integer toDate, @PathParam("list")String binsList)
	{
		boolean result = false;
		ResponseBuilder respBuilder = null;
		ArrayList<Integer> intArr = new ArrayList<Integer>();
		String [] strArr = binsList.split(",");
		for(int i = 0; i < strArr.length ; i++)
		{
			intArr.add(Integer.parseInt(strArr[i]));
		}
		result = repGen.executeFillTrendReport(fromDate, toDate, intArr);
		if(result == false)
			respBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to generate the report");
		else
			respBuilder = Response.status(Response.Status.OK).entity(result);		
		return respBuilder.build();
	}
}
