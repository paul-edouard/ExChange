package com.munch.exchange.model.core.chart.signals;

import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.analytic.indicator.trend.AdaptiveMovingAverage;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.chart.ChartSerie.RendererType;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;

public class ChartSimpleDerivate extends ChartIndicator {
	
	public static final String SD="SD";
	
	public ChartSimpleDerivate(TimeSeries series) {
		super(series);
	}
	
	
	public ChartSimpleDerivate(ChartIndicatorGroup parent) {
		super(parent);
	}
	

	@Override
	public void initName() {
		this.name="Simple Derivate";
	}

	@Override
	public void compute(HistoricalData hisData) {
		ValuePointList pricesList=hisData.getPricesAsValuePointList(HistoricalPoint.Type.CLOSE);
		
		double[] ama=SimpleDerivate.compute(pricesList.toDoubleArray());
		
		this.getChartSerie(SD).setValues(pricesList,ama);
		this.getChartSerie(SD).setValidAtPosition(1);
		
		setDirty(false);

	}

	@Override
	public void createSeries() {
		int[] color={50,143,89};
		ChartSerie serie=new ChartSerie(this,SD,RendererType.SECOND,true,false,color);
		this.chartSeries.add(serie);
	}

	@Override
	public void createParameters() {

	}

}
