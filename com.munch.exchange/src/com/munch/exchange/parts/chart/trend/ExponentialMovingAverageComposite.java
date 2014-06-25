package com.munch.exchange.parts.chart.trend;

import java.awt.Color;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.parts.chart.IndicatorComposite;
import com.munch.exchange.parts.chart.IndicatorParameter;
import com.munch.exchange.parts.chart.IndicatorParameter.Type;

public class ExponentialMovingAverageComposite extends IndicatorComposite {
	
	public static final String EMA="EMA";
	
	public static final String FACTOR="Factor";
	
	private XYSeries EMASeries;
	
	@Inject
	public ExponentialMovingAverageComposite( Composite parent) {
		super(EMA, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Factor=new IndicatorParameter(FACTOR, Type.DOUBLE, 0.2, 0, 1, 2, this);
		paramMap.put(Factor.getName(),Factor);

	}

	@Override
	protected void clearSeries() {
		removeSerie(mainCollection, EMA);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		double[] ema=MovingAverage.EMA(prices,
							paramMap.get(FACTOR).getValue());
		//for(int i=0;i<ama.length;i++)
		//	System.out.println(ama[i]);
		EMASeries=createSerieFromPeriod(EMA,ema);
		
		addSeriesAsLine(mainPlotRenderer, mainCollection, EMASeries, Color.DARK_GRAY);

	}
}
