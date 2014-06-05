package com.munch.exchange.model.core.limit;

public class OrderTrigger {
	
	private double value=0;
	private double profit=0;
	private LimitRange limitRange=null;
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
	
	
	

}
