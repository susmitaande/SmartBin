package com.smartbin.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.client.MongoDatabase;
import com.smartbin.dblayer.MongoDBService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import com.jaspersoft.mongodb.connection.MongoDbConnection;

public class JasperReportGenerator {

	MongoDatabase database = null;
	static String HISTORY_COLLECTION = "BinHistory";
	String mongoURI = null;
	
	public JasperReportGenerator(MongoDBService dbService)
	{
		database = dbService.getDb();
		mongoURI = dbService.getMongURI();
	}
	
	public boolean executeFillTrendReport(Integer fromDate, Integer toDate, ArrayList<Integer> binsList)
	{
		JasperPrint jprint = null;
		
		//Step 1: Get the jrxml file
		String jrxmlFileName = "C:\\report_jrxmls\\fillTrend_jrxml.jrxml";
		
		//Step2: Compile the report
		String jasperFileName =  "fillTrend.jasper";
		try {
			JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);
		} catch (JRException jre)
		{
			System.out.println("Exception while compiling the report" + jre.getMessage());
			jre.printStackTrace();
			return false;
		}
		
		//Step3 : Create Parameters
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("From", fromDate);
		paramMap.put("To", toDate);
		paramMap.put("BinsList", binsList);

		//Step 4 : Create the MongoDB Data Source
		try {
			MongoDbConnection connection = new MongoDbConnection(mongoURI,null,null);

			//Step 5 : Generate the Jasper print
			jprint = (JasperPrint) JasperFillManager.fillReport(jasperFileName, paramMap, connection);

			//Step 6 : Export the generated report
			if(jprint != null)
				JasperExportManager.exportReportToHtmlFile(jprint, "webapps/jasperserver/fillTrend.html");
		} catch (JRException jre)
		{
			System.out.println("Unable to run report for fillTrend");
			return false;
		}
		return true;
	}
}
