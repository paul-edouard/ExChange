package com.munch.exchange.model.core.chart.trend;

import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartParameter;
import com.munch.exchange.model.core.chart.ChartParameter.ParameterType;
import com.munch.exchange.model.core.chart.ChartSerie;
import com.munch.exchange.model.core.chart.ChartSerie.RendererType;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;

public class ChartSimpleMovingAverage extends ChartIndicator {
	
	public static final String SMA="SMA";
	public static final String PERIOD="Period";

	public ChartSimpleMovingAverage() {
		super();
		this.name="Moving Average";
	}

	@Override
	public void compute(HistoricalData hisData) {
		double[] prices=hisData.getPrices(HistoricalPoint.Type.CLOSE);
		double[] sma=MovingAverage.SMA(prices,
				this.getChartParameter(PERIOD).getIntegerValue());
		
		this.getChartSerie(SMA).setValues(sma);
		
	}

	@Override
	public void createSeries() {
		int[] color={50,44,89};
		ChartSerie serie=new ChartSerie(SMA,RendererType.MAIN,true,false,color);
		this.chartSeries.add(serie);

	}

	@Override
	public void createParameters() {
		ChartParameter param=new ChartParameter(PERIOD, ParameterType.INTEGER, 12, 1, 200, 0);
		this.chartParameters.add(param);
	}
	
	

}
