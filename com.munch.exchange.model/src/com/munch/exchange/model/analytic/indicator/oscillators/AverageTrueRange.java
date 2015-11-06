package com.munch.exchange.model.analytic.indicator.oscillators;

import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;

/*
 * Average True Range

Average True Range Technical Indicator (ATR) is an indicator that shows volatility of the market.
It was introduced by Welles Wilder in his book "New concepts in technical trading systems".
This indicator has been used as a component of numerous other indicators and trading systems ever since.

Average True Range can often reach a high value at the bottom of the market after a sheer fall in prices
occasioned by panic selling. Low values of the indicator are typical for the periods of sideways movement
of long duration which happen at the top of the market and during consolidation.
Average True Range can be interpreted according to the same principles as other volatility indicators.
The principle of forecasting based on this indicator can be worded the following way: the higher the value of the indicator,
the higher the probability of a trend change; the lower the indicator’s value, the weaker the trend’s movement is.

Average True Range

	###################################
	##        CACULATION             ##
	###################################

True Range is the greatest of the following three values:

    difference between the current maximum and minimum (high and low);
    difference between the previous closing price and the current maximum;
    difference between the previous closing price and the current minimum.

The indicator of Average True Range is a moving average of values of the true range.
 *
 */
public class AverageTrueRange {
	
	private static double[] calculateTR(double[] close, double[] high, double[] low){
		double[] TR=new double[close.length];
		if(close.length==0)return TR;
		
		TR[0]=high[0]-low[0];
		for(int i=1;i<close.length;i++){
			double maxMinDiff=Math.abs(high[i]-low[i]);
			double maxLastCloseDiff=Math.abs(high[i]-close[i-1]);
			double lastCloseMinDiff=Math.abs(close[i-1]-low[i]);
			
			TR[i]=Math.max(maxMinDiff, Math.max(maxLastCloseDiff, lastCloseMinDiff));
		}
		
		return TR;
	}
	
	public static double[] compute(double[] close, double[] high, double[] low, int Period){
		double[] TR=calculateTR(close, high, low);
		return MovingAverage.EMA(TR, Period);
	}
	

}
