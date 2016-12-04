package com.munch.exchange.model.analytic.indicator.signals;

import com.munch.exchange.model.core.ib.IbCommission;

public class ReverseMaxProfitClass {
	
	
	private double localMin;
	private int localMinIndex;
	
	private double localMax;
	private int localMaxIndex;
	
	private double profitLimit;
	private long volume;
	
	private IbCommission commission;
	
	public ReverseMaxProfitClass(double profitLimit, long volume, IbCommission commission) {
		super();
		this.profitLimit = profitLimit;
		this.volume = volume;
		this.commission = commission;
	}

	public double[] compute(double[] price){
		return this.compute(price,price);
	}
	
	public double[] compute(double[] ask, double[] bid){
		if(ask.length!=bid.length)return null;
		if(ask.length==0)return null;
		
		double[] signal=new double[ask.length];
		signal[0] = 0;
		
		localMin = ask[0];
		localMinIndex = 0;
		
		localMax = bid[0];
		localMaxIndex = 0;
		
		signal[0] = 0.0;
		for(int i=1;i<ask.length;i++){
			if(bid[i] > localMax){
				resetMaxValues(bid, i);
				if(signal[i] > 0)
					resetMinValues(ask, i);
			}
			if(ask[i] < localMin){
				resetMinValues(ask, i);
				if(signal[i] < 0)
					resetMaxValues(bid, i);
			}
			
			double MinMaxdiff = Math.abs(localMax-localMin)*volume;
			
			if(MinMaxdiff<2*commission.calculate(volume, (ask[i]+bid[i])/2)){
				signal[i] = signal[i-1];
				continue;
			}
			
			if(localMaxIndex>=localMinIndex){
				resetFromToWith(signal, localMinIndex, localMaxIndex, 1.0);
				resetMinValues(ask, i);
			}
			else{
				resetFromToWith(signal, localMaxIndex, localMinIndex, -1.0);
				resetMaxValues(bid, i);
			}
			
		}
		
//		Remove the signal where the profit is lower that the given limit
		int lastChangeIndex = 0;
		for(int i=1;i<ask.length;i++){
			if(signal[i-1] == signal[i])continue;
			
			double profitDiff = Double.POSITIVE_INFINITY;
			if(signal[i]>0){
				profitDiff = Math.abs(bid[i]-ask[lastChangeIndex])*volume - 2*commission.calculate(volume, (ask[i]+bid[i])/2);
			}
			else{
				profitDiff = Math.abs(ask[i]-bid[lastChangeIndex])*volume - 2*commission.calculate(volume, (ask[i]+bid[i])/2);
			}
			
			if(profitDiff < profitLimit){
				resetFromToWith(signal, lastChangeIndex, i-1, 0);
			}
			
			
			lastChangeIndex = i;
			
		}
		
		
		return signal;
	}
	
	private void resetMinValues(double[] array, int index){
		localMin = array[index];
		localMinIndex = index;
	}
	private void resetMaxValues(double[] array, int index){
		localMax = array[index];
		localMaxIndex = index;
	}
	
	private void resetFromToWith(double[] array, int from, int to, double value){
		for(int i=from;i<=to;i++){
			array[i] = value;
		}
	}

}
