package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

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
public class IbChartResistanceLine extends IbChartIndicator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;
	
	public static final String MAX_RES_LINE="Max Resistance Line";
	public static final String MIN_RES_LINE="Min Resistance Line";
	
	public static final String MAX_BREAKOUT_VALUE="Max Breakout Value";
	public static final String MIN_BREAKOUT_VALUE="Min Breakout Value";
	
	public static final String MAX_RES_VALUE="Max Resistance Value";
	public static final String MIN_RES_VALUE="Min Resistance Value";
	
	public static final String MAX_RANGE_UP="Max Range Up";
	public static final String MAX_RANGE_DOWN="Max Range Down";
	
	public static final String MIN_RANGE_UP="Min Range Up";
	public static final String MIN_RANGE_DOWN="Min Range Down";
	
	
	public static final String PERIOD="Period";
	public static final String MAX_RESISTANCE_SEARCH_PERIOD="Max Resistance Search Period";
	public static final String RESISTANCE_RANGE="Resistance Range";
	

	
	public IbChartResistanceLine() {
		super();
	}

	public IbChartResistanceLine(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartResistanceLine();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Resistance Line";
	}

	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+MAX_RES_LINE,RendererType.MAIN,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_RES_LINE,RendererType.MAIN,true,true,50, 244, 189));
		
		this.series.add(new IbChartSerie(this,this.name+" "+MAX_RANGE_UP,RendererType.MAIN,false,true,50, 144, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+MAX_RANGE_DOWN,RendererType.MAIN,false,true,50, 144, 89));
		
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_RANGE_UP,RendererType.MAIN,false,true,150, 244, 189));
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_RANGE_DOWN,RendererType.MAIN,false,true,150, 244, 189));
		
		
		this.series.add(new IbChartSerie(this,this.name+" "+MAX_RES_VALUE,RendererType.SECOND,false,false,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_RES_VALUE,RendererType.SECOND,false,false,50, 244, 189));
		
		this.series.add(new IbChartSerie(this,this.name+" "+MAX_BREAKOUT_VALUE,RendererType.SECOND,false,false,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_BREAKOUT_VALUE,RendererType.SECOND,false,false,50, 244, 189));
		
		
		
		
	}

	@Override
	public void createParameters() {
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 30, 1, 200, 0));

//		MAX RESISTANCE SEARCH PERIOD
		this.parameters.add(new IbChartParameter(this, MAX_RESISTANCE_SEARCH_PERIOD,ParameterType.INTEGER, 1000, 500, 2000, 0));		
		
//		RESISTANCE RANGE
		this.parameters.add(new IbChartParameter(this, RESISTANCE_RANGE,ParameterType.DOUBLE, 0.0001, 0.00001, 0.005, 5));

		
	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int maxResSearchPeriod=this.getChartParameter(MAX_RESISTANCE_SEARCH_PERIOD).getIntegerValue();
		double range = this.getChartParameter(RESISTANCE_RANGE).getValue();

		long[] times=BarUtils.getTimeArray(bars);
		
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		
		
		double[][] RES = Resistance.compute(high, low, period, range, maxResSearchPeriod);
		
		double[] maxRangeUp = new double[high.length];
		double[] maxRangeDown = new double[high.length];
		
		double[] minRangeUp = new double[high.length];
		double[] minRangeDown = new double[high.length];
		
		for(int i=0;i<high.length;i++){
			maxRangeUp[i] = RES[0][i]*(1+range);
			maxRangeDown[i] = RES[0][i]*(1-range);
			
			minRangeUp[i] = RES[1][i]*(1+range);
			minRangeDown[i] = RES[1][i]*(1-range);
		}
		
		
		refreshSerieValues(this.name+" "+MAX_RES_LINE, reset, times, RES[0], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+MIN_RES_LINE, reset, times, RES[1], maxResSearchPeriod-1);
		
		refreshSerieValues(this.name+" "+MAX_RANGE_UP, reset, times, maxRangeUp, maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+MAX_RANGE_DOWN, reset, times, maxRangeDown, maxResSearchPeriod-1);
		
		refreshSerieValues(this.name+" "+MIN_RANGE_UP, reset, times, 	minRangeUp, maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+MIN_RANGE_DOWN, reset, times, 	minRangeDown, maxResSearchPeriod-1);
		
		refreshSerieValues(this.name+" "+MAX_RES_VALUE, reset, times, RES[2], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+MIN_RES_VALUE, reset, times, RES[3], maxResSearchPeriod-1);
		
		
		refreshSerieValues(this.name+" "+MAX_BREAKOUT_VALUE, reset, times, RES[6], maxResSearchPeriod-1);
		refreshSerieValues(this.name+" "+MIN_BREAKOUT_VALUE, reset, times, RES[7], maxResSearchPeriod-1);
		
		

	}
	
	
	

}
