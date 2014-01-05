package com.munch.exchange.model.core.financials;

import com.munch.exchange.model.xml.ParameterElement;

public class Financials extends ParameterElement {
	
	
	public static final String FIELD_BalanceSheet = "BalanceSheet";
	public static final String FIELD_IncomeStatement = "IncomeStatement";
	public static final String FIELD_CashFlow = "CashFlow";
	
	private HistoricalBalanceSheet BalanceSheet=new HistoricalBalanceSheet();
	private HistoricalIncomeStatement IncomeStatement=new HistoricalIncomeStatement();
	private HistoricalCashFlow CashFlow=new HistoricalCashFlow();
	
	
	public HistoricalBalanceSheet getBalanceSheet() {
		return BalanceSheet;
	}
	public void setBalanceSheet(HistoricalBalanceSheet balanceSheet) {
		changes.firePropertyChange(FIELD_BalanceSheet, BalanceSheet,
				BalanceSheet = balanceSheet);
	}
	public HistoricalIncomeStatement getIncomeStatement() {
		return IncomeStatement;
	}
	public void setIncomeStatement(HistoricalIncomeStatement incomeStatement) {
		changes.firePropertyChange(FIELD_IncomeStatement, IncomeStatement,
				IncomeStatement = incomeStatement);
	}
	public HistoricalCashFlow getCashFlow() {
		return CashFlow;
	}
	public void setCashFlow(HistoricalCashFlow cashFlow) {
		changes.firePropertyChange(FIELD_CashFlow, CashFlow, CashFlow = cashFlow);
	}
	
	
	
	

}
