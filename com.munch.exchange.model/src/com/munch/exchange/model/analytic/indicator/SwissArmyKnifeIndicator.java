package com.munch.exchange.model.analytic.indicator;


/*
 * http://www.mesasoftware.com/papers/SwissArmyKnifeIndicator.pdf
 */

public class SwissArmyKnifeIndicator {
	
	/**
	 * 
	 * 
	 */
	public static double[] exponentialMovingAverage(double[] input, int period){
		
		double alpha = (Math.cos(2*Math.PI/period)+Math.sin(2*Math.PI/period)-1) / (Math.cos(2*Math.PI/period));
		
		double c0 = 1;
		
		double b0 = alpha;
		double b1 = 0;
		double b2 = 0;
		
		double a1 = 1 - alpha;
		double a2 = 0;
		
		return compute(input, c0, b0, b1, b2, a1, a2);
		
	}
	
	/**
	 * A Gaussian Filter offers a very low lag compared to other smoothing filters of like 
	order.    (The  order  is  the  largest  exponent  in  the  transfer  equation).    It  can  be 
	implemented  by  taking  an  EMA  of  an  EMA,  but  that   method  leaves  the 
	computation of the correct alpha to be a little nebulous.  The double EMA is the 
	equivalent  of  squaring  the  Transfer  Response  of  an  EMA.
	 */
	public static double[] TwoPoleGaussianFilter(double[] input, int period){
		
		double beta = 2.415*(1 - Math.cos(2*Math.PI/period));
		double alpha = -beta +Math.sqrt(beta*beta+2*beta);
		
//		System.out.println("Compute new with period: "+period);
//		System.out.println("Compute new with beta: "+beta);
//		System.out.println("Compute new with alpha: "+alpha);
		
		double c0 = alpha*alpha;
		
		double b0 = 1;
		double b1 = 0;
		double b2 = 0;
		
		double a1 = 2*(1 - alpha);
		double a2 = -(1 - alpha)*(1 - alpha);
		
		return compute(input, c0, b0, b1, b2, a1, a2);
		
	}
	
	public static double[] lowPass(double[] input, int period){
		return TwoPoleGaussianFilter(input, period);
	}
	
	public static double[] Gauss(double[] input, int period){
		return TwoPoleGaussianFilter(input, period);
	}
	
	/**
	 * 
	 * The   response   of   a   two   pole   Butterworth   Filter   can   be   approximated   by 
	introducing a second order polynomial with binomial coefficients in the numerator 
	of  the  Transfer  Response  of  the  Gaussian  Filter.    The  alpha  is  computed  the 
	same  as  for  the  Gaussian  Filter.  
	 * 
	 * @param input
	 * @param period
	 * @return
	 */
	public static double[] TwoPoleButterworthFilter(double[] input, int period){
		
		double beta = 2.415*(1 - Math.cos(2*Math.PI/period));
		double alpha = -beta +Math.sqrt(beta*beta+2*beta);
		
		double c0 = alpha*alpha/4.0;
		
		double b0 = 1;
		double b1 = 2;
		double b2 = 1;
		
		double a1 = 2*(1 - alpha);
		double a2 = -(1 - alpha)*(1 - alpha);
		
		return compute(input, c0, b0, b1, b2, a1, a2);
		
	}
	
	public static double[] Butter(double[] input, int period){
		return TwoPoleButterworthFilter(input, period);
	}
	
	
	/**
	 * While trivial, but shown for completeness, a low lag three tap smoothing filter is 
	obtained by eliminating the higher order terms in the denominator of the Transfer 
	Response.
	 * @param input
	 * @return
	 */
	public static double[] SmoothingFilter(double[] input){
		double c0 = 1.0/4.0;
		
		double b0 = 1;
		double b1 = 2;
		double b2 = 1;
		
		double a1 = 0;
		double a2 = 0;
		
		return compute(input, c0, b0, b1, b2, a1, a2);
	}
	
	public static double[] Smooth(double[] input){
		return SmoothingFilter(input);
	}
	
	
	/**
	 * A  High  Pass  Filter  is  the  more  general  version  of  a  momentum  function.    Its 
	purpose  is  to  eliminate  the  constant  (DC)  term  and  lower  frequency  (longer 
	period) terms that do not contribute to the shorter term cycles in the data.  
	 * 
	 * 
	 * @param input
	 * @param period
	 * @return
	 */
	
	public static double[] HighPassFilter(double[] input, int period){
		double alpha = (Math.cos(2*Math.PI/period)+Math.sin(2*Math.PI/period)-1) / (Math.cos(2*Math.PI/period));
		
		double c0 = 1-alpha/2;
		
		double b0 = 1;
		double b1 = -1;
		double b2 = 0;
		
		double a1 = 1 - alpha;
		double a2 = 0;
		
		return compute(input, c0, b0, b1, b2, a1, a2);
	}
	
	public static double[] HP(double[] input, int period){
		return HighPassFilter(input, period);
	}
	
	/**
	 * Just as we got sharper filtering by squaring the Transfer Response of the EMA to 
	get  a  Gaussian  Low  Pass  Filter,  we  can  square  the  Transfer  Response  of  the 
	High Pass filter to improve the filtering.  In general, there is an approximate one 
	bar  lag  penalty  for  obtaining  the  improved  filtering.
	 * 
	 * 
	 * @param input
	 * @param period
	 * @return
	 */
	public static double[] TwoPoleHighPassFilter(double[] input, int period){
		
		double beta = 2.415*(1 - Math.cos(2*Math.PI/period));
		double alpha = -beta +Math.sqrt(beta*beta+2*beta);
		
		
		double c0 = (1-alpha/2)*(1-alpha/2);
		
		double b0 = 1;
		double b1 = -2;
		double b2 = 1;
		
		double a1 = 2*(1 - alpha);
		double a2 = -(1 - alpha)*(1 - alpha);
		
		return compute(input, c0, b0, b1, b2, a1, a2);
	}
	
	public static double[] TwoPHP(double[] input, int period){
		return TwoPoleHighPassFilter(input, period);
	}
	
	/**
	 * This  is  where  the  Swiss  Army  Knife  Indicator  really  starts  to  get  interesting!    It 
	can  generate  bandpass  response  to  extract  only  the  frequency  component  of 
	interest.  For example, if you want to examine the weekly cycle in the data, just 
	set the period to 5.  Monthly data can be extracted by setting the period to 20, 21, 
	or  22.    Since  the  passband  of  the  filter  is  finite,  the  exact  setting  of  the  center 
	period is not crucial.
	Another way to use the BandPass Filter is to create a bank of them, separated by 
	a fixed percentage, and display all of the filter outputs as an indicator set.  Doing 
	this, you can see at which filter the data peaks the strongest.  For filters tuned to 
	periods shorter than the dominant cycle in the data, the filter response will peak 
	before  the  peak  in  the  data.    Filters  tuned  to  periods  longer  than  the  dominant 
	cycle  in  the  data,  the  filter  response  will  peak  after  the  peak  in  the  data.    This 
	way, you can estimate the current dominant cycle and, if the data are stationary, 
	you can even predict the turning point.
	 * 
	 * 
	 * @param input
	 * @param period
	 * @param sigma between 0.05 and 0.5
	 * @return
	 */
	public static double[] BandPassFilter(double[] input, int period, double sigma){
		double beta = Math.cos(2*Math.PI/period);
		
		double gama = Math.cos(2*Math.PI*sigma/period);
		double alpha = 1/gama -Math.sqrt(1/(gama*gama)-1);
		
		
		double c0 = (1-alpha)/2;
		
		double b0 = 1;
		double b1 = 0;
		double b2 = -1;
		
		double a1 = beta*(1 - alpha);
		double a2 = - alpha;
		
		return compute(input, c0, b0, b1, b2, a1, a2);
		
	}
	
	public static double[] BP(double[] input, int period, double sigma){
		return BandPassFilter(input, period, sigma);
	}
	
	
	/**
	 * It  might  be  interesting  to  also  examine  the  data  if  the  weekly  or  monthly 
	components  were  removed,  leaving  all  the  other  components  in  place.    The 
	alpha  and  beta  variables  are  computed  exactly  as  they  were  for  the  BandPass 
	filter.  
	 * 
	 * @param input
	 * @param period
	 * @param sigma
	 * @return
	 */
	public static double[] BandStopFilter(double[] input, int period, double sigma){
		double beta = Math.cos(2*Math.PI/period);
		
		double gama = Math.cos(2*Math.PI*sigma/period);
		double alpha = 1/gama -Math.sqrt(1/(gama*gama)-1);
		
		
		double c0 = (1+alpha)/2;
		
		double b0 = 1;
		double b1 = -2*beta;
		double b2 = 1;
		
		double a1 = beta*(1 + alpha);
		double a2 = - alpha;
		
		return compute(input, c0, b0, b1, b2, a1, a2);
	}
	
	public static double[] BS(double[] input, int period, double sigma){
		return BandStopFilter(input, period, sigma);
	}
	
	
	
	private static double[] compute(double[] input, 
			double c0,
			double b0, double b1, double b2,
			double a1, double a2){
		
		double[] output = new double[input.length];
		output[0] = input[0];
		output[1] = input[1];
		for(int i = 2; i<input.length; i++){
			output[i] = c0*(b0*input[i] + 	b1*input[i-1] + b2*input[i-2]) +
							a1*output[i-1]+ a2*output[i-2];
//			System.out.println("c0"+c0+ ", b0: "+b0+", "+b1+", "+b2);
//			System.out.println("output: "+output[i]+ ", Input: "+input[i]+", "+input[i-1]+", "+input[i-2]);
		}
		
		
		return output;
	}
	
	
	public static void main(String[] args){
		double[] list = new double[5];
		list[0]= 1;
		list[1]= 8;
		list[2]= 2;
		list[3]= 4;
		list[4]= 10;
		
//		double[] med = median(list, 6);
//		System.out.println(Arrays.toString(med));
		
	}
	

}
