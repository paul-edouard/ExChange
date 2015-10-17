package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartDownwardTrendLine extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3608837329089475460L;
	
	public static final String DTL="DTL";
	public static final String PERIOD="Period";
	public static final String OFFSET="Offset";
	
	
	
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
		IbChartParameter paramP=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(paramP);
		
		//Offset
		IbChartParameter paramO=new IbChartParameter(this, OFFSET,ParameterType.INTEGER, 0, 0, 200, 0);
		this.parameters.add(paramO);

	}

	@Override
	public void compute(List<IbBar> bars) {
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int numberOfValues=period+
				this.getChartParameter(OFFSET).getIntegerValue();
		double[] Eprices=this.barsToDoubleArray(bars, DataType.HIGH,numberOfValues);
		long[] Etimes=this.getTimeArray(bars,numberOfValues);
		double[] prices=new double[period];
		long[] times=new long[period];
		
		for(int i=0;i<period;i++){
			prices[i]=Eprices[i];
			times[i]=Etimes[i];
		}
		
		double[] ab=calculateTrendLineParameters(times, prices);
		double[] YValues=calculateYValues(Etimes, Eprices, ab);
		
		this.getChartSerie(DTL).setPointValues(Etimes,YValues);
		this.getChartSerie(DTL).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		
		setDirty(false);
		
	}

	public double[] calculateTrendLineParameters(long[] times,double[] prices){
		double[] ab=new double[2];
		
		if(times.length<2)return ab;
		
		double a=(prices[0]-prices[prices.length-1])/(times[0]-times[prices.length-1]);
		double b=prices[0]-a*times[0];
		
		ab[0]=a;
		ab[1]=b;
		
		NondominatedPopulation result = new Executor()
		.withProblem("UF1")
		.withAlgorithm("NSGAII")
		.withMaxEvaluations(10000)
		.distributeOnAllCores()
		.run();

//display the results
System.out.format("Objective1  Objective2%n");

for (Solution solution : result) {
	System.out.format("%.4f      %.4f%n",
			solution.getObjective(0),
			solution.getObjective(1));
}
		
		
		return ab;
	}
	
	public double[] calculateYValues(long[] times,double[] prices,double[] ab){
		double[] YValues=new double[times.length];
		
		if(prices.length!=times.length)return YValues;
		
		for(int i=0;i<times.length;i++){
			YValues[i]=ab[0]*times[i]+ab[1];
			System.out.println("YValue: "+YValues[i]+", x="+times[i]);
		}
		
		
		return YValues;
		
	}
	
	
	
	@Override

	public void computeLast(List<IbBar> bars) {
		// TODO Auto-generated method stub

	}

}
