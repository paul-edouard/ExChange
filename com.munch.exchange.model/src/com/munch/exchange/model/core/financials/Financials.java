package com.munch.exchange.model.core.financials;

import java.util.Calendar;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.ParameterElement;

public class Financials extends ParameterElement {
	
	private static Logger logger = Logger.getLogger(Financials.class);
	
	public static final String FIELD_BalanceSheet = "BalanceSheet";
	public static final String FIELD_IncomeStatement = "IncomeStatement";
	public static final String FIELD_CashFlow = "CashFlow";
	public static final String FIELD_ReportReaderConfiguration = "ReportReaderConfiguration";
	
	private HistoricalBalanceSheet BalanceSheet=new HistoricalBalanceSheet();
	private HistoricalIncomeStatement IncomeStatement=new HistoricalIncomeStatement();
	private HistoricalCashFlow CashFlow=new HistoricalCashFlow();
	private ReportReaderConfiguration reportReaderConfiguration=new ReportReaderConfiguration();
	
	
	public ReportReaderConfiguration getReportReaderConfiguration() {
		return reportReaderConfiguration;
	}
	public void setReportReaderConfiguration(ReportReaderConfiguration reportReaderConfiguration) {
	changes.firePropertyChange(FIELD_ReportReaderConfiguration, this.reportReaderConfiguration, this.reportReaderConfiguration = reportReaderConfiguration);}
	
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
	
	public void addPoint(String periodType){
		Calendar lastpoint=getDateList(periodType).getLast();
		logger.info("Last point:"+DateTool.dateToDayString(lastpoint));
		Calendar newpoint=Calendar.getInstance();
		newpoint.setTimeInMillis(lastpoint.getTimeInMillis());
		
		if(periodType.equals(FinancialPoint.PeriodeTypeQuaterly)){
			newpoint.add(Calendar.MONTH, -3);
			//logger.info("Max Day of Month:"+newpoint.getActualMaximum(Calendar.DAY_OF_MONTH));
			newpoint.set(Calendar.DAY_OF_MONTH, newpoint.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		else if(periodType.equals(FinancialPoint.PeriodeTypeAnnual)){
			newpoint.add(Calendar.YEAR, -1);
		}
		
		BalanceSheetPoint bs_point=new BalanceSheetPoint();
		bs_point.setDate(newpoint);bs_point.setPeriodEnding(newpoint);
		bs_point.setPeriodType(periodType);
		BalanceSheet.addLast(bs_point);
		
		IncomeStatementPoint is_point=new IncomeStatementPoint();
		is_point.setDate(newpoint);is_point.setPeriodEnding(newpoint);
		is_point.setPeriodType(periodType);
		IncomeStatement.addLast(is_point);
		
		CashFlowPoint cf_point=new CashFlowPoint();
		cf_point.setDate(newpoint);cf_point.setPeriodEnding(newpoint);
		cf_point.setPeriodType(periodType);
		CashFlow.addLast(cf_point);
		
	}
	
	public Calendar getQ1Date(int year){
		LinkedList<Calendar> dates=getDateList(FinancialPoint.PeriodeTypeQuaterly);
		for(Calendar d :dates){
			if(d.get(Calendar.YEAR)!=year)continue;
			if(d.get(Calendar.MONTH)>0 && d.get(Calendar.MONTH)<=3)
				return d;
		}
		
		return null;
	}
	
	public Calendar getQ2Date(int year){
		LinkedList<Calendar> dates=getDateList(FinancialPoint.PeriodeTypeQuaterly);
		for(Calendar d :dates){
			if(d.get(Calendar.YEAR)!=year)continue;
			if(d.get(Calendar.MONTH)>3 && d.get(Calendar.MONTH)<=6)
				return d;
		}
		
		return null;
	}
	
	public Calendar getQ3Date(int year){
		LinkedList<Calendar> dates=getDateList(FinancialPoint.PeriodeTypeQuaterly);
		for(Calendar d :dates){
			if(d.get(Calendar.YEAR)!=year)continue;
			if(d.get(Calendar.MONTH)>6 && d.get(Calendar.MONTH)<=9)
				return d;
		}
		
		return null;
	}
	
	public Calendar getQ4Date(int year){
		LinkedList<Calendar> dates=getDateList(FinancialPoint.PeriodeTypeQuaterly);
		for(Calendar d :dates){
			if(d.get(Calendar.YEAR)!=year)continue;
			if(d.get(Calendar.MONTH)>9 && d.get(Calendar.MONTH)<=12)
				return d;
		}
		
		return null;
	}
	
	public Calendar getNextExpectedDate(String periodType){
		LinkedList<Calendar> allDates=getDateList(periodType);
		Calendar expectedNextValue=Calendar.getInstance();
		if(allDates.size()<2)return expectedNextValue;
		
		Long sum=0L;
		for(int i=0;i<allDates.size()-1;i++){
			sum+=Math.abs(allDates.get(i+1).getTimeInMillis()-allDates.get(i).getTimeInMillis());
		}
		Long mid=sum/(allDates.size()-1);
		
		expectedNextValue.setTimeInMillis(getEffectiveDate(periodType, allDates.getFirst()).getTimeInMillis()+mid);
		
		return expectedNextValue;
		
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
	
	public void setValue(String periodType,Calendar date,String key,String sectorKey,long value){
		if(sectorKey.equals(FIELD_BalanceSheet)){
			for(DatePoint point:BalanceSheet.getPoints(periodType)){
				FinancialPoint p=(FinancialPoint)point;
				if(!p.getDate().equals(date))continue;
				
				p.setValue(key, value);
				
			}
			}
			
			if(sectorKey.equals(FIELD_IncomeStatement)){
			for(DatePoint point:IncomeStatement.getPoints(periodType)){
				FinancialPoint p=(FinancialPoint)point;
				if(!p.getDate().equals(date))continue;
				
				p.setValue(key, value);
				
			}
			}
			
			if(sectorKey.equals(FIELD_CashFlow)){
			for(DatePoint point:CashFlow.getPoints(periodType)){
				FinancialPoint p=(FinancialPoint)point;
				if(!p.getDate().equals(date))continue;
				
				p.setValue(key, value);
				
			}
			}
		
	}
	
	
	public Calendar getEffectiveDate(String periodType,Calendar date){
		for(DatePoint point:IncomeStatement.getPoints(periodType)){
			FinancialPoint p=(FinancialPoint)point;
			if(!p.getDate().equals(date))continue;
			
			return p.getEffectiveDate();
			
		}
		
		return null;
	}
	
	public void setEffectiveDate(String periodType,Calendar date,Calendar effectiveDate){
		
		for(DatePoint point:IncomeStatement.getPoints(periodType)){
			FinancialPoint p=(FinancialPoint)point;
			if(!p.getDate().equals(date))continue;
			
			p.setEffectiveDate(effectiveDate);
			
		}
		
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
