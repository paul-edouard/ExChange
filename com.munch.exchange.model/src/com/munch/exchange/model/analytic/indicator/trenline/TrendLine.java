package com.munch.exchange.model.analytic.indicator.trenline;

import java.util.LinkedList;
import java.util.List;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import com.munch.exchange.model.core.moea.InjectedSolutionsAlgorithmFactory;

public class TrendLine {
	
//	private static int MAX_EVALUATION=10000;
	private static int MAX_EVALUATION=1000;

	
	/**
	 * compute the trend line values
	 * 
	 * @param close
	 * @param high
	 * @param low
	 * @param period
	 * @param factor
	 * @param variance is used for the calculation of the resistance [0.1 5]
	 * @return
	 */
	public static double[][] compute(double[] high, double[] low,  int period, double factor, double variance){
		
		System.out.println("High length: "+high.length);
		
		double[][] TrLi=new double[6][high.length];
		
		double[] A_UP=new double[high.length];
		double[] B_UP=new double[high.length];
		double[] RESISTANCE_UP=new double[high.length];
		double[] DISTANCE_UP=new double[high.length];
		
		
		double[] A_DOWN=new double[high.length];
		double[] B_DOWN=new double[high.length];
		double[] RESISTANCE_DOWN=new double[high.length];
		double[] DISTANCE_DOWN=new double[high.length];
		
		
		double[] local_high=new double[period];
		double[] local_low=new double[period];
		
		for(int i=period;i<high.length;i++){
			for(int j=i-period;j<i;j++){
				local_high[j-(i-period)]=high[j];
				local_low[j-(i-period)]=low[j];
			}
			
//			Calculate the Upward Trend
			double[] AB_UP=calculateAB_Direct(local_low, factor, 1.0);
			A_UP[i]=AB_UP[0];
			B_UP[i]=AB_UP[1];
			RESISTANCE_UP[i]=calculateResistanceOfPoints(A_UP[i], B_UP[i], local_low, variance);
			DISTANCE_UP[i]=low[i] - (period*A_UP[i]+B_UP[i]);
			
//			Calculate the Downward Trend
			double[] AB_DOWN=calculateAB_Direct(local_high, factor, -1.0);
			A_DOWN[i]=AB_DOWN[0];
			B_DOWN[i]=AB_DOWN[1];
			RESISTANCE_DOWN[i]=calculateResistanceOfPoints(A_DOWN[i], B_DOWN[i], local_high, variance);
			DISTANCE_DOWN[i]=period*A_DOWN[i]+B_DOWN[i] - high[i];
			
		}

		TrLi[0]=A_UP;
		TrLi[1]=RESISTANCE_UP;
		TrLi[2]=DISTANCE_UP;

		TrLi[3]=A_DOWN;
		TrLi[4]=RESISTANCE_DOWN;
		TrLi[5]=DISTANCE_DOWN;

		return TrLi;
		
	}
	
	private static double calculateResistanceOfPoints(double A, double B, double[] prices, double variance){
		double res=0;
		for(int i=0;i<prices.length;i++){
			double linePoint=A*i+B;
			res+= calculateResistanceOfPoint(linePoint, prices[i], variance);
		}
		return res;
	}
	
	
	private static double calculateResistanceOfPoint(double linePoint, double price, double variance){
		double diff=linePoint-price;
		return Math.exp(-diff*diff/variance);
	}
	
	private static double[] calculateAB(double[] price, double factor, double sign){
		Problem problem=new Problem(price, factor, sign);
		AlgorithmFactory down_factory=new InjectedSolutionsAlgorithmFactory(problem.newStartSolutions());
		
		NondominatedPopulation down_result = new Executor()
				.withProblemClass(Problem.class,price,factor,sign)
				.withAlgorithm("NSGAII")
				.usingAlgorithmFactory(down_factory)
				.withMaxEvaluations(MAX_EVALUATION)
				.distributeOnAllCores()
				.run();
		
		double[] ab=new double[2];
		
		ab[0]=((RealVariable)down_result.get(0).getVariable(0)).getValue();
		ab[1]=((RealVariable)down_result.get(0).getVariable(1)).getValue();
		return ab;
	}
	
	
	public static double[] calculateAB_Direct(double[] price, double factor, double sign){
		
		double ABS=0;
		double A = 0;
		double B = 0;
		
		for(int i = 0;i < price.length - 1; i++){
			double yi = price[i];
			for(int j = i+1; j< price.length; j++){
				double yj = price[j];
				double abs = j-i;
				
				if(abs < ABS)continue;
				
				double a =(yj-yi)/abs;
				double b = yi - a*i;
				
				if(ABS==0){
					ABS = abs;A=a;B=b;
					continue;
				}
					
				
				
				boolean upper=false;
				boolean lower=false;
				for(int k = 0; k<price.length; k++){
					if(upper && lower)break;
					if(k==i || k==j)continue;
					
					double yk = a*k+b;
					if(yk>=price[k]){
						upper = true;
						continue;
					}
					
					if(yk<=price[k]){
						lower=true;
						continue;
					}
				}
				
				if(upper && lower)continue;
				
				if(abs > ABS ){
					System.out.println("abs:"+abs);
					System.out.println("lower:"+lower);
					System.out.println("upper:"+upper);
					
					
					if((sign > 0 && lower) ||  (sign < 0 && upper)){
						System.out.println("ABS reseted to:" + abs);
						ABS = abs;A=a;B=b;
					}
				}
				
				
			}
		}
		
//		y  = a*x + b;
		
		double[] AB = new double[2];AB[0]=A;AB[1]=B;
		return AB;
		
	}
	
	
	
	
	public static class Problem extends AbstractProblem{
		private double a_min;
		private double a_max;
		private double a;
		
		private double b_min;
		private double b_max;
		private double b;
		
		private double[] prices;
		
		private double powFactor;
//		private double fac;
		private double sign=1;
		

		public Problem(double[] prices,double factor) {
			super(2, 1);
			this.prices=prices;
			this.powFactor=Math.pow(10, factor);
//			this.fac=factor;
			
			calculateABMinMaxValues();
			
		}
		
		public Problem(double[] prices,double factor,double sign) {
			super(2, 1);
			this.prices=prices;
			this.powFactor=Math.pow(10, factor);
			this.sign=sign;
			
			calculateABMinMaxValues();
			
		}
		
		public void setUpwardTrend(){
			sign=1.0;
		}
		
		public void setDownwardTrend(){
			sign=-1.0;
		}
		
		
		private void calculateABMinMaxValues(){
			
			
			if(prices.length<2)return ;
			
			double startValue=prices[0];
			double endValue=prices[prices.length-1];
			
			int startTime=0;
			int endTime=prices.length-1;
			
			a=(startValue-endValue)/(startTime-endTime);
			b=startValue;
				
			double min=Double.MAX_VALUE;
			double max=Double.MIN_VALUE;
			
			for(int i=0;i<prices.length;i++){
				if(prices[i]>max)
					max=prices[i];
				if(prices[i]<min)
					min=prices[i];
			}
			
			double a_x=max-min;
			
			double a_diff=Math.abs(a_x-a);
			double b_diff=Math.abs(max-min)*2;
			
			a_min=a-a_diff;
			a_max=a+a_diff;
			
			b_min=b-b_diff;
			b_max=b+b_diff;
			
			//System.out.println("a="+a+", a_min="+a_min+", a_max="+a_max);
			//System.out.println("b="+b+", b_min="+b_min+", b_max="+b_max);
			
		}
		

		@Override
		public void evaluate(Solution solution) {
			double[] ab = EncodingUtils.getReal(solution);
			double F = 0;
			
			a=ab[0];b=ab[1];
			
			for(int i=0;i<prices.length;i++){
				
				double y=a*i+b;
				double abs=(prices[i]-y)*sign;
				double abs_quad=abs*abs;
				
				if(abs>0){
					F+=abs_quad;
				}
				else{
					F+=powFactor*abs_quad;
				}
				
			}
			
			
			double[] f = new double[numberOfObjectives];
			f[0]=F;
			solution.setObjectives(f);
			
		}

		@Override
		public Solution newSolution() {
			
			Solution solution = new Solution(getNumberOfVariables(), 
					getNumberOfObjectives());
			
			solution.setVariable(0, new RealVariable(a_min, a_max));
			solution.setVariable(1, new RealVariable(b_min, b_max));
			
			return solution;
			
		}
		
		public List<Solution> newStartSolutions() {
			Solution sol = new Solution(getNumberOfVariables(), 
					getNumberOfObjectives());
			
			sol.setVariable(0, new RealVariable(a, a_min, a_max));
			sol.setVariable(1, new RealVariable(b, b_min, b_max));
			
			List<Solution> solutions=new LinkedList<Solution>();
			solutions.add(sol);
			
			return solutions;
			
		}
		
		
	}
	
	
	

}
