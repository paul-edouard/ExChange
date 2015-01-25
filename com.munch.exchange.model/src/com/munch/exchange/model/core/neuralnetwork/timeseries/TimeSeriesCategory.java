package com.munch.exchange.model.core.neuralnetwork.timeseries;

import java.util.LinkedList;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;

public enum TimeSeriesCategory {
	ROOT("Root"),
	RATE("Rate"),
	FINANCIAL("Financial"),
	TARGET_OUTPUT("Target Output");
	
	private String categoryLabel;
	
	private TimeSeriesCategory(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}
	
	public String getCategoryLabel() {
		return categoryLabel;
	}
	
	public LinkedList<String> getAvailableSerieNames(){
		LinkedList<String> serieNames=new LinkedList<String>();
		
		switch (this) {
		case RATE:
			serieNames.add(DatePoint.FIELD_Close);
			serieNames.add(DatePoint.FIELD_High);
			serieNames.add(DatePoint.FIELD_Low);
			serieNames.add(DatePoint.FIELD_Open);
			serieNames.add(DatePoint.FIELD_Volume);
			serieNames.add(DatePoint.FIELD_Adj_Close);
			break;
		case FINANCIAL:
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_TotalRevenue);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_NetIncome);
			break;
		case TARGET_OUTPUT:
			serieNames.add("Desired Output");
			break;

		default:
			break;
		}
		
		
		return serieNames;
	}
	
	
	public static TimeSeriesCategory fromString(String input){
		if(input.equals("Rate")){
			return RATE;
		}
		else if(input.equals("Financial")){
			return FINANCIAL;
		}
		else if(input.equals("Root")){
			return ROOT;
		}
		else if(input.equals("Target Output")){
			return TARGET_OUTPUT;
		}
		
		return RATE;
	}
	
	public  TimeSeriesCategory createCopy(){
		return fromString(categoryLabel);
	}

}
