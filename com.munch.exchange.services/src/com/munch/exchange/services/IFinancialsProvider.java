package com.munch.exchange.services;

import java.util.LinkedList;

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
	
	// ===================================
	// ==  REPORT READER CONFIGURATION  ==
	// ===================================
	
	boolean loadReportReaderConfiguration(Stock stock);

	boolean saveReportReaderConfiguration(Stock stock);
	
	String loadReportDocument(Stock stock,String url);
	
	
	String[] searchAllMatchingDocuments(Stock stock);
	
	String getHtmlContent(String url);
	LinkedList<String> findPDFDocument(String url);
	
	
}
