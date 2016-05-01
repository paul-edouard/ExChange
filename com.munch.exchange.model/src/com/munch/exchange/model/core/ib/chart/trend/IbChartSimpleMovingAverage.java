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
	
	
	public static final String SMA="SMA";
	public static final String PERIOD="Period";

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
		int[] color=new int[3];
		color[0]=50;
		color[1]=44;
		color[2]=89;
		//int[] color={50,44,89};
		IbChartSerie serie=new IbChartSerie(this,SMA,RendererType.MAIN,true,true,color);
		this.series.add(serie);
		
	}


	@Override
	public void createParameters() {
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);	
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		long[] times=BarUtils.getTimeArray(bars);
		double[] sma=MovingAverage.SMA(prices,
			this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(SMA).setPointValues(times,sma);
			this.getChartSerie(SMA).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(SMA).addNewPointsOnly(times,sma);
		}
		
	}

	


}
