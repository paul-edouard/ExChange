package com.munch.exchange.model.analytic.indicator.signals;

public class SimpleDerivate {
	
	
	public static double[] compute(double[] Price){
		
		double[] SD=new double[Price.length];
		SD[0]=0;
		for(int i=1;i<Price.length;i++){
			SD[i]=Price[i]-Price[i-1];
		}
		
		return SD;
		
	}
	

}
