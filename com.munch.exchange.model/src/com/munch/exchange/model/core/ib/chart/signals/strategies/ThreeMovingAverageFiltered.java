package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.AverageTrueRange;
import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.RelativeStrengthIndex;
import com.munch.exchange.model.analytic.indicator.oscillators.StochasticOscillator;
import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.analytic.indicator.trend.AverageDirectionalMovementIndexWilder;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.StandardDeviation;
import com.munch.exchange.model.analytic.indicator.trend.SuperTrend;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;

@Entity
public class ThreeMovingAverageFiltered extends IbChartSignal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6457568536068231929L;
	
	
	public static final String PARAM_PERIOD="Moving Average Period Slow";
	public static final String PARAM_MA_FACTOR_MIDDLE="Moving Average Factor Middle";
	public static final String PARAM_MA_FACTOR_FAST="Moving Average Factor Fast";
	
	public static final String PARAM_PRICE="Price";
	public static final String PARAM_MOV_AVE_ALG="Moving Average Algorithm";
	
	
	public static final String SERIE_MA_SLOW="Moving Average Slow";
	public static final String SERIE_MA_MIDDLE="Moving Average Middle";
	public static final String SERIE_MA_FAST="Moving Average Fast";
	
	
	public static final String SERIE_FILTER_ACTIVATION="Filter Activation";
	
	
	public static final String PARAM_FILTER_ADX_PERIOD="Filter ADX Period";
	public static final String PARAM_FILTER_ADX_LIMIT="Filter ADX Limit";
	
	

	public ThreeMovingAverageFiltered() {
		super();
	}

	public ThreeMovingAverageFiltered(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new ThreeMovingAverageFiltered();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "3 Moving Average with ADX Filter";
	}

	@Override
	public void createParameters() {
//		3MA: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_PERIOD,ParameterType.INTEGER, 18, 1, 400, 0));
		
//		3MA: PERIOD FACTOR MIDDLE
		this.parameters.add(new IbChartParameter(this, PARAM_MA_FACTOR_MIDDLE,ParameterType.DOUBLE, 0.5, 0.1, 0.9, 2));
		
//		3MA: PERIOD FACTOR FAST
		this.parameters.add(new IbChartParameter(this, PARAM_MA_FACTOR_FAST,ParameterType.DOUBLE, 0.5, 0.1, 0.9, 2));
		

//		3MA: PRICE
		this.parameters.add(new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray()));
		
//		3MA: MOVING AVERAGE ALGORITHM
		this.parameters.add(new IbChartParameter(this, PARAM_MOV_AVE_ALG,MACD.defaultAlgorithm,MACD.algorithms));

//		ADX: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_FILTER_ADX_PERIOD,ParameterType.INTEGER, 30, 1, 200, 0));	
		
//		ADX: LIMIT
		this.parameters.add(new IbChartParameter(this, PARAM_FILTER_ADX_LIMIT,ParameterType.DOUBLE, 0.15, 0.01, 0.3, 2));


	}
	
	
	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MA_SLOW,RendererType.MAIN,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MA_MIDDLE,RendererType.MAIN,false,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MA_FAST,RendererType.MAIN,false,true,50, 44, 89));
		
//		Stochastic Oscillator: LOWER LINE
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_FILTER_ACTIVATION,RendererType.SECOND,false,false,240, 100, 0));
		
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		int filter_val = this.getChartParameter(PARAM_FILTER_ADX_PERIOD).getIntegerValue();
		validAtPosition=Math.max(filter_val, validAtPosition);
		validAtPosition=Math.max(this.getChartParameter(PARAM_PERIOD).getIntegerValue(), validAtPosition);
		
		return validAtPosition;
	}
	
	

	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		
//		Step 1: Read the parameters
		long[] times=BarUtils.getTimeArray(bars);
		double[] close=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		double[] high=BarUtils.barsToDoubleArray(bars, DataType.HIGH);
		double[] low=BarUtils.barsToDoubleArray(bars, DataType.LOW);
		
		
		double[] prices=getPrices(bars,PARAM_PRICE);
		int N1 = this.getChartParameter(PARAM_PERIOD).getIntegerValue();
		int N2 = (int)(this.getChartParameter(PARAM_MA_FACTOR_MIDDLE).getValue() * N1);
		if(N2==0)N2=1;
		int N3 = (int)(this.getChartParameter(PARAM_MA_FACTOR_FAST).getValue() * N2);
		if(N3==0)N3=1;
		
		String algorithm=this.getChartParameter(PARAM_MOV_AVE_ALG).getStringValue();
		

//		Step 2: Calculate the MAs
		double[] MA1=MovingAverage.compute(algorithm, prices, N1);
		double[] MA2=MovingAverage.compute(algorithm, prices, N2);
		double[] MA3=MovingAverage.compute(algorithm, prices, N3);
		
		int period=this.getChartParameter(PARAM_FILTER_ADX_PERIOD).getIntegerValue();
		double limit = this.getChartParameter(PARAM_FILTER_ADX_LIMIT).getValue()*100;
//		
		
		double[] adx = AverageDirectionalMovementIndexWilder.computeADXWi(close, high, low, period);
		double[] ACT = new double[adx.length];
		for(int i=0;i<adx.length;i++){
			if(adx[i]>limit)
				ACT[i]=1.0;
		}
		
		double[] signal=new double[prices.length];
		
		signal[0] = 0;
		for(int i=1;i<prices.length;i++){
//			Long 
			if(MA2[i] > MA1[i] && signal[i-1] != 1.0 && MA3[i] > MA2[i] &&
					ACT[i] > 0 ){
				signal[i] = 1.0;
				continue;
			}
			if(signal[i-1] == 1.0 &&  MA3[i] < MA2[i]){
				signal[i] = 0.0;
				continue;
			}
			
//			Short
			if(MA2[i] < MA1[i] && signal[i-1] != -1.0 && MA3[i] < MA2[i] && 
					ACT[i] > 0){
				signal[i] = -1.0;
				continue;
			}
			if(signal[i-1] == -1.0 && MA3[i] > MA2[i]){
				signal[i] = 0.0;
				continue;
			}
			
			signal[i] = signal[i-1];
		}

		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, signal, N1-1);
		refreshSerieValues(this.name+" "+SERIE_MA_SLOW, reset, times, MA1, N1-1);
		refreshSerieValues(this.name+" "+SERIE_MA_MIDDLE, reset, times, MA2, N2-1);
		refreshSerieValues(this.name+" "+SERIE_MA_FAST, reset, times, MA3, N3-1);
		
		refreshSerieValues(this.name+" "+SERIE_FILTER_ACTIVATION, reset, times, ACT, getValidAtPosition());
		
	}
	
	
	
	private double[] getPrices(List<ExBar> bars, String paramName){
		
		String priceLabel=this.getChartParameter(paramName).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	

}
