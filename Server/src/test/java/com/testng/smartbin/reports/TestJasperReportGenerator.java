package com.testng.smartbin.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.smartbin.data.Bin;
import com.smartbin.dblayer.MongoDBService;
import com.smartbin.reports.JasperReportGenerator;
import com.smartbin.service.ServiceMgmt;

import net.sf.jasperreports.engine.JRException;

public class TestJasperReportGenerator {

	MongoDBService dbservice = null;
	JasperReportGenerator repGen = null;
	Bin bin = null;
	ServiceMgmt ser = null;
	
	@BeforeTest
	public void SetUp()
	{
		System.out.println("in Setup for TestJasperReportGenerator");
		ser = new ServiceMgmt();
		dbservice = ServiceMgmt.getDbService();
		repGen =  new JasperReportGenerator(dbservice);	
	}
	
	@AfterTest
	public void tearDown()
	{
		dbservice = null;
		repGen = null;
		ser = null;
	}
	
	@Test
	public void TestexecuteFillTrendReport()
	{
		System.out.println("TestexecuteFillTrendReport -> ENTER");
		ArrayList<Integer> binsList = new ArrayList<Integer>();
		binsList.add(1);
		binsList.add(1000);
		binsList.add(56);
		repGen.executeFillTrendReport(20151024, 20151029, binsList );
		
	}
}
