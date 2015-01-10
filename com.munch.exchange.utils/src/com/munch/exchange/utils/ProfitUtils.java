package com.munch.exchange.utils;

public class ProfitUtils {
	
	
	public static double PENALTY=0.0025;
	public static double SIGNAL_LIMIT=0.0;
	
	
	/**
	 * 
	 * calculate the profit generated from the buy sell signal array.
	 * A signal abow the given limit will send buy order.
	 * 
	 * @param signal
	 * @param changes
	 * @param signalLimit
	 * @param penalty
	 * @return
	 */
	public static double calculate(double[] signal, double[] changes, double signalLimit,double penalty){
		if(signal.length!=changes.length-1)return 0;
		double[] profitArray=calculateArray(signal, changes, signalLimit, penalty);
		return profitArray[profitArray.length-1];
		
	}
	
	public static double[] calculateArray(double[] signal, double[] changes){
		return calculateArray(signal, changes, SIGNAL_LIMIT, PENALTY);
	}
	
	public static double[] calculateArray(double[] signal, double[] changes, double signalLimit,double penalty){
		if(signal.length!=changes.length-1)return null;
		
		double[] profitArray=new double[signal.length];
		double profit=0;
		boolean bought = false;
		
		for(int i=0;i<signal.length;i++){
			if(signal[i]>signalLimit){
				//pay the buy taxes
				if(!bought){
					bought=true;
					profit=profit-penalty*changes[i];
				}
				//Add the win or the looses
				double diff=changes[i+1]-changes[i];
				profit+=diff;
				
			}
			//pay the sell taxes
			else if(bought){
					bought=false;
					profit=profit-penalty*changes[i];
			}			
			profitArray[i]=profit;
		}
		
		return profitArray;
	}
	
	
	public static double calculate(double[] signal, double[] changes){
		return calculate(signal, changes, SIGNAL_LIMIT, PENALTY);
	}
	
	

}
