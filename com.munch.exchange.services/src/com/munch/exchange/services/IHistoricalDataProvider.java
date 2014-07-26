package com.munch.exchange.services;

import java.util.Calendar;

import com.munch.exchange.model.core.EconomicData;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalData;

public interface IHistoricalDataProvider {
	
	// ==================================
	// ==      HISTORICAL DATA         ==
	// ==================================
	/**
	 * delete all saved history points
	 * 
	 * @param rate
	 */
	void clear(ExchangeRate rate);
	
	/**
	 * try to load the old historical of the given stock
	 * 
	 * @param symbol
	 * @return null if no exchange
	 */
	boolean load(ExchangeRate rate);
	
	boolean loadVintageDates(HistoricalData hisDatas,EconomicData ecoData);

	/**
	 * search if new historical data are available
	 * 
	 * @param rate
	 * @return true on success and false if case of failure
	 */
	boolean update(ExchangeRate rate);
	
	
	/**
	 * check if the rate has local data
	 * @param rate
	 * @return
	 */
	boolean hasLocalData(ExchangeRate rate);
	
	/**
	 * return the number of years of data
	 * @param rate
	 * @return
	 */
	Calendar[] getIntervals(ExchangeRate rate);
	
	/**
	 * load the historical data from a given interval
	 * 
	 * @param rate
	 * @param start
	 * @param end
	 */
	void loadInterval(ExchangeRate rate, HistoricalData hisDatas,Calendar start,Calendar end);
	
	/**
	 * save all the historical data found
	 * @param rate
	 * @return
	 */
	boolean save(ExchangeRate rate,HistoricalData hisDatas );

}
