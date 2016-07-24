package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.trend.AverageDirectionalMovementIndexWilder;
import com.munch.exchange.model.analytic.indicator.trend.BollingerBands;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.Resistance;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter.ParameterType;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.IbChartSerie.RendererType;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;

@Entity
public class Bladerunner extends IbChartSignal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6820307845346049387L;
	
//	RESISTANCE LINES
	public static final String SERIE_MAX_RES_LINE="Max Resistance Line";
	public static final String SERIE_MIN_RES_LINE="Min Resistance Line";
	
	public static final String SERIE_MAX_BREAKOUT_VALUE="Max Breakout Value";
	public static final String SERIE_MIN_BREAKOUT_VALUE="Min Breakout Value";
	
	public static final String PARAM_RESISTANCE_NB_OF_EXTREMUMS="Number of Extremums";
	public static final String PARAM_MAX_RESISTANCE_SEARCH_PERIOD="Max Resistance Search Period";
	public static final String PARAM_RESISTANCE_RANGE="Resistance Range";

	
//	BOLLINGER BANDS
	public static final String SERIE_BB_TOP_LINE="Bollinger Bands: Top Line";
	public static final String SERIE_BB_BOTTOM_LINE="Bollinger Bands: Bottom Line";
	
	public static final String PARAM_BB_PERIOD="Bollinger Bands: Period";
	public static final String PARAM_BB_FACTOR="Bollinger Bands: Factor";
	
//	RISK and PROFIT PARAMETERS
	public static final String PARAM_RISK="Risk";
	public static final String PARAM_PROFIT_RISK_FACTOR="Profit/Risk Factor";
	
//	CONTROL
	public static final String SERIE_CONTROL_POS="Control Positiv";
	public static final String SERIE_CONTROL_NEG="Control Negativ";
	

	public Bladerunner() {
		super();
	}

	public Bladerunner(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new Bladerunner();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "Balderunner";
	}

	@Override
	public void createParameters() {
		
//		RESISTANCE PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_RESISTANCE_NB_OF_EXTREMUMS,ParameterType.INTEGER, 12, 1, 200, 0));

//		MAX RESISTANCE SEARCH PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_MAX_RESISTANCE_SEARCH_PERIOD,ParameterType.INTEGER, 1000, 500, 2000, 0));		
		
//		RESISTANCE RANGE
		this.parameters.add(new IbChartParameter(this, PARAM_RESISTANCE_RANGE,ParameterType.DOUBLE, 0.0001, 0.00001, 0.005, 5));

		
//		BB: PERIOD
		this.parameters.add(new IbChartParameter(this, PARAM_BB_PERIOD,ParameterType.INTEGER, 12, 1, 200, 0));
		
//		BB: FACTOR
		this.parameters.add(new IbChartParameter(this, PARAM_BB_FACTOR,ParameterType.DOUBLE, 0.1, 0.01, 1, 2));
		
		
//		Risk & Profit: Risk
		this.parameters.add(new IbChartParameter(this, PARAM_RISK,ParameterType.DOUBLE, 0.0005, 0.0001, 0.004, 4));
		
//		Risk & Profit: Risk
		this.parameters.add(new IbChartParameter(this, PARAM_PROFIT_RISK_FACTOR,ParameterType.DOUBLE, 2, 1, 5, 2));

		
		

	}
	
	
	@Override
	public void createSeries() {
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MAX_RES_LINE,RendererType.MAIN,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MIN_RES_LINE,RendererType.MAIN,true,true,50, 244, 189));
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MAX_BREAKOUT_VALUE,RendererType.SECOND,false,false,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MIN_BREAKOUT_VALUE,RendererType.SECOND,false,false,50, 244, 189));
		
//		"Bollinger Bands: Top Line";
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_BB_TOP_LINE,RendererType.MAIN,false,true,250,0,0));
		
//		"Bollinger Bands: Bottom Line";
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_BB_BOTTOM_LINE,RendererType.MAIN,false,true,250,0,0));

		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_CONTROL_POS,RendererType.PERCENT,false,true,250,0,0));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_CONTROL_NEG,RendererType.PERCENT,false,true,0,250,0));

		
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		int param1 = this.getChartParameter(PARAM_MAX_RESISTANCE_SEARCH_PERIOD).getIntegerValue();
		int param2 = this.getChartParameter(PARAM_BB_PERIOD).getIntegerValue();
		validAtPosition=Math.max(param1, validAtPosition);
		validAtPosition=Math.max(param2, validAtPosition);
		
		return validAtPosition;
	}
	
	

	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		
//		Step 1: Read the parameters
		long[] times=getTimeArrayFromBar(bars);
		double[] close=getDataFromBars(bars, DataType.CLOSE);
		double[] high=getDataFromBars(bars, DataType.HIGH);
		double[] low=getDataFromBars(bars, DataType.LOW);
		
		int nbOfEtremums=this.getChartParameter(PARAM_RESISTANCE_NB_OF_EXTREMUMS).getIntegerValue();
		int maxResSearchPeriod=this.getChartParameter(PARAM_MAX_RESISTANCE_SEARCH_PERIOD).getIntegerValue();
		double range = this.getChartParameter(PARAM_RESISTANCE_RANGE).getValue();

		
		int N=this.getChartParameter(PARAM_BB_PERIOD).getIntegerValue();
		double D=this.getChartParameter(PARAM_BB_FACTOR).getValue();
		
		
		double risk = this.getChartParameter(PARAM_RISK).getValue();
		double profitRisk_Factor = this.getChartParameter(PARAM_PROFIT_RISK_FACTOR).getValue();
		
		
//		Step 2: Compute the Bollinger Bands & the resistance lines
		double[][] ADX=BollingerBands.computeADX(close, N, D);
		
		double[] BB_Top_Line = ADX[1];
		double[] BB_Bottom_Line = ADX[2];
		
		
		double[][] RES = Resistance.compute(high, low, nbOfEtremums, range, maxResSearchPeriod);
		
		double[] maxResLine = RES[0];
		double[] minResLine = RES[1];
		
		double[] maxBreakout = RES[6];
		double[] minBreakout = RES[7];
		
		double[][] BC = createBuyAndControlSignal(close, low, maxBreakout, maxResLine, BB_Top_Line, BB_Bottom_Line, risk, profitRisk_Factor);
		
		double[] signal_buy=BC[0];
		double[] control_buy=BC[1];
		
		double[][] SC = createSellAndControlSignal(close, high, minBreakout, minResLine, BB_Top_Line, BB_Bottom_Line, risk, profitRisk_Factor);
		
		double[] signal_sell=SC[0];
		double[] control_sell=SC[1];
		
		
		double[] signal=new double[close.length];
		for(int i=0;i<close.length;i++){
			signal[i] = signal_buy[i] + signal_sell[i];
		}
		
		
		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, signal,getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_MAX_RES_LINE, reset, times, maxResLine, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_MIN_RES_LINE, reset, times, minResLine, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_MAX_BREAKOUT_VALUE, reset, times, maxBreakout, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_MIN_BREAKOUT_VALUE, reset, times, minBreakout, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_BB_TOP_LINE, reset, times, BB_Top_Line, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_BB_BOTTOM_LINE, reset, times, BB_Bottom_Line, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_CONTROL_POS, reset, times, control_buy, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_CONTROL_NEG, reset, times, control_sell, getValidAtPosition());
		
		
	}
	
	
	private double[][] createBuyAndControlSignal(double[] close, double[] low,
			double[] maxBreakout, double[] maxResLine,
			double[] BB_Top_Line,  double[] BB_Bottom_Line,
			double risk, double profitRisk_Factor){
		
		double[][] BC=new double[2][close.length];
		
		double[] signal=new double[close.length];
		double[] control=new double[close.length];
		
		boolean upBreakoutActivated = false;
		boolean upBBTopLineEntryActivated = false;
		boolean upBBTopLineExitActivated = false;
		
		boolean isTrading = false;
		
		double newHighValue = 0;
		double breakOutValue = 0;
		double exitLimit = 0;
		double stopLoss = 0;
		
		for(int i=1;i<close.length;i++){
			control[i]= control[i-1];
			
//			The breakout is activated
			if(maxBreakout[i] > 0 && upBreakoutActivated == false){
				upBreakoutActivated = true;
				breakOutValue = maxResLine[i];
				control[i] = 1;
			}
			if(!isTrading && upBreakoutActivated == true && maxResLine[i] < breakOutValue){
//				System.out.println("maxBreakout[i]: "+maxResLine[i]);
				upBreakoutActivated = false;
				upBBTopLineEntryActivated = false;
				upBBTopLineExitActivated = false;
				isTrading = false;
				newHighValue = 0;
				control[i] = 0;
				continue;
			}
			
			
//			Save the new high position
			if(newHighValue == 0 && upBreakoutActivated && maxResLine[i] > breakOutValue){
//				newHighValue = maxResLine[i];
				newHighValue = breakOutValue;
			}
			
//			The close price enter for the first time into the Bollinger Band
			if(upBreakoutActivated == true &&
					low[i-1] >= BB_Top_Line[i] &&  low[i] <= BB_Top_Line[i] &&
					upBBTopLineEntryActivated == false && newHighValue > 0){
				upBBTopLineEntryActivated = true;
				control[i] = 2;
			}
			
//			The price exit the Bollinger bands
			if(upBBTopLineEntryActivated &&
					low[i-1] <= BB_Top_Line[i] &&  low[i] > BB_Top_Line[i] &&
					upBBTopLineExitActivated == false){
				upBBTopLineExitActivated = true;
				
//				The price is above the last new high
				if(close[i] > newHighValue){
					control[i] = 3;
					isTrading = true;
					signal[i] = 1.0;
					stopLoss = close[i]-risk;
					exitLimit = close[i] + risk*profitRisk_Factor;
				}
//				False Signal reset all to false and wait for the next signal
				else{
					upBreakoutActivated = false;
					upBBTopLineEntryActivated = false;
					upBBTopLineExitActivated = false;
					isTrading = false;
					newHighValue = 0;
					control[i] = 0;
					continue;
				}
			}
			
//			The price is goes out of the Bollinger Bands but from the wrong side
			if(isTrading == false && upBBTopLineEntryActivated && low[i] <= BB_Bottom_Line[i]){
				upBreakoutActivated = false;
				upBBTopLineEntryActivated = false;
				upBBTopLineExitActivated = false;
				isTrading = false;
				newHighValue = 0;
				control[i] = 0;
				continue;				
			}
			
			if(isTrading){
				if(stopLoss < close[i] && close[i] < exitLimit /*&& close[i] >= BB_Bottom_Line[i]*/){
					signal[i] = 1.0;
					control[i] = 4;
				}
				else{
					upBreakoutActivated = false;
					upBBTopLineEntryActivated = false;
					upBBTopLineExitActivated = false;
					isTrading = false;
					newHighValue = 0;
					control[i] = 0;
					continue;
				}
				
			}
			
			
		}
		
		BC[0]=signal;
		BC[1]=control;
		
		
		return BC;
	}
	
	private double[][] createSellAndControlSignal(double[] close, double[] high,
			double[] minBreakout, double[] minResLine,
			double[] BB_Top_Line,  double[] BB_Bottom_Line,
			double risk, double profitRisk_Factor){
		
		double[][] BC=new double[2][close.length];
		
		double[] signal=new double[close.length];
		double[] control=new double[close.length];
		
		boolean downBreakoutActivated = false;
		boolean downBBTopLineEntryActivated = false;
		boolean downBBTopLineExitActivated = false;
		
		boolean isTrading = false;
		
		double newLowValue = 0;
		double breakOutValue = 0;
		double exitLimit = 0;
		double stopLoss = 0;
		
		for(int i=1;i<close.length;i++){
			control[i]= control[i-1];
			
//			The breakout is activated
			if(minBreakout[i] > 0 && downBreakoutActivated == false){
				downBreakoutActivated = true;
				breakOutValue = minResLine[i];
				control[i] = 1;
			}
			if(!isTrading && downBreakoutActivated == true && minResLine[i] > breakOutValue){
//				System.out.println("maxBreakout[i]: "+maxResLine[i]);
				downBreakoutActivated = false;
				downBBTopLineEntryActivated = false;
				downBBTopLineExitActivated = false;
				isTrading = false;
				newLowValue = 0;
				control[i] = 0;
				continue;
			}
			
			
//			Save the new high position
			if(newLowValue == 0 && downBreakoutActivated && minResLine[i] < breakOutValue){
//				newLowValue = minResLine[i];
				newLowValue = breakOutValue;
			}
			
//			The close price enter for the first time into the Bollinger Band
			if(downBreakoutActivated == true &&
					high[i-1] <= BB_Bottom_Line[i] &&  high[i] >= BB_Bottom_Line[i] &&
					downBBTopLineEntryActivated == false && newLowValue > 0){
				downBBTopLineEntryActivated = true;
				control[i] = 2;
			}
			
//			The price exit the Bollinger bands
			if(downBBTopLineEntryActivated &&
					high[i-1] >= BB_Bottom_Line[i] &&  high[i] < BB_Bottom_Line[i] &&
					downBBTopLineExitActivated == false){
				downBBTopLineExitActivated = true;
				
//				The price is above the last new high
				if(close[i] < newLowValue){
					control[i] = 3;
					isTrading = true;
					signal[i] = -1.0;
					stopLoss = close[i]+risk;
					exitLimit = close[i] - risk*profitRisk_Factor;
					continue;
				}
//				False Signal reset all to false and wait for the next signal
				else{
					downBreakoutActivated = false;
					downBBTopLineEntryActivated = false;
					downBBTopLineExitActivated = false;
					isTrading = false;
					newLowValue = 0;
					control[i] = 0;
					continue;
				}
			}
			
//			The price is goes out of the Bollinger Bands but from the wrong side
			if(isTrading == false && downBBTopLineEntryActivated && high[i] >= BB_Top_Line[i]){
				downBreakoutActivated = false;
				downBBTopLineEntryActivated = false;
				downBBTopLineExitActivated = false;
				isTrading = false;
				newLowValue = 0;
				control[i] = 0;
				continue;				
			}
			
			if(isTrading){
				if(stopLoss > close[i] && close[i] > exitLimit /*&& close[i] >= BB_Bottom_Line[i]*/){
					signal[i] = -1.0;
					control[i] = 4;
				}
				else{
					downBreakoutActivated = false;
					downBBTopLineEntryActivated = false;
					downBBTopLineExitActivated = false;
					isTrading = false;
					newLowValue = 0;
					control[i] = 0;
					continue;
				}
				
			}
			
			
		}
		
		BC[0]=signal;
		BC[1]=control;
		
		
		return BC;
	}
	

	

}
