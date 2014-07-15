package com.munch.exchange.services;

import com.munch.exchange.model.core.Stock;

public interface IFinancialsProvider {
	
	// ==========================================
	// ==      SAVE THE FINANCIALS DATA        ==
	// ==========================================
	
	boolean saveAll(Stock stock);
	
	
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
