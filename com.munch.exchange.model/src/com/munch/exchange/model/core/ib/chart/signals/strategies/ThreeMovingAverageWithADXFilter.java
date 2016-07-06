package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.AverageTrueRange;
import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.RelativeStrengthIndex;
import com.munch.exchange.model.analytic.indicator.oscillators.StochasticOscillator;
import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
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
public class ThreeMovingAverageWithADXFilter extends IbChartSignal {

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
	
	
	public static final String PARAM_FILTER_PERIOD="Filter Period";
	public static final String PARAM_MA_FILTER_PERIOD="Moving Average Filter Period";
	public static final String PARAM_SIGNAL_LINE_POSITION_RSI="Signal Line Position RSI";
	public static final String PARAM_SIGNAL_LINE_POSITION_KD="Signal Line Position Stochastic Oscillator";
	
	
	public static final String SERIE_ST_UP="Trend Limit Up";
	public static final String SERIE_ST_DN="Trend Limit Down";
	
	public static final String PARAM_ST_PERIOD="Super Trend Period";
	public static final String PARAM_ST_FACTOR="Super Trend Factor";
	

	public ThreeMovingAverageWithADXFilter() {
		super();
	}

	public ThreeMovingAverageWithADXFilter(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new ThreeMovingAverageWithADXFilter();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "3 Moving Average Filtered";
	}

	@Override
	public void createParameters() {
//		3MA: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_PERIOD,ParameterType.INTEGER, 18, 1, 1000, 0));
		
//		3MA: PERIOD FACTOR MIDDLE
		this.parameters.add(new IbChartParameter(this, PARAM_MA_FACTOR_MIDDLE,ParameterType.DOUBLE, 0.5, 0.1, 0.9, 2));
		
//		3MA: PERIOD FACTOR FAST
		this.parameters.add(new IbChartParameter(this, PARAM_MA_FACTOR_FAST,ParameterType.DOUBLE, 0.5, 0.1, 0.9, 2));
		

//		3MA: PRICE
		this.parameters.add(new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray()));
		
//		3MA: MOVING AVERAGE ALGORITHM
		this.parameters.add(new IbChartParameter(this, PARAM_MOV_AVE_ALG,MACD.defaultAlgorithm,MACD.algorithms));

		
//		FILTERED: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_FILTER_PERIOD,ParameterType.INTEGER, 14, 1, 200, 0));
		
//		FILTERED: SMA PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_MA_FILTER_PERIOD,ParameterType.INTEGER, 3, 1, 50, 0));

//		FILTERED: SIGNAL LINE POSITION KD
		this.parameters.add(new IbChartParameter(this, PARAM_SIGNAL_LINE_POSITION_KD,ParameterType.DOUBLE, 0.2, 0.01, 0.49, 2));

//		FILTERED: SIGNAL LINE POSITION RSI
		this.parameters.add(new IbChartParameter(this, PARAM_SIGNAL_LINE_POSITION_RSI,ParameterType.DOUBLE, 0.3, 0.01, 0.49, 2));
		
		
		
//		SUPER TREND: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_ST_PERIOD,ParameterType.INTEGER, 12, 1, 200, 0));
//		SUPER TREND: FACTOR
		this.parameters.add(new IbChartParameter(this, PARAM_ST_FACTOR,ParameterType.DOUBLE, 5, 0.1, 20, 1));
		

	}
	
	
	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MA_SLOW,RendererType.MAIN,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MA_MIDDLE,RendererType.MAIN,false,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MA_FAST,RendererType.MAIN,false,true,50, 44, 89));
		
//		Stochastic Oscillator: LOWER LINE
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_FILTER_ACTIVATION,RendererType.SECOND,false,false,240, 100, 0));

//		Super Trend
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_ST_UP,RendererType.MAIN,false,true,10, 10, 200));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_ST_DN,RendererType.MAIN,false,true,200, 10, 10));

		
		
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		int filter_val = this.getChartParameter(PARAM_FILTER_PERIOD).getIntegerValue();
		filter_val += this.getChartParameter(PARAM_MA_FILTER_PERIOD).getIntegerValue();
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
		
		double[] signal=new double[prices.length];
		
		double[] KD_ACT = calculateStocasticActivation(close, high, low);
		double[] RSI_ACT = calculateRSIActivation(close);
		double[] ACT = new double[RSI_ACT.length];
		
		for(int i=1;i<RSI_ACT.length;i++){
			if(KD_ACT[i] > 0 && RSI_ACT[i] > 0){
				ACT[i] = 1.0;
			}
			else if(KD_ACT[i] < 0 && RSI_ACT[i] < 0){
				ACT[i] = -1.0;
			}
		}
		
//		Super Trend Signal
		double[] SP_UP = new double[close.length];
		double[] SP_DN = new double[close.length];
		for(int i=1;i<close.length;i++){
			SP_UP[i] = Double.NaN;
			SP_DN[i] = Double.NaN;
		}
		
		int st_periode = this.getChartParameter(PARAM_ST_PERIOD).getIntegerValue();
		double st_factor = this.getChartParameter(PARAM_ST_FACTOR).getValue();
		double[] ATR=AverageTrueRange.compute(close, high, low, st_periode);
		
		signal[0] = 0;
//		double last_filtered = 0;
//		boolean longActivated = false;
		for(int i=1;i<prices.length;i++){
//			if(ACT[i]!=0)
//				last_filtered = ACT[i];
//			
//			Long Signal			
			if(ACT[i] > 0  || signal[i-1] == 1.0){
				
				double medianPrice=(high[i]+low[i])/2;
				SP_UP[i] = medianPrice-st_factor*ATR[i];
				if(!Double.isNaN(SP_UP[i-1]) && SP_UP[i]<SP_UP[i-1])
					SP_UP[i] = SP_UP[i-1];
				
				if(close[i] >= SP_UP[i]){
					signal[i] = 1.0;
				}
			}
			if(signal[i]>0 && ACT[i]<0)
				signal[i]=0;
			
//			Short Signal
			if(ACT[i] < 0  || signal[i-1] == -1.0){
				double medianPrice=(high[i]+low[i])/2;
				SP_DN[i] = medianPrice+st_factor*ATR[i];
				if(!Double.isNaN(SP_DN[i-1]) && SP_DN[i]>SP_DN[i-1])
					SP_DN[i] = SP_DN[i-1];
				
				if(close[i] <= SP_DN[i]){
					signal[i] = -1.0;
				}
			}
			if(signal[i]<0 && ACT[i]>0)
				signal[i]=0;
			
			
//			Long 
//			if(MA2[i] > MA1[i] && signal[i-1] != 1.0 && MA3[i] > MA2[i] &&
//					last_filtered > 0 ){
//				signal[i] = 1.0;
//				continue;
//			}
//			if(signal[i-1] == 1.0 &&  MA3[i] < MA2[i]){
//				signal[i] = 0.0;
//				continue;
//			}
			
//			Short
//			if(MA2[i] < MA1[i] && signal[i-1] != -1.0 && MA3[i] < MA2[i] && 
//					last_filtered < 0){
//				signal[i] = -1.0;
//				continue;
//			}
//			if(signal[i-1] == -1.0 && MA3[i] > MA2[i]){
//				signal[i] = 0.0;
//				continue;
//			}
			
//			signal[i] = signal[i-1];
		}

		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, signal, N1-1);
		refreshSerieValues(this.name+" "+SERIE_MA_SLOW, reset, times, MA1, N1-1);
		refreshSerieValues(this.name+" "+SERIE_MA_MIDDLE, reset, times, MA2, N2-1);
		refreshSerieValues(this.name+" "+SERIE_MA_FAST, reset, times, MA3, N3-1);
		
		refreshSerieValues(this.name+" "+SERIE_FILTER_ACTIVATION, reset, times, ACT, getValidAtPosition());
		
		
		refreshSerieValues(this.name+" "+SERIE_ST_UP, reset, times, SP_UP, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_ST_DN, reset, times, SP_DN, getValidAtPosition());
		
	}
	
	private double[] calculateStocasticActivation(double[] close, double[] high, double[] low ){
		int period = this.getChartParameter(PARAM_FILTER_PERIOD).getIntegerValue();
		int sma_period = this.getChartParameter(PARAM_MA_FILTER_PERIOD).getIntegerValue();
		double line_pos = this.getChartParameter(PARAM_SIGNAL_LINE_POSITION_KD).getValue();
		
		double[][] KD=StochasticOscillator.calculateKD(close, high, low,period,sma_period);
//		double[] K=KD[0];
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
		
		return ACT;
	}
	
	private double[] calculateRSIActivation(double[] close){
		int period = this.getChartParameter(PARAM_FILTER_PERIOD).getIntegerValue();
		int sma_period = this.getChartParameter(PARAM_MA_FILTER_PERIOD).getIntegerValue();
		double line_pos = this.getChartParameter(PARAM_SIGNAL_LINE_POSITION_RSI).getValue();
		
		
//		RSI
		double[] RSI=RelativeStrengthIndex.compute(close,period);
		double[] MA_RSI = MovingAverage.SMA(RSI, sma_period);
		
//		POSITION & ACTIVATION
		double[] UP_POS = new double[RSI.length];
		double[] LO_POS = new double[RSI.length];
		double[] ACT = new double[RSI.length];
		
		
		for(int i = 0; i< MA_RSI.length; i++){
			UP_POS[i] = (1 - line_pos)*100;
			LO_POS[i] = line_pos*100;
			
			if(MA_RSI[i] > UP_POS[i]){
				ACT[i] = -1.0;
			}
			else if(MA_RSI[i] < LO_POS[i]){
				ACT[i] = 1.0;
			}
			
			
		}
		
		return ACT;
		
	}
	
	
	private double[] getPrices(List<ExBar> bars, String paramName){
		
		String priceLabel=this.getChartParameter(paramName).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	

}
