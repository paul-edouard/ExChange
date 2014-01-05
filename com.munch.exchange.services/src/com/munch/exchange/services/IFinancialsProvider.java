package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;

public interface IFinancialsProvider {
	
	
	// ==================================
	// ==      INCOME STATEMENT        ==
	// ==================================

	
	boolean loadIncomeStatement(Stock stock);

	
	boolean updateIncomeStatement(Stock stock);
	
	
	// ==================================
	// ==        BALANCE SHEET         ==
	// ==================================


	boolean loadBalanceSheet(Stock stock);

	
	boolean updateBalanceSheet(Stock stock);
	
	
	
	// ==================================
	// ==          CASH FLOW           ==
	// ==================================

	boolean loadCashFlow(Stock stock);

	
	boolean updateCashFlow(Stock stock);


}
