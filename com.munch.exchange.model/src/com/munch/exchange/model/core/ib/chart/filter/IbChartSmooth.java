package com.munch.exchange.model.core.ib.chart.filter;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.SwissArmyKnifeIndicator;
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
public class IbChartSmooth extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4344644928610589015L;
	
	public static final String SERIE_SMOOTH="Smooth Filter";
	
	public static final String PARAM_PRICE="Price";

	
	
	public IbChartSmooth() {
		super();
	}
	
	
	public IbChartSmooth(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartSmooth();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Smooth";
	}


	@Override
	public void createSeries() {
		
//		Two Pole Smooth Filter
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_SMOOTH,RendererType.MAIN,true,true,150, 44, 89));
		

		
	}


	@Override
	public void createParameters() {
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		double[] smooth=SwissArmyKnifeIndicator.Smooth(prices);
		
		refreshSerieValues(this.name+" "+SERIE_SMOOTH, reset, times, smooth, 2);
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PARAM_PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
