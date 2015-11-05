package com.munch.exchange.model.core.ib.chart.signals;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartSimpleDerivate extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3707413090610641022L;
	
	
	public static final String SD="SD";
	

	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartSimpleDerivate();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Simple Derivate";
	}

	@Override
	public void createSeries() {
		int[] color={50,143,89};
		IbChartSerie serie=new IbChartSerie(this,SD,RendererType.SECOND,true,false,color);
		this.series.add(serie);

	}

	@Override
	public void createParameters() {

	}

	@Override
	public void compute(List<IbBar> bars) {
		double[] prices=this.barsToDoubleArray(bars, DataType.CLOSE);
		long[] times=this.getTimeArray(bars);
		double[] ama=SimpleDerivate.compute(prices);
		
		this.getChartSerie(SD).setPointValues(times,ama);
		this.getChartSerie(SD).setValidAtPosition(1);

	}

	@Override
	public void computeLast(List<IbBar> bars) {
		// TODO Auto-generated method stub

	}

}
