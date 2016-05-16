package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.TripleMovingAverage;
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
public class IbChartTripleMovingAverage extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1478325913606253711L;
	
	public static final String TMA="Triple Moving Average";
	public static final String TEMA="Triple Exponential Moving Average";
	public static final String TSMMA="Triple Smoothed Moving Average";
	public static final String TLWMA="Triple Linear Weighted Moving Average";
	public static final String PERIOD="Period";
	public static final String PRICE="Price";

	
	
	public IbChartTripleMovingAverage() {
		super();
	}
	
	
	public IbChartTripleMovingAverage(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartTripleMovingAverage();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Triple Moving Average";
	}


	@Override
	public void createSeries() {
		
//		Simple Moving Average
		this.series.add(new IbChartSerie(this,TMA,RendererType.MAIN,true,true,50, 44, 89));
		
//		Exponential Moving Average
		this.series.add(new IbChartSerie(this,TEMA,RendererType.MAIN,false,false,156, 47, 43));
		
//		Smoothed Moving Average
		this.series.add(new IbChartSerie(this,TSMMA,RendererType.MAIN,false,false,156, 47, 43));
		
//		Linear Weighted Moving Average
		this.series.add(new IbChartSerie(this,TLWMA,RendererType.MAIN,false,false,240, 47, 3));
		
	}


	@Override
	public void createParameters() {
		
//		PERIOD
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);
		
		
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
//		SMA
		double[] sma=TripleMovingAverage.TMA(prices,
			this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(TMA).setPointValues(times,sma);
			this.getChartSerie(TMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(TMA).addNewPointsOnly(times,sma);
		}
		
//		EMA
		double[] ema=TripleMovingAverage.TEMA(prices,
		this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(TEMA).setPointValues(times,ema);
			this.getChartSerie(TEMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(TEMA).addNewPointsOnly(times,ema);
		}	
		
//		SMMA
		double[] smma=TripleMovingAverage.TSMMA(prices,
		this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(TSMMA).setPointValues(times,smma);
			this.getChartSerie(TSMMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(TSMMA).addNewPointsOnly(times,smma);
		}
		
//		LWMA
		double[] lwma=TripleMovingAverage.TLWMA(prices,
		this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(TLWMA).setPointValues(times,lwma);
			this.getChartSerie(TLWMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(TLWMA).addNewPointsOnly(times,lwma);
		}
		
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
