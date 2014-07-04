package com.munch.exchange.model.analytic.indicator.trend;

/**
 * Envelopes
 * 
 * Envelopes Technical Indicator is formed with two Moving Averages, one of
 * which is shifted upward and another one is shifted downward. The selection of
 * optimum relative number of band margins shifting is determined with the
 * market volatility: the higher the latter is, the stronger the shift is.
 * 
 * Envelopes define the upper and the lower margins of the price range. Signal
 * to sell appears when the price reaches the upper margin of the band; signal
 * to buy appears when the price reaches the lower margin.
 * 
 * The logic behind envelopes is that overzealous buyers and sellers push the
 * price to the extremes (i.e., the upper and lower bands), at which point the
 * prices often stabilize by moving to more realistic levels. This is similar to
 * the interpretation of Bollinger Bands® (BB).
 * 
 * You can test the trade signals of this indicator by creating an Expert
 * Advisor in MQL5 Wizard.
 * 
 * Envelopes Calculation
 * 
 * UPPER BAND = SMA (CLOSE, N) * [1 + K / 1000]
 * 
 * LOWER BAND = SMA (CLOSE, N) * [1 - K / 1000]
 * 
 * Where:
 * 
 * UPPER BAND — upper line of the indicator; LOWER BAND — lower line of the
 * indicator; SMA — Simple Moving Average; CLOSE — close price; N — period of
 * averaging; K / 1000 — the value of shifting from the average (measured in
 * basis points).
 * 
 * @author paul-edouard
 * 
 */

public class Envelopes {
	
	public static double[][] compute(double[] Price ,int N, double K){
		
		double[][] ENV=new double[2][Price.length];
		double[] ML=MovingAverage.SMA(Price, N);
		
		double[] UPPER_BAND=new double[Price.length];
		double[] LOWER_BAND=new double[Price.length];
		
		for(int i=0;i<Price.length;i++){
			UPPER_BAND[i]=ML[i]*(1+K/1000);
			LOWER_BAND[i]=ML[i]*(1-K/1000);
		}
		
		ENV[0]=UPPER_BAND;
		ENV[1]=LOWER_BAND;
		
		return ENV;
	}

}
