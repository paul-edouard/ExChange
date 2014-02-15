package com.munch.exchange.services.internal.fred;

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
				//economicData.set
				
				
				
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
		FredSeries s=new FredSeries("GNPCA");
		Seriess result=s.getResult();
		
		List<Series> seriesList = result.getSeriesList();
		for(Series se:seriesList ){
			System.out.println(se.getTitle());
		}
		
	}

}
