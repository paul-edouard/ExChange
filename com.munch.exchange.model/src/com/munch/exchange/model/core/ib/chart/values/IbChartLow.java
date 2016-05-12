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
public class IbChartLow extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -386418000285846146L;
	
	
	public static final String LOW="Values: Low";
	
	
	public IbChartLow() {
		super();
	}
	
	
	public IbChartLow(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartLow();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Low";
	}

	@Override
	public void createSeries() {
//		"Values: Open"
		this.series.add(new IbChartSerie(this,LOW,RendererType.MAIN,true,true,0,250,250));

	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		long[] times=BarUtils.getTimeArray(bars);
		
		if(reset){
			this.getChartSerie(LOW).setPointValues(times,prices);
			this.getChartSerie(LOW).setValidAtPosition(1);
		}
		else{
			this.getChartSerie(LOW).addNewPointsOnly(times, prices);
		}
		
	}

}
