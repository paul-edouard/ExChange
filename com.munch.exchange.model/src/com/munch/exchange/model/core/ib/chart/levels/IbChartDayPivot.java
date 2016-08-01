package com.munch.exchange.model.core.ib.chart.levels;

import java.util.List;

import javax.persistence.Entity;

import com.ibm.icu.util.Calendar;
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
public class IbChartDayPivot extends IbChartIndicator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;
	
	public static final String SERIE_P="Central Pivot Point";
	
	public static final String SERIE_R1="First Resistance";
	public static final String SERIE_S1="First Support";
	
	public static final String SERIE_R2="Second Resistance";
	public static final String SERIE_S2="Second Support";
	
	
		
	public IbChartDayPivot() {
		super();
	}

	public IbChartDayPivot(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartDayPivot();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Day Pivot";
	}

	@Override
	public void createSeries() {
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_P,RendererType.MAIN,true,true,50, 144, 189));
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_R1,RendererType.MAIN,false,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_S1,RendererType.MAIN,false,true,50, 44, 89));
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_R2,RendererType.MAIN,false,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_S2,RendererType.MAIN,false,true,50, 44, 89));
		
		
	}

	@Override
	public void createParameters() {



	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		if(bars.isEmpty())return;
		
		long[] times=BarUtils.getTimeArray(bars);
		
		
		double[] p = new double[times.length];
		
		double[] r1 = new double[times.length];
		double[] s1 = new double[times.length];
		
		double[] r2 = new double[times.length];
		double[] s2 = new double[times.length];
		
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(bars.get(0).getTimeInMs());
		
		int day = date.get(Calendar.DAY_OF_YEAR);
		
		p[0] = r1[0] = s1[0] = r2[0] = s2[0] = Double.NaN;
		
		double dayHigh = bars.get(0).getHigh();
		double dayLow = bars.get(0).getLow();
		double close = bars.get(0).getClose();
		int i=0;
		for(ExBar bar:bars){
			if(i==0){
				i++;
				continue;
			}
			
			date.setTimeInMillis(bar.getTimeInMs());
			int currentDay = date.get(Calendar.DAY_OF_YEAR);
			
			if(currentDay !=day){
				
				p[i] = (dayHigh + dayLow + close)/3;
				
				r1[i] = (2*p[i]) - dayLow;
				s1[i] = (2*p[i]) - dayHigh;
				
				r2[i] = p[i] + (r1[i]-s1[i]);
				s2[i] = p[i] - (r1[i]-s1[i]);
								
				dayHigh = bar.getHigh();
				dayLow = bar.getLow();
				
				day = currentDay;
			}
			else{
				if(bar.getHigh()>dayHigh){
					dayHigh = bar.getHigh();
				}
				if(bar.getLow() < dayLow){
					dayLow = bar.getLow();
				}
				
				p[i] = p[i-1];
				
				r1[i] = r1[i-1];
				s1[i] = s1[i-1];
				
				r2[i] = r2[i-1];
				s2[i] = s2[i-1];

			}
			
			close = bar.getClose();
			
			i++;
			
		}
		
		
		
		refreshSerieValues(this.name+" "+SERIE_P, reset, times, p, 1);
		
		refreshSerieValues(this.name+" "+SERIE_R1, reset, times, r1, 1);
		refreshSerieValues(this.name+" "+SERIE_S1, reset, times, s1, 1);

		refreshSerieValues(this.name+" "+SERIE_R2, reset, times, r2, 1);
		refreshSerieValues(this.name+" "+SERIE_S2, reset, times, s2, 1);

	}
	
	double getMinValue(double close,double up,double mid,double down ){
		double absUp = Math.abs(close - up);
		double absMid = Math.abs(close - mid);
		double absDown = Math.abs(close - down);
		
		if(absUp <=absMid && absUp <=absDown)
			return close - up;
		
		if(absMid <=absUp && absMid <=absDown)
			return close - mid;
		
		if(absDown <=absUp && absDown <=absMid)
			return close - down;
		
		return 0;
		
	}
	
	

}
