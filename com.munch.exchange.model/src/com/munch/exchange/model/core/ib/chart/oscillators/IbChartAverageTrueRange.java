package com.munch.exchange.model.core.ib.chart.oscillators;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.AverageTrueRange;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartAverageTrueRange extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final String ATR="Average True Range Serie";
	public static final String PERIOD="Period";
	
	public IbChartAverageTrueRange() {
		super();
	}
	
	
	public IbChartAverageTrueRange(IbChartIndicatorGroup group) {
		super(group);
	}

	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartAverageTrueRange();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Average True Range";
	}

	@Override
	public void createSeries() {
		int[] color=new int[3];
		color[0]=145;
		color[1]=20;
		color[2]=89;
		//int[] color={50,44,89};
		IbChartSerie serie=new IbChartSerie(this,ATR,RendererType.SECOND,true,true,color);
		this.series.add(serie);

	}

	@Override
	public void createParameters() {
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param);	
	}

	@Override
	protected void computeSeriesPointValues(List<IbBar> bars, boolean reset) {
		double[] close=IbBar.barsToDoubleArray(bars, DataType.CLOSE);
		double[] high=IbBar.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=IbBar.barsToDoubleArray(bars, DataType.LOW);
		
		long[] times=IbBar.getTimeArray(bars);
		
		double[] sma=AverageTrueRange.compute(close, high, low,
			this.getChartParameter(PERIOD).getIntegerValue());
		if(reset){
			this.getChartSerie(ATR).setPointValues(times,sma);
			this.getChartSerie(ATR).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(ATR).addNewPointsOnly(times,sma);
		}

	}

}
