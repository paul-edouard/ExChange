package com.munch.exchange.services;

import java.util.LinkedList;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;

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
	 * test if the symbol was already used. If yes the function return true
	 * 
	 * @param symbol
	 * @return
	 */
	boolean isSymbolAlreadyUsed(String symbol);
	
	/**
	 * try to load the exchange rate corresponding to the given symbol.
	 * If no local symbol found the data will be search on the web
	 * 
	 * @param symbol
	 * @return null if no exchange 
	 */
	ExchangeRate load(String symbol);
	
	/**
	 * load all local Exchange rate of a given type
	 * @param clazz
	 * @return
	 */
	LinkedList<ExchangeRate> loadAll(Class<? extends ExchangeRate> clazz);
	
	/**
	 * search the last data on the web. If new data are available, those data
	 * will be automatically updated and saved.
	 * 
	 * @param rate
	 * @return true on success and false if case of failure
	 */
	boolean update(ExchangeRate rate);
	
	
	/**
	 * remove the rate
	 * @param rate
	 * @return
	 */
	boolean delete(ExchangeRate rate);
	
	
	/**
	 * Save the given rate
	 * @param rate
	 * @return
	 */
	boolean save(ExchangeRate rate);
	
	/**
	 * try to find a stock on YQL
	 * 
	 * @param symbol
	 * @return
	 */
	Stock loadStock(String symbol);
	
	/**
	 * return all the cached rate
	 * @return
	 */
	LinkedList<ExchangeRate> getCachedRates();
	

	
}
