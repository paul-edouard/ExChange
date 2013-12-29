package com.munch.exchange.services.internal.yql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;

import com.munch.exchange.model.core.divident.Dividend;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;


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
	
	
	public LinkedList<Dividend> getDividendList(){
		String[] divs=getDividendData().split(";");
		LinkedList<Dividend> l=new LinkedList<Dividend>();
		for(int i=0;i<divs.length;i++){
			Dividend d=new Dividend(divs[i]);
			if(d.getDate()!=null)
				l.add(d);
		}
		
		return l;
		
	}
	
	private String getDividendData(){
		
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

               if (nextLine !=null )
               {
            	   
            	   if(!nextLine.startsWith("Date,Dividends")){
            		   output+=nextLine+";";
            	   }
            	 //  System.out.println("Line:"+nextLine);
            	   
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
		//	System.out.println(baseUrl + query);
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
	
	
	private HistoricalPoint createHisPoint(JSONObject obj){
		
		HistoricalPoint point=new HistoricalPoint();
		point.setLow(obj.getFloat("Low"));
		point.setOpen(obj.getFloat("Open"));
		point.setAdjClose(obj.getFloat("Adj_Close"));
		point.setClose(obj.getFloat("Close"));
		try {
			point.setDate(obj.getDate("Date"));
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		point.setVolume(obj.getLong("Volume"));
		point.setHigh(obj.getFloat("High"));
		
		return point;
	}
	
	public LinkedList<HistoricalPoint> getHisPointList(){
		
		LinkedList<HistoricalPoint> plist=new LinkedList<HistoricalPoint>();
		JSONArray array=  this.getResult().getJSONArray("quote");
		
		for(int i=0;i<array.length();i++){
			plist.add(this.createHisPoint(array.getJSONObject(i)));
			//System.out.println(hisData.createHisPoint(array.getJSONObject(i)));
		}
		
		return plist;
	}
	
	
	
	
	public static void main(String[] args) {
		
		Calendar date=Calendar.getInstance();
		date.set(2013, 03, 1);
		Calendar date2=Calendar.getInstance();
		date2.set(2013, 03, 20);
		
		YQLHistoricalData hisData=new YQLHistoricalData("DAI.DE",date,date2);
		for(HistoricalPoint point: hisData.getHisPointList()){
			System.out.println(point);
		}
		for(Dividend div:hisData.getDividendList()){
			System.out.println(div);
		}
		
		
		//EURUSD=X
		//YQLHistoricalData hisData=new YQLHistoricalData("EURUSD=X",date,date2);
		
		//System.out.println(hisData.getDividendData());
		
	}
	
	
	
	
	
	
}
