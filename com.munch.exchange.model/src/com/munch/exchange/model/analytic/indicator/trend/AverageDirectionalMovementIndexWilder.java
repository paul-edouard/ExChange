package com.munch.exchange.model.analytic.indicator.trend;

/**
 * 
 * Average Directional Movement Index Wilder (ADX Wilder) helps to determine if
 * there is a price trend. This technical indicator is constructed as a strict
 * correspondence with the algorithm described by Welles Wilder in his book
 * "New concepts in technical trading systems".
 * 
 * Trading rules of this indicator are described in the section
 * "Average Directional Movement Index".
 * 
 * Average Directional Movement Index Wilder Calculation
 * 
 * First positive (dm_plus) and negative (dm_minus) changes at each bar are
 * calculated, as well as the true range tr:
 * 
 * If High(i) - High(i-1) > 0 dm_plus(i) = High[(i) - High(i-1), otherwise
 * dm_plus(i) = 0. If Low(i-1) - Low(i) > 0 dm_minus(i) = Low(i-1) - Low(i),
 * otherwise dm_minus(i) = 0.
 * 
 * tr(i) = Max(ABS(High(i) - High(i-1)), ABS(High(i) - Close(i-1)), ABS(Low(i) -
 * Close(i-1)))
 * 
 * Where:
 * 
 * High(i) — maximal price of the current bar; Low(i) — minimal price of the
 * current bar; High(i-1) — maximal price of the previous bar; Low(i-1) —
 * minimal price of the previous bar; Close(i-1) — close price of the previous
 * bar; Max (a, b , c) — maximal value out of three numbers: a, b and c; ABS(X)
 * — value of the number X absolute in its module.
 * 
 * After that smoothed values are calculated: Plus_D(i), Minus_D(i) and ATR():
 * 
 * ATR(i) = SMMA(tr, Period_ADX,i)
 * 
 * Plus_D(i) = SMMA(dm_plus, Period_ADX,i)/ATR(i)*100
 * 
 * Minus_D(i) = SMMA(dm_minus, Period_ADX,i)/ATR(i)*100
 * 
 * Where:
 * 
 * SMMA(X, N, i) — Smoothed Moving Average by values of X series on the current
 * bar; Period_ADX — number of periods used for calculation.
 * 
 * Now Directional Movement Index - DX(i) - is calculated:
 * 
 * DX(i) = ABS(Plus_D(i) - Minus_D(i))/(Plus_D(i) + Minus_D(i)) * 100
 * 
 * After preliminary calculations we obtain the value of the ADX(i) indicator on
 * the current bar by smoothing DX index values:
 * 
 * ADX(i) = SMMA(DX, Period_ADX, i)
 * 
 * @author paul-edouard
 * 
 */
public class AverageDirectionalMovementIndexWilder {
	
	private static double[] dmPlus( double[] High){
		double[] dm_plus=new double[High.length];
		
		for(int i=1;i<High.length;i++){
			double abs=High[i] - High[i-1];
			if(abs > 0){
				dm_plus[i]=abs;
			}
		}
		
		return dm_plus;
	}
	
	private static double[] dmMinus( double[] Low){
		double[] dm_minus=new double[Low.length];
		
		for(int i=1;i<Low.length;i++){
			double abs=Low[i-1] - Low[i];
			if(abs > 0){
				dm_minus[i]=abs;
			}
		}
		
		return dm_minus;
	}
	
	private static double[] trueRange( double[] Close, double[] High, double[] Low){
		double[] tr=new double[Low.length];
		
		for(int i=1;i<Low.length;i++){
			double absHigh=Math.abs(High[i] - Low[i]);
			double absHighLastClose=Math.abs(High[i] - Close[i-1]);
			double absLowLastClose=Math.abs(Low[i] - Close[i-1]);
			
			
			tr[i]=Math.max(absHigh, Math.max(absHighLastClose,absLowLastClose));
			
		}
		
		return tr;
	}
	
	
	
	public static double[] computeADXWi(double[] Close, double[] High, double[] Low ,int Period){
		
		double[] DX=new double[Close.length];
		
		double[] dm_plus=dmPlus(High);
		double[] dm_minus=dmMinus(Low);
		double[] tr=trueRange(Close,High,Low);
		
		double[] ATR=MovingAverage.SMMA(tr,Period);
		
		double[] Plus_D=MovingAverage.SMMA(dm_plus,Period);
		for(int i=1;i<Low.length;i++){
			Plus_D[i]=Plus_D[i]/ATR[i]*100;
		}
		
		double[] Minus_D=MovingAverage.SMMA(dm_minus,Period);
		for(int i=1;i<Low.length;i++){
			Minus_D[i]=Minus_D[i]/ATR[i]*100;
		}
		
		
		//DX(i) = ABS(Plus_D(i) - Minus_D(i))/(Plus_D(i) + Minus_D(i)) * 100
		for(int i=1;i<Low.length;i++){
			DX[i]=Math.abs(Plus_D[i]-Minus_D[i])/(Plus_D[i]+Minus_D[i])*100;
			//System.out.println("DX: "+DX[i]+", Plus_D: "+Plus_D[i]+", Minus_D: "+Minus_D[i]);
			//System.out.println("tr: "+tr[i]+", dm_plus: "+dm_plus[i]+", dm_minus: "+dm_minus[i]);
		}
		
		double[] ADX=MovingAverage.SMMA(DX,Period);
		
		return ADX;
	}
	
	
	
	
	/**
	 * Average Directional Movement Index
	 * 
	 * Average Directional Movement Index Technical Indicator (ADX) helps to
	 * determine if there is a price trend. It was developed and described in
	 * detail by Welles Wilder in his book
	 * "New concepts in technical trading systems".
	 * 
	 * The simplest trading method based on the system of directional movement
	 * implies comparison of two direction indicators: the 14-period +DI one and
	 * the 14-period -DI. To do this, one either puts the charts of indicators
	 * one on top of the other, or +DI is subtracted from -DI. W. Wilder
	 * recommends buying when +DI is higher than -DI, and selling when +DI sinks
	 * lower than -DI.
	 * 
	 * To these simple commercial rules Wells Wilder added
	 * "a rule of points of extremum". It is used to eliminate false signals and
	 * decrease the number of deals. According to the principle of points of
	 * extremum, the "point of extremum" is the point when +DI and -DI cross
	 * each other. If +DI raises higher than -DI, this point will be the maximum
	 * price of the day when they cross. If +DI is lower than -DI, this point
	 * will be the minimum price of the day they cross.
	 * 
	 * The point of extremum is used then as the market entry level. Thus, after
	 * the signal to buy (+DI is higher than -DI) one must wait till the price
	 * has exceeded the point of extremum, and only then buy. However, if the
	 * price fails to exceed the level of the point of extremum, one should
	 * retain the short position.
	 * 
	 * Average Directional Movement Index Calculation
	 * 
	 * ADX = SUM ((+DI - (-DI)) / (+DI + (-DI)), N) / N
	 * 
	 * Where:
	 * 
	 * N — the number of periods used in the calculation; SUM (..., N) — sum for
	 * N periods; +DI — value of the indicator of the positive price movement
	 * (positive directional index); -DI — value of the indicator of the
	 * negative price movement (negative directional index).
	 * 
	 */
	public static double[] computeADX(double[] Close, double[] High, double[] Low ,int Period){
		
		double[] DX=new double[Close.length];
		
		double[] dm_plus=dmPlus(High);
		double[] dm_minus=dmMinus(Low);
		
		for(int i=1;i<Low.length;i++){
			double pos=dm_plus[i]+dm_minus[i];
			if(pos>0)
				DX[i]=100*(dm_plus[i]-dm_minus[i])/pos;
			//System.out.println(", dm_plus: "+dm_plus[i]+", dm_minus: "+dm_minus[i]);
		}
		
		double[] ADX=MovingAverage.SMA(DX,Period);
		
		return ADX;
	}
	

}
