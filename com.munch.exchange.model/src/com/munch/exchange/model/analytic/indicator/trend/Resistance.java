package com.munch.exchange.model.analytic.indicator.trend;

public class Resistance {
	
	public static double[][] compute(double[] prices, int period){
		
		double standardDeviation = StandardDeviation.compute(prices);
		double variance = standardDeviation * standardDeviation;
		
		double[] maxResPos = computeMaxResistance(prices, period);
		double[] minResPos = computeMinResistance(prices, period);
		
		double[] maxResVal = calculateResistanceOfPoints(maxResPos, prices, period, variance);
		double[] minResVal = calculateResistanceOfPoints(minResPos, prices, period, variance);
		
		double[] minMaxDist =  new double[prices.length];
		double[] resVal =  new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			minMaxDist[i] = maxResPos[i] - minResPos[i];
			resVal[i] = (maxResVal[i] + minResVal[i])/2;
		}
		
		double[][] RES =  new double[6][prices.length];
		
		RES[0] = maxResPos;
		RES[1] = minResPos;
		
		RES[2] = maxResVal;
		RES[3] = minResVal;
		
		RES[4] = minMaxDist;
		RES[5] = resVal;
		
		return RES;
	}
	
	
	public static double[] computeMaxResistance(double[] prices, int period){
		double[] maxRes=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			maxRes[i] = prices[i];
			for(int j=0;j<period;j++){
				if(i-j<0)break;
				if(prices[i-j] > maxRes[i]){
					maxRes[i] = prices[i-j];
				}
			}
		}
		
		return maxRes;
	}
	
	public static double[] computeMinResistance(double[] prices, int period){
		double[] minRes=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			minRes[i] = prices[i];
			for(int j=0;j<period;j++){
				if(i-j<0)break;
				if(prices[i-j] < minRes[i]){
					minRes[i] = prices[i-j];
				}
			}
		}
		
		return minRes;
	}
	
	public static double[] calculateResistanceOfPoints(double[] reslinePoints, double[] prices, int period, double variance){
		double[] res = new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			int sum = 0;
			for(int j=0;j<period;j++){
				if(i-j<0)break;
				
				res[i] += calculateResistanceOfPoint(reslinePoints[i-j], prices[i-j], variance);
				sum++;
			}
			
			res[i] /= sum;
		}
		
		return res;
	}
	
	private static double calculateResistanceOfPoint(double reslinePoint, double price, double variance){
		double diff=reslinePoint-price;
		return Math.exp(-diff*diff/variance);
	}
	
	
	
}
