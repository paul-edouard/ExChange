package com.munch.exchange.model.core.limit;

public class Limit {
	
	private boolean isActive=false;
	private double value=0;
	
	public Limit(boolean isActive, double value) {
		super();
		this.isActive = isActive;
		this.value = value;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	
	

}
