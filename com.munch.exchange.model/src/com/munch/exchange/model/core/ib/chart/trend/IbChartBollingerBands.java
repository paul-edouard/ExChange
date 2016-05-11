package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.trend.BollingerBands;
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
public class IbChartBollingerBands extends IbChartIndicator {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1298603239008905742L;
	
	
	public static final String BB="Bollinger Bands";
	public static final String BB_ML="Bollinger Bands: Middle Line";
	public static final String BB_TL="Bollinger Bands: Top Line";
	public static final String BB_TL_UT="Bollinger Bands: Top Line Up Trend";
	
	public static final String BB_BL="Bollinger Bands: Bottom Line";
	public static final String BB_BL_DT="Bollinger Bands: Bottom Line Down Trend";
	
	public static final String BB_RD="Bollinger Bands: Relativ Distance";
	public static final String BB_RD_DML="Bollinger Bands: Relativ Distance x Dev Mid Line";
	public static final String PERIOD="Period";
	public static final String FACTOR="Factor";
	
	
	public IbChartBollingerBands() {
		super();
	}
	
	
	public IbChartBollingerBands(IbChartIndicatorGroup group) {
		super(group);
	}
	

	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartBollingerBands();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name=BB;

	}

	@Override
	public void createSeries() {
		
//		"Bollinger Bands: Middle Line";
		this.series.add(new IbChartSerie(this,BB_ML,RendererType.MAIN,true,true,50,44,89));
		
//		"Bollinger Bands: Top Line";
		this.series.add(new IbChartSerie(this,BB_TL,RendererType.MAIN,false,true,250,0,0));
		
//		"Bollinger Bands: Bottom Line";
		this.series.add(new IbChartSerie(this,BB_BL,RendererType.MAIN,false,true,250,0,0));
		
//		"Bollinger Bands: Relativ Distance";
		this.series.add(new IbChartSerie(this,BB_RD,RendererType.PERCENT,false,false,250,0,0));

//		"Bollinger Bands: Relativ Distance x Dev Mid Line";
		this.series.add(new IbChartSerie(this,BB_RD_DML,RendererType.SECOND,false,false,0,250,0));

		
//		"Bollinger Bands: Top Line Up Trend";
		this.series.add(new IbChartSerie(this,BB_TL_UT,RendererType.SECOND,false,false,250,250,0));
		
//		"Bollinger Bands: Bottom Line Down Trend";
		this.series.add(new IbChartSerie(this,BB_BL_DT,RendererType.SECOND,false,false,250,0,250));

		
		

	}

	@Override
	public void createParameters() {
		
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 12, 1, 200, 0));
		
//		FACTOR
		this.parameters.add(new IbChartParameter(this, FACTOR,ParameterType.DOUBLE, 2, 0, 10, 1));

	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		long[] times=BarUtils.getTimeArray(bars);
		
		int N=this.getChartParameter(PERIOD).getIntegerValue();
		double D=this.getChartParameter(FACTOR).getValue();
		
		double[][] ADX=BollingerBands.computeADX(prices, N, D);
		
		
		if(reset){
			this.getChartSerie(BB_ML).setPointValues(times,ADX[0]);
			this.getChartSerie(BB_ML).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
			
			this.getChartSerie(BB_TL).setPointValues(times,ADX[1]);
			this.getChartSerie(BB_TL).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
			
			this.getChartSerie(BB_BL).setPointValues(times,ADX[2]);
			this.getChartSerie(BB_BL).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
			
			this.getChartSerie(BB_RD).setPointValues(times,ADX[3]);
			this.getChartSerie(BB_RD).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue()-1);
			
			this.getChartSerie(BB_RD_DML).setPointValues(times,ADX[4]);
			this.getChartSerie(BB_RD_DML).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue());
			
			
			this.getChartSerie(BB_TL_UT).setPointValues(times,ADX[5]);
			this.getChartSerie(BB_TL_UT).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue());
			
			this.getChartSerie(BB_BL_DT).setPointValues(times,ADX[6]);
			this.getChartSerie(BB_BL_DT).setValidAtPosition(this.getChartParameter(PERIOD).getIntegerValue());
			
			
			
		}
		else{
			this.getChartSerie(BB_ML).addNewPointsOnly(times,ADX[0]);
			this.getChartSerie(BB_TL).addNewPointsOnly(times,ADX[1]);
			this.getChartSerie(BB_BL).addNewPointsOnly(times,ADX[2]);
			this.getChartSerie(BB_RD).addNewPointsOnly(times,ADX[3]);
			this.getChartSerie(BB_RD_DML).addNewPointsOnly(times,ADX[4]);
			
			this.getChartSerie(BB_TL_UT).addNewPointsOnly(times,ADX[5]);
			this.getChartSerie(BB_BL_DT).addNewPointsOnly(times,ADX[6]);
		
		}
		
		
		
	}

}
