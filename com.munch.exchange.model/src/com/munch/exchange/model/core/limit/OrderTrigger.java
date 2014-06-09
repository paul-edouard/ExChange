package com.munch.exchange.model.core.limit;

public class OrderTrigger {
	
	private double value=0;
	private double profit=0;
	private LimitRange limitRange=null;
	
	
	public enum TriggerType { TO_BUY, CLOSE_TO_BUY, TO_SELL, CLOSE_TO_SELL, NONE};
	
	public OrderTrigger(double value, double profit, LimitRange limitRange) {
		super();
		this.value = value;
		this.profit = profit;
		this.limitRange = limitRange;
	}
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
	this.profit = profit;
	}
	
	public LimitRange getLimitRange() {
		return limitRange;
	}
	public void setLimitRange(LimitRange limitRange) {
	this.limitRange = limitRange;
	}
	
	public TriggerType calculateTriggerType(double distFromLimit){
		double dist=0;
		if(this.value==0)
			return TriggerType.NONE;
		
		switch (limitRange.getType()) {
		case BUY:
			if(this.value<=limitRange.getLowerLimit().getValue()){
				return TriggerType.TO_BUY;
			}
			
			dist=100*(this.value-limitRange.getLowerLimit().getValue())/this.value;
			if(dist<distFromLimit)
				return TriggerType.CLOSE_TO_BUY;
			
			if(limitRange.getUpperLimit().isActive()){
				dist=100*(limitRange.getUpperLimit().getValue()-this.value)/this.value;
				if(dist<distFromLimit)
					return TriggerType.CLOSE_TO_BUY;
			}
			break;
		case SELL:
			if(this.value>=limitRange.getUpperLimit().getValue()){
				return TriggerType.TO_SELL;
			}
			
			dist=100*(limitRange.getUpperLimit().getValue()-this.value)/this.value;
			if(dist<distFromLimit)
				return TriggerType.CLOSE_TO_SELL;
			
			if(limitRange.getLowerLimit().isActive()){
				dist=100*(this.value-limitRange.getLowerLimit().getValue())/this.value;
				if(dist<distFromLimit)
					return TriggerType.CLOSE_TO_SELL;
			}
			break;
		case NONE:
			return TriggerType.NONE;
		}
		
		return TriggerType.NONE;
	}
	
	@Override
	public String toString() {
		return String.format("%,.2f%%",
				profit * 100)+" "+this.getLimitRange();
	}
	
	
	

}
