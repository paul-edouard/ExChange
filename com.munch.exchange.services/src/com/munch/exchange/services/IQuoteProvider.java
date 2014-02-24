package com.munch.exchange.services;

import java.util.List;

import com.munch.exchange.model.core.ExchangeRate;

public interface IQuoteProvider {
	
	// ==================================
	// ==            QUOTE             ==
	// ==================================

	/**
	 * try to load the old historical of the given stock
	 * 
	 * @param symbol
	 * @return null if no exchange
	 */
	boolean load(ExchangeRate rate);
	boolean load(List<ExchangeRate> rate);

	/**
	 * search if new historical data are available
	 * 
	 * @param rate
	 * @return true on success and false if case of failure
	 */
	boolean update(ExchangeRate rate);
	boolean update(List<ExchangeRate> rate);

}
