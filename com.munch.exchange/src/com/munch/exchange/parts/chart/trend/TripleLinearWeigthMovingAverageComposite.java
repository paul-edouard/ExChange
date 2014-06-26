package com.munch.exchange.parts.chart.trend;

import java.awt.Color;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.analytic.indicator.trend.DoubleMovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.TripleMovingAverage;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.parts.chart.IndicatorComposite;
import com.munch.exchange.parts.chart.IndicatorParameter;
import com.munch.exchange.parts.chart.IndicatorParameter.Type;

public class TripleLinearWeigthMovingAverageComposite  extends IndicatorComposite {
	
	public static final String TLWMA="TLWMA";
	
	public static final String PERIOD="PERIOD";
	
	private XYSeries series;
	
	@Inject
	public TripleLinearWeigthMovingAverageComposite( Composite parent) {
		super(TLWMA, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Factor=new IndicatorParameter(PERIOD, Type.INTEGER, 12, 1, 200, 0, this);
		paramMap.put(Factor.getName(),Factor);

	}

	@Override
	protected void clearSeries() {
		removeSerie(mainCollection, TLWMA);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		double[] computed=TripleMovingAverage.computeLWMA(prices,
							paramMap.get(PERIOD).getIntegerValue());
		//for(int i=0;i<ama.length;i++)
		//	System.out.println(ama[i]);
		series=createSerieFromPeriod(TLWMA,computed);
		
		addSeriesAsLine(mainPlotRenderer, mainCollection, series, Color.RED);

	}
}