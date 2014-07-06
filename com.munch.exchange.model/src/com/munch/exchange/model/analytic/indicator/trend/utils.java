package com.munch.exchange.model.analytic.indicator.trend;

public class utils {
	
	public static double getHighestPrice(double[] Price,int i,int from, int to){
		double h=Double.NEGATIVE_INFINITY;
		for(int j=from;j<to;j++){
			if((i-j)<0)continue;
			if(Price[i-j]>h)
				h=Price[i-j];
		}
		if(h==Double.NEGATIVE_INFINITY)return 0;
		return h;
	}
	
	public static double getLowestPrice(double[] Price,int i,int from, int to){
		double l=Double.POSITIVE_INFINITY;
		for(int j=from;j<to;j++){
			if((i-j)<0)continue;
			if(Price[i-j]<l)
				l=Price[i-j];
		}
		if(l==Double.POSITIVE_INFINITY)return 0;
		return l;
	}

}
