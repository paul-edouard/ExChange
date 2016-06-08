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
	
	public static final String MAX_RES_VALUE="Max Resistance Value";
	public static final String MIN_RES_VALUE="Min Resistance Value";
	
	public static final String MIN_MAX_DIST="Min Max Distance";
	public static final String RES_VALUE="Resistance Value";
	
	
	public static final String PERIOD="Period";
	public static final String PRICE="Price";
	

	
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
		
		this.series.add(new IbChartSerie(this,this.name+" "+MAX_RES_VALUE,RendererType.SECOND,false,false,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_RES_VALUE,RendererType.SECOND,false,false,50, 244, 189));
		
		this.series.add(new IbChartSerie(this,this.name+" "+MIN_MAX_DIST,RendererType.PERCENT,false,false,150, 150, 150));
		this.series.add(new IbChartSerie(this,this.name+" "+RES_VALUE,RendererType.SECOND,false,false,150, 150, 150));
		
	}

	@Override
	public void createParameters() {
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 30, 1, 200, 0));

//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);		
		
	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		double[] prices=getPrices(bars);
		int period=this.getChartParameter(PERIOD).getIntegerValue();
//		System.out.println("Ib Chart Resis Line Period: " + period);
		long[] times=BarUtils.getTimeArray(bars);
		
		
		double[][] RES = Resistance.compute(prices, period);
		
		refreshSerieValues(this.name+" "+MAX_RES_LINE, reset, times, RES[0], period-1);
		refreshSerieValues(this.name+" "+MIN_RES_LINE, reset, times, RES[1], period-1);
		
		refreshSerieValues(this.name+" "+MAX_RES_VALUE, reset, times, RES[2], period-1);
		refreshSerieValues(this.name+" "+MIN_RES_VALUE, reset, times, RES[3], period-1);
		
		refreshSerieValues(this.name+" "+MIN_MAX_DIST, reset, times, RES[4], period-1);
		refreshSerieValues(this.name+" "+RES_VALUE, reset, times, RES[5], period-1);
		

	}
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}
	

}
