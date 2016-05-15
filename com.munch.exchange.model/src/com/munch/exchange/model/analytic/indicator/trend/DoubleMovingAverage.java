package com.munch.exchange.model.analytic.indicator.trend;

/**
 * 
 * Double Exponential Moving Average
 * 
 * Double Exponential Moving Average Technical Indicator (DEMA) was developed by
 * Patrick Mulloy and published in February 1994 in the
 * "Technical Analysis of Stocks & Commodities" magazine. It is used for
 * smoothing price series and is applied directly on a price chart of a
 * financial security. Besides, it can be used for smoothing values of other
 * indicators.
 * 
 * The advantage of this indicator is that it eliminates false signals at the
 * saw-toothed price movement and allows saving a position at a strong trend.
 * 
 * You can test the trade signals of this indicator by creating an Expert
 * Advisor in MQL5 Wizard.
 * 
 * Double Exponential Moving Average Calculation
 * 
 * This indicator is based on the Exponential Moving Average (EMA). Let's view
 * the error of price deviation from EMA value:
 * 
 * err(i) = Price(i) - EMA(Price, N, i)
 * 
 * Where:
 * 
 * err(i) — current EMA error; Price(i) — current price; EMA(Price, N, i) —
 * current EMA value of Price series with N period.
 * 
 * Let's add the value of the exponential average error to the value of the
 * exponential moving average of a price and we will receive DEMA:
 * 
 * DEMA(i) = EMA(Price, N, i) + EMA(err, N, i) = EMA(Price, N, i) + EMA(Price -
 * EMA(Price, N, i), N, i) =
 * 
 * = 2 * EMA(Price, N, i) - EMA(Price - EMA(Price, N, i), N, i) = 2 * EMA(Price,
 * N, i) - EMA2(Price, N, i)
 * 
 * Where:
 * 
 * EMA(err, N, i) — current value of the exponential average of error err;
 * EMA2(Price, N, i) — current value of the double consequential smoothing of
 * prices.
 * 
 * @author paul-edouard
 * 
 */

public class DoubleMovingAverage {
	
	
	public static double[] DLWMA(double[] Price, int Period){
		
		double[] DEMA=new double[Price.length];
		double[] LWMA=MovingAverage.LWMA(Price, Period);
		double[] LWMA2=MovingAverage.LWMA(LWMA, Period);
		
		for(int i=0;i<Price.length;i++){
			DEMA[i]=2*LWMA[i]-LWMA2[i];
		}
		return DEMA;
	}
	
	public static double[] DEMA(double[] Price, int Period){
		
		double[] DEMA=new double[Price.length];
		double[] EMA=MovingAverage.EMA(Price, Period);
		double[] EMA2=MovingAverage.EMA(EMA, Period);
		
		for(int i=0;i<Price.length;i++){
			DEMA[i]=2*EMA[i]-EMA2[i];
		}
		return DEMA;
	}
	
	public static double[] DMA(double[] Price, int Period){
		
		double[] DMA=new double[Price.length];
		double[] EMA=MovingAverage.SMA(Price, Period);
		double[] EMA2=MovingAverage.SMA(EMA, Period);
		
		for(int i=0;i<Price.length;i++){
			DMA[i]=2*EMA[i]-EMA2[i];
		}
		return DMA;
	}
	
	public static double[] DSMMA(double[] Price, int Period){
		
		double[] DSMMA=new double[Price.length];
		double[] EMA=MovingAverage.SMMA(Price, Period);
		double[] EMA2=MovingAverage.SMMA(EMA, Period);
		
		for(int i=0;i<Price.length;i++){
			DSMMA[i]=2*EMA[i]-EMA2[i];
		}
		return DSMMA;
	}
	
	
	

}
