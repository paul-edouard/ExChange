package com.munch.exchange.services.internal.yql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;


public class YQLHistoricalData extends YQLTable {
	
	private static String table="yahoo.finance.historicaldata";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private Calendar startDate;
	private Calendar endDate;
	
	private static String dividend_url="http://ichart.finance.yahoo.com/table.csv?s=";
	
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
	
	
	public String getDividendData(){
		
		String output="";
		
		URL url;
		try {
			url = new URL(createDividendUrl());
		
		URLConnection connection = url.openConnection();
		
		InputStreamReader inStream = new InputStreamReader(connection.getInputStream());

        BufferedReader buff= new BufferedReader(inStream);
        
        String nextLine="";
        
        while (true)
        {
               nextLine =buff.readLine();  

               if (nextLine !=null)
               {
            	   output+=nextLine+"\n";
               }

               else
               {
                  break;
               } 
           }
        
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
		
	}
	
	private String createDividendUrl(){
		try {
			String baseUrl=dividend_url;
			
			String query=	URLEncoder.encode(symbol, "UTF-8")+
							"&a="+YQL.getMonthString(startDate)+
							"&b="+YQL.getDayString(startDate)+
							"&c="+YQL.getYearString(startDate)+
							"&d="+YQL.getMonthString(endDate)+
							"&e="+YQL.getDayString(endDate)+
							"&f="+YQL.getYearString(endDate)+
							"&g=v&ignore=.csv";
			System.out.println(baseUrl + query);
			return baseUrl + query;
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
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
	
	/*
	 * 
	 *   InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());

                     BufferedReader buff= new BufferedReader(inStream);

                     while (true)
                     {
                            nextLine =buff.readLine();  

                            if (nextLine !=null)
                            {
                                other.setText(nextLine);
                            }

                            else
                            {
                               break;
                            } 
                        }

	 * 
	 */
	
	public static void main(String[] args) {
		
		Calendar date=Calendar.getInstance();
		date.set(2013, 03, 1);
		Calendar date2=Calendar.getInstance();
		date2.set(2013, 03, 20);
		
		YQLHistoricalData hisData=new YQLHistoricalData("DAI.DE",date,date2);
		//EURUSD=X
		//YQLHistoricalData hisData=new YQLHistoricalData("EURUSD=X",date,date2);
		
		
		System.out.println(hisData.getResult().toString(1));
		System.out.println(hisData.getDividendData());
		
	}
	
	
	
	
	
	
}
