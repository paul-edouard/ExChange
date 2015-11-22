package com.munch.exchange.model.core.ib.chart.signals;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.SuperTrend;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.chart.trend.IbChartSuperTrend;


@Entity
public class SuperTrendSignal extends IbChartSignal {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5565692656448939936L;
	
	
	public static final String ST_UP="Trend Limit Up";
	public static final String ST_DN="Trend Limit Down";
	public static final String PERIOD="Period";
	public static final String FACTOR="Factor";
	
	

	public SuperTrendSignal() {
		super();
	}

	public SuperTrendSignal(IbChartIndicatorGroup group) {
		super(group);
	}
	
	

	@Override
	public void createSeries() {
		

		int[] colorB=new int[3];
		colorB[0]=10;
		colorB[1]=10;
		colorB[2]=200;
		IbChartSerie serie_up=new IbChartSerie(this,this.name+" "+ST_UP,RendererType.MAIN,false,true,colorB);
		this.series.add(serie_up);
		
		int[] colorR=new int[3];
		colorR[0]=200;
		colorR[1]=10;
		colorR[2]=10;
		IbChartSerie serie_down=new IbChartSerie(this,this.name+" "+ST_DN,RendererType.MAIN,false,true,colorR);
		this.series.add(serie_down);
		
		super.createSeries();
	}

	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new SuperTrendSignal();
		c.copyData(this);
		return c;
	}

	@Override
	public void computeSignalPointFromBarBlock(List<IbBar> bars, boolean reset) {
		
		long[] times=this.getTimeArray(bars);
		
		//Set all signal point to -1
		//System.out.println("Bar Size: "+bars.size()+", Valid at position: "+this.getValidAtPosition());
		if(bars.size()<this.getValidAtPosition() || bars.size()<=1){
			for(int i=0;i<times.length;i++){
				this.getSignalSerie().addPoint(times[i],-1);
				this.getChartSerie(this.name+" "+ST_UP).addPoint(times[i],Double.NaN);
				this.getChartSerie(this.name+" "+ST_DN).addPoint(times[i],Double.NaN);
			}
			return;
		}
		
		
		double[] close=this.barsToDoubleArray(bars, DataType.CLOSE);
		double[] high=this.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=this.barsToDoubleArray(bars, DataType.LOW);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		double factor=this.getChartParameter(FACTOR).getValue();
		double startTrend=-1.0;
		
		if(!reset){
			//Compute only last values
			IbChartSerie trend=this.getSignalSerie();
			if(trend.getPoints().size()>trend.getValidAtPosition()){
				startTrend=trend.getPoints().get(trend.getPoints().size()-trend.getValidAtPosition()).getValue();
			}
			
		}
		else{
			this.getSignalSerie().setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue());
			this.getChartSerie(this.name+" "+ST_UP).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue());
			this.getChartSerie(this.name+" "+ST_DN).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue());
			
		}
		
		//Compute all super trend values
		double[][] SuTr=SuperTrend.compute(close, high, low, period, factor,startTrend);
		
		this.getSignalSerie().addNewPointsOnly(times,SuTr[0]);
		this.getChartSerie(this.name+" "+ST_UP).addNewPointsOnly(times,SuTr[1]);
		this.getChartSerie(this.name+" "+ST_DN).addNewPointsOnly(times,SuTr[2]);
		
		
	}

	@Override
	protected int getValidAtPosition() {
		return this.getChartParameter(PERIOD).getIntegerValue();
	}

	@Override
	public void initName() {
		this.name= "Super Trend Signal";

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

}
