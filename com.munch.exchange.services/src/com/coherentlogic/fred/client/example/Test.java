package com.coherentlogic.fred.client.example;


public class Test {
	
	 public static void main(String[] args) {
		 
		 QueryBuilderTest tes=new QueryBuilderTest();
		 try {
			tes.setUp();
			// tes.getSeriesObservationsExpectingTxtFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// tes.getCategory();
		 tes.getSeriesObservationsExpectingXML();
		 //tes.getSeriesObservationsExpectingXmlFile();
		
		 
	 }
	

}
