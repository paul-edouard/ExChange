package com.munch.exchange.model.core.ib.chart.oscillators;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.RelativeStrengthIndex;
import com.munch.exchange.model.analytic.indicator.trend.FractalAdaptiveMovingAverage;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartRSI extends IbChartIndicator {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7599234527907676636L;
	
	public static final String RSI_Serie="Relativ Strength Index";
	public static final String PERIOD="Period";
	public static final String PRICE="Price";

	
	
	public IbChartRSI() {
		super();
	}
	
	
	public IbChartRSI(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartRSI();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="RSI: Relativ Strength Index";
	}


	@Override
	public void createSeries() {
		
//		RSI
		this.series.add(new IbChartSerie(this,RSI_Serie,RendererType.PERCENT,true,true,50, 44, 89));
		
		
	}


	@Override
	public void createParameters() {
		

		
//		SIGNAL PERIOD
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 14, 1, 200, 0);
		this.parameters.add(param);
		
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int validAtPosition=period;
		
		
//		MACD
		double[] RSI=RelativeStrengthIndex.compute(prices,period);
		
		refreshSerieValues(RSI_Serie, 		reset, times, RSI, validAtPosition);
		
	}
	
	
	
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
