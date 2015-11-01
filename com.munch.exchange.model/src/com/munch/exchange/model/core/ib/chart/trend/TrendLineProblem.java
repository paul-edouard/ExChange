package com.munch.exchange.model.core.ib.chart.trend;

import java.util.LinkedList;
import java.util.List;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

public class TrendLineProblem extends AbstractProblem{
	
	private double a_min;
	private double a_max;
	private double a;
	
	private double b_min;
	private double b_max;
	private double b;
	
	private double[] prices;
	private long[] times;
	
	private double powFactor;
	private double sign=1;
	

	public TrendLineProblem(long[] times,double[] prices,double factor) {
		super(2, 1);
		this.prices=prices;
		this.times=times;
		this.powFactor=Math.pow(10, factor);
		
		calculateABMinMaxValues();
		
	}
	
	public TrendLineProblem(long[] times,double[] prices,double factor,double sign) {
		super(2, 1);
		this.prices=prices;
		this.times=times;
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
		
		long startTime=times[0];
		long endTime=times[times.length-1];
		
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
		
		double a_x=(max-min)/(times[1]-times[0]);
		
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
			double y=a*(times[i]-times[0])+b;
			
			double abs=(prices[i]-y)*sign;
			double abs_quad=abs*abs;
			
			if(abs>0){
				F+=abs_quad;
			}
			else{
				F+=powFactor*abs_quad;
			}
		}
		
		//System.out.println("F="+F);
		
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
