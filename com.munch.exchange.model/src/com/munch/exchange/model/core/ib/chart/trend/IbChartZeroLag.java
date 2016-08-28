package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.AdaptiveMovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.ZeroLag;
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
public class IbChartZeroLag extends IbChartIndicator {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1867810380400438036L;

	public static final String SERIE_ZL="Zero Lag";
	
	public static final String PARAM_PERIOD="Period";
	public static final String PARAM_GAIN_LIMIT = "Gain Limit";
	public static final String PARAM_PRICE="Price";

	
	
	public IbChartZeroLag() {
		super();
	}
	
	
	public IbChartZeroLag(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartZeroLag();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Zero Lag";
	}


	@Override
	public void createSeries() {
		
//		Adaptive Moving Average
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_ZL,RendererType.MAIN,true,true,150, 44, 89));
		
		
	}


	@Override
	public void createParameters() {
		
//		PERIOD
		IbChartParameter param=new IbChartParameter(this, PARAM_PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);
		
//		GAIN_LIMIT
		this.parameters.add(new IbChartParameter(this, PARAM_GAIN_LIMIT,ParameterType.INTEGER, 50, 1, 100, 0));
		
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PARAM_PERIOD).getIntegerValue();
		int gainLimit=this.getChartParameter(PARAM_GAIN_LIMIT).getIntegerValue();
		
//		SMA
		double[] cl=ZeroLag.CL(prices, period, gainLimit);
		
		refreshSerieValues(this.name+" "+SERIE_ZL, reset, times, cl, period -1 );
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PARAM_PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
