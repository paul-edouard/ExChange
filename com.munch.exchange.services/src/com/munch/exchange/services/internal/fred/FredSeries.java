package com.munch.exchange.services.internal.fred;

import java.util.Calendar;
import java.util.List;

import com.coherentlogic.fred.client.IServiceKey;
import com.coherentlogic.fred.client.core.builders.QueryBuilder;
import com.coherentlogic.fred.client.core.domain.Series;
import com.coherentlogic.fred.client.core.domain.Seriess;
import com.munch.exchange.model.core.EconomicData;

public class FredSeries {
	
	static final String FRED_SERIES_HTTP_PATH = "http://api.stlouisfed.org/fred/series";
	
	private String symbol;
	private Seriess result=null;
	private EconomicData economicData=null;
	
	
	public FredSeries(String symbol) {
		super();
		this.symbol = symbol;
	}

	public EconomicData getEconomicData(){
		
		if(economicData==null){
			Series series=getFirstSeries();
			if(series!=null){
				economicData=new EconomicData();
				
				economicData.setName(series.getTitle());
				economicData.setSymbol(series.getId());
				
				Calendar start=Calendar.getInstance();
				start.setTime(series.getObservationStart());
				economicData.setStart(start);
				
				Calendar end=Calendar.getInstance();
				end.setTime(series.getObservationEnd());
				economicData.setStart(end);
				
				economicData.setFrequency(series.getFrequency());
				economicData.setFrequencyShort(series.getFrequencyShort());
				
				economicData.setUnits(series.getUnits());
				economicData.setUnitsShort(series.getUnitsShort());
				
				economicData.setSeasonalAdjustment(series.getSeasonalAdjustment());
				economicData.setSeasonalAdjustmentShort(series.getSeasonalAdjustmentShort());
				
				Calendar lastUpdate=Calendar.getInstance();
				lastUpdate.setTime(series.getLastUpdated());
				economicData.setLastUpdated(lastUpdate);
				economicData.setPopularity(String.valueOf(series.getPopularity()));
				economicData.setNotes(series.getNotes());
		
				
			}
			
			
		}
		
		return economicData;
		
	}
	
	private Series getFirstSeries(){
		if(getResult()==null)return null;
		
		List<Series> seriesList = getResult().getSeriesList();
		for(Series se:seriesList ){
			return se;
		}
		
		return null;
	}
	
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




	public static void main(String[] args) {
		//CPIAUCSL
		//FredSeries s=new FredSeries("GNPCA");
		FredSeries s=new FredSeries("CPIAUCSL");
		System.out.println(s.getEconomicData());
		
		//TODO Implement Caterogy search!
		
	}

}
