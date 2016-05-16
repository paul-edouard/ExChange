package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.AdaptiveMovingAverage;
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
public class IbChartAdaptiveMovingAverage extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8048534784073634868L;

	public static final String AMA="Adaptive Moving Average";
	
	public static final String PERIOD="Period";
	public static final String SLOW_MEA="SlowMEA";
	public static final String FAST_MEA="FastMEA";
	public static final String PRICE="Price";

	
	
	public IbChartAdaptiveMovingAverage() {
		super();
	}
	
	
	public IbChartAdaptiveMovingAverage(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartAdaptiveMovingAverage();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Adaptive Moving Average";
	}


	@Override
	public void createSeries() {
		
//		Adaptive Moving Average
		this.series.add(new IbChartSerie(this,AMA,RendererType.MAIN,true,true,50, 44, 89));
		
		
	}


	@Override
	public void createParameters() {
		
//		PERIOD
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);
		
//		Slow MEA
		this.parameters.add(new IbChartParameter(this, SLOW_MEA,ParameterType.INTEGER, 30, 20, 200, 0));
		
//		Fast MEA
		this.parameters.add(new IbChartParameter(this, FAST_MEA,ParameterType.INTEGER, 2, 1, 50, 0));
		
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int slowEMA=this.getChartParameter(SLOW_MEA).getIntegerValue();
		int fastEMA=this.getChartParameter(FAST_MEA).getIntegerValue();
		
//		SMA
		double[] ama=AdaptiveMovingAverage.compute(prices, period, slowEMA, fastEMA);
		if(reset){
			int validAtPosition=Math.max(period, Math.max(slowEMA,fastEMA ))-1;
			this.getChartSerie(AMA).setPointValues(times,ama);
			this.getChartSerie(AMA).setValidAtPosition(validAtPosition);
		}
		else{
			this.getChartSerie(AMA).addNewPointsOnly(times,ama);
		}
		

		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
