package com.munch.exchange.parts.chart.trend;

import java.awt.Color;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.analytic.indicator.trend.BollingerBands;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.parts.chart.IndicatorComposite;
import com.munch.exchange.parts.chart.IndicatorParameter;
import com.munch.exchange.parts.chart.IndicatorParameter.Type;

public class BollingerBandsComposite extends IndicatorComposite {
	
	public static final String BOLLINGER_BAND="Bollinger Band";
	public static final String BOLLINGER_BAND_TL="Bollinger Band Top Line";
	public static final String BOLLINGER_BAND_BL="Bollinger Band Bottom Line";
	
	public static final String PERIOD="PERIOD";
	public static final String FACTOR="FACTOR";
	
	private XYSeries series;
	private XYSeries seriesTL;
	private XYSeries seriesBL;
	
	@Inject
	public BollingerBandsComposite( Composite parent) {
		super(BOLLINGER_BAND, parent);
	}

	@Override
	protected void createParameters() {
		IndicatorParameter Period=new IndicatorParameter(PERIOD, Type.INTEGER, 6, 1, 200, 0, this);
		IndicatorParameter Factor=new IndicatorParameter(FACTOR, Type.DOUBLE, 2, 0, 4, 2, this);
		paramMap.put(Period.getName(),Period);
		paramMap.put(Factor.getName(),Factor);

	}

	@Override
	protected void clearSeries() {
		removeSerie(mainCollection, BOLLINGER_BAND);

	}

	@Override
	protected void createSeries() {
		double[] prices=rate.getHistoricalData().getPrices(HistoricalPoint.Type.CLOSE);
		
		double[][] ADX=BollingerBands.computeADX(prices,
				paramMap.get(PERIOD).getIntegerValue(),
				paramMap.get(FACTOR).getValue());
		
		series=createSerieFromPeriod(BOLLINGER_BAND,ADX[0]);
		seriesTL=createSerieFromPeriod(BOLLINGER_BAND_TL,ADX[1]);
		seriesBL=createSerieFromPeriod(BOLLINGER_BAND_BL,ADX[2]);
		
		
		addSeriesAsLine(mainPlotRenderer, mainCollection, series, Color.BLACK);
		addSeriesAsLine(mainPlotRenderer, mainCollection, seriesTL, Color.WHITE);
		addSeriesAsLine(mainPlotRenderer, mainCollection, seriesBL, Color.WHITE);

	}
}
