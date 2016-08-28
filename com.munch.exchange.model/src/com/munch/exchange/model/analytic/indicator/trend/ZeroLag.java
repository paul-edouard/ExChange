package com.munch.exchange.model.analytic.indicator.trend;

public class ZeroLag {
	
	/*
	 * 
	 * alpha = 2 / (Length + 1);
EMA = alpha*Close + (1 - alpha)*EMA[1];
LeastError = 1000000;
For Value1 = -GainLimit to GainLimit Begin
	Gain = Value1 / 10;
	EC = alpha*(EMA + Gain*(Close - EC[1])) + (1 -alpha)*EC[1];
	Error = Close - EC;
	If AbsValue(Error) < LeastError Then Begin
		LeastError = AbsValue(Error);
		BestGain = Gain;
	End;
End;
EC = alpha*(EMA + BestGain*(Close - EC[1])) + (1 -alpha)*EC[1];
	 * 
	 */
	
	public static double[] CL(double[] Price, int Period, int GainLimit){
		double[] cl=new double[Price.length];
		double[] ema = MovingAverage.EMA(Price, Period);
		
		double alpha = 2 / ((double)Period + 1);
		
		cl[0]=Price[0];
		for(int i = 1;i<Price.length;i++){
			double leastError = Double.POSITIVE_INFINITY;
			double bestCl = 0; 
			for(double val= -GainLimit;val <=GainLimit;val++){
				double gain = val/10;
				cl[i] = alpha*(ema[i]+gain*(Price[i]-cl[i-1]))+(1-alpha)*cl[i-1];
				double error = Price[i] - cl[i];
				if(Math.abs(error) < leastError){
					leastError = Math.abs(error);
					bestCl = cl[i];
				}
			}
//			System.out.println("Best CL: "+bestCl+", alpha="+alpha);
			cl[i] = bestCl;
		}
		
		
		return cl;
	}
	
	/*
	 * alpha = 2 / (Length + 1);
	EMA = alpha*Close + (1 - alpha)*EMA[1];
	LeastError = 1000000;
	For Value1 = -GainLimit to GainLimit Begin
		Gain = Value1 / 10;
		EC = alpha*(EMA + Gain*(Close - EC[1])) + (1 - alpha)*EC[1];
		Error = Close - EC;
		If AbsValue(Error) < LeastError Then Begin
			LeastError = AbsValue(Error);
			BestGain = Gain;
		End;
	End;
	EC = alpha*(EMA + BestGain*(Close- EC[1])) + (1 - alpha)*EC[1];
	If EC Crosses Over EMA and 100*LeastError / Close > Thresh Then Buy Next Bar on Open;
	If EC Crosses Under EMA and 100*LeastError / Close > Thresh Then Sell Short Next Bar on Open;
	 * 
	 */
	public static double[][] signal(double[] Price, int Period, int GainLimit, double Thresh){
		double[] cl=new double[Price.length];
		double[] signal=new double[Price.length];
		double[] ema = MovingAverage.EMA(Price, Period);
		
		double alpha = 2 / ((double)Period + 1);
		
		cl[0]=Price[0];
		signal[0] = 0;
		for(int i = 1;i<Price.length;i++){
			double leastError = Double.POSITIVE_INFINITY;
			double bestCl = 0; 
			for(double val= -GainLimit;val <=GainLimit;val++){
				double gain = val/10;
				cl[i] = alpha*(ema[i]+gain*(Price[i]-cl[i-1]))+(1-alpha)*cl[i-1];
				double error = Price[i] - cl[i];
				if(Math.abs(error) < leastError){
					leastError = Math.abs(error);
					bestCl = cl[i];
				}
			}

			cl[i] = bestCl;
//			EC Crosses Over EMA 
			if(cl[i-1] <= ema[i-1] && cl[i] > ema[i] && 100*leastError/Price[i] > Thresh){
//				signal[i] = 1 + signal[i-1];
//				signal[i] = Math.min(1 , signal[i]);
				signal[i] = 1;
				continue;
			}
			
//			EC Under Over EMA
			if(cl[i-1] >= ema[i-1] && cl[i] < ema[i] && 100*leastError/Price[i] > Thresh){
//				signal[i] = signal[i-1] -1;
//				signal[i] = Math.max(-1 , signal[i]);
				signal[i] = -1;
				continue;
			}
			
			signal[i] = signal[i-1];
			
		}
		
		
		double[][] R = new double[3][Price.length];
		R[0] = ema;
		R[1] = cl;
		R[2] = signal;
		
		
		return R;
	}
	
	
	

}
