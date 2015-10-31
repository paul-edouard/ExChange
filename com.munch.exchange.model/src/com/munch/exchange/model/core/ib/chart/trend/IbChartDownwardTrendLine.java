package com.munch.exchange.model.core.ib.chart.trend;


import java.util.List;

import javax.persistence.Entity;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.moea.InjectedSolutionsAlgorithmFactory;

@Entity
public class IbChartDownwardTrendLine extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3608837329089475460L;
	
	public static final String DTL="DTL";
	public static final String PERIOD="Period";
	public static final String OFFSET="Offset";
	public static final String FACTOR="Factor";
	
	
	
	public IbChartDownwardTrendLine(){
		super();
	}
	
	public IbChartDownwardTrendLine(IbChartIndicatorGroup group) {
		super(group);
	}

	@Override
	public void initName() {
		this.name="Downward Trend Line";
	}

	@Override
	public void createSeries() {
		int[] color=new int[3];
		color[0]=150;
		color[1]=44;
		color[2]=89;
		IbChartSerie serie=new IbChartSerie(this,DTL,RendererType.MAIN,true,false,color);
		this.series.add(serie);
	}

	@Override
	public void createParameters() {
		//Period
		IbChartParameter paramP=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 1000, 0);
		this.parameters.add(paramP);
		
		//Offset
		IbChartParameter paramO=new IbChartParameter(this, OFFSET,ParameterType.INTEGER, 0, 0, 1000, 0);
		this.parameters.add(paramO);
		
		//Factor
		IbChartParameter paramF=new IbChartParameter(this, FACTOR,ParameterType.DOUBLE, 3, 0, 5, 1);
		this.parameters.add(paramF);

	}

	@Override
	public void compute(List<IbBar> bars) {
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int numberOfValues=period+
				this.getChartParameter(OFFSET).getIntegerValue();
		double factor=this.getChartParameter(FACTOR).getValue();
		
		double[] Eprices=this.barsToDoubleArray(bars, DataType.HIGH,numberOfValues);
		long[] Etimes=this.getTimeArray(bars,numberOfValues);
		double[] prices=new double[period];
		long[] times=new long[period];
		
		for(int i=0;i<period;i++){
			prices[i]=Eprices[i];
			times[i]=Etimes[i];
		}
		
		Solution sol=getStartSolution(times, prices, factor);
		AlgorithmFactory factory=new InjectedSolutionsAlgorithmFactory(sol);
		
		NondominatedPopulation result = new Executor()
		.withProblemClass(DownwardTrendLineProblem.class, times,prices,factor)
		.withAlgorithm("NSGAII")
		.usingAlgorithmFactory(factory)
		.withMaxEvaluations(10000)
		.distributeOnAllCores()
		.run();
		
		//AlgorithmProvider implementieren um NSGAII mit restart zu implemetiren
		
		double[] ab=new double[2];
		ab[0]=((RealVariable)result.get(0).getVariable(0)).getValue();
		ab[1]=((RealVariable)result.get(0).getVariable(1)).getValue();
		
		//double[] YValues=calculateYValues(Etimes, Eprices, ab);
		double[] YValues=calculateYValues(times, prices, ab);
		
		this.getChartSerie(DTL).setPointValues(times,YValues);
		this.getChartSerie(DTL).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		
		setDirty(false);
		
	}
	
	private Solution getStartSolution(long[] times,double[] prices,double factor){
		//double[] ab=this.calculateStartVariables(times, prices);
		
		DownwardTrendLineProblem problem=new DownwardTrendLineProblem(times, prices, factor);
		
		
		Solution sol=new Solution(2, 1);
		sol.setVariable(0, problem.createAVariable());
		sol.setVariable(1, problem.createBVariable());
		
		return sol;
	}
	
	private double[] calculateStartVariables(long[] times,double[] prices){
		double[] variables=new double[2];
		
		if(prices.length<2)return variables;
		
		double startValue=prices[0];
		double endValue=prices[prices.length-1];
		
		long startTime=times[0];
		long endTime=times[times.length-1];
		
		variables[0]=(startValue-endValue)/(startTime-endTime);
		//b=startValue-a*startTime;
		variables[1]=startValue;
		
		return variables;
	}
	
	private double[] calculateTrendLineParameters(long[] times,double[] prices){
		double[] ab=new double[2];
		
		if(times.length<2)return ab;
		
		double a=(prices[0]-prices[prices.length-1])/(times[0]-times[prices.length-1]);
		//double b=prices[0]-a*times[0];
		double b=prices[0];
		
		
		ab[0]=a;
		ab[1]=b;
		
		System.out.println("Param a="+a);
		System.out.println("Param b="+b);
		
		return ab;
	}
	
	public double[] calculateYValues(long[] times,double[] prices,double[] ab){
		double[] YValues=new double[times.length];
		
		if(prices.length!=times.length)return YValues;
		
		for(int i=0;i<times.length;i++){
			YValues[i]=ab[0]*(times[i]-times[0])+ab[1];
			//System.out.println("YValue: "+YValues[i]+", x="+times[i]);
		}
		
		
		return YValues;
		
	}
	
	
	@Override

	public void computeLast(List<IbBar> bars) {
		// TODO Auto-generated method stub

	}
	
	public static class DownwardTrendLineProblem extends AbstractProblem {
		
		
		private double a_min;
		private double a_max;
		private double a;
		
		private double b_min;
		private double b_max;
		private double b;
		
		private double[] prices;
		private long[] times;
		
		//private double k1=10;
		private double factor;
		
		
		//List<IbBar> bars;
		

		public DownwardTrendLineProblem(long[] times,double[] prices,double factor) {
			super(2, 1);
			this.prices=prices;
			this.times=times;
			this.factor=factor;
			
			calculateABMinMaxValues();
			
		}
		
		
		public RealVariable createAVariable(){
			return new RealVariable(a, a_min, a_max);
		}
		public RealVariable createBVariable(){
			return new RealVariable(b, b_min, b_max);
		}
		
		
		private void calculateABMinMaxValues(){
			
			
			if(prices.length<2)return ;
			
			double startValue=prices[0];
			double endValue=prices[prices.length-1];
			
			long startTime=times[0];
			long endTime=times[times.length-1];
			
			a=(startValue-endValue)/(startTime-endTime);
			//b=startValue-a*startTime;
			b=startValue;
			
			
			double min=Double.MAX_VALUE;
			double max=Double.MIN_VALUE;
			long time_min=0;
			long time_max=0;
			
			
			for(int i=0;i<prices.length;i++){
				if(prices[i]>max){
					max=prices[i];
					time_max=times[i];
				}
				if(prices[i]<min){
					min=prices[i];
					time_min=times[i];
				}
			}
			
			//double a_x=(max-min)/(time_max-time_min);
			double a_x=(max-min)/(times[1]-times[0]);
			//double b_x=max-a_x*(time_max-startTime);
			
			double a_diff=Math.abs(a_x-a);
			//double b_diff=Math.abs(b_x-b);max-min
			double b_diff=Math.abs(max-min)*2;
			
			a_min=a-a_diff;
			a_max=a+a_diff;
			
			b_min=b-b_diff;
			b_max=b+b_diff;
			
			System.out.println("a="+a+", a_min="+a_min+", a_max="+a_max);
			System.out.println("b="+b+", b_min="+b_min+", b_max="+b_max);
			
		}
		

		@Override
		public void evaluate(Solution solution) {
			double[] ab = EncodingUtils.getReal(solution);
			double F = 0;
			
			a=ab[0];b=ab[1];
			
			for(int i=0;i<prices.length;i++){
				double y=a*(times[i]-times[0])+b;
				
				double abs=y-prices[i];
				double abs_quad=abs*abs;
				
				if(abs>0){
					F+=abs_quad;
				}
				else{
					F+=Math.pow(10, factor)*abs_quad;
					//F+=abs_quad;
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
		
	}
	
	
	
}
