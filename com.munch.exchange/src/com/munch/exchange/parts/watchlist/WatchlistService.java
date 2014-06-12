package com.munch.exchange.parts.watchlist;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.munch.exchange.job.HistoricalDataLoader;
import com.munch.exchange.job.objectivefunc.BollingerBandObjFunc;
import com.munch.exchange.job.objectivefunc.RelativeStrengthIndexObjFunc;
import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.limit.OrderTrigger;
import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.watchlist.Watchlist;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;
import com.munch.exchange.parts.composite.RateChart;
import com.munch.exchange.parts.composite.RateChartBollingerBandsComposite;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IQuoteProvider;
import com.munch.exchange.services.IWatchlistProvider;

public class WatchlistService {
	
	private static Logger logger = Logger.getLogger(WatchlistService.class);
	
	@Inject
	IWatchlistProvider watchlistProvider;
	
	@Inject
	IQuoteProvider quoteProvider;
	
	@Inject
	IExchangeRateProvider rateProvider;
	
	@Inject
	IEclipseContext context;
	
	private HashMap<String, HistoricalDataLoader> histLoaderMap=new HashMap<String, HistoricalDataLoader>();
	
	
	@Inject
	WatchlistService(){
		
	}
	
	public void loadAllHistoricalData(WatchlistTreeContentProvider contentProvider){
		
		//Clear the Map
		
		List<String> keyl=new LinkedList<String>();
		for(String key:histLoaderMap.keySet()){
			keyl.add(key);
		}
		for(String key:keyl){
			HistoricalDataLoader loader=histLoaderMap.get(key);
			if(loader.getState()!=Job.RUNNING && loader.getState()!=Job.WAITING)
				histLoaderMap.remove(key);
		}
		
		
		
		//Add new loader to the map and start them
		for(WatchlistEntity ent:contentProvider.getCurrentList().getList()){
			if(ent.getRate()==null)continue;
			if(!ent.getRate().getHistoricalData().isEmpty())continue;
			
			/*
			if(histLoaderMap.containsKey(ent.getRateUuid())){
				HistoricalDataLoader loader=histLoaderMap.get(ent.getRateUuid());
				if(loader.getState()!=Job.RUNNING){
					loader.cancel();
					histLoaderMap.remove(ent.getRateUuid());
				}
				//loader.cancel();
				//loader.join();
			}
			*/
			if(!histLoaderMap.containsKey(ent.getRateUuid())){
				HistoricalDataLoader Loader=ContextInjectionFactory.make( HistoricalDataLoader.class,context);
				Loader.setRate(ent.getRate());
				histLoaderMap.put(ent.getRateUuid(), Loader);
				Loader.schedule();
			}
			
			
		}
	}
	
	
	/**
	 * if not loaded the quote will be loaded
	 */
	private QuotePoint searchLastQuote(WatchlistEntity entity){
		if(entity.getRate()==null)return null;
		
		if(entity.getRate().getRecordedQuote().isEmpty() && !(entity.getRate() instanceof EconomicData)){
			quoteProvider.load(entity.getRate());
		}
		
		if(!entity.getRate().getRecordedQuote().isEmpty()){
			QuotePoint point = (QuotePoint) entity.getRate().getRecordedQuote().getLast();
			return point;
		}
		return null;
	}
	
	
	public void refreshQuote(WatchlistEntity entity){
		entity.setLastQuote(searchLastQuote(entity));
		if(entity.getBollingerBandTrigger()!=null){
			entity.getBollingerBandTrigger().setValue(entity.getLastQuote().getLastTradePrice());
		}
	}
	
	public void refreshHistoricalData(WatchlistEntity entity,Calendar startWatchDate){
		if(entity.getRate()!=null && !entity.getRate().getHistoricalData().isEmpty()){
			//System.out.println("startWatchDate"+startWatchDate.getTime().toLocaleString());
			
			//Buy and Old
			entity.setBuyAndOld(entity.getRate().getHistoricalData().calculateKeepAndOld(startWatchDate, DatePoint.FIELD_Close));
			//Max Profit
			entity.setMaxProfit(entity.getRate().getHistoricalData().calculateMaxProfit(startWatchDate, DatePoint.FIELD_Close));
			//Bollinger Band
			BollingerBandObjFunc func=this.getBollingerBandObjFunc(entity.getRate(),startWatchDate);
			if(func!=null ){
				//System.out.println("--- Bollinger func created");
				double v=func.compute(entity.getRate());
				double profit=func.getMaxProfit()- v;
				OrderTrigger trigger=new OrderTrigger(entity.getLastQuote().getLastTradePrice(), profit,
						func.getLimitRange(),func.getUpperLimitRange(),func.getLowerLimitRange());
				trigger.setBuySellActivated(true);
				entity.setBollingerBandTrigger(trigger);
			}
			
			//RSI func
			RelativeStrengthIndexObjFunc rsiFunc=this.getRSIObjFunc(entity.getRate(),startWatchDate);
			if(rsiFunc!=null ){
				//System.out.println("--- Bollinger func created");
				double v=rsiFunc.compute(entity.getRate());
				double profit=rsiFunc.getMaxProfit()- v;
				OrderTrigger trigger=new OrderTrigger(entity.getLastQuote().getLastTradePrice(), profit,
						rsiFunc.getLimitRange(),rsiFunc.getUpperLimitRange(),rsiFunc.getLowerLimitRange());
				entity.setRSITrigger(trigger);
			}
			
		}
		
	}
	
	
	
	public List<WatchlistEntity> findAllWatchlistEntities(String uuid){
		List<WatchlistEntity> list=new LinkedList<WatchlistEntity>();
		
		for(Watchlist watchlist:this.watchlistProvider.load().getLists()){
			for(WatchlistEntity ent:watchlist.getList()){
				if(ent.getRateUuid().equals(uuid)){
					list.add(ent);
				}
			}
		}
		
		return list;
	}
	
	
	
	public WatchlistEntity findEntityFromList(Watchlist watchlist,String uuid){
		
		
			for(WatchlistEntity ent:watchlist.getList()){
				if(ent.getRateUuid().equals(uuid)){
					return ent;
				}
			}
		
		
		return null;
	}
	
	
	public BollingerBandObjFunc getBollingerBandObjFunc(ExchangeRate rate, Calendar startWatchDate ){
		if(rate!=null 
				&& !rate.getHistoricalData().isEmpty()
				//&& rate.getOptResultsMap().get(Type.BILLINGER_BAND)!=null
				&& !rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().isEmpty()){
			
			float maxProfit=rate.getHistoricalData().calculateMaxProfit(startWatchDate, DatePoint.FIELD_Close);
			
			//Create the Bollinger Band function
			BollingerBandObjFunc bollingerBandObjFunc=new BollingerBandObjFunc(
					 HistoricalPoint.FIELD_Close,
					 RateChart.PENALTY,
					 rate.getHistoricalData().getNoneEmptyPoints(),
					 RateChartBollingerBandsComposite.maxNumberOfDays,
					 RateChartBollingerBandsComposite.maxBandFactor,
					 maxProfit
					 );
			bollingerBandObjFunc.setPeriod(rate.getHistoricalData().calculatePeriod(startWatchDate));
			return bollingerBandObjFunc;
			
		}
		
		return null;
	}
	
	
	public RelativeStrengthIndexObjFunc getRSIObjFunc(ExchangeRate rate, Calendar startWatchDate ){
		if(rate!=null 
				&& !rate.getHistoricalData().isEmpty()
				//&& rate.getOptResultsMap().get(Type.BILLINGER_BAND)!=null
				&& !rate.getOptResultsMap().get(Type.RELATIVE_STRENGTH_INDEX).getResults().isEmpty()){
			
			float maxProfit=rate.getHistoricalData().calculateMaxProfit(startWatchDate, DatePoint.FIELD_Close);
			
			//Create the Bollinger Band function
			RelativeStrengthIndexObjFunc func=new RelativeStrengthIndexObjFunc(
					 HistoricalPoint.FIELD_Close,
					 RateChart.PENALTY,
					 rate.getHistoricalData().getNoneEmptyPoints(),
					 maxProfit
					 );
			func.setPeriod(rate.getHistoricalData().calculatePeriod(startWatchDate));
			return func;
			
		}
		
		return null;
	}
	
	
	
	
	

}
