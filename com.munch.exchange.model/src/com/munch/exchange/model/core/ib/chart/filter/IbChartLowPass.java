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
public class IbChartLowPass extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4344644928610589015L;
	
	public static final String SERIE_GAUSS="Two Pole Gaussian Filter";
	public static final String SERIE_BUTTER="Two Pole Butterworth Filter";
	
	public static final String PARAM_PERIOD="Period";
	public static final String PARAM_PRICE="Price";

	
	
	public IbChartLowPass() {
		super();
	}
	
	
	public IbChartLowPass(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartLowPass();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Low Pass";
	}


	@Override
	public void createSeries() {
		
//		Two Pole Gaussian Filter
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_GAUSS,RendererType.MAIN,true,true,150, 44, 89));
		
//		Two Pole Butterworth Filter
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_BUTTER,RendererType.MAIN,true,true,150, 144, 89));

		
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
		
		double[] gauss=SwissArmyKnifeIndicator.Gauss(prices, period);
		double[] butter=SwissArmyKnifeIndicator.Butter(prices, period);
		
		refreshSerieValues(this.name+" "+SERIE_GAUSS, reset, times, gauss, period -1 );
		refreshSerieValues(this.name+" "+SERIE_BUTTER, reset, times, butter, period -1 );
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PARAM_PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
