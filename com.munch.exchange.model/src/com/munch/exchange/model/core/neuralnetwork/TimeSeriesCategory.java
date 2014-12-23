package com.munch.exchange.model.core.neuralnetwork;

import java.util.LinkedList;

import com.munch.exchange.model.core.DatePoint;

public enum TimeSeriesCategory {
	ROOT("Root"),
	RATE("Rate"),
	FINANCIAL("Financial");
	
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
			serieNames.add("no");
			
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
		
		return RATE;
	}
	
	public  TimeSeriesCategory createCopy(){
		return fromString(categoryLabel);
	}

}
