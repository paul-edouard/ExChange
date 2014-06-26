package com.munch.exchange.model.analytic.indicator.trend;

/**
 * The Moving Average Technical Indicator shows the mean instrument price value
 * for a certain period of time. When one calculates the moving average, one
 * averages out the instrument price for this time period. As the price changes,
 * its moving average either increases, or decreases.
 * 
 * There are four different types of moving averages: Simple (also referred to
 * as Arithmetic), Exponential, Smoothed and Weighted. Moving Average may be
 * calculated for any sequential data set, including opening and closing prices,
 * highest and lowest prices, trading volume or any other indicators. It is
 * often the case when double moving averages are used.
 * 
 * The only thing where moving averages of different types diverge considerably
 * from each other, is when weight coefficients, which are assigned to the
 * latest data, are different. In case we are talking of Simple Moving Average,
 * all prices of the time period in question, are equal in value. Exponential
 * Moving Average and Linear Weighted Moving Average attach more value to the
 * latest prices.
 * 
 * The most common way to interpreting the price moving average is to compare
 * its dynamics to the price action. When the instrument price rises above its
 * moving average, a buy signal appears, if the price falls below its moving
 * average, what we have is a sell signal.
 * 
 * This trading system, which is based on the moving average, is not designed to
 * provide entrance into the market right in its lowest point, and its exit
 * right on the peak. It allows to act according to the following trend: to buy
 * soon after the prices reach the bottom, and to sell soon after the prices
 * have reached their peak.
 * 
 * Moving averages may also be applied to indicators. That is where the
 * interpretation of indicator moving averages is similar to the interpretation
 * of price moving averages: if the indicator rises above its moving average,
 * that means that the ascending indicator movement is likely to continue: if
 * the indicator falls below its moving average, this means that it is likely to
 * continue going downward.
 * 
 * Here are the types of moving averages on the chart:
 * 
 * •Simple Moving Average (SMA)
 * 
 * •Exponential Moving Average (EMA)
 * 
 * •Smoothed Moving Average (SMMA)
 * 
 * •Linear Weighted Moving Average (LWMA)
 * 
 * You can test the trade signals of this indicator by creating an Expert
 * Advisor in MQL5 Wizard.
 * 
 * Moving Average Calculation Simple Moving Average (SMA)
 * 
 * Simple, in other words, arithmetical moving average is calculated by summing
 * up the prices of instrument closure over a certain number of single periods
 * (for instance, 12 hours). This value is then divided by the number of such
 * periods.
 * 
 * SMA = SUM (CLOSE (i), N) / N
 * 
 * Where:
 * 
 * SUM — sum; CLOSE (i) — current period close price; N — number of calculation
 * periods. Exponential Moving Average (EMA)
 * 
 * Exponentially smoothed moving average is calculated by adding of a certain
 * share of the current closing price to the previous value of the moving
 * average. With exponentially smoothed moving averages, the latest close prices
 * are of more value. P-percent exponential moving average will look like:
 * 
 * EMA = (CLOSE (i) * P) + (EMA (i - 1) * (1 - P))
 * 
 * Where:
 * 
 * CLOSE (i) — current period close price; EMA (i - 1) — value of the Moving
 * Average of a preceding period; P — the percentage of using the price value.
 * Smoothed Moving Average (SMMA)
 * 
 * The first value of this smoothed moving average is calculated as the simple
 * moving average (SMA):
 * 
 * SUM1 = SUM (CLOSE (i), N)
 * 
 * SMMA1 = SUM1 / N
 * 
 * The second moving average is calculated according to this formula:
 * 
 * SMMA (i) = (SMMA1*(N-1) + CLOSE (i)) / N
 * 
 * Succeeding moving averages are calculated according to the below formula:
 * 
 * PREVSUM = SMMA (i - 1) * N
 * 
 * SMMA (i) = (PREVSUM - SMMA (i - 1) + CLOSE (i)) / N
 * 
 * Where:
 * 
 * SUM — sum; SUM1 — total sum of closing prices for N periods; it is counted
 * from the previous bar; PREVSUM — smoothed sum of the previous bar; SMMA (i-1)
 * — smoothed moving average of the previous bar; SMMA (i) — smoothed moving
 * average of the current bar (except for the first one); CLOSE (i) — current
 * close price; N — smoothing period.
 * 
 * After arithmetic conversions the formula can be simplified:
 * 
 * SMMA (i) = (SMMA (i - 1) * (N - 1) + CLOSE (i)) / N Linear Weighted Moving
 * Average (LWMA)
 * 
 * In the case of weighted moving average, the latest data is of more value than
 * more early data. Weighted moving average is calculated by multiplying each
 * one of the closing prices within the considered series, by a certain weight
 * coefficient:
 * 
 * LWMA = SUM (CLOSE (i) * i, N) / SUM (i, N)
 * 
 * Where:
 * 
 * SUM — sum; CLOSE(i) — current close price; SUM (i, N) — total sum of weight
 * coefficients; N — smoothing period.
 * 
 * @author paul-edouard
 * 
 */
public class MovingAverage {
	
	
	/**
	 * Simple Moving Average (SMA)
	 * 
	 * Moving Average Calculation Simple Moving Average (SMA)
	 * 
	 * Simple, in other words, arithmetical moving average is calculated by
	 * summing up the prices of instrument closure over a certain number of
	 * single periods (for instance, 12 hours). This value is then divided by
	 * the number of such periods.
	 * 
	 * SMA = SUM (CLOSE (i), N) / N
	 * 
	 * @param Price
	 * @param N: the period
	 * @return
	 */
	public static double[] SMA(double[] Price, int N){
		double[] sma=new double[Price.length];
		
		for(int i=0;i<Price.length;i++){
			sma[i]=0;int pos=0;
			for(int j=0;j<N;j++){
				if(i-j<0)continue;
				sma[i]+=Price[i-j];pos++;
			}
			sma[i]=sma[i]/pos;
		}
		
		return sma;
		
	}
	
	/**
	 * 
	 * Exponential Moving Average (EMA)
	 * 
	 * Exponentially smoothed moving average is calculated by adding of a
	 * certain share of the current closing price to the previous value of the
	 * moving average. With exponentially smoothed moving averages, the latest
	 * close prices are of more value. P-percent exponential moving average will
	 * look like:
	 * 
	 * EMA = (CLOSE (i) * P) + (EMA (i - 1) * (1 - P))
	 * 
	 * Where:
	 * 
	 * CLOSE (i) — current period close price; EMA (i - 1) — value of the Moving
	 * Average of a preceding period; P — the percentage of using the price
	 * value.
	 * 
	 * @param Price
	 * @param P
	 * @return
	 */
	public static double[] EMA(double[] Price, double P){
		double[] ema=new double[Price.length];
		
		ema[0]=Price[0];
		double inv=1-P;
		
		for(int i=1;i<Price.length;i++){
			
			ema[i]=Price[i]*P+ema[i-1]*inv;
		}
		
		return ema;
		
	}
	
	public static double[] EMA(double[] Price, int Period){
		double P=2.0/((double)Period+1);
		return EMA(Price,P);
	}
	
	/**
	 * Smoothed Moving Average (SMMA)
	 * 
	 * The first value of this smoothed moving average is calculated as the
	 * simple moving average (SMA):
	 * 
	 * SUM1 = SUM (CLOSE (i), N)
	 * 
	 * SMMA1 = SUM1 / N
	 * 
	 * The second moving average is calculated according to this formula:
	 * 
	 * SMMA (i) = (SMMA1*(N-1) + CLOSE (i)) / N
	 * 
	 * Succeeding moving averages are calculated according to the below formula:
	 * 
	 * PREVSUM = SMMA (i - 1) * N
	 * 
	 * SMMA (i) = (PREVSUM - SMMA (i - 1) + CLOSE (i)) / N
	 * 
	 * Where:
	 * 
	 * SUM — sum; SUM1 — total sum of closing prices for N periods; it is
	 * counted from the previous bar; PREVSUM — smoothed sum of the previous
	 * bar; SMMA (i-1) — smoothed moving average of the previous bar; SMMA (i) —
	 * smoothed moving average of the current bar (except for the first one);
	 * CLOSE (i) — current close price; N — smoothing period.
	 * 
	 * After arithmetic conversions the formula can be simplified:
	 * 
	 * SMMA (i) = (SMMA (i - 1) * (N - 1) + CLOSE (i)) / N
	 * 
	 * @param Price
	 * @param N
	 * @return
	 */
	public static double[] SMMA(double[] Price, double N){
		double[] smma=new double[Price.length];
		double[] sum1=new double[Price.length];
		double[] smma1=new double[Price.length];
		
		
		for(int i=0;i<Price.length;i++){
			int pos=0;
			for(int j=0;j<N;j++){
				if(i-j<0)continue;
				sum1[i]+=Price[i-j];pos++;
			}
			smma1[i]=sum1[i]/pos;
			
			smma[i]= (sum1[i]-smma1[i]+Price[i])/ N;
			
			
		}
		
		return smma;
		
	}
	
	/**
	 * Linear Weighted Moving Average (LWMA)
	 * 
	 * In the case of weighted moving average, the latest data is of more value
	 * than more early data. Weighted moving average is calculated by
	 * multiplying each one of the closing prices within the considered series,
	 * by a certain weight coefficient:
	 * 
	 * LWMA = SUM (CLOSE (i) * i, N) / SUM (i, N)
	 * 
	 * Where:
	 * 
	 * SUM — sum; CLOSE(i) — current close price; SUM (i, N) — total sum of
	 * weight coefficients; N — smoothing period.
	 * 
	 * @param Price
	 * @param N
	 * @return
	 */
	public static double[] LWMA(double[] Price, double N){
		double[] lwma=new double[Price.length];
		
		int sum=0;
		for(int j=1;j<=N;j++){
			sum+=j;
		}
		
		
		for(int i=0;i<Price.length;i++){
			for(int j=0;j<N;j++){
				if(i-j<0)continue;
				lwma[i]+=Price[i-j]*(N-j);
			}
			lwma[i]=lwma[i]/sum;
			
			
		}
		
		return lwma;
		
	}
	
	
	

}
