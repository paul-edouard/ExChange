package com.munch.exchange.model.core;

import com.munch.exchange.model.tool.DateTool;

public class Indice extends ExchangeRate {
	
	
	

	public Indice() {
		super();
		
		this.setStart(DateTool.StringToDay("1990-01-01"));
		
	}

	@Override
	public String toString() {
		return "Indice [start=" + DateTool.dateToString(start) + ", end=" + DateTool.dateToString(end) + ", name=" + name
				+ ", symbol=" + symbol + ", dataPath=" + dataPath
				+ ", stockExchange=" + stockExchange + ", changes=" + changes
				+ ", parameter=" + parameter + "]";
	}
	
	
	

}
