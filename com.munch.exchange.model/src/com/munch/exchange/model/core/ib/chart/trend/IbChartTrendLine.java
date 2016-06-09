package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
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
	public static final String UP_MA_GRADIENT="Up Moving Average Gradient";
	public static final String UP_DISTANCE="Up Distance";
	public static final String UP_RESISTANCE="Up Resistance";

	public static final String DOWN_GRADIENT="Down Gradient";
	public static final String DOWN_MA_GRADIENT="Down Moving Average Gradient";
	public static final String DOWN_DISTANCE="Down Distance";
	public static final String DOWN_RESISTANCE="Down Resistance";
	
	public static final String PERIOD="Period";
	public static final String FACTOR="Factor";
	public static final String POW_VARIANCE="Pow Variance";
	public static final String MA_PERIOD="Moving Average Period";
	

	
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
		this.series.add(new IbChartSerie(this,this.name+" "+UP_MA_GRADIENT,RendererType.PERCENT,false,true,50, 44, 189));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_RESISTANCE,RendererType.SECOND,false,true,250, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_DISTANCE,RendererType.SECOND,false,false,50, 244, 89));

		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_GRADIENT,RendererType.PERCENT,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_MA_GRADIENT,RendererType.PERCENT,false,true,50, 44, 189));
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
//		this.parameters.add(new IbChartParameter(this, VARIANCE,ParameterType.DOUBLE, 0.001, 0.0001, 0.1, 4));
		this.parameters.add(new IbChartParameter(this, POW_VARIANCE,ParameterType.DOUBLE, -5, -7, 3, 1));
		
//		MA PERIOD
		this.parameters.add(new IbChartParameter(this, MA_PERIOD,ParameterType.INTEGER, 3, 1, 50, 0));
		
	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		long[] times=BarUtils.getTimeArray(bars);
		
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int ma_period=this.getChartParameter(MA_PERIOD).getIntegerValue();
		double factor=this.getChartParameter(FACTOR).getValue();
		double variance=Math.pow(10,this.getChartParameter(POW_VARIANCE).getValue());
		
		double[][] TrLi=TrendLine.compute(high, low, period, factor, variance);
		
		if(TrLi[0].length==0)return;
		
		refreshSerieValues(this.name+" "+UP_GRADIENT, reset, times, TrLi[0], period-1);
		refreshSerieValues(this.name+" "+UP_MA_GRADIENT, reset, times, MovingAverage.EMA(TrLi[0], ma_period), period-1);
		
		refreshSerieValues(this.name+" "+UP_RESISTANCE, reset, times, TrLi[1], period-1);
		refreshSerieValues(this.name+" "+UP_DISTANCE, reset, times, TrLi[2], period-1);
	
		refreshSerieValues(this.name+" "+DOWN_GRADIENT, reset, times, TrLi[3], period-1);
		refreshSerieValues(this.name+" "+DOWN_MA_GRADIENT, reset, times, MovingAverage.EMA(TrLi[3], ma_period), period-1);
		
		refreshSerieValues(this.name+" "+DOWN_RESISTANCE, reset, times, TrLi[4], period-1);
		refreshSerieValues(this.name+" "+DOWN_DISTANCE, reset, times, TrLi[5], period-1);
		
	}

}
