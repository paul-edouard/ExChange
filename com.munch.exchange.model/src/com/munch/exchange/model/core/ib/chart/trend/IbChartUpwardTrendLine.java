package com.munch.exchange.model.core.ib.chart.trend;

import java.util.Arrays;
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
public class IbChartUpwardTrendLine extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1932238979675913940L;
	
	
	public static final String UTL="UTL";
	public static final String PERIOD="Period";
	public static final String OFFSET="Offset";
	public static final String FACTOR="Factor";
	
	public IbChartUpwardTrendLine(){
		super();
	}
	
	public IbChartUpwardTrendLine(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartUpwardTrendLine();
		c.copyData(this);
		return c;
	}
	

	@Override
	public void initName() {
		this.name="Upward Trend Line";
	}

	@Override
	public void createSeries() {
		int[] color=new int[3];
		color[0]=150;
		color[1]=144;
		color[2]=89;
		IbChartSerie serie=new IbChartSerie(this,UTL,RendererType.MAIN,true,false,color);
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
		
		double[] Eprices=this.barsToDoubleArray(bars, DataType.LOW,numberOfValues);
		long[] Etimes=this.getTimeArray(bars,numberOfValues);
		double[] prices=new double[period];
		long[] times=new long[period];
		
		for(int i=0;i<period;i++){
			prices[i]=Eprices[i];
			times[i]=Etimes[i];
		}
		
		TrendLineProblem problem=new TrendLineProblem(times, prices, factor);
		AlgorithmFactory factory=new InjectedSolutionsAlgorithmFactory(problem.newStartSolutions());
		
		
		NondominatedPopulation result = new Executor()
		.withProblemClass(TrendLineProblem.class, times,prices,factor,1.0)
		.withAlgorithm("NSGAII")
		.usingAlgorithmFactory(factory)
		.withMaxEvaluations(10000)
		.distributeOnAllCores()
		.run();
		
		double[] ab=new double[2];
		ab[0]=((RealVariable)result.get(0).getVariable(0)).getValue();
		ab[1]=((RealVariable)result.get(0).getVariable(1)).getValue();
		
		System.out.println("Opt values: "+Arrays.toString(ab));
		double[] YValues=calculateYValues(times, prices, ab);
		
		
		this.getChartSerie(UTL).setPointValues(times,YValues);
		this.getChartSerie(UTL).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		
		setDirty(false);

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

	
	
	
	
	

}
