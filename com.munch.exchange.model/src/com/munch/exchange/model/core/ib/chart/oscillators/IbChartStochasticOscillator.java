package com.munch.exchange.model.core.ib.chart.oscillators;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.AverageTrueRange;
import com.munch.exchange.model.analytic.indicator.oscillators.StochasticOscillator;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;

@Entity
public class IbChartStochasticOscillator extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final String K_Serie="K Serie";
	public static final String D_Serie="D Serie";
	public static final String KD_UPPER_LINE_Serie="Stochastic Oscillator Upper Line";
	public static final String KD_LOWER_LINE_Serie="Stochastic Oscillator Lower Line";
	public static final String KD_ACTIVATION_Serie="Stochastic Oscillator Activation";
	
	
	public static final String PERIOD="Period";
	public static final String SMA_PERIOD="SMA Period";
	public static final String SIGNAL_LINE_POSITION="Signal Line Position";
	
	public IbChartStochasticOscillator() {
		super();
	}
	
	
	public IbChartStochasticOscillator(IbChartIndicatorGroup group) {
		super(group);
	}

	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartStochasticOscillator();
		c.copyData(this);
		return c;
	}

	@Override
	public void initName() {
		this.name="Stochastic Oscillator";
	}

	@Override
	public void createSeries() {
		
//		K
		this.series.add(new IbChartSerie(this,K_Serie,RendererType.PERCENT,true,true,50, 44, 89));

//		D
		this.series.add(new IbChartSerie(this,D_Serie,RendererType.PERCENT,false,true,150, 144, 89));
		
//		Stochastic Oscillator: UPPER LINE
		this.series.add(new IbChartSerie(this,KD_UPPER_LINE_Serie,RendererType.PERCENT,false,true,240, 0, 240));
		
//		Stochastic Oscillator: LOWER LINE
		this.series.add(new IbChartSerie(this,KD_LOWER_LINE_Serie,RendererType.PERCENT,false,true,240, 0, 240));
		
//		Stochastic Oscillator: LOWER LINE
		this.series.add(new IbChartSerie(this,KD_ACTIVATION_Serie,RendererType.SECOND,false,false,240, 100, 0));


	}

	@Override
	public void createParameters() {
//		PERIOD
		this.parameters.add(new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 14, 1, 200, 0));
		
//		SMA PERIOD
		this.parameters.add(new IbChartParameter(this, SMA_PERIOD,ParameterType.INTEGER, 3, 1, 50, 0));

//		SIGNAL LINE POSITION
		this.parameters.add(new IbChartParameter(this, SIGNAL_LINE_POSITION,ParameterType.DOUBLE, 0.2, 0.01, 0.49, 2));

		
	}

	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] close=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		
		long[] times=BarUtils.getTimeArray(bars);
		
		int period = this.getChartParameter(PERIOD).getIntegerValue();
		int sma_period = this.getChartParameter(SMA_PERIOD).getIntegerValue();
		double line_pos = this.getChartParameter(SIGNAL_LINE_POSITION).getValue();
		
		double[][] KD=StochasticOscillator.calculateKD(close, high, low,period,sma_period);
		double[] K=KD[0];
		double[] D=KD[1];
		
		
//		POSITION & ACTIVATION
		double[] UP_POS = new double[D.length];
		double[] LO_POS = new double[D.length];
		double[] ACT = new double[D.length];
		
		
		for(int i = 0; i< D.length; i++){
			UP_POS[i] = (1 - line_pos)*100;
			LO_POS[i] = line_pos*100;
			
			if(D[i] > UP_POS[i]){
				ACT[i] = -1.0;
			}
			else if(D[i] < LO_POS[i]){
				ACT[i] = 1.0;
			}
			
			
		}
		
		int validAtPosition =  period + sma_period -1;
		
		refreshSerieValues(K_Serie, 		reset, times, K, period -1);
		refreshSerieValues(D_Serie, 		reset, times, D, validAtPosition);
		
		refreshSerieValues(KD_UPPER_LINE_Serie,reset, times, UP_POS, 	validAtPosition);
		refreshSerieValues(KD_LOWER_LINE_Serie,reset, times, LO_POS, 	validAtPosition);
		refreshSerieValues(KD_ACTIVATION_Serie,reset, times, ACT, 		validAtPosition);
		

	}

}
