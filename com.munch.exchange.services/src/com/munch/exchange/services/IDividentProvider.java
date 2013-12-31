package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;

public interface IDividentProvider {
	
	//==================================
	//==         DIVIDENT             ==
	//==================================
	
	/**
	 * try to load the old divident of the given stock
	 * 
	 * @param symbol
	 * @return null if no exchange 
	 */
	boolean load(Stock stock);
	
	/**
	 * search if new divident are available
	 * 
	 * @param rate
	 * @return true on success and false if case of failure
	 */
	boolean update(Stock stock);

}
