package com.munch.exchange.services.internal.fred;

import java.util.Calendar;
import java.util.LinkedList;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class FredObservations {
	
	static final String FRED_OBSERVATIONS_HTTP_PATH = "http://api.stlouisfed.org/fred/series/observations?";
	
	private Calendar startDate;
	private Calendar endDate;
	
	private String symbol;
	
	LinkedList<HistoricalPoint> plist=null;
	
	public FredObservations(String symbol, Calendar startDate, Calendar endDate) {
		super();
		this.symbol=symbol;
		this.startDate=startDate;
		this.endDate=endDate;
	}
	
	public FredObservations(EconomicData economicData) {
		super();
		this.symbol=economicData.getId();
		this.startDate=economicData.getStart();
		this.endDate=economicData.getEnd();
	}
	
	private String createUrl(){
		String url=FredApi.URL+"series/observations?";
		url+="series_id="+this.symbol+"&";
		url+="api_key="+IServiceKey.API_KEY+"&";
		url+="observation_start="+DateTool.dateToDayString(this.startDate)+"&";
		url+="observation_end="+DateTool.dateToDayString(this.endDate)+"&";
		//url+="frequency=d&";
		url+="file_type=json";
		return url;
	}
	private JSONObject getJSONObject(){
		return FredApi.getJSONObject(this.createUrl());
	}
	
	private HistoricalPoint JSONToHistoricalPoint(JSONObject observation){
		HistoricalPoint point=new HistoricalPoint();
		point.setDate(DateTool.StringToDay(observation.getString("date")));
		if(observation.getString("value").equals("."))return null;
		point.setClose(Float.parseFloat(observation.getString("value")));
		return point;
	}
	
	public LinkedList<HistoricalPoint> getObservations(){
		
		if(plist!=null)return plist;
		
		plist=new LinkedList<HistoricalPoint>();
		
		JSONObject obj=this.getJSONObject();
		if(obj==null)return plist;
		JSONArray  array=obj.getJSONArray("observations");
		for(int i=0;i<array.length();i++){
			if(array.get(i) instanceof JSONObject){
				JSONObject j_o=(JSONObject) array.get(i);
				HistoricalPoint point=JSONToHistoricalPoint(j_o);
				if(point!=null)
					plist.add(point);
				
			}
		}
		
		return plist;
	}
	
	public static void main(String[] args) {
		//CPIAUCSL
		//FredSeries s=new FredSeries("GNPCA");
		//FredSeries s=new FredSeries("CPIAUCSL");
		
		FredObservations o=new FredObservations("DEXUSEU",
				DateTool.StringToDay("2013-01-01"),
				DateTool.StringToDay("2014-01-01"));
		System.out.println(o.getJSONObject().toString(1));
		for(HistoricalPoint point:o.getObservations()){
			System.out.println(point);
		}
		
		//TODO Implement Caterogy search!
		
	}

}
