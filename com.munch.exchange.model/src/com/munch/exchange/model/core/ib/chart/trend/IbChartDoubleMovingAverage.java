package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.DoubleMovingAverage;
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
public class IbChartDoubleMovingAverage extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1368898386469901055L;
	
	public static final String DMA="Double Moving Average";
	public static final String DEMA="Double Exponential Moving Average";
	public static final String DSMMA="Double Smoothed Moving Average";
	public static final String DLWMA="Double Linear Weighted Moving Average";
	public static final String PERIOD="Period";
	public static final String PRICE="Price";

	
	
	public IbChartDoubleMovingAverage() {
		super();
	}
	
	
	public IbChartDoubleMovingAverage(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartDoubleMovingAverage();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="Double Moving Average";
	}


	@Override
	public void createSeries() {
		
//		Simple Moving Average
		this.series.add(new IbChartSerie(this,DMA,RendererType.MAIN,true,true,50, 44, 89));
		
//		Exponential Moving Average
		this.series.add(new IbChartSerie(this,DEMA,RendererType.MAIN,false,false,156, 47, 43));
		
//		Smoothed Moving Average
		this.series.add(new IbChartSerie(this,DSMMA,RendererType.MAIN,false,false,156, 47, 43));
		
//		Linear Weighted Moving Average
		this.series.add(new IbChartSerie(this,DLWMA,RendererType.MAIN,false,false,240, 47, 3));
		
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
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		
//		SMA
		double[] sma=DoubleMovingAverage.DMA(prices,period);
		if(reset){
			this.getChartSerie(DMA).setPointValues(times,sma);
			this.getChartSerie(DMA).setValidAtPosition(period-1);
		}
		else{
			this.getChartSerie(DMA).addNewPointsOnly(times,sma);
		}
		
//		EMA
		double[] ema=DoubleMovingAverage.DEMA(prices,period);
		if(reset){
			this.getChartSerie(DEMA).setPointValues(times,ema);
			this.getChartSerie(DEMA).setValidAtPosition(period-1);
		}
		else{
			this.getChartSerie(DEMA).addNewPointsOnly(times,ema);
		}	
		
//		SMMA
		double[] smma=DoubleMovingAverage.DSMMA(prices,period);
		if(reset){
			this.getChartSerie(DSMMA).setPointValues(times,smma);
			this.getChartSerie(DSMMA).setValidAtPosition(period-1);
		}
		else{
			this.getChartSerie(DSMMA).addNewPointsOnly(times,smma);
		}
		
//		LWMA
		double[] lwma=DoubleMovingAverage.DLWMA(prices,period);
		if(reset){
			this.getChartSerie(DLWMA).setPointValues(times,lwma);
			this.getChartSerie(DLWMA).setValidAtPosition(period-1);
		}
		else{
			this.getChartSerie(DLWMA).addNewPointsOnly(times,lwma);
		}
		
		
	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
