package com.munch.exchange.parts.watchlist;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.core.watchlist.Watchlist;
import com.munch.exchange.model.core.watchlist.WatchlistEntity;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IQuoteProvider;
import com.munch.exchange.services.IWatchlistProvider;

public class WatchlistService {
	
	@Inject
	IWatchlistProvider watchlistProvider;
	
	@Inject
	IQuoteProvider quoteProvider;
	
	@Inject
	IExchangeRateProvider rateProvider;
	
	@Inject
	WatchlistService(){
		
	}
	
	
	/**
	 * if not loaded the quote will be loaded
	 */
	public QuotePoint searchLastQuote(WatchlistEntity entity){
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
	
	
	

}
