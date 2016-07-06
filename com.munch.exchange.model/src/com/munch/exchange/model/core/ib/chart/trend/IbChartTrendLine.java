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
	public static final String UP_VALUE="Up Value";
	public static final String UP_RESISTANCE="Up Resistance";
	public static final String UP_BREAKOUT_VALUE="Up Breakout Value";
	
	public static final String DOWN_GRADIENT="Down Gradient";
	public static final String DOWN_MA_GRADIENT="Down Moving Average Gradient";
	public static final String DOWN_VALUE="Down Value";
	public static final String DOWN_RESISTANCE="Down Resistance";
	public static final String DOWN_BREAKOUT_VALUE="Down Breakout Value";
	
	public static final String PERIOD="Period";
	public static final String MA_PERIOD="Moving Average Period";
	
	public static final String MAX_RESISTANCE_SEARCH_PERIOD="Max Resistance Search Period";
	public static final String RESISTANCE_RANGE="Resistance Range";

	

	
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
		this.series.add(new IbChartSerie(this,this.name+" "+UP_MA_GRADIENT,RendererType.PERCENT,false,false,50, 44, 189));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_RESISTANCE,RendererType.SECOND,false,true,250, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_VALUE,RendererType.MAIN,false,true,50, 244, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_BREAKOUT_VALUE,RendererType.SECOND,false,true,50, 244, 89));

		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_GRADIENT,RendererType.PERCENT,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_MA_GRADIENT,RendererType.PERCENT,false,false,50, 44, 189));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_RESISTANCE,RendererType.SECOND,false,true,250, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_VALUE,RendererType.MAIN,false,true,50, 244, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+DOWN_BREAKOUT_VALUE,RendererType.SECOND,false,true,190, 244, 89));
	}

	@Override
	public void createParameters() {
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 30, 1, 200, 0));
		
//		MA PERIOD
		this.parameters.add(new IbChartParameter(this, MA_PERIOD,ParameterType.INTEGER, 3, 1, 50, 0));
		
//		MAX RESISTANCE SEARCH PERIOD
		this.parameters.add(new IbChartParameter(this, MAX_RESISTANCE_SEARCH_PERIOD,ParameterType.INTEGER, 500, 250, 1000, 0));		
		
//		RESISTANCE RANGE
		this.parameters.add(new IbChartParameter(this, RESISTANCE_RANGE,ParameterType.DOUBLE, 0.0001, 0.00001, 0.005, 5));

	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		long[] times=BarUtils.getTimeArray(bars);
		
		
		int nbOfExtremum=this.getChartParameter(PERIOD).getIntegerValue();
		int ma_period=this.getChartParameter(MA_PERIOD).getIntegerValue();
		
		int maxResSearchPeriod=this.getChartParameter(MAX_RESISTANCE_SEARCH_PERIOD).getIntegerValue();
		double range = this.getChartParameter(RESISTANCE_RANGE).getValue();

		double[][] TrLi=TrendLine.compute(high, low, nbOfExtremum, maxResSearchPeriod, range);
		
		if(TrLi[0].length==0)return;
		
		refreshSerieValues(this.name+" "+UP_GRADIENT, reset, times, TrLi[0], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+UP_MA_GRADIENT, reset, times, MovingAverage.EMA(TrLi[0], ma_period), maxResSearchPeriod-1);
		
		refreshSerieValues(this.name+" "+UP_RESISTANCE, reset, times, TrLi[2], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+UP_VALUE, reset, times, TrLi[1], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+UP_BREAKOUT_VALUE, reset, times, TrLi[3], maxResSearchPeriod-1);
	
		refreshSerieValues(this.name+" "+DOWN_GRADIENT, reset, times, TrLi[4], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+DOWN_MA_GRADIENT, reset, times, MovingAverage.EMA(TrLi[4], ma_period), maxResSearchPeriod-1);
		
		refreshSerieValues(this.name+" "+DOWN_RESISTANCE, reset, times, TrLi[6], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+DOWN_VALUE, reset, times, TrLi[5], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+DOWN_BREAKOUT_VALUE, reset, times, TrLi[7], maxResSearchPeriod-1);
		
	}

}
