package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;

public interface IFinancialsProvider {
	
	
	// ==================================
	// ==      INCOME STATEMENT        ==
	// ==================================

	
	boolean loadIncomeStatement(Stock stock);

	
	boolean updateIncomeStatement(Stock stock);

}
