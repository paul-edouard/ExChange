package com.munch.exchange.model.analytic.indicator.trend;

/**
 * 
 * Ichimoku Kinko Hyo
 * 
 * Ichimoku Kinko Hyo Technical Indicator is predefined to characterize the
 * market Trend, Support and Resistance Levels, and to generate signals of
 * buying and selling. This indicator works best at weekly and daily charts.
 * 
 * When defining the dimension of parameters, four time intervals of different
 * length are used. The values of individual lines composing this indicator are
 * based on these intervals:
 * 
 * •Tenkan-sen shows the average price value during the first time interval
 * defined as the sum of maximum and minimum within this time, divided by two;
 * 
 * •Kijun-sen shows the average price value during the second time interval;
 * 
 * •Senkou Span A shows the middle of the distance between two previous lines
 * shifted forwards by the value of the second time interval;
 * 
 * •Senkou Span B shows the average price value during the third time interval
 * shifted forwards by the value of the second time interval.
 * 
 * Chikou Span shows the closing price of the current candle shifted backwards
 * by the value of the second time interval. The distance between the Senkou
 * lines is hatched with another color and called "cloud". If the price is
 * between these lines, the market should be considered as non-trend, and then
 * the cloud margins form the support and resistance levels.
 * 
 * •If the price is above the cloud, its upper line forms the first support
 * level, and the second line forms the second support level;
 * 
 * •If the price is below cloud, the lower line forms the first resistance
 * level, and the upper one forms the second level;
 * 
 * •If the Chikou Span line traverses the price chart in the bottom-up direction
 * it is signal to buy. If the Chikou Span line traverses the price chart in the
 * top-down direction it is signal to sell.
 * 
 * Kijun-sen is used as an indicator of the market movement. If the price is
 * higher than this indicator, the prices will probably continue to increase.
 * When the price traverses this line the further trend changing is possible.
 * Another kind of using the Kijun-sen is giving signals. Signal to buy is
 * generated when the Tenkan-sen line traverses the Kijun-sen in the bottom-up
 * direction. Top-down direction is the signal to sell. Tenkan-sen is used as an
 * indicator of the market trend. If this line increases or decreases, the trend
 * exists. When it goes horizontally, it means that the market has come into the
 * channel.
 * 
 */

public class IchimokuKinkoHyo {
	
	public static double[][] compute(double[] Price ,int TenkaSenP,int KijunSenP,int SenkouSpanBP){
		
		double[][] IKH=new double[5][Price.length];
		
		double[] TenkanSen=new double[Price.length];
		double[] KijunSen=new double[Price.length];
		double[] SenkouSpanA=new double[Price.length];
		double[] SenkouSpanB=new double[Price.length];
		double[] ChikouSpan=new double[Price.length];
		
		for(int i=0;i<Price.length;i++){
			TenkanSen[i]=(	utils.getHighestPrice(Price, i, 0, TenkaSenP)
							+utils.getLowestPrice(Price, i, 0, TenkaSenP))/2;
			KijunSen[i]=(	utils.getHighestPrice(Price, i, 0, KijunSenP)
							+utils.getLowestPrice(Price, i, 0, KijunSenP))/2;
			if(i+KijunSenP<Price.length){
				SenkouSpanA[i+KijunSenP]=(TenkanSen[i]+KijunSen[i])/2;
				SenkouSpanB[i+KijunSenP]=(	utils.getHighestPrice(Price, i, 0, SenkouSpanBP)
											+utils.getLowestPrice(Price, i, 0, SenkouSpanBP))/2;
			}
			if(i-KijunSenP>0)
				ChikouSpan[i-KijunSenP]=Price[i];
			
		}
		
		IKH[0]=TenkanSen;
		IKH[1]=KijunSen;
		IKH[2]=SenkouSpanA;
		IKH[3]=SenkouSpanB;
		IKH[4]=ChikouSpan;
		
		return IKH;
	}
	

}
