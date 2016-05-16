package com.munch.exchange.model.analytic.indicator.oscillators;

import com.munch.exchange.model.analytic.indicator.trend.DoubleMovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.TripleMovingAverage;

/*
 *
 *MACD

Moving Average Convergence/Divergence (MACD) is a trend-following dynamic indicator.
It indicates the correlation between two Moving Averages of a price.

The Moving Average Convergence/Divergence (MACD) Technical Indicator is the difference between
a 26-period and 12-period Exponential moving averages (EMA). In order to clearly show buy/sell
opportunities, a so-called signal line (9-period moving average of the indicator) is plotted on the MACD chart.

The MACD proves most effective in wide-swinging trading markets. There are three popular ways
to use the Moving Average Convergence/Divergence: crossovers, overbought/oversold conditions, and divergences.

*Crossovers

The basic MACD trading rule is to sell when the MACD falls below its signal line.
Similarly, a buy signal occurs when the Moving Average Convergence/Divergence rises above its signal line.
It is also popular to buy/sell when the MACD goes above/below zero.

*Overbought/Oversold Conditions

The MACD is also useful as an overbought/oversold indicator. When the shorter moving average pulls away
dramatically from the longer moving average (i.e., the MACD rises), it is likely that the symbol price is
overextending and will soon return to more realistic levels.

*Divergence

An indication that an end to the current trend may be near occurs when the MACD diverges from the symbol.
A bullish divergence occurs when the Moving Average Convergence/Divergence indicator is making new highs
while prices fail to reach new highs. A bearish divergence occurs when the MACD is making new lows while
prices fail to reach new lows. Both of these divergences are most significant when they occur at relatively
overbought/oversold levels.

You can test the trade signals of this indicator by creating an Expert Advisor in MQL5 Wizard.

Moving Average Convergence/Divergence

	###################################
	##        CACULATION             ##
	###################################

The MACD is calculated by subtracting the value of a 26-period exponential moving average from a 12-period exponential moving average. A 9-period dotted simple moving average of the MACD (the signal line) is then plotted on top of the MACD.

MACD = EMA(CLOSE, 12) - EMA(CLOSE, 26)

SIGNAL = SMA(MACD, 9)

Where:

EMA — Exponential Moving Average;
SMA — Simple Moving Average;
SIGNAL — the signal line of the indicator.
 * 
 * 
 */
public class MACD {
	
	
	
	public static String[] algorithms=new String[]{"EMA", "LWMA", "DEMA", "DLWMA", "TEMA", "TLWMA"};
	public static String defaultAlgorithm=algorithms[0];
	
	
	public static double[][] compute(String algorithm,double[] prices, int slowMA, int fastMA, int periodSMA){
		if(algorithm.equals("EMA")){
			return computeWithEMA(prices, slowMA, fastMA, periodSMA);
		}
		else if(algorithm.equals("LWMA")){
			return computeWithLWMA(prices, slowMA, fastMA, periodSMA);
		}
		else if(algorithm.equals("DEMA")){
			return computeWithDEMA(prices, slowMA, fastMA, periodSMA);
		}
		else if(algorithm.equals("DLWMA")){
			return computeWithDLWMA(prices, slowMA, fastMA, periodSMA);
		}
		else if(algorithm.equals("TEMA")){
			return computeWithTEMA(prices, slowMA, fastMA, periodSMA);
		}
		else if(algorithm.equals("TLWMA")){
			return computeWithTLWMA(prices, slowMA, fastMA, periodSMA);
		}
		
		return computeWithEMA(prices, slowMA, fastMA, periodSMA);
	}
	
	
	/**
	 * compute the MACD with the Exponential Moving Average (EMA) algorithm
	 * 
	 * @param prices
	 * @param slowEMA
	 * @param fastEMA
	 * @param periodSMA
	 * @return
	 */
	public static double[][] computeWithEMA(double[] prices, int slowEMA, int fastEMA, int periodSMA){
		double[] MACD=new double[prices.length];
		double[] signal=new double[prices.length];
		
		double[] S_MA=MovingAverage.EMA(prices, slowEMA);
		double[] F_MA=MovingAverage.EMA(prices, fastEMA);
		
		for(int i=0;i<prices.length;i++){
			MACD[i]=F_MA[i]-S_MA[i];
		}
		
		signal=MovingAverage.SMA(MACD, periodSMA);
		
		double[][] R=new double[4][prices.length];
		R[0]=MACD;
		R[1]=signal;
		R[2]=S_MA;
		R[3]=F_MA;
		
		
		return R;
	}
	
	
	/**
	 * 
	 * compute the MACD with the Linear Weighted Moving Average (LWMA) algorithm
	 * 
	 * @param prices
	 * @param slowLWMA
	 * @param fastLWMA
	 * @param periodSMA
	 * @return
	 */
	public static double[][] computeWithLWMA(double[] prices, int slowLWMA, int fastLWMA, int periodSMA){
		double[] MACD=new double[prices.length];
		double[] signal=new double[prices.length];
		
		double[] S_MA=MovingAverage.LWMA(prices, slowLWMA);
		double[] F_MA=MovingAverage.LWMA(prices, fastLWMA);
		
		for(int i=0;i<prices.length;i++){
			MACD[i]=F_MA[i]-S_MA[i];
		}
		
		signal=MovingAverage.SMA(MACD, periodSMA);
		
		double[][] R=new double[4][prices.length];
		R[0]=MACD;
		R[1]=signal;
		R[2]=S_MA;
		R[3]=F_MA;
		
		
		return R;
	}
	
	
	
	/**
	 * compute the MACD with the Double Exponential Moving Average (DEMA) algorithm
	 * 
	 * @param prices
	 * @param slowEMA
	 * @param fastEMA
	 * @param periodSMA
	 * @return
	 */
	public static double[][] computeWithDEMA(double[] prices, int slowEMA, int fastEMA, int periodSMA){
		double[] MACD=new double[prices.length];
		double[] signal=new double[prices.length];
		
		double[] S_MA=DoubleMovingAverage.DEMA(prices, slowEMA);
		double[] F_MA=DoubleMovingAverage.DEMA(prices, fastEMA);
		
		for(int i=0;i<prices.length;i++){
			MACD[i]=F_MA[i]-S_MA[i];
		}
		
		signal=MovingAverage.SMA(MACD, periodSMA);
		
		double[][] R=new double[4][prices.length];
		R[0]=MACD;
		R[1]=signal;
		R[2]=S_MA;
		R[3]=F_MA;
		
		
		return R;
	}
	
	
	/**
	 * 
	 * compute the MACD with the Double Linear Weighted Moving Average (DLWMA) algorithm
	 * 
	 * @param prices
	 * @param slowLWMA
	 * @param fastLWMA
	 * @param periodSMA
	 * @return
	 */
	public static double[][] computeWithDLWMA(double[] prices, int slowLWMA, int fastLWMA, int periodSMA){
		double[] MACD=new double[prices.length];
		double[] signal=new double[prices.length];
		
		double[] S_MA=DoubleMovingAverage.DLWMA(prices, slowLWMA);
		double[] F_MA=DoubleMovingAverage.DLWMA(prices, fastLWMA);
		
		for(int i=0;i<prices.length;i++){
			MACD[i]=F_MA[i]-S_MA[i];
		}
		
		signal=MovingAverage.SMA(MACD, periodSMA);
		
		double[][] R=new double[4][prices.length];
		R[0]=MACD;
		R[1]=signal;
		R[2]=S_MA;
		R[3]=F_MA;
		
		return R;
	}
	
	
	
	/**
	 * compute the MACD with the Double Exponential Moving Average (DEMA) algorithm
	 * 
	 * @param prices
	 * @param slowEMA
	 * @param fastEMA
	 * @param periodSMA
	 * @return
	 */
	public static double[][] computeWithTEMA(double[] prices, int slowEMA, int fastEMA, int periodSMA){
		double[] MACD=new double[prices.length];
		double[] signal=new double[prices.length];
		
		double[] S_MA=TripleMovingAverage.TEMA(prices, slowEMA);
		double[] F_MA=TripleMovingAverage.TEMA(prices, fastEMA);
		
		for(int i=0;i<prices.length;i++){
			MACD[i]=F_MA[i]-S_MA[i];
		}
		
		signal=MovingAverage.SMA(MACD, periodSMA);
		
		double[][] R=new double[4][prices.length];
		R[0]=MACD;
		R[1]=signal;
		R[2]=S_MA;
		R[3]=F_MA;
		
		
		return R;
	}
	
	
	/**
	 * 
	 * compute the MACD with the Double Linear Weighted Moving Average (DLWMA) algorithm
	 * 
	 * @param prices
	 * @param slowLWMA
	 * @param fastLWMA
	 * @param periodSMA
	 * @return
	 */
	public static double[][] computeWithTLWMA(double[] prices, int slowLWMA, int fastLWMA, int periodSMA){
		double[] MACD=new double[prices.length];
		double[] signal=new double[prices.length];
		
		double[] S_MA=TripleMovingAverage.TLWMA(prices, slowLWMA);
		double[] F_MA=TripleMovingAverage.TLWMA(prices, fastLWMA);
		
		for(int i=0;i<prices.length;i++){
			MACD[i]=F_MA[i]-S_MA[i];
		}
		
		signal=MovingAverage.SMA(MACD, periodSMA);
		
		double[][] R=new double[4][prices.length];
		R[0]=MACD;
		R[1]=signal;
		R[2]=S_MA;
		R[3]=F_MA;
		
		
		return R;
	}
	
	
	
}
