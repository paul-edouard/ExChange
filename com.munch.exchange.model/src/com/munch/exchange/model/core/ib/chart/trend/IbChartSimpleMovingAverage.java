package com.munch.exchange.model.core.ib.chart.trend;

import javax.persistence.Entity;

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
	
	public IbChartSimpleMovingAverage(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public void initName() {
		this.name="Simple Moving Average";
	}


	@Override
	public void createSeries() {
		int[] color={50,44,89};
		IbChartSerie serie=new IbChartSerie(this,SMA,RendererType.MAIN,true,false,color);
		this.series.add(serie);
		
	}


	@Override
	public void createParameters() {
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);	
	}

}
