package com.munch.exchange.model.core.chart.trend;

import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartParameter;
import com.munch.exchange.model.core.chart.ChartParameter.ParameterType;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.chart.ChartSerie.RendererType;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;

public class ChartSimpleMovingAverage extends ChartIndicator {
	
	public static final String SMA="SMA";
	public static final String PERIOD="Period";

	public ChartSimpleMovingAverage(TimeSeries series) {
		super(series);
	}
	
	public ChartSimpleMovingAverage(ChartIndicatorGroup parent) {
		super(parent);
	}

	@Override
	public void compute(HistoricalData hisData) {
		//double[] prices=hisData.getPrices(HistoricalPoint.Type.CLOSE);
		ValuePointList pricesList=hisData.getPricesAsValuePointList(HistoricalPoint.Type.CLOSE);
		
		double[] sma=MovingAverage.SMA(pricesList.toDoubleArray(),
				this.getChartParameter(PERIOD).getIntegerValue());
		
		this.getChartSerie(SMA).setValues(pricesList,sma);
		this.getChartSerie(SMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		
		
		setDirty(false);
		
	}

	@Override
	public void createSeries() {
		int[] color={50,44,89};
		ChartSerie serie=new ChartSerie(this,SMA,RendererType.MAIN,true,false,color);
		this.chartSeries.add(serie);

	}

	@Override
	public void createParameters() {
		ChartParameter param=new ChartParameter(this, PERIOD, ParameterType.INTEGER, 12, 1, 200, 0);
		this.chartParameters.add(param);
		
	}

	@Override
	public void initName() {
		this.name="Simple Moving Average";
	}
	
	

}
