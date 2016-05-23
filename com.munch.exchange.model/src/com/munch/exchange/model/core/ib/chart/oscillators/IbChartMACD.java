package com.munch.exchange.model.core.ib.chart.oscillators;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
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
public class IbChartMACD extends IbChartIndicator {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7599234527907676636L;
	
	public static final String MACD_Serie="Moving Average Convergence/Divergence";
	public static final String SIGNAL_Serie="Signal";
	public static final String FAST_MA_Serie="Fast Moving Average";
	public static final String SLOW_MA_Serie="Slow Moving Average";
	
	public static final String MOV_AVE_ALG="Moving Average Algorithm";
	public static final String SLOW_MA="Slow Moving Average";
	public static final String FAST_MA="FAST Moving Average";
	public static final String SIGNAL_PERIOD="Signal Period";
	
	public static final String PRICE="Price";

	
	
	public IbChartMACD() {
		super();
	}
	
	
	public IbChartMACD(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartMACD();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="MACD: Moving Average Convergence/Divergence";
	}


	@Override
	public void createSeries() {
		
//		MACD
		this.series.add(new IbChartSerie(this,MACD_Serie,RendererType.SECOND,true,true,50, 44, 89));
		
//		SIGNAL
		this.series.add(new IbChartSerie(this,SIGNAL_Serie,RendererType.SECOND,false,true,50, 239, 89));
		
//		SLOW MA
		this.series.add(new IbChartSerie(this,SLOW_MA_Serie,RendererType.MAIN,false,true,200, 44, 1));
		
//		FAST MA
		this.series.add(new IbChartSerie(this,FAST_MA_Serie,RendererType.MAIN,false,true,200, 44, 200));
		
	}


	@Override
	public void createParameters() {
		
//		SLOW MA
		this.parameters.add(new IbChartParameter(this, SLOW_MA,ParameterType.INTEGER, 26, 15, 200, 0));
		
//		FAST MA
		this.parameters.add(new IbChartParameter(this, FAST_MA,ParameterType.INTEGER, 12, 1, 50, 0));
		
//		SIGNAL PERIOD
		IbChartParameter param=new IbChartParameter(this, SIGNAL_PERIOD,ParameterType.INTEGER, 9, 1, 200, 0);
		this.parameters.add(param);
			
//		MOVING AVERAGE ALGORITHM
		IbChartParameter algo=new IbChartParameter(this, MOV_AVE_ALG,MACD.defaultAlgorithm,MACD.algorithms);
		this.parameters.add(algo);
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int slowMA=this.getChartParameter(SLOW_MA).getIntegerValue();
		int fastMA=this.getChartParameter(FAST_MA).getIntegerValue();
		int signalPeriod=this.getChartParameter(SIGNAL_PERIOD).getIntegerValue();
		
		int validAtPosition=Math.max(slowMA, Math.max(fastMA, signalPeriod))-1;
		
		String algorithm=this.getChartParameter(MOV_AVE_ALG).getStringValue();
		
//		MACD
		double[][] R=MACD.compute(algorithm,prices,slowMA,fastMA,signalPeriod);
		
		
		refreshSerieValues(MACD_Serie, 		reset, times, R[0], validAtPosition);
		refreshSerieValues(SIGNAL_Serie, 	reset, times, R[1], validAtPosition);
		refreshSerieValues(SLOW_MA_Serie, 	reset, times, R[2], validAtPosition);
		refreshSerieValues(FAST_MA_Serie, 	reset, times, R[3], validAtPosition);
		

		
	}
	
	
	
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
