package com.munch.exchange.parts.chart.trend;

import java.awt.Color;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.analytic.indicator.trend.AverageDirectionalMovementIndexWilder;
import com.munch.exchange.model.analytic.indicator.trend.FractalAdaptiveMovingAverage;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.parts.chart.IndicatorComposite;
import com.munch.exchange.parts.chart.IndicatorParameter;
import com.munch.exchange.parts.chart.IndicatorParameter.Type;

public class AverageDirectionalMovementIndexWilderComposite extends IndicatorComposite {
	
	public static final String ADX="ADX";
	
	public static final String PERIOD="PERIOD";
	
	private XYSeries series;
	
	@Inject
	public AverageDirectionalMovementIndexWilderComposite( Composite parent) {
		super(ADX, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Factor=new IndicatorParameter(PERIOD, Type.INTEGER, 12, 1, 200, 0, this);
		paramMap.put(Factor.getName(),Factor);

	}

	@Override
	protected void clearSeries() {
		removeSerie(percentCollection, ADX);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		double[] high=rate.getHistoricalData().getPrices(HistoricalPoint.Type.HIGH);
		double[] low=rate.getHistoricalData().getPrices(HistoricalPoint.Type.LOW);
		double[] computed=AverageDirectionalMovementIndexWilder.compute(prices,high,low,
							paramMap.get(PERIOD).getIntegerValue());
		//for(int i=0;i<ama.length;i++)
		//	System.out.println(ama[i]);
		series=createSerieFromPeriod(ADX,computed);
		
		addSeriesAsLine(percentPlotrenderer, percentCollection, series, Color.BLACK);

	}
}