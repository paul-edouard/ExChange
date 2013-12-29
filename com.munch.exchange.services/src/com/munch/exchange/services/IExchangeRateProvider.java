package com.munch.exchange.services;

import com.munch.exchange.model.core.ExchangeRate;

public interface IExchangeRateProvider {
	
	//public enum Type { LOCAL, FTP}
	
	//==================================
	//==   SERVICE INITIALIZATION     ==
	//==================================
	/**
	 * define the working directory and the modus
	 * @param workspace
	 * @param type
	 */
	void init(String workspace);
	//==================================
	
	
	//==================================
	//==         EXCHANGE RATE        ==
	//==================================
	/**
	 * save the given exchange rate
	 * 
	 * @param rate
	 * @return true on success false otherwise
	 */
	boolean save(ExchangeRate rate);
	
	/**
	 * try to load the exchange rate corresponding to the given symbol.
	 * If no local symbol found the data will be search on the web
	 * 
	 * @param symbol
	 * @return null if no exchange 
	 */
	ExchangeRate load(String symbol);
	
	/**
	 * search the last data on the web. If new data are available, those data
	 * will be automatically updated and saved.
	 * 
	 * @param rate
	 * @return true on success and false if case of failure
	 */
	boolean update(ExchangeRate rate);
	//==================================
	
	
	//==================================
	//==         DIVIDENT             ==
	//==================================

	
}
