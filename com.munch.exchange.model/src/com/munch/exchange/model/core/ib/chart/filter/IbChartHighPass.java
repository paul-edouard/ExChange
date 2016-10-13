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
public class IbChartHighPass extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4344644928610589015L;
	
	public static final String SERIE_HP="High Pass Filter";
	public static final String SERIE_2PHP="Two Pole High Pass Filter";
	
	public static final String PARAM_PERIOD="Period";
	public static final String PARAM_PRICE="Price";

	
	
	public IbChartHighPass() {
		super();
	}
	
	
	public IbChartHighPass(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartHighPass();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="High Pass";
	}


	@Override
	public void createSeries() {
		
//		Two Pole Gaussian Filter
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_HP,RendererType.SECOND,true,true,150, 44, 89));
		
//		Two Pole Butterworth Filter
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_2PHP,RendererType.SECOND,true,true,150, 144, 89));

		
	}


	@Override
	public void createParameters() {
		
//		PERIOD
		IbChartParameter param=new IbChartParameter(this, PARAM_PERIOD,ParameterType.INTEGER, 12, 2, 200, 0);
		this.parameters.add(param);
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PARAM_PERIOD).getIntegerValue();
		
		double[] hp=SwissArmyKnifeIndicator.HP(prices, period);
		double[] twophp=SwissArmyKnifeIndicator.TwoPHP(prices, period);
		
		refreshSerieValues(this.name+" "+SERIE_HP, reset, times, hp, period -1 );
		refreshSerieValues(this.name+" "+SERIE_2PHP, reset, times, twophp, period -1 );
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PARAM_PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
