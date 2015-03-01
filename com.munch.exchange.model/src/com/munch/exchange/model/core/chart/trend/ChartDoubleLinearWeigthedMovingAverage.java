package com.munch.exchange.model.core.chart.trend;

import com.munch.exchange.model.analytic.indicator.trend.DoubleMovingAverage;
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

public class ChartDoubleLinearWeigthedMovingAverage extends ChartIndicator {
	
	public static final String DLWMA="DLWMA";
	
	public static final String PERIOD="PERIOD";

	
	public ChartDoubleLinearWeigthedMovingAverage(TimeSeries series) {
		super(series);
	}
	
	public ChartDoubleLinearWeigthedMovingAverage(ChartIndicatorGroup parent) {
		super(parent);
	}

	@Override
	public void compute(HistoricalData hisData) {
		//double[] prices=hisData.getPrices(HistoricalPoint.Type.CLOSE);
		
		ValuePointList pricesList=hisData.getPricesAsValuePointList(HistoricalPoint.Type.CLOSE);
		
		
		double[] computed=DoubleMovingAverage.computeLWMA(pricesList.toDoubleArray(),
				this.getChartParameter(PERIOD).getIntegerValue());
		
		this.getChartSerie(DLWMA).setValues(pricesList,computed);
		this.getChartSerie(DLWMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		setDirty(false);

	}

	@Override
	public void createSeries() {
		int[] color={50,200,89};
		ChartSerie serie=new ChartSerie(this,DLWMA,RendererType.MAIN,true,false,color);
		this.chartSeries.add(serie);

	}

	@Override
	public void createParameters() {
		ChartParameter param=new ChartParameter(this, PERIOD, ParameterType.INTEGER, 12, 1, 200, 0);
		this.chartParameters.add(param);
	}
	
	@Override
	public void initName() {
		this.name="Double Linear Weigthed Moving Average";
	}

}
