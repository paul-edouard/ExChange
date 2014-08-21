package com.munch.exchange.services.internal.fred;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class FredSeries {
	
	static final String FRED_SERIES_HTTP_PATH = "http://api.stlouisfed.org/fred/series";
	
	
	//http://api.stlouisfed.org/fred/series?series_id=GNPCA&api_key=abcdefghijklmnopqrstuvwxyz123456&file_type=json
	
	private String symbol;
	//private Seriess result=null;
	private EconomicData economicData=null;
	
	
	public FredSeries(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	public FredSeries(EconomicData economicData) {
		super();
		this.symbol = economicData.getId();
	}
	
	private String createUrl(){
		String url=FredApi.URL+"series?";
		url+="series_id="+this.symbol+"&";
		url+="api_key="+IServiceKey.API_KEY+"&";
		url+="file_type=json";
		return url;
	}
	
	public JSONObject getJSONObject(){
		return FredApi.getJSONObject(this.createUrl());
	}
	
	
	public EconomicData getEconomicData(){
		
		if(economicData==null){
			//Series series=getFirstSeries();
			
			JSONObject series=getFirstSeries();
			
			
			if(series!=null){
			//	System.out.println(series);
				economicData=new EconomicData();
				
				economicData.setName(series.getString("title"));
				economicData.setSymbol(EconomicData.FRED_SYMBOL_PREFIX+series.getString("id"));
				economicData.setId(series.getString("id"));
				
				
				economicData.setStart(DateTool.StringToDay(series.getString("observation_start")));
				economicData.setEnd(DateTool.StringToDay(series.getString("observation_end")));
				
				economicData.setFrequency(series.getString("frequency"));
				economicData.setFrequencyShort(series.getString("frequency_short"));
				
				economicData.setUnits(series.getString("units"));
				economicData.setUnitsShort(series.getString("units_short"));
				
				economicData.setSeasonalAdjustment(series.getString("seasonal_adjustment"));
				economicData.setSeasonalAdjustmentShort(series.getString("seasonal_adjustment_short"));
				
				
				economicData.setLastUpdated(DateTool.StringToMs(series.getString("last_updated")));
				economicData.setPopularity(String.valueOf(series.getInt("popularity")));
				economicData.setNotes(series.getString("notes"));
				
				//Find out the series categories
				FredSeriesCategory fredSeriesCategory=new FredSeriesCategory(economicData);
				economicData.setCategories(fredSeriesCategory.getCategories());
		
			}
			
			
		}
		
		return economicData;
		
	}
	
	private JSONObject getFirstSeries(){
		JSONObject obj=this.getJSONObject();
		if(obj==null)return null;
		if(!obj.has("seriess"))return null;
		JSONArray  array=obj.getJSONArray("seriess");
		for(int i=0;i<array.length();i++){
			//Object o=array.get(i);
			if(array.get(i) instanceof JSONObject){
				JSONObject j_o=(JSONObject) array.get(i);
				return j_o;
		//		System.out.println(j_o);
			}
			
		}
		return null;
		
		
		/*
		if(getResult()==null)return null;
		
		List<Series> seriesList = getResult().getSeriesList();
		for(Series se:seriesList ){
			return se;
		}
		
		return null;
		*/
	}
	
	/*
	private Seriess getResult() {
		if(result==null){
			 QueryBuilder builder = new QueryBuilder (
			            FredContext.INSTANCE.getRestTemplate(),
			            FRED_SERIES_HTTP_PATH);

			        result = builder
			            .setApiKey(IServiceKey.API_KEY)
			            .setSeriesId(this.symbol)
			           // .setRealtimeStart(realtimeStart)
			           // .setRealtimeEnd(realtimeEnd)
			            .doGet (Seriess.class);
		}
		
		return result;
	}

	*/


	public static void main(String[] args) {
		//CPIAUCSL
		//FredSeries s=new FredSeries("GNPCA");
		FredSeries s=new FredSeries("CPIAUCSL");
		System.out.println(s.getEconomicData());
		
		
		
		//TODO Implement Caterogy search!
		
	}

}
