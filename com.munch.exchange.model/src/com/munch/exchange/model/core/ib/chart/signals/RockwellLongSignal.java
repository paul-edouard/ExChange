package com.munch.exchange.model.core.ib.chart.signals;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.RelativeStrengthIndex;
import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.analytic.indicator.trend.BollingerBands;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.StandardDeviation;
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
public class RockwellLongSignal extends IbChartSignal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -383915448793461274L;

	public static final String PARAM_PERIOD="Period";
	public static final String PARAM_PRICE="Price";
	public static final String PARAM_MOV_AVE_ALG="Moving Average Algorithm";
	
//	public static final String PARAM_BB_FACTOR="Bollinger Band Factor";
	
//	public static final String PARAM_MACD_SLOW_MA="MACD Slow Moving Average";
	public static final String PARAM_MACD_FAST_MA="MACD Fast Moving Average";
	public static final String PARAM_MACD_SIGNAL_PERIOD="MACD Signal Period";
//	public static final String PARAM_MACD_PRICE="MACD Price";
	
//	public static final String PARAM_RSI_PERIOD="RSI Period";
	
	
	public static final String PARAM_SIGNAL_ENTRY="Entry limit";
	public static final String PARAM_SIGNAL_EXIT="Exit limit";
	public static final String PARAM_SIGNAL_PERIOD="Signal Period";
	
	
	public static final String UP_serie="Trend Up";
	public static final String UP_Step1="Step 1";
	public static final String UP_Step2="Step 2";
	public static final String UP_Step3="Step 3";
	public static final String UP_Step4="Step 4";
	public static final String UP_EMA_serie="Trend Up EMA";
	
	
	public RockwellLongSignal() {
		super();
	}

	public RockwellLongSignal(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new RockwellLongSignal();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "Rockwell Long Signal";
	}

	@Override
	public void createParameters() {
//		BOLLINGER BAND: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_PERIOD,ParameterType.INTEGER, 26, 1, 200, 0));
		
//		BOLLINGER BAND: FACTOR
//		this.parameters.add(new IbChartParameter(this, PARAM_BB_FACTOR,ParameterType.DOUBLE, 2, 0, 10, 1));

		
//		MACD: SLOW MA
//		this.parameters.add(new IbChartParameter(this, PARAM_MACD_SLOW_MA,ParameterType.INTEGER, 26, 15, 200, 0));
		
//		MACD: FAST MA
		this.parameters.add(new IbChartParameter(this, PARAM_MACD_FAST_MA,ParameterType.INTEGER, 12, 1, 50, 0));
		
//		MACD: SIGNAL PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_MACD_SIGNAL_PERIOD,ParameterType.INTEGER, 9, 1, 200, 0));
		
//		MACD: MOVING AVERAGE ALGORITHM
		this.parameters.add(new IbChartParameter(this, PARAM_MOV_AVE_ALG,MACD.defaultAlgorithm,MACD.algorithms));
				
//		MACD: PRICE
//		this.parameters.add(new IbChartParameter(this, PARAM_MACD_PRICE,DataType.CLOSE.name(),DataType.toStringArray()));
		
		
//		RSI: SIGNAL PERIOD
//		this.parameters.add(new IbChartParameter(this, PARAM_RSI_PERIOD,ParameterType.INTEGER, 14, 1, 200, 0));
		
//		RSI: PRICE
		this.parameters.add(new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray()));
		
		
//		Entry Limit
		this.parameters.add(new IbChartParameter(this, PARAM_SIGNAL_ENTRY,ParameterType.DOUBLE, 0.1, 0.001, 1, 3));

//		Exit Limit
		this.parameters.add(new IbChartParameter(this, PARAM_SIGNAL_EXIT,ParameterType.DOUBLE, 0.1, 0.001, 1, 3));
		
//		Exit Limit
		this.parameters.add(new IbChartParameter(this, PARAM_SIGNAL_PERIOD,ParameterType.INTEGER, 26, 1, 200, 0));

	}
	
	
	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+UP_serie,RendererType.PERCENT,false,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_Step1,RendererType.PERCENT,false,true,150, 44, 44));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_Step2,RendererType.PERCENT,false,true,250, 244, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_Step3,RendererType.PERCENT,false,true,250, 44, 200));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_Step4,RendererType.PERCENT,false,true,250, 244, 200));
		this.series.add(new IbChartSerie(this,this.name+" "+UP_EMA_serie,RendererType.PERCENT,false,true,250, 44, 89));
		
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		validAtPosition=Math.max(this.getChartParameter(PARAM_PERIOD).getIntegerValue(), validAtPosition);
//		validAtPosition=Math.max(this.getChartParameter(PARAM_MACD_SLOW_MA).getIntegerValue(), validAtPosition);
		validAtPosition=Math.max(this.getChartParameter(PARAM_MACD_FAST_MA).getIntegerValue(), validAtPosition);
		validAtPosition=Math.max(this.getChartParameter(PARAM_MACD_SIGNAL_PERIOD).getIntegerValue(), validAtPosition);
		validAtPosition=Math.max(this.getChartParameter(PARAM_SIGNAL_PERIOD).getIntegerValue(), validAtPosition);
		
		return validAtPosition;
	}
	
	

	@Override
	public void computeSignalPointFromBarBlock(List<ExBar> bars, boolean reset) {
		

		double[] prices=getPrices(bars,PARAM_PRICE);
//		double[] closes=BarUtils.barsToDoubleArray(bars, DataType.CLOSE);
		long[] times=BarUtils.getTimeArray(bars);
		int N=this.getChartParameter(PARAM_PERIOD).getIntegerValue();
		String algorithm=this.getChartParameter(PARAM_MOV_AVE_ALG).getStringValue();
		
//		double D=this.getChartParameter(PARAM_BB_FACTOR).getValue();
		
		
//		Step 1: Calculate the up factor of the Bollinger Band
		double[] stdDev=StandardDeviation.compute(prices, N);
		double[] avg=MovingAverage.compute(algorithm, prices, N);
		double[] avgDer=SimpleDerivate.compute(avg);
		
		
		double[] BB_UP_FAC=new double[prices.length];
		
		for(int i=0;i<prices.length;i++){
//			if(BB_RelDist[i]>0){
//				BB_UP_FAC[i]=(Math.tanh(BB_TL_DEV[i])+1)*BB_RelDist[i]/2;
//				BB_UP_FAC[i]=(Math.tanh(BB_TL_DEV[i]) + Math.tanh(BB_RelDist[i]) + 2)/4;
//				BB_UP_FAC[i]=Math.tanh(BB_TL_DEV[i]*100)+1;
//				BB_UP_FAC[i]*=Math.tanh(BB_RelDist[i])+1;
//			}
//			System.out.println(stdDev[i]*(prices[i]-avg[i]));
				
			BB_UP_FAC[i]=Math.tanh(100000000*stdDev[i]*(prices[i]-avg[i]))+1;
			BB_UP_FAC[i]+=Math.tanh(10000*avgDer[i])+1;
			BB_UP_FAC[i]/=4;
				
		}
		
//		Step 2: Calculate the up factor of the MACD
//		double[] prices_macd=getPrices(bars,PARAM_MACD_PRICE);
//		int slowMA=this.getChartParameter(PARAM_MACD_SLOW_MA).getIntegerValue();
		int fastMA=this.getChartParameter(PARAM_MACD_FAST_MA).getIntegerValue();
		int MACDSignalPeriod=this.getChartParameter(PARAM_MACD_SIGNAL_PERIOD).getIntegerValue();
		
		
//		MACD
		double[][] R=MACD.compute(algorithm,prices,N,fastMA,MACDSignalPeriod);
		double[] MACD_Serie	=	R[0];
		double[] MACD_Signal=	R[1];
		
		double[] MACD_UP_FAC=new double[prices.length];
		for(int i=0;i<prices.length;i++){
			MACD_UP_FAC[i]=Math.tanh((MACD_Serie[i] - MACD_Signal[i])*100000)+1;
			MACD_UP_FAC[i]+=Math.tanh((MACD_Serie[i])*100000)+1;
			MACD_UP_FAC[i]/=4;
//			if(MACD_Serie[i]>MACD_Signal[i]){
//				MACD_UP_FAC[i]=(1+Math.tanh(MACD_Serie[i]))/2;
//				MACD_UP_FAC[i]=Math.tanh(MACD_Serie[i]*10000);
//			}
		}	
		
//		Step 3: Calculate the up factor of RSI
//		double[] prices_rsi=getPrices(bars,PARAM_PRICE);
//		int period_rsi=this.getChartParameter(PARAM_RSI_PERIOD).getIntegerValue();
		double[] RSI=RelativeStrengthIndex.compute(prices,N);
		double[] RSI_UP_FAC=new double[prices.length];
		for(int i=0;i<prices.length;i++){
			RSI_UP_FAC[i]=Math.tanh(RSI[i]-50)+1;
			RSI_UP_FAC[i]/=2;
		}
		
		
//		Step 4: Combine Up Factor Bollinger Band, Up factor MACD and RSI factor
		double[] UP_FAC=new double[prices.length];
		for(int i=0;i<prices.length;i++){
//			UP_FAC[i]=RSI_UP_FAC[i]*MACD_UP_FAC[i]*BB_UP_FAC[i];
			
			UP_FAC[i]=RSI_UP_FAC[i]+MACD_UP_FAC[i]+BB_UP_FAC[i];
			UP_FAC[i]/=3;
			
//			UP_FAC[i]=MACD_UP_FAC[i]*(RSI_UP_FAC[i] + BB_UP_FAC[i]);
		}
		
		
//		Step 5: Calculate the signal
		
		double[] signal=new double[prices.length];
		double entry_limit=this.getChartParameter(PARAM_SIGNAL_ENTRY).getValue();
		double exit_limit=this.getChartParameter(PARAM_SIGNAL_EXIT).getValue();
		int signalP=this.getChartParameter(PARAM_SIGNAL_PERIOD).getIntegerValue();
		double[] upEMA=MovingAverage.EMA(UP_FAC, signalP);
		
		boolean isUpActivated=false;
		for(int i=1;i<prices.length;i++){
			if(UP_FAC[i] > upEMA[i] && isUpActivated && UP_FAC[i] >  exit_limit){
				signal[i]=1;
			}
			if(UP_FAC[i] <= upEMA[i]){
				isUpActivated=false;
			}
			if(upEMA[i]<entry_limit){
				isUpActivated=true;
			}
//			else if(signal[i-1]==1 && UP_FAC[i] < exit_limit){
//				signal[i]=0;
//			}
			
		}
		
		this.getSignalSerie().addNewPointsOnly(times,signal);
		this.getChartSerie(this.name+" "+UP_serie).addNewPointsOnly(times,UP_FAC);
		this.getChartSerie(this.name+" "+UP_Step1).addNewPointsOnly(times,BB_UP_FAC);
		this.getChartSerie(this.name+" "+UP_Step2).addNewPointsOnly(times,MACD_UP_FAC);
		this.getChartSerie(this.name+" "+UP_Step3).addNewPointsOnly(times,RSI_UP_FAC);
		this.getChartSerie(this.name+" "+UP_Step4).addNewPointsOnly(times,UP_FAC);
		this.getChartSerie(this.name+" "+UP_EMA_serie).addNewPointsOnly(times,MovingAverage.EMA(UP_FAC, signalP));
		
//		System.out.println("Say Hallo!");
		
	}
	
	
	private double[] getPrices(List<ExBar> bars, String paramName){
		
		String priceLabel=this.getChartParameter(paramName).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	

}
