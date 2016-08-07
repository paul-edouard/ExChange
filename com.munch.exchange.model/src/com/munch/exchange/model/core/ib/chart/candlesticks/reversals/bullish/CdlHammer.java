package com.munch.exchange.model.core.ib.chart.candlesticks.reversals.bullish;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import com.ibm.icu.util.Calendar;
import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.analytic.indicator.trend.AverageDirectionalMovementIndexWilder;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.Resistance;
import com.munch.exchange.model.analytic.indicator.trenline.TrendLine;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

@Entity
public class CdlHammer extends IbChartIndicator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;
	
	public static final String SERIE_SIGNAL="";
	
	
//	private Core lib;
//    private int lookback;
	
	public CdlHammer() {
		super();
//		lib = new Core();
	}

	public CdlHammer(IbChartIndicatorGroup group) {
		super(group);
//		lib = new Core();
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new CdlHammer();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Hammer";
		this.note = "As with any single candlestick, confirmation is required.\n The Bullish Hammer formation shows the price goes much lower than the open then closes near the opening price.\n This fact reduces the confidence of the bears.\n Ideally, a white real body Hammer with a higher open the following day could be a bullish signal for the days ahead.";
	}

	@Override
	public void createSeries() {
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_SIGNAL,RendererType.SECOND,true,true,50, 144, 189));
		
		
	}

	@Override
	public void createParameters() {



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
		
		Core lib = new Core();
		
		double[] outDoubles = new double[inClose.length];
		
//		lib.cdl3BlackCrowsLookback(3);
		RetCode retCode = lib.cdlHammer(0, inClose.length-1, inOpen, inHigh, inLow, inClose, outBegIdx, outNBElement, outInteger);
//		lib.sma(0, inClose.length-1, inClose, 10, outBegIdx, outNBElement, outDoubles);
//		System.out.println("RetCode: "+retCode);
//		System.out.println("outBegIdx: "+outBegIdx.value);
//		System.out.println("outNBElement: "+outNBElement.value);
//		System.out.println("inClose.length: "+inClose.length);
		
		for(int i=0;i<outInteger.length-outBegIdx.value;i++){
			outDoubles[i+outBegIdx.value]=outInteger[i];
		}
		
//		double[] outDoubles = Arrays.stream(outInteger).asDoubleStream().toArray();
		
		
		refreshSerieValues(this.name+" "+SERIE_SIGNAL, reset, times, outDoubles, 1);
		
		

	}
	
	
	
	

}
