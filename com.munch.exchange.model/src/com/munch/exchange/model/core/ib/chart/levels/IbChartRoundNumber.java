package com.munch.exchange.model.core.ib.chart.levels;

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
public class IbChartRoundNumber extends IbChartIndicator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4810238346550873960L;
	
	public static final String SERIE_RN_UP="Up";
	public static final String SERIE_RN_MID="Mid";
	public static final String SERIE_RN_DOWN="Down";
	
	public static final String SERIE_RN_ENERGY="Energy";
	public static final String SERIE_RN_RL_DISTANCE="Relativ Distance";
	
	
	public static final String PARAM_NUMBER="Number";
	public static final String PARAM_DECIMAL="Decimal";

		
	public IbChartRoundNumber() {
		super();
	}

	public IbChartRoundNumber(IbChartIndicatorGroup group) {
		super(group);
	}
	
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartRoundNumber();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Round Number";
	}

	@Override
	public void createSeries() {
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_RN_UP,RendererType.MAIN,false,true,50, 144, 189));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_RN_MID,RendererType.MAIN,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_RN_DOWN,RendererType.MAIN,false,true,50, 144, 189));
	
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_RN_ENERGY,RendererType.PERCENT,false,false,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_RN_RL_DISTANCE,RendererType.PERCENT,false,true,150, 144, 189));
		
	}

	@Override
	public void createParameters() {
//		NUMBER
		this.parameters.add(new IbChartParameter(this, PARAM_NUMBER,ParameterType.INTEGER, 1, 1, 9, 0));	
//		DECIMAL
		this.parameters.add(new IbChartParameter(this, PARAM_DECIMAL,ParameterType.INTEGER, -3, -4, 3, 0));	


	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		
		double[] close=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		long[] times=BarUtils.getTimeArray(bars);
		
		int number=this.getChartParameter(PARAM_NUMBER).getIntegerValue();
		int decimal=this.getChartParameter(PARAM_DECIMAL).getIntegerValue();
//		
		
		double[] mid = new double[close.length];
		double[] up = new double[close.length];
		double[] down = new double[close.length];
		
		double[] ernergy = new double[close.length];
		double[] rl_distance = new double[close.length];
		
		
		double range = number * Math.pow(10, decimal);
		
		mid[0] = Math.rint(close[0]/(range))*range;
		up[0] = mid[0]+range;
		down[0] = mid[0]-range;
		
		for(int i=1;i<close.length;i++){
			
			ernergy[i] = Math.sin(2*Math.PI*close[i]/range - Math.PI/2);
			
			if(close[i] > up[i-1]){
				
				up[i]=up[i-1]+range;
				mid[i]=up[i-1];
				down[i]=down[i-1]+range;
				
				down[i-1]=Double.NaN;
				up[i-1]=Double.NaN;
				mid[i-1]=Double.NaN;
				
//				rl_distance[i] = 2*(getMinValue(close[i], up[i], mid[i], down[i])) / range; 
				rl_distance[i] = (close[i] - mid[i]) / range; 
				
				continue;
			}
			else if(close[i] < down[i-1]){
				
				up[i]=up[i-1]-range;
				mid[i]=down[i-1];
				down[i]=down[i-1]-range;
				
				up[i-1]=Double.NaN;
				down[i-1]=Double.NaN;
				mid[i-1]=Double.NaN;
				
//				rl_distance[i] = 2*( getMinValue(close[i], up[i], mid[i], down[i])) / range; 
				rl_distance[i] = (close[i] - mid[i]) / range;
				
				continue;
			}
			
			mid[i]=mid[i-1];
			up[i]=up[i-1];
			down[i]=down[i-1];
			
//			rl_distance[i] = 2*(getMinValue(close[i], up[i], mid[i], down[i]) )/ range; 
			rl_distance[i] = (close[i] - mid[i]) / range;
			
		}
		
		
	
		
		refreshSerieValues(this.name+" "+SERIE_RN_UP, reset, times, up, 1);
		refreshSerieValues(this.name+" "+SERIE_RN_MID, reset, times, mid, 1);
		refreshSerieValues(this.name+" "+SERIE_RN_DOWN, reset, times, down, 1);
	
		refreshSerieValues(this.name+" "+SERIE_RN_ENERGY, reset, times, ernergy, 1);
		refreshSerieValues(this.name+" "+SERIE_RN_RL_DISTANCE, reset, times, rl_distance, 1);
		

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
