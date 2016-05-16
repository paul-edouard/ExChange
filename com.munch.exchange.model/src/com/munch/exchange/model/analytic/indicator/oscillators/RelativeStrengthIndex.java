package com.munch.exchange.model.analytic.indicator.oscillators;

import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;

/*
 * 
 * Relative Strength Index

The Relative Strength Index Technical Indicator (RSI) is a price-following oscillator that ranges between 0 and 100. When Wilder introduced the Relative Strength Index, he recommended using a 14-period RSI. Since then, the 9-period and 25-period Relative Strength Index indicators have also gained popularity. A popular method of analyzing the RSI is to look for a divergence in which the security is making a new high, but the RSI is failing to surpass its previous high. This divergence is an indication of an impending reversal. When the Relative Strength Index then turns down and falls below its most recent trough, it is said to have completed a "failure swing". The failure swing is considered a confirmation of the impending reversal.

The following signals of Relative Strength Index are used in chart analyzing:

•Tops and Bottoms
The Relative Strength Index usually tops above 70 and bottoms below 30. It usually forms these tops and bottoms before the underlying price chart.

•Chart Formations
The RSI often forms chart patterns such as head and shoulders or triangles that may be or may not be visible on the price chart.

•Failure Swing (Support or Resistance breakout)
This is where the Relative Strength Index surpasses a previous high (peak) or falls below a recent low (trough).

•Support and Resistance levels
The Relative Strength Index shows, sometimes more clearly than price themselves, levels of support and resistance.

•Divergences
As discussed above, divergences occur when the price makes a new high (or low) that is not confirmed by a new high (or low) in the Relative Strength Index. Prices usually correct and move in the direction of the RSI.

You can test the trade signals of this indicator by creating an Expert Advisor in MQL5 Wizard.

Relative Strength Index
	###################################
	##        CACULATION             ##
	###################################
This is the main formula of Relative Strength Index calculation:

RSI = 100 - (100 / (1 + U / D))

Where:

U — average number of positive price changes;
D — average number of negative price changes.
 * 
 */

public class RelativeStrengthIndex {
	
	
	public static double[] compute(double[] prices, int period){
		
		double[] RSI=new double[prices.length];
		double[] RS=new double[prices.length];
		double[] devPrices=SimpleDerivate.compute(prices);
		
		double[] avgGain=new double[prices.length];
		double[] avgLoss=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
			if(i<=14){
				for(int j=0;j<i;j++){
					if(devPrices[j]>0){
						avgGain[i]+=devPrices[j];
					}
					else{
						avgLoss[i]-=devPrices[j];
					}
				}
				avgGain[i]/=period;
				avgLoss[i]/=period;
			}
			else{
				if(devPrices[i]>0){
					avgGain[i]=avgGain[i-1]*(period-1)+devPrices[i];
					avgGain[i]/=period;
					
					avgLoss[i]=avgLoss[i-1]*(period-1);
					avgLoss[i]/=period;
				}
				else{
					avgLoss[i]=avgLoss[i-1]*(period-1)-devPrices[i];
					avgLoss[i]/=period;
					
					avgGain[i]=avgGain[i-1]*(period-1);
					avgGain[i]/=period;
				}
			}
			
			
//			System.out.println("i="+i+", Avg Loss: "+avgLoss[i]+" Avg Gain: "+avgGain[i]);
			
			if(avgLoss[i]>0)
				RS[i]=avgGain[i]/avgLoss[i];
			else{
				RS[i]=Double.MAX_VALUE;
			}
			
			
//			System.out.println("RS: "+RS[i]);
			if(avgGain[i]==0)
				RSI[i]=0;
			else if(avgLoss[i]==0){
				RSI[i]=100;
			}
			else{
				RSI[i]=100-100/(1+RS[i]);
			}
			
//			System.out.println("RSI: "+RSI[i]);
			
		}
		
		
		return RSI;
		
	}
	
	
	

}
