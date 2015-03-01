package com.munch.exchange.model.core.chart.trend;

import com.munch.exchange.model.analytic.indicator.trend.AdaptiveMovingAverage;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartParameter;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.chart.ChartParameter.ParameterType;
import com.munch.exchange.model.core.chart.ChartSerie.RendererType;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;

public class ChartAdaptiveMovingAverage extends ChartIndicator {
	
	
	public static final String AMA="AMA";
	
	public static final String PERIOD="Period";
	public static final String SLOW_MEA="Slow MEA";
	public static final String FAST_EMA="Fast MEA";
	
	public ChartAdaptiveMovingAverage(TimeSeries series) {
		super(series);
	}
	
	
	public ChartAdaptiveMovingAverage(ChartIndicatorGroup parent) {
		super(parent);
	}
	
	
	@Override
	public void compute(HistoricalData hisData) {
		//double[] prices=hisData.getPrices(HistoricalPoint.Type.CLOSE);
		
		ValuePointList pricesList=hisData.getPricesAsValuePointList(HistoricalPoint.Type.CLOSE);
		
		double[] ama=AdaptiveMovingAverage.compute(pricesList.toDoubleArray(),
				this.getChartParameter(PERIOD).getIntegerValue(),
				this.getChartParameter(SLOW_MEA).getIntegerValue(),
				this.getChartParameter(FAST_EMA).getIntegerValue());
		
		this.getChartSerie(AMA).setValues(pricesList,ama);
		this.getChartSerie(AMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		
		setDirty(false);
		
	}

	@Override
	public void createSeries() {
		int[] color={50,44,89};
		ChartSerie serie=new ChartSerie(this,AMA,RendererType.MAIN,true,false,color);
		this.chartSeries.add(serie);

	}

	@Override
	public void createParameters() {
		ChartParameter p1=new ChartParameter(this, PERIOD, ParameterType.INTEGER, 12, 1, 200, 0);
		ChartParameter p2=new ChartParameter(this, SLOW_MEA, ParameterType.INTEGER, 30, 1, 100, 0);
		ChartParameter p3=new ChartParameter(this, FAST_EMA, ParameterType.INTEGER, 2, 1, 10, 0);
		
		
		
		this.chartParameters.add(p1);
		this.chartParameters.add(p2);
		this.chartParameters.add(p3);
		
	}
	
	@Override
	public void initName() {
		this.name="Adaptive Moving Average";
	}

	

}
