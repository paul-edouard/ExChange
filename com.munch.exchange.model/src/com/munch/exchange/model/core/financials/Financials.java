package com.munch.exchange.model.core.financials;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

import com.munch.exchange.model.core.DatePoint;
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
	
	
	public LinkedList<Calendar> getDateList(String periodType){
		LinkedList<Calendar> list=new LinkedList<Calendar>();
		for(DatePoint point:BalanceSheet.getPoints(periodType)){
			if(!list.contains(point.getDate()))
				list.add(point.getDate());
		}
		for(DatePoint point:IncomeStatement.getPoints(periodType)){
			if(!list.contains(point.getDate()))
				list.add(point.getDate());
		}
		for(DatePoint point:CashFlow.getPoints(periodType)){
			if(!list.contains(point.getDate()))
				list.add(point.getDate());
		}
		
		java.util.Collections.sort(list);
		java.util.Collections.reverse(list);
		
		return list;
		
	}
	
	
	public long getValue(String periodType,Calendar date,String key,String sectorKey){
		
		if(sectorKey.equals(FIELD_BalanceSheet)){
		for(DatePoint point:BalanceSheet.getPoints(periodType)){
			FinancialPoint p=(FinancialPoint)point;
			if(!p.getDate().equals(date))continue;
			
			long val=p.getValue(key);
			if(val!=0)return val;
			
		}
		}
		
		if(sectorKey.equals(FIELD_IncomeStatement)){
		for(DatePoint point:IncomeStatement.getPoints(periodType)){
			FinancialPoint p=(FinancialPoint)point;
			if(!p.getDate().equals(date))continue;
			
			//System.out.println("Key in INcone: "+key);
			
			long val=p.getValue(key);
			if(val!=0)return val;
			
		}
		}
		
		if(sectorKey.equals(FIELD_CashFlow)){
		for(DatePoint point:CashFlow.getPoints(periodType)){
			FinancialPoint p=(FinancialPoint)point;
			if(!p.getDate().equals(date))continue;
			
			long val=p.getValue(key);
			if(val!=0)return val;
			
		}
		}
		
		
		return Long.MIN_VALUE;
	}
	
	
	
	

}
