package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.trend.AverageDirectionalMovementIndexWilder;
import com.munch.exchange.model.analytic.indicator.trend.BollingerBands;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.Resistance;
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
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;

@Entity
public class ZeroLagStrategy extends IbChartSignal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7100998942949490912L;
	
	public static final String SERIE_ZL="Zero Lag";
	public static final String SERIE_EMA="Exponentiel Moving Average";
	
	public static final String PARAM_PERIOD="Period";
	public static final String PARAM_THRESH = "Thresh";
	public static final String PARAM_GAIN_LIMIT = "Gain Limit";
	public static final String PARAM_PRICE="Price";
	

	public ZeroLagStrategy() {
		super();
	}

	public ZeroLagStrategy(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new ZeroLagStrategy();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "Zero Lag Strategy";
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
		
//		RESISTANCE RANGE
		this.parameters.add(new IbChartParameter(this, PARAM_THRESH,ParameterType.DOUBLE, 1, 0.001, 1, 3));
	
		

	}
	
	
	@Override
	public void createSeries() {
		
//		Adaptive Moving Average
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_ZL,RendererType.MAIN,true,true,150, 44, 89));

//		Adaptive Moving Average
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_EMA,RendererType.MAIN,true,true,150, 144, 89));

		
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		int param1 = this.getChartParameter(PARAM_PERIOD).getIntegerValue();
		validAtPosition=Math.max(param1, validAtPosition);
		
		return validAtPosition;
	}
	
	

	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PARAM_PERIOD).getIntegerValue();
		int gainLimit=this.getChartParameter(PARAM_GAIN_LIMIT).getIntegerValue();
		double thresh = this.getChartParameter(PARAM_THRESH).getValue();

		double[][] R =ZeroLag.signal(prices, period, gainLimit, thresh) ;
		
		
		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, R[2],getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_ZL, reset, times, R[1], getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_EMA, reset, times, R[0], getValidAtPosition());
		
		
		
	}
	
	
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PARAM_PRICE).getStringValue();
		double[] prices=getDataFromBars(bars, DataType.fromString(priceLabel));
		
		return prices;
	}
	

	

}
