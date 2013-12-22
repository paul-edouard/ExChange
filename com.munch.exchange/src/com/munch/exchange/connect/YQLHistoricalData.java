package com.munch.exchange.connect;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;


public class YQLHistoricalData extends YQLTable {
	
	private static String table="yahoo.finance.historicaldata";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private Calendar startDate;
	private Calendar endDate;
	
	
	
	public YQLHistoricalData(String symbol, Calendar startDate, Calendar endDate ){
		super();
		this.symbol=symbol;
		this.startDate=startDate;
		this.endDate=endDate;
	}
	
	public YQLHistoricalData(String symbol, Calendar startDate ){
		super();
		this.symbol=symbol;
		this.startDate=startDate;
		this.endDate=Calendar.getInstance();
	}
	
	protected String createUrl(){
		try {
		String baseUrl=YQL.URL;
		String query = "select * from "+this.getTable()
						+" where symbol = \""+symbol+"\""
						+" and "
						+"startDate = \""+YQL.getDateString(startDate)+"\""+
						" and "+
						"endDate = \""+YQL.getDateString(endDate)+"\"";
		
		//System.out.println("Query"+query);
		
		return baseUrl + URLEncoder.encode(query, "UTF-8") +this.getFormat();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}
	
	protected String getTable(){
		return table;
	}
	
	protected String getFormat(){
		return format;
	}
	
	
	
	public static void main(String[] args) {
		
		Calendar date=Calendar.getInstance();
		date.set(2013, 10, 1);
		YQLHistoricalData hisData=new YQLHistoricalData("^OEX",date);
		System.out.println(hisData.getResult().toString(1));
		
	}
	
	
	
	
	
	
}
