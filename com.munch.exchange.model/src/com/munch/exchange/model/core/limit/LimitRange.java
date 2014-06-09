package com.munch.exchange.model.core.limit;

public class LimitRange {
	
	private Limit upperLimit;
	private Limit lowerLimit;
	private LimitRangeType type=LimitRangeType.NONE;
	
	public enum LimitRangeType { BUY, SELL, NONE};
	
	public LimitRange(Limit upperLimit, Limit lowerLimit) {
		super();
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
	}
	
	public LimitRange(Limit upperLimit, Limit lowerLimit, LimitRangeType type) {
		super();
		this.upperLimit = upperLimit;
		this.lowerLimit = lowerLimit;
		this.type = type;
	}
	

	public LimitRangeType getType() {
		return type;
	}

	public Limit getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(Limit upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public Limit getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(Limit lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
	private String typeToString(){
		switch (type) {
		case BUY:
			return "Buy";
		case SELL:
			return "Sell";
		case NONE:
			return "";
		}
			
		return "";
	}

	@Override
	public String toString() {
		
		String limits_str="["+
				String.format("%,.3f",upperLimit.getValue())+
				((upperLimit.isActive())?"*":"")+
				", "+
				String.format("%,.3f",lowerLimit.getValue())+
				((lowerLimit.isActive())?"*":"")+
				"]";
		
		
		
		
		return typeToString()+" "+limits_str;
	}
	
	
	

}
