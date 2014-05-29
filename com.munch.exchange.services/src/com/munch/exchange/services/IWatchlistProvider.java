package com.munch.exchange.services;

import com.munch.exchange.model.core.watchlist.Watchlists;

public interface IWatchlistProvider {
	
	
	//==================================
	//==      Watchlist Provider      ==
	//==================================
	
	void init(String workspace);
	
	Watchlists load();
	
	boolean save();

}
