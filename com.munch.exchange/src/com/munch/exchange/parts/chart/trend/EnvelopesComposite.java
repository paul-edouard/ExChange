package com.munch.exchange.parts.chart.trend;

import java.awt.Color;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.analytic.indicator.trend.Envelopes;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.parts.chart.IndicatorComposite;
import com.munch.exchange.parts.chart.IndicatorParameter;
import com.munch.exchange.parts.chart.IndicatorParameter.Type;

public class EnvelopesComposite extends IndicatorComposite {
	
	public static final String ENVELOPES="Envelopes";
	public static final String UPPER_BAND="Upper Band";
	public static final String LOWER_BAND="Lower Band";
	
	public static final String PERIOD="PERIOD";
	public static final String FACTOR="FACTOR";
	
	private XYSeries seriesUB;
	private XYSeries seriesLB;
	
	@Inject
	public EnvelopesComposite( Composite parent) {
		super(ENVELOPES, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Period=new IndicatorParameter(PERIOD, Type.INTEGER, 6, 1, 200, 0, this);
		IndicatorParameter Factor=new IndicatorParameter(FACTOR, Type.DOUBLE, 20, 0, 50, 2, this);
		paramMap.put(Period.getName(),Period);
		paramMap.put(Factor.getName(),Factor);

	}

	@Override
	protected void clearSeries() {
		removeSerie(mainCollection, ENVELOPES);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		
		double[][] ENV=Envelopes.compute(prices,
				paramMap.get(PERIOD).getIntegerValue(),
				paramMap.get(FACTOR).getValue());
		
		seriesUB=createSerieFromPeriod(UPPER_BAND,ENV[0]);
		seriesLB=createSerieFromPeriod(LOWER_BAND,ENV[1]);
		
		addSeriesAsLine(mainPlotRenderer, mainCollection, seriesUB, Color.WHITE);
		addSeriesAsLine(mainPlotRenderer, mainCollection, seriesLB, Color.WHITE);

	}
}