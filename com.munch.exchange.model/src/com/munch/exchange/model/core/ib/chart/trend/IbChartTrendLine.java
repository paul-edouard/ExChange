package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trenline.TrendLine;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartTrendLine extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8615834718012431697L;
	
	
	public static final String UP_GRADIENT="Up Gradient";
	public static final String UP_DISTANCE="Up Distance";
	public static final String UP_RESISTANCE="Up Resistance";

	public static final String DOWN_GRADIENT="Down Gradient";
	public static final String DOWN_DISTANCE="Down Distance";
	public static final String DOWN_RESISTANCE="Down Resistance";
	
	public static final String PERIOD="Period";
	public static final String FACTOR="Factor";
	public static final String VARIANCE="Variance";
	

	
	public IbChartTrendLine() {
		super();
	}

	public IbChartTrendLine(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartTrendLine();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Trend Line";
	}

	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+UP_GRADIENT,RendererType.PERCENT,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_RESISTANCE,RendererType.SECOND,false,true,250, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_DISTANCE,RendererType.SECOND,false,false,50, 244, 89));

		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_GRADIENT,RendererType.PERCENT,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_RESISTANCE,RendererType.SECOND,false,true,250, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_DISTANCE,RendererType.SECOND,false,false,50, 244, 89));
	}

	@Override
	public void createParameters() {
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 30, 1, 200, 0));

//		FACTOR
		this.parameters.add(new IbChartParameter(this, FACTOR,ParameterType.DOUBLE, 3, 0, 5, 1));
		
//		VARIANCE
		this.parameters.add(new IbChartParameter(this, VARIANCE,ParameterType.DOUBLE, 0.001, 0.0001, 0.1, 4));
		
	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		long[] times=BarUtils.getTimeArray(bars);
		
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		double factor=this.getChartParameter(FACTOR).getValue();
		double variance=this.getChartParameter(VARIANCE).getValue();
		
		double[][] TrLi=TrendLine.compute(high, low, period, factor, variance);
		
		this.getChartSerie(this.name+" "+UP_GRADIENT).addNewPointsOnly(times,TrLi[0]);
		this.getChartSerie(this.name+" "+UP_RESISTANCE).addNewPointsOnly(times,TrLi[1]);
		this.getChartSerie(this.name+" "+UP_DISTANCE).addNewPointsOnly(times,TrLi[2]);
		
		this.getChartSerie(this.name+" "+DOWN_GRADIENT).addNewPointsOnly(times,TrLi[3]);
		this.getChartSerie(this.name+" "+DOWN_RESISTANCE).addNewPointsOnly(times,TrLi[4]);
		this.getChartSerie(this.name+" "+DOWN_DISTANCE).addNewPointsOnly(times,TrLi[5]);
		
	}

}
