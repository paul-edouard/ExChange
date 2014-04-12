package com.munch.exchange.services;

import com.munch.exchange.model.core.ExchangeRate;

public interface IOptimizationResultsProvider {
	
	//==================================
	//==     Optimization Results     ==
	//==================================
		
		/**
		 * try to load the old Optimization Results of the given stock
		 * 
		 * @param symbol
		 * @return null if no exchange 
		 */
		boolean load(ExchangeRate rate);
		
		/**
		 * search if new Optimization Results are available
		 * 
		 * @param rate
		 * @return true on success and false if case of failure
		 */
		boolean save(ExchangeRate rate);

}
