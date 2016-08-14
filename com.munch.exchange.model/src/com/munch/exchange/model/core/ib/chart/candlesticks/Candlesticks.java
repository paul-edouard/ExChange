package com.munch.exchange.model.core.ib.chart.candlesticks;

import java.util.List;

import com.munch.exchange.model.analytic.indicator.candlesticks.ExchangeCore;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public abstract class Candlesticks extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1376346615903252056L;

	public static final String SERIE_SIGNAL="";

	public Candlesticks() {
		super();
	}

	public Candlesticks(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_SIGNAL,RendererType.SECOND,true,true,50, 144, 189));

	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		if(bars.isEmpty())return;
		
		long[] times=BarUtils.getTimeArray(bars);
		
		double[] inHigh=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] inLow=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		double[] inClose=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		double[] inOpen=BarUtils.barsToDoubleArray(bars, DataType.OPEN);
		
		int[] outInteger = new int[inClose.length];
		MInteger outBegIdx = new MInteger();
		MInteger outNBElement = new MInteger();
		
		Core lib = new ExchangeCore();
		
		callCdlStickFunction(lib, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
		
		double[] outDoubles = new double[inClose.length];
		
		for(int i=0;i<outInteger.length-outBegIdx.value;i++){
			if(Math.abs(outInteger[i])<100 && i>0){
				outDoubles[i+outBegIdx.value]=outDoubles[i+outBegIdx.value-1]*0.6666;
			}
			else{
				outDoubles[i+outBegIdx.value]=outInteger[i];
			}
			
		}
		
		refreshSerieValues(this.name+" "+SERIE_SIGNAL, reset, times, outDoubles, outBegIdx.value);

	}
	
	
	protected abstract RetCode callCdlStickFunction(Core lib, double[] inOpen, double[] inHigh, double[] inLow, double[] inClose, MInteger outBegIdx, MInteger outNBElement, int[] outInteger);
	
	
	
	
	

}
