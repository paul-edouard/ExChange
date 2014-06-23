package com.munch.exchange.model.analytic.indicator.trend;

/*
 * 	Adaptive Moving Average (AMA) Technical Indicator is used for constructing a moving average
 * 	with low sensitivity to price series noises and is characterized by the minimal lag for trend
 * 	detection. This indicator was developed and described by Perry Kaufman in his book "Smarter Trading".

	One of disadvantages of different smoothing algorithms for price series is that accidental price
	leaps can result in the appearance of false trend signals. On the other hand, smoothing leads to
	the unavoidable lag of a signal about trend stop or change. This indicator was developed for eliminating
	these two disadvantages.
	
	
	###################################
	##        CACULATION             ##
	###################################
	
	To define the current market state Kaufman introduced the notion of Efficiency Ratio (ER), which is calculated by the below formula:

	ER(i) = Signal(i)/Noise(i)

	Where:

		ER(i) — current value of the Efficiency Ratio;
		Signal(i) = ABS(Price(i) - Price(i - N)) — current signal value, absolute value of difference between the current price and price N period ago;
		Noise(i) = Sum(ABS(Price(i) - Price(i-1)),N) — current noise value, sum of absolute values of the difference between
		the price of the current period and price of the previous period for N periods.

	At a strong trend the Efficiency Ratio (ER) will tend to 1; if there is no directed movement,
	it will be a little more than 0. The obtained value of ER is used in the exponential smoothing formula:

	EMA(i) = Price(i) * SC + EMA(i-1) * (1 - SC)

	Where:

	SC = 2/(n+1) — EMA smoothing constant, n — period of the exponential moving;
	EMA(i-1) — previous value of EMA.

	The smoothing ratio for the fast market must be as for EMA with period 2 (fast SC = 2/(2+1) = 0.6667),
	and for the period of no trend EMA period must be equal to 30 (slow SC = 2/(30+1) = 0.06452).
	Thus the new changing smoothing constant is introduced (scaled smoothing constant) SSC:

	SSC(i) = (ER(i) * ( fast SC - slow SC) + slow SC

	or

	SSC(i) = ER(i) * 0.60215 + 0.06425

	For a more efficient influence of the obtained smoothing constant on the averaging period Kaufman recommends squaring it.

	Final calculation formula:

	AMA(i) = Price(i) * (SSC(i)^2) + AMA(i-1)*(1-SSC(i)^2)

	or (after rearrangement):

	AMA(i) = AMA(i-1) + (SSC(i)^2) * (Price(i) - AMA(i-1))

	Where:

		AMA(i) — current value of AMA;
		AMA(i—1) — previous value of AMA;
		SSC(i)  — current value of the scaled smoothing constant.
	
 */
public class AdaptiveMovingAverage {
	
	/**
	 * 
	 * To define the current market state Kaufman introduced the notion of Efficiency Ratio (ER), which is calculated by the below formula:

	ER(i) = Signal(i)/Noise(i)

	Where:

		ER(i) — current value of the Efficiency Ratio;
		Signal(i) = ABS(Price(i) - Price(i - N)) — current signal value, absolute value of difference between the current price and price N period ago;
		Noise(i) = Sum(ABS(Price(i) - Price(i-1)),N) — current noise value, sum of absolute values of the difference between
		the price of the current period and price of the previous period for N periods.

	At a strong trend the Efficiency Ratio (ER) will tend to 1; if there is no directed movement,
	it will be a little more than 0. The obtained value of ER is used in the exponential smoothing formula:
	 * 
	 * @param Price	
	 * @param N
	 * @return
	 */
	private static double[] calculateER(double[] Price, int N){
		double[] ER=new double[Price.length];
		double[] Signal=new double[Price.length];
		double[] Noise=new double[Price.length];
		
		for(int i=0;i<Price.length;i++){
			if(i-N<0)continue;
			Signal[i]=Math.abs(Price[i]-Price[i-N]);
			for(int j=0;j<N;j++){
				Noise[i]+=Math.abs(Price[i-j]-Price[i-1-j]);
			}
			if(Signal[i]==0 && Noise[i]==0)
				ER[i]=1;
			else if(Noise[i]==0)
				ER[i]=1;
			else if(Signal[i]==0)
				ER[i]=0;
			else
				ER[i]=Signal[i]/Noise[i];
		}
		
		return ER;
	}
	
	/**
	 * The smoothing ratio for the fast market must be as for EMA with period 2 (fast SC = 2/(2+1) = 0.6667),
	and for the period of no trend EMA period must be equal to 30 (slow SC = 2/(30+1) = 0.06452).
	Thus the new changing smoothing constant is introduced (scaled smoothing constant) SSC:

	SSC(i) = (ER(i) * ( fast SC - slow SC) + slow SC

	or

	SSC(i) = ER(i) * 0.60215 + 0.06425
	 * 
	 * @return
	 */
	private static double[] calculateSSC(double[] ER, int SlowMEA, int FastMEA){
		double[] SSC=new double[ER.length];
		double fastSC=2.0/((double)FastMEA+1.0);
		double slowSC=2.0/((double)SlowMEA+1.0);
		
		
		for(int i=0;i<ER.length;i++){
			SSC[i]=ER[i]*(fastSC-slowSC)+slowSC;
		}
		
		return SSC;
	}
	
	
	public static double[] compute(double[] Price, int Period, int SlowMEA, int FastMEA){
		
		double[] ER=calculateER(Price, Period);
		double[] SSC=calculateSSC(ER, SlowMEA, FastMEA);
		
		double[] AMA=new double[Price.length];
		AMA[0]=Price[0];
		
		for(int i=1;i<Price.length;i++){
			AMA[i] = AMA[i-1] + Math.pow(SSC[i], 2) * (Price[i] - AMA[i-1]);
		}
		/*
		for(int i=Price.length-1;i>=1;i--){
			System.out.println("Price: "+Price[i]+", ER: "+ER[i]+", SSC: "+SSC[i]+", AMA: "+AMA[i]);
		}
		System.out.println("Period: "+Period+", SlowMEA: "+SlowMEA+", FastMEA: "+FastMEA);
		*/
		return AMA;
		
	}
	
	public static double[] compute(double[] Price, int Period){
		return compute(Price,Period,30,2);
	}
	
	

}
