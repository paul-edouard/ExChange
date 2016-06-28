package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

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

@Entity
public class IbChartADX extends IbChartIndicator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;
	
	public static final String ADX="ADX";
	public static final String ADX_DIR="ADX Direction";
	
	public static final String PERIOD="Period";
	public static final String TREND_PERIOD=" Trend Period";
//	public static final String PRICE="Price";
	

	
	public IbChartADX() {
		super();
	}

	public IbChartADX(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartADX();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Average Directional Movement Index Wilder";
	}

	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+ADX,RendererType.SECOND,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+ADX_DIR,RendererType.PERCENT,false,true,50, 44, 89));

	}

	@Override
	public void createParameters() {
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 30, 1, 200, 0));	

//		TREND PERIOD
		this.parameters.add(new IbChartParameter(this, TREND_PERIOD,ParameterType.INTEGER, 4, 1, 50, 0));	
		
		
	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		double[] close=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int trend_period=this.getChartParameter(TREND_PERIOD).getIntegerValue();
		long[] times=BarUtils.getTimeArray(bars);
		
		
		double[] adx = AverageDirectionalMovementIndexWilder.computeADXWi(close, high, low, period);
		double[][] AB = TrendLine.computeAB(adx, trend_period, 1.0);
		
		
		double[] adx_diff = AB[0];
		for(int i=0;i<adx_diff.length;i++){
			adx_diff[i] = adx_diff[i] * adx[i];
		}
		
		refreshSerieValues(this.name+" "+ADX, reset, times, adx, period-1);
		refreshSerieValues(this.name+" "+ADX_DIR, reset, times, adx_diff, period-2);
		

	}
	
	

}
