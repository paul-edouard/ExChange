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

import com.munch.exchange.model.analytic.indicator.trend.StandardDeviation;
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
	public static double[][] compute(double[] high, double[] low,  int nbOfExtremum, int maxResSearchPeriod, double range){
		
//		System.out.println("High length: "+high.length);
		
		double[][] TrLi=new double[8][high.length];
		
		
		double[][] AB_UP=computeAB(low, nbOfExtremum, 1.0, maxResSearchPeriod, range );
		double[] A_UP=AB_UP[0];
		double[] VALUE_UP=AB_UP[2];
		double[] RESISTANCE_UP=AB_UP[3];
		double[] BREAKOUT_UP=AB_UP[4];
		
		double[][] AB_DOWN=computeAB(high, nbOfExtremum, -1.0, maxResSearchPeriod, range);
		double[] A_DOWN=AB_DOWN[0];
		double[] VALUE_DOWN=AB_DOWN[2];
		double[] RESISTANCE_DOWN=AB_DOWN[3];
		double[] BREAKOUT_DOWN=AB_DOWN[4];
		

		TrLi[0]=A_UP;
		TrLi[1]=VALUE_UP;
		TrLi[2]=RESISTANCE_UP;
		TrLi[3]=BREAKOUT_UP;

		TrLi[4]=A_DOWN;
		TrLi[5]=VALUE_DOWN;
		TrLi[6]=RESISTANCE_DOWN;
		TrLi[7]=BREAKOUT_DOWN;

		return TrLi;
		
	}
	
	public static double[][] compute(double[] high, double[] low,  int nbOfExtremum, int maxResSearchPeriod){
		
//		System.out.println("High length: "+high.length);
		
		double[][] TrLi=new double[6][high.length];
		
		
		double[][] AB_UP=computeABWithoutResistance(low, nbOfExtremum, 1.0, maxResSearchPeriod );
		double[] A_UP=AB_UP[0];
		double[] VALUE_UP=AB_UP[2];
		double[] BREAKOUT_UP=AB_UP[3];
		
		double[][] AB_DOWN=computeABWithoutResistance(high, nbOfExtremum, -1.0, maxResSearchPeriod);
		double[] A_DOWN=AB_DOWN[0];
		double[] VALUE_DOWN=AB_DOWN[2];
		double[] BREAKOUT_DOWN=AB_DOWN[3];
		

		TrLi[0]=A_UP;
		TrLi[1]=VALUE_UP;
		TrLi[2]=BREAKOUT_UP;

		TrLi[3]=A_DOWN;
		TrLi[4]=VALUE_DOWN;
		TrLi[5]=BREAKOUT_DOWN;

		return TrLi;
		
	}
	
	
	
	
	public static double[][] computeABWithoutResistance(double[] price, int nbOfExtremum, double sign, int maxResSearchPeriod){
		double[][] AB=new double[4][price.length];
		
		double[] A=new double[price.length];
		double[] B=new double[price.length];
		
		double[] Val=new double[price.length];
		double[] Breakout=new double[price.length];
		
		
		for(int i=0;i<1;i++){
			A[i]=Double.NaN;
			B[i]=Double.NaN;
			Val[i]=Double.NaN;
			Breakout[i]=Double.NaN;
		}
		
		for(int i=1;i<price.length;i++){
			
//			Search the Extremums
			LinkedList<Integer> extPos=new LinkedList<Integer>();
			int pos = i;
			while(pos > 1 && extPos.size()<nbOfExtremum && (i - pos)<=maxResSearchPeriod){
				pos--;
				if(sign < 0){
					if(price[pos-1]<=price[pos] && price[pos] >= price[pos+1]){
						extPos.add(0, pos);
					}
				}
				else{
					if(price[pos-1]>=price[pos] && price[pos] <= price[pos+1]){
						extPos.add(0, pos);
					}
				}
			}
			if(extPos.size()<2)continue;
			
//			Create the local serie
			double[] local_ext=new double[extPos.getLast()-extPos.getFirst()+1];
			int k=0;
			for(int j=extPos.getFirst();j<=extPos.getLast();j++){
				local_ext[k]=price[j];
				k++;
			}
			
//			Calculate the Trend
			double[] AB_Loc=calculateAB_Direct(local_ext, sign, 1.0);
			A[i]=AB_Loc[0];
			B[i]=AB_Loc[1];
			
//			Calculate the current Value
			Val[i] = (i-extPos.getFirst())*A[i] + B[i];
			
			
//			Calculate the breakout value
			if(sign < 0 && price[i] > Val[i]){
				Breakout[i] = price[i] - Val[i];
			}
			else if (sign > 0 && price[i] < Val[i]){
				Breakout[i] = Val[i] - price[i];
			}
			
			
		}
		
		
		AB[0]=A;
		AB[1]=B;
		AB[2]=Val;
		AB[3]=Breakout;
		
		return AB;
		
	}
		
	
	
	public static double[][] computeAB(double[] price, int nbOfExtremum, double sign, int maxResSearchPeriod, double range){
		double[][] AB=new double[5][price.length];
		
		double[] A=new double[price.length];
		double[] B=new double[price.length];
		
		double[] Val=new double[price.length];
		double[] Res=new double[price.length];
		
		double[] Breakout=new double[price.length];
		
//		double[] local_ext=new double[period];
		
		for(int i=0;i<price.length;i++){
			
//			Search the Extremums
			LinkedList<Integer> extPos=new LinkedList<Integer>();
			int pos = i;
			while(pos > 1 && extPos.size()<nbOfExtremum && (i - pos)<=maxResSearchPeriod){
				pos--;
				if(sign < 0){
					if(price[pos-1]<=price[pos] && price[pos] >= price[pos+1]){
						extPos.add(0, pos);
					}
				}
				else{
					if(price[pos-1]>=price[pos] && price[pos] <= price[pos+1]){
						extPos.add(0, pos);
					}
				}
			}
			if(extPos.size()<2)continue;
			
//			Create the local serie
			double[] local_ext=new double[extPos.getLast()-extPos.getFirst()+1];
			int k=0;
			for(int j=extPos.getFirst();j<=extPos.getLast();j++){
				local_ext[k]=price[j];
				k++;
			}
			
//			Calculate the Trend
			double[] AB_Loc=calculateAB_Direct(local_ext, sign, 1.0);
			A[i]=AB_Loc[0];
			B[i]=AB_Loc[1];
			
//			Calculate the current Value
			Val[i] = (i-extPos.getFirst())*A[i] + B[i];
			
//			Calculate the resistance
			double resVal = 0;
			pos = i;
			int nbOfTops = 0;
			while(pos > 1 && (i-pos)<=maxResSearchPeriod){
				pos--;
				if(Val[i]==0)break;
				
				if(sign < 0){
					if(price[pos-1]<=price[pos] && price[pos] >= price[pos+1]){
						double trend_line_val = (pos - extPos.getFirst())*A[i] + B[i];
						double topPos = (trend_line_val-price[pos])/trend_line_val;
						
						if(topPos < -range)break;
						if(topPos > range)continue;
						
						nbOfTops++;
						resVal += (i-pos);
					}
				}
				else{
					if(price[pos-1]>=price[pos] && price[pos] <= price[pos+1]){
						double trend_line_val = (pos - extPos.getFirst())*A[i] + B[i];
						double topPos = (price[pos]-trend_line_val)/trend_line_val; 

						if(topPos < -range)break;						
						if(topPos > range)continue;
						
						nbOfTops++;
						resVal += (i-pos);
					}
				}
				
			}
			
			if(nbOfTops > 1)
				Res[i]=resVal;
			
//			Calculate the breakout value
			if(sign < 0 && price[i] > Val[i]){
				Breakout[i] = price[i] - Val[i];
			}
			else if (sign > 0 && price[i] < Val[i]){
				Breakout[i] = Val[i] - price[i];
			}
			
			
		}
		
		AB[0]=A;
		AB[1]=B;
		AB[2]=Val;
		AB[3]=Res;
		AB[4]=Breakout;
		
		return AB;
		
	}
	
	
	
	
	private static double calculateResistanceOfPoints(double A, double B, double[] prices){
		double res=0;
		for(int i=0;i<prices.length;i++){
			double linePoint=A*i+B;
			res+= calculateResistanceOfPoint(linePoint, prices[i]);
		}
		res/=prices.length;
		return res;
	}
	
	
	private static double calculateResistanceOfPoint(double linePoint, double price){
		double diff=linePoint-price;
		return Math.exp(-Math.abs(diff));		
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
	
	
	private static double calculate_RelDist(double A, double B, double[] prices){
		double res=0;
		for(int i=0;i<prices.length;i++){
			double linePoint=A*i+B;
			res+= Math.abs(linePoint-prices[i]);
		}
		res/=prices.length;
		return res;
	}
	
	public static double[] calculateAB_Direct(double[] price, double sign, double variance){
		
		double RES=0;
		double A = 0;
		double B = 0;
		
//		double stdDev = StandardDeviation.compute(price);
		
		for(int i = 0;i < price.length - 1; i++){
			double yi = price[i];
			for(int j = i+1; j< price.length; j++){
				double yj = price[j];
				double abs = j-i;
				
//				if(abs < ABS)continue;
				
				double a =(yj-yi)/abs;
				double b = yi - a*i;
				
//				if(RES==0){
////					ABS = abs;
//					A=a;B=b;
//					RES = calculateResistanceOfPoints(A, B, price, variance);
//					continue;
//				}
					
				
				
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
				
				double res = calculateResistanceOfPoints(a, b, price);
				if(res > RES ){					
					
					if((sign > 0 && lower) ||  (sign < 0 && upper)){
//						System.out.println("RES reseted to:" + res);
						RES = res;A=a;B=b;
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
