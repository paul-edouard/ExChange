package com.munch.exchange.services.internal.fred;

import java.util.Calendar;
import java.util.LinkedList;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class FredSeriesVintageDate {
	
	
	static final String FRED_SERIES_VINTAGE_DATE_HTTP_PATH = "http://api.stlouisfed.org/fred/series/vintagedates";
	
	private String symbol;
	
	public FredSeriesVintageDate(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	public FredSeriesVintageDate(EconomicData economicData) {
		super();
		this.symbol = economicData.getId();
	}
	
	private String createUrl(){
		String url=FredApi.URL+"series/vintagedates?";
		url+="series_id="+this.symbol+"&";
		url+="api_key="+IServiceKey.API_KEY+"&";
		url+="file_type=json";
		return url;
	}
	
	public JSONObject getJSONObject(){
		return FredApi.getJSONObject(this.createUrl());
	}
	
	public LinkedList<Calendar> getVintageList(){
		LinkedList<Calendar> dateList=new LinkedList<Calendar>();
		
		JSONObject obj=this.getJSONObject();
		JSONArray  array=obj.getJSONArray("vintage_dates");
		//System.out.println(array.length());
		for(int i=0;i<array.length();i++){
			//System.out.println(array.get(i));
			Calendar date =DateTool.StringToDay(array.get(i).toString());
			dateList.add(date);
		}
		
		return dateList;
		
	}
	
	
	
	public static void main(String[] args) {
		//CPIAUCSL
		//FredSeries s=new FredSeries("GNPCA");
		FredSeriesVintageDate s=new FredSeriesVintageDate("UNRATE");
		for(Calendar date:s.getVintageList()){
			System.out.println(DateTool.dateToDayString(date));
		}
		//System.out.println(s.getJSONObject());
		
		
	}

}
