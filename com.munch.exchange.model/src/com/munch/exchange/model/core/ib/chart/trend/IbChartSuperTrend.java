package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.SuperTrend;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;


@Entity
public class IbChartSuperTrend extends IbChartIndicator{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6352634644533908043L;
	
	public static final String ST_UP="Trend Limit Up";
	public static final String ST_DN="Trend Limit Down";
	public static final String ST_TR="Trend";
	public static final String PERIOD="Period";
	public static final String FACTOR="Factor";
	

	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartSuperTrend();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Super trend";
	}

	@Override
	public void createSeries() {
		
		int[] color=new int[3];
		color[0]=10;
		color[1]=10;
		color[2]=10;
		IbChartSerie serie_trend=new IbChartSerie(this,ST_TR,RendererType.SECOND,true,false,color);
		this.series.add(serie_trend);
		
		
		int[] colorB=new int[3];
		colorB[0]=10;
		colorB[1]=10;
		colorB[2]=200;
		IbChartSerie serie_up=new IbChartSerie(this,ST_UP,RendererType.MAIN,false,true,colorB);
		this.series.add(serie_up);
		
		int[] colorR=new int[3];
		colorR[0]=200;
		colorR[1]=10;
		colorR[2]=10;
		IbChartSerie serie_down=new IbChartSerie(this,ST_DN,RendererType.MAIN,false,true,colorR);
		this.series.add(serie_down);
		
	}

	@Override
	public void createParameters() {
		//Period
		IbChartParameter param_period=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0);
		this.parameters.add(param_period);
		
		//Factor
		IbChartParameter paramF=new IbChartParameter(this, FACTOR,ParameterType.DOUBLE, 5, 0.1, 20, 1);
		this.parameters.add(paramF);
		
	}


	@Override
	protected void computeSeriesPointValues(List<IbBar> bars, boolean reset) {
		double[] close=this.barsToDoubleArray(bars, DataType.CLOSE);
		double[] high=this.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=this.barsToDoubleArray(bars, DataType.LOW);
		
		long[] times=this.getTimeArray(bars);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		double factor=this.getChartParameter(FACTOR).getValue();
		
		double[][] SuTr=SuperTrend.compute(close, high, low, period, factor);
		
		if(reset){
			this.getChartSerie(ST_TR).setPointValues(times,SuTr[0]);
			this.getChartSerie(ST_UP).setPointValues(times,SuTr[1]);
			this.getChartSerie(ST_DN).setPointValues(times,SuTr[2]);
			
			this.getChartSerie(ST_TR).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
			this.getChartSerie(ST_UP).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
			this.getChartSerie(ST_DN).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
		}
		else{
			this.getChartSerie(ST_TR).addNewPointsOnly(times,SuTr[0]);
			this.getChartSerie(ST_UP).addNewPointsOnly(times,SuTr[1]);
			this.getChartSerie(ST_DN).addNewPointsOnly(times,SuTr[2]);
		}
		
	}

	

}
