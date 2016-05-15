package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
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
public class IbChartSimpleMovingAverage extends IbChartIndicator {
	
	
	public static final String SMA="Simple Moving Average";
	public static final String EMA="Exponential Moving Average";
	public static final String SMMA="Smoothed Moving Average";
	public static final String LWMA="Linear Weighted Moving Average";
	public static final String PERIOD="Period";
	public static final String PRICE="Price";

	/**
	 * 
	 */
	private static final long serialVersionUID = -828064667118819957L;
	
	
	public IbChartSimpleMovingAverage() {
		super();
	}
	
	
	public IbChartSimpleMovingAverage(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartSimpleMovingAverage();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Simple Moving Average";
	}


	@Override
	public void createSeries() {
		
//		Simple Moving Average
		this.series.add(new IbChartSerie(this,SMA,RendererType.MAIN,true,true,50, 44, 89));
		
//		Exponential Moving Average
		this.series.add(new IbChartSerie(this,EMA,RendererType.MAIN,false,false,156, 47, 43));
		
//		Smoothed Moving Average
		this.series.add(new IbChartSerie(this,SMMA,RendererType.MAIN,false,false,156, 47, 43));
		
//		Linear Weighted Moving Average
		this.series.add(new IbChartSerie(this,LWMA,RendererType.MAIN,false,false,240, 47, 3));
		
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
		double[] sma=MovingAverage.SMA(prices,
			this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(SMA).setPointValues(times,sma);
			this.getChartSerie(SMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(SMA).addNewPointsOnly(times,sma);
		}
		
//		EMA
		double[] ema=MovingAverage.EMA(prices,
		this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(EMA).setPointValues(times,ema);
			this.getChartSerie(EMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(EMA).addNewPointsOnly(times,ema);
		}	
		
//		SMMA
		double[] smma=MovingAverage.SMMA(prices,
		this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(SMMA).setPointValues(times,smma);
			this.getChartSerie(SMMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(SMMA).addNewPointsOnly(times,smma);
		}
		
//		LWMA
		double[] lwma=MovingAverage.LWMA(prices,
		this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(LWMA).setPointValues(times,lwma);
			this.getChartSerie(LWMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(LWMA).addNewPointsOnly(times,lwma);
		}
		
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
