package com.munch.exchange.model.core;

public class EconomicData extends ExchangeRate {

	
	private String frequency;
	private String frequencyShort;
	
	private String units;
	private String unitsShort;
	
	private String seasonalAdjustment;
	private String seasonalAdjustmentShort;
	
	private String lastUpdated;
	private String popularity;
	private String notes;
	
	
	public EconomicData() {
		super();
		this.stockExchange="St. Louis FED";
	}
	
	
	

}
