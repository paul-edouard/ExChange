package com.munch.exchange.model.core.ib.chart.cycle;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.candlesticks.ExchangeCore;
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
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

@Entity
public class IbChartHilbertTrSineWave extends IbChartIndicator {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1867810380400438036L;

	public static final String SERIE_SINE="Sine";
	public static final String SERIE_LEAD_SINE="Lead Sine";
	
	public static final String PARAM_PRICE="Price";

	
	
	public IbChartHilbertTrSineWave() {
		super();
	}
	
	
	public IbChartHilbertTrSineWave(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartHilbertTrSineWave();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Hilbert Transform - SineWave";
	}


	@Override
	public void createSeries() {
		
//		Sine
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_SINE,RendererType.SECOND,true,true,150, 44, 89));
		
//		Lead Sine
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_LEAD_SINE,RendererType.SECOND,true,true,150, 44, 89));
		
		
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
		
	
		double[] outSine = new double[prices.length] ;
		double[] outLeadSine = new double[prices.length]; 
						
		MInteger outBegIdx = new MInteger();
		MInteger outNBElement = new MInteger();
		
		Core lib = new ExchangeCore();
		lib.htSine(0, prices.length-1, prices, outBegIdx, outNBElement, outSine, outLeadSine);
		
		double[] outDoublesSine = new double[prices.length];
		double[] outDoublesLeadSine = new double[prices.length];
		
		for(int i=0;i<outBegIdx.value;i++){
			outDoublesSine[i]=Double.NaN;
			outDoublesLeadSine[i]=Double.NaN;
		}
		for(int i=0;i<outSine.length-outBegIdx.value;i++){
			outDoublesSine[i+outBegIdx.value]=outSine[i];
		}
		
		for(int i=0;i<outLeadSine.length-outBegIdx.value;i++){
			outDoublesLeadSine[i+outBegIdx.value]=outLeadSine[i];
		}
		
		refreshSerieValues(this.name+" "+SERIE_SINE, reset, times, outDoublesSine, outBegIdx.value);
		refreshSerieValues(this.name+" "+SERIE_LEAD_SINE, reset, times, outDoublesLeadSine, outBegIdx.value);
		
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PARAM_PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
