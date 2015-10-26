package com.munch.exchange.model.core.ib.chart.trend;

public abstract class DerivableFunction {
	
	private double[] variables;
	
	
	public DerivableFunction(double[] variables){
		this.variables=variables;
	}
	
	
	public abstract double calculate();
	public abstract double[] calculateGradients();
	
	public double[] getVariables() {
		return variables;
	}
	public void setVariables(double[] variables) {
		this.variables = variables;
	}
	
	
	
	
}
