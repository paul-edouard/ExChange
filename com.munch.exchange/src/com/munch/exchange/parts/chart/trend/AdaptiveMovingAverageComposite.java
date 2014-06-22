package com.munch.exchange.parts.chart.trend;

import java.awt.Color;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.analytic.indicator.trend.AdaptiveMovingAverage;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.parts.chart.IndicatorComposite;
import com.munch.exchange.parts.chart.IndicatorParameter;
import com.munch.exchange.parts.chart.IndicatorParameter.Type;

public class AdaptiveMovingAverageComposite extends IndicatorComposite {
	
	public static final String AMA="AMA";
	
	public static final String PERIOD="Period";
	public static final String SLOW_MEA="Slow MEA";
	public static final String FAST_EMA="Fast MEA";
	
	private XYSeries AMASeries;
	
	@Inject
	public AdaptiveMovingAverageComposite( Composite parent) {
		super(AMA, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Period=new IndicatorParameter(PERIOD, Type.INTEGER, 12, 1, 200, 0, this);
		IndicatorParameter SlowMEA=new IndicatorParameter(SLOW_MEA, Type.INTEGER, 30, 1, 100, 0, this);
		IndicatorParameter FastMEA=new IndicatorParameter(FAST_EMA, Type.INTEGER, 2, 1, 10, 0, this);
		paramMap.put(Period.getName(),Period);
		paramMap.put(SlowMEA.getName(),SlowMEA);
		paramMap.put(FastMEA.getName(),FastMEA);

	}

	@Override
	protected void clearSeries() {
		removeSerie(mainCollection, AMA);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		double[] ama=AdaptiveMovingAverage.compute(prices,
							paramMap.get(PERIOD).getIntegerValue(),
							paramMap.get(SLOW_MEA).getIntegerValue(),
							paramMap.get(FAST_EMA).getIntegerValue());
		//for(int i=0;i<ama.length;i++)
		//	System.out.println(ama[i]);
		AMASeries=createSerieFromPeriod(AMA,ama);
		
		addSeriesAsLine(mainPlotRenderer, mainCollection, AMASeries, Color.MAGENTA);

	}

}
