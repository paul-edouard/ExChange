package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

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
public class IbChartFractalAdaptMovAver extends IbChartIndicator {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2059220603907409167L;

	public static final String FRAMA="Fratal Adaptive Moving Average";
	
	public static final String PERIOD="Period";
	public static final String PRICE="Price";

	
	
	public IbChartFractalAdaptMovAver() {
		super();
	}
	
	
	public IbChartFractalAdaptMovAver(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartFractalAdaptMovAver();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Fratal Adaptive Moving Average";
	}


	@Override
	public void createSeries() {
		
//		Adaptive Moving Average
		this.series.add(new IbChartSerie(this,FRAMA,RendererType.MAIN,true,true,50, 44, 89));
		
		
	}


	@Override
	public void createParameters() {
		
//		PERIOD
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		double[] High=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] Low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		
		
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		
		
//		FRAMA
		double[] frama=FractalAdaptiveMovingAverage.compute(prices, High, Low, period);
		
		refreshSerieValues(FRAMA, reset, times, frama, period-1);
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
