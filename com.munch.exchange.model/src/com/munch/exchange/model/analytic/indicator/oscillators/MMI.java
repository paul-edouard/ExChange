package com.munch.exchange.model.analytic.indicator.oscillators;

import com.munch.exchange.model.analytic.functions;

/*
 * 
 * 
 * var MMI(var* Data,int TimePeriod)
{
	TimePeriod = Min(TimePeriod,1000);
	var m = Median(Data,TimePeriod);
	TimePeriod = Min(TimePeriod,g->nBar);
	if(TimePeriod <= 2) return 75;
	int i, nh=0, nl=0;
	for(i=1; i<TimePeriod; i++) {
		if(Data[i] > m && Data[i] > Data[i-1])
			nl++;
		else if(Data[i] < m && Data[i] < Data[i-1])
			nh++;
	}
	return 100.*(nl+nh)/(TimePeriod-1);
}
 * 
 */
public class MMI {
	
	
	public static double[] compute(double[] prices, int timePeriod){
		
		double[] mmi = new double[prices.length];
		if(timePeriod<=2)return mmi;
		
		double[] median = functions.median(prices, timePeriod);
		
		int nh = 0;
		int nl = 0;
		int pos = 0;
		
		for(int i = 0; i < prices.length; i++){
			nh = 0;
			nl = 0;
			
			for(int j = 0; j < timePeriod ; j++){
				pos = i - j ;
				if(pos <1)continue;
				
				if(prices[pos] > median[i] && prices[pos] > prices[pos-1]){
					nl++;
				}
				else if(prices[pos] < median[i] && prices[pos] < prices[pos-1]){
					nh++;
				}
			}
			
			mmi[i] = 100.*(nl+nh)/(timePeriod-1);
		}
		
		
		return mmi;
		
	}
			

}
