package com.munch.exchange.model.analytic.indicator.trend;

import java.util.LinkedList;

public class Resistance {
	
	public static double[][] compute(double[] high,double[] low, int period, double range, int maxResSearchPeriod){
				
		double[] maxResPos = computeMaxResistance(high, period, maxResSearchPeriod);
		double[] minResPos = computeMinResistance(low, period, maxResSearchPeriod);
		
		double[] maxResVal = computeMaxResistanceValues( high, maxResPos, range, maxResSearchPeriod);
		double[] minResVal = computeMinResistanceValues( low, minResPos, range, maxResSearchPeriod);
		
		double[] maxBreakoutVal = new double[high.length];
		double[] minBreakoutVal = new double[low.length];
		
		double[] minMaxDist =  new double[high.length];
		double[] resVal =  new double[high.length];
		
		
		for(int i=0;i<high.length;i++){			
			
			minMaxDist[i] = maxResPos[i] - minResPos[i];
			
			if(high[i] - maxResPos[i] > 0 )
				maxBreakoutVal[i] = (high[i] - maxResPos[i]) * maxResVal[i];
			
			if(minResPos[i] - low[i] > 0 )
				minBreakoutVal[i] = (minResPos[i] - low[i]) * minResVal[i];
			
			resVal[i] = (maxResVal[i] + minResVal[i])/2;
		}
		
		double[][] RES =  new double[8][high.length];
		
		RES[0] = maxResPos;
		RES[1] = minResPos;
		
		RES[2] = maxResVal;
		RES[3] = minResVal;
		
		RES[4] = minMaxDist;
		RES[5] = resVal;
		
		RES[6] = maxBreakoutVal;
		RES[7] = minBreakoutVal;
		
		return RES;
	}
	
	public static double[] computeMaxResistanceValues(double[] prices, double[] maxResPos, double range, int maxResSearchPeriod){
		double[] maxResValues=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			double resVal = 0;
			int pos = i;
			int nbOfTops = 0;
			while(pos > 1 && (i-pos)<=maxResSearchPeriod){
				pos--;
				if(maxResPos[i]==0)break;
				
				if(prices[pos-1]<=prices[pos] && prices[pos] >= prices[pos+1]){
					double topPos = (maxResPos[i]-prices[pos])/maxResPos[i];
//					System.out.println("Top Pos: "+topPos+", Range: "+range);
					
					if(topPos < -range)break;
					
					if(topPos > range)continue;
					
					nbOfTops++;
//					resVal += (i-pos) * nbOfTops;
					resVal += (i-pos);
				}
			}
			
			if(nbOfTops > 1)
				maxResValues[i]=resVal;
			
		}
		
		return maxResValues;
	}
	
	public static double[] computeMinResistanceValues(double[] prices, double[] minResPos, double range, int maxResSearchPeriod){
		double[] minResValues=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			double resVal = 0;
			int pos = i;
			int nbOfTops = 0;
			while(pos > 1 && (i-pos)<=maxResSearchPeriod){
				pos--;
				if(minResPos[i]==0)break;
				
				if(prices[pos-1]>=prices[pos] && prices[pos] <= prices[pos+1]){
					double topPos = (prices[pos]-minResPos[i])/minResPos[i]; 
//					System.out.println("Min Pos: "+topPos+", range: "+range);
					if(topPos < -range)break;
					
					if(topPos > range)continue;
					
					nbOfTops++;
//					resVal += (i-pos) * nbOfTops;
					resVal += (i-pos);
				}
			}
			if(nbOfTops > 1)
				minResValues[i]=resVal;
			
		}
		
		return minResValues;
	}
	
	
		
	

	public static double[] computeMaxResistance(double[] prices, int period, int maxResSearchPeriod){
		double[] maxRes=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			
			double[] tops = extractTopFrom(prices, i, period, maxResSearchPeriod);
			if(tops!=null){
			for(int j=0;j<tops.length;j++){
				if(tops[j] > maxRes[i]){
					maxRes[i] = tops[j];
				}
			}
			}
			
			if(maxRes[i]==0)
				maxRes[i] = prices[i];
			
			
			
			
//			for(int j=0;j<period;j++){
//				if(i-j<0)break;
//				if(prices[i-j] > maxRes[i]){
//					maxRes[i] = prices[i-j];
//				}
//			}
		}
		
		return maxRes;
	}
	
	public static double[] computeMinResistance(double[] prices, int period, int maxResSearchPeriod){
		double[] minRes=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			 minRes[i]= Double.MAX_VALUE;
			double[] bottoms = extractBottomFrom(prices, i, period, maxResSearchPeriod);
			if(bottoms!=null){
			for(int j=0;j<bottoms.length;j++){
				if(bottoms[j] < minRes[i]){
					minRes[i] = bottoms[j];
				}
			}
			}
			
			if(minRes[i]==Double.MAX_VALUE)
				minRes[i] = prices[i];
			
			
			
			
//			minRes[i] = prices[i];
//			for(int j=0;j<period;j++){
//				if(i-j<0)break;
//				if(prices[i-j] < minRes[i]){
//					minRes[i] = prices[i-j];
//				}
//			}
		}
		
		return minRes;
	}
	
	public static double[] calculateResistanceOfPoints(double[] reslinePoints, double[] prices, int period, double variance){
		double[] res = new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			int sum = 0;
			for(int j=0;j<period;j++){
				if(i-j<0)break;
				
				res[i] += calculateResistanceOfPoint(reslinePoints[i], prices[i-j], variance);
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
	
	
	private static double[] extractTopFrom(double[] prices, int from, int nbOfTops, int maxResSearchPeriod){
		LinkedList<Double> tops=new LinkedList<Double>(); 
		int pos = from;
		if(pos>=prices.length)return null;
		
		while(pos > 1 && tops.size()<nbOfTops && (from - pos)<=maxResSearchPeriod){
			pos--;
			if(prices[pos-1]<=prices[pos] && prices[pos] >= prices[pos+1]){
				tops.add(0, prices[pos]);
			}
		}
		
//		Convert Double to double
		Double[] d=tops.toArray(new Double[0]);
		double[] dd = new double[d.length];
		
		for(int i=0;i<d.length;i++){
			dd[i] = d[i];
		}
		
		
		return dd;
		
	}
	
	private static double[] extractBottomFrom(double[] prices, int from, int nbOfBotttoms, int maxResSearchPeriod){
		LinkedList<Double> bottoms=new LinkedList<Double>(); 
		int pos = from;
		if(pos>=prices.length)return null;
		
		while(pos > 1 && bottoms.size()<nbOfBotttoms && (from - pos)<=maxResSearchPeriod){
			pos--;
			if(prices[pos-1]>=prices[pos] && prices[pos] <= prices[pos+1]){
				bottoms.add(0, prices[pos]);
			}
		}
		
//		Convert Double to double
		Double[] d=bottoms.toArray(new Double[0]);
		double[] dd = new double[d.length];
		
		for(int i=0;i<d.length;i++){
			dd[i] = d[i];
		}
		
		
		return dd;
		
	}
	
	
}
