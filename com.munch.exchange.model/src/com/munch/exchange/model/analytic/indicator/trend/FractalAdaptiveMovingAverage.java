package com.munch.exchange.model.analytic.indicator.trend;

/**
 * Fractal Adaptive Moving Average
 * 
 * Fractal Adaptive Moving Average Technical Indicator (FRAMA) was developed by
 * John Ehlers. This indicator is constructed based on the algorithm of the
 * Exponential Moving Average, in which the smoothing factor is calculated based
 * on the current fractal dimension of the price series. The advantage of FRAMA
 * is the possibility to follow strong trend movements and to sufficiently slow
 * down at the moments of price consolidation.
 * 
 * All types of analysis used for Moving Averages can be applied to this
 * indicator.
 * 
 * You can test the trade signals of this indicator by creating an Expert
 * Advisor in MQL5 Wizard.
 * 
 * Fractal Adaptive Moving Average Calculation
 * 
 * FRAMA(i) = A(i) * Price(i) + (1 - A(i)) * FRAMA(i-1)
 * 
 * Where:
 * 
 * FRAMA(i) — current value of FRAMA; Price(i) — current price; FRAMA(i-1) —
 * previous value of FRAMA; A(i) — current factor of exponential smoothing.
 * 
 * Exponential smoothing factor is calculated according to the below formula:
 * 
 * A(i) = EXP(-4.6 * (D(i) - 1))
 * 
 * Where:
 * 
 * D(i) — current fractal dimension; EXP() — mathematical function of exponent.
 * 
 * Fractal dimension of a straight line is equal to one. It is seen from the
 * formula that if D = 1, then A = EXP(-4.6 *(1-1)) = EXP(0) = 1. Thus if price
 * changes in straight lines, exponential smoothing is not used, because in such
 * a case the formula looks like this::
 * 
 * FRAMA(i) = 1 * Price(i) + (1 — 1) * FRAMA(i—1) = Price(i)
 * 
 * I.e. the indicator exactly follows the price.
 * 
 * The fractal dimension of a plane is equal to two. From the formula we get
 * that if D = 2, then the smoothing factor A = EXP(-4.6*(2-1)) = EXP(-4.6) =
 * 0.01. Such a small value of the exponential smoothing factor is obtained at
 * moments when price makes a strong saw-toothed movement. Such a strong
 * slow-down corresponds to approximately 200-period simple moving average.
 * 
 * Formula of fractal dimension:
 * 
 * D = (LOG(N1 + N2) - LOG(N3))/LOG(2)
 * 
 * It is calculated based on the additional formula:
 * 
 * N(Length,i) = (HighestPrice(i) - LowestPrice(i))/Length
 * 
 * Where:
 * 
 * HighestPrice(i) — current maximal value for Length periods; LowestPrice(i) —
 * current minimal value for Length periods;
 * 
 * Values N1, N2 and N3 are respectively equal to:
 * 
 * N1(i) = N(Length,i)
 * 
 * N2(i) = N(Length,i + Length)
 * 
 * N3(i) = N(2 * Length,i)
 * 
 * @author paul-edouard
 * 
 */

public class FractalAdaptiveMovingAverage {
	
	
	
	private static double[] fractalDimension(double[] Low,double[] High,int Period){
		double[] D=new double[High.length];
		double N1;
		double N2;
		double N3;
		
		for(int i=0;i<High.length;i++){
			N1=(utils.getHighestPrice(High,i,0,Period)-utils.getLowestPrice(Low,i,0,Period))/((double)Period);
			N2=(utils.getHighestPrice(High,i,Period,2*Period)-utils.getLowestPrice(Low,i,Period,2*Period))/((double)Period);
			N3=(utils.getHighestPrice(High,i,0,2*Period)-utils.getLowestPrice(Low,i,0,2*Period))/((double) (2*Period));
			
			if( N1>0 && N2>0 && N3>0)
				D[i]=(Math.log(N1+N2)-Math.log(N3))/Math.log(2);
			/*
			System.out.print("Periode 1[");
			for(int j=0;j<Period;j++){
				if((i-j)<0)continue;
				System.out.print(Price[i-j]+",");
			}
			System.out.println("]");
			
			System.out.print("Periode 2[");
			for(int j=Period;j<Period*2;j++){
				if((i-j)<0)continue;
				System.out.print(Price[i-j]+",");
			}
			System.out.println("]");
			
			
			System.out.println(	"Highest [0,Period]: "+getHighestPrice(Price,i,0,Period)+
								", Highest [Period,2Period]: "+getHighestPrice(Price,i,Period,2*Period)+
								", Highest [0,2Period]: "+getHighestPrice(Price,i,0,2*Period));
			System.out.println(	"Lowest [0,Period]: "+getLowestPrice(Price,i,0,Period)+
					", Lowest [Period,2Period]: "+getLowestPrice(Price,i,Period,2*Period)+
					", Lowest [0,2Period]: "+getLowestPrice(Price,i,0,2*Period));
					*/
			//System.out.println("D: "+D[i]+", N1:"+N1+", N2:"+N2+", N3:"+N3);
			
		}
		
		
		return D;
	}
	
	private static double[] exponentialSmoothingFactor(double[] Low, double[] High,int Period){
		double[] D=fractalDimension(Low,High,Period);
		double[] A=new double[Low.length];
		for(int i=0;i<Low.length;i++){
			A[i]=Math.exp(-4.6 * (D[i] - 1));
			if(A[i]<0.01)A[i]=0.01;
			if(A[i]>1)A[i]=1;
		}
		return A;
	}
	
	
	public static double[] compute(double[] Price, double[] High, double[] Low ,int Period){
		
		double[] FRAMA=new double[Price.length];
		double[] A=exponentialSmoothingFactor(Low,High, Period);
		
		FRAMA[0]=Price[0];
		
		for(int i=1;i<Price.length;i++){
			FRAMA[i] = A[i] * Price[i] + (1 - A[i]) * FRAMA[i-1];
			//System.out.println("FRAMA: "+FRAMA[i]+", A:"+A[i]);
		}
		
		/*
		for(int i=Price.length-1;i>=0;i--){
			System.out.println("FRAMA: "+FRAMA[i]+", A:"+A[i]);
		}
		*/
		
		
		return FRAMA;
	}
	

}
