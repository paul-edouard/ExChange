package com.munch.exchange.model.core.neuralnetwork;


public enum PeriodType {
	DAY(24*60*60*1000),
	HOUR(60*60*1000),
	MINUTE(60*1000),
	SECONDE(1000);
	
	private int period;
	
	private PeriodType(int period) {
		this.period = period;
	}
	public int getPeriod() {
		return this.period;
	}
	
	public static PeriodType fromString(String input){
		if(input.equals("DAY")){
			return DAY;
		}
		else if(input.equals("HOUR")){
			return HOUR;
		}
		else if(input.equals("MINUTE")){
			return MINUTE;
		}
		else if(input.equals("SECONDE")){
			return SECONDE;
		}
		
		return DAY;
	}
	
	public static String toString(PeriodType type){
		switch (type) {
		case DAY:
			return "DAY";
		case HOUR:
			return "HOUR";
		case MINUTE:
			return "MINUTE";
		case SECONDE:
			return "SECONDE";
			

		default:
			return "DAY";
		}
	}
	
}
