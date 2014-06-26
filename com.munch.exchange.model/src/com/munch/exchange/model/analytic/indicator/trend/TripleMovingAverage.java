package com.munch.exchange.model.analytic.indicator.trend;

/**
 * 
 * Triple Exponential Moving Average
 * 
 * Triple Exponential Moving Average Technical Indicator (TEMA) was developed by
 * Patrick Mulloy and published in the
 * "Technical Analysis of Stocks & Commodities" magazine. The principle of its
 * calculation is similar to DEMA (Double Exponential Moving Average). The name
 * "Triple Exponential Moving Average" does not very correctly reflect its
 * algorithm. This is a unique blend of the single, double and triple
 * exponential moving average providing the smaller lag than each of them
 * separately.
 * 
 * TEMA can be used instead of traditional moving averages. It can be used for
 * smoothing price data, as well as for smoothing other indicators.
 * 
 * You can test the trade signals of this indicator by creating an Expert
 * Advisor in MQL5 Wizard.
 * 
 * Triple Exponential Moving Average Calculation
 * 
 * First DEMA is calculated, then the error of price deviation from DEMA is
 * calculated:
 * 
 * err(i) = Price(i) — DEMA(Price, N, ii)
 * 
 * Where:
 * 
 * err(i) — current DEMA error; Price(i) — current price; DEMA(Price, N, i) —
 * current DEMA value from Price series with N period.
 * 
 * Then add value of the exponential average of the error and get TEMA:
 * 
 * TEMA(i) = DEMA(Price, N, i) + EMA(err, N, i) = DEMA(Price, N, i) + EMA(Price
 * - EMA(Price, N, i), N, i) =
 * 
 * = DEMA(Price, N, i) + EMA(Price - DEMA(Price, N, i), N, i) = 3 * EMA(Price,
 * N, i) - 3 * EMA2(Price, N, i) + EMA3(Price, N, i)
 * 
 * Where:
 * 
 * EMA(err, N, i) — current value of the exponential average of the err error;
 * EMA2(Price, N, i) — current value of the double sequential price smoothing;
 * EMA3(Price, N, i) — current value of the triple sequential price smoothing.
 * 
 * @author paul-edouard
 * 
 */

public class TripleMovingAverage {
	
	
	public static double[] computeLWMA(double[] Price, int Period){
		
		double[] DEMA=new double[Price.length];
		double[] LWMA=MovingAverage.LWMA(Price, Period);
		double[] LWMA2=MovingAverage.LWMA(LWMA, Period);
		double[] LWMA3=MovingAverage.LWMA(LWMA2, Period);
		
		for(int i=0;i<Price.length;i++){
			DEMA[i]=3*LWMA[i]-3*LWMA2[i]+LWMA3[i];
		}
		return DEMA;
	}
	
	public static double[] computeEMA(double[] Price, int Period){
		
		double[] DEMA=new double[Price.length];
		double[] EMA=MovingAverage.EMA(Price, Period);
		double[] EMA2=MovingAverage.EMA(EMA, Period);
		double[] EMA3=MovingAverage.EMA(EMA2, Period);
		
		for(int i=0;i<Price.length;i++){
			DEMA[i]=3*EMA[i]-3*EMA2[i]+EMA3[i];
		}
		return DEMA;
	}

}
