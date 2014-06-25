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

public class SimpleMovingAverageComposite extends IndicatorComposite {
	
	public static final String SMA="SMA";
	
	public static final String PERIOD="Period";
	
	private XYSeries SMASeries;
	
	@Inject
	public SimpleMovingAverageComposite( Composite parent) {
		super(SMA, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Period=new IndicatorParameter(PERIOD, Type.INTEGER, 12, 1, 200, 0, this);
		paramMap.put(Period.getName(),Period);

	}

	@Override
	protected void clearSeries() {
		removeSerie(mainCollection, SMA);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		double[] sma=MovingAverage.SMA(prices,
							paramMap.get(PERIOD).getIntegerValue());
		//for(int i=0;i<ama.length;i++)
		//	System.out.println(ama[i]);
		SMASeries=createSerieFromPeriod(SMA,sma);
		
		addSeriesAsLine(mainPlotRenderer, mainCollection, SMASeries, Color.CYAN);

	}
}
