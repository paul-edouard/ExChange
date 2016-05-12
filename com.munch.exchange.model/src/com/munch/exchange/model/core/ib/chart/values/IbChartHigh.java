package com.munch.exchange.model.core.ib.chart.values;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartHigh extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -386418000285846146L;
	
	
	public static final String HIGH="Values: High";
	
	
	public IbChartHigh() {
		super();
	}
	
	
	public IbChartHigh(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartHigh();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="High";
	}

	@Override
	public void createSeries() {
//		"Values: Open"
		this.series.add(new IbChartSerie(this,HIGH,RendererType.MAIN,true,true,250,0,250));

	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		long[] times=BarUtils.getTimeArray(bars);
		
		if(reset){
			this.getChartSerie(HIGH).setPointValues(times,prices);
			this.getChartSerie(HIGH).setValidAtPosition(1);
		}
		else{
			this.getChartSerie(HIGH).addNewPointsOnly(times, prices);
		}
		
	}

}
