package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.Calendar;
import java.util.LinkedList;
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
public class OpenRange extends IbChartSignal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6820307845346049387L;
	
//	RESISTANCE LINES
	public static final String SERIE_MAX_RES_LINE="Max Resistance Line";
	public static final String SERIE_MIN_RES_LINE="Min Resistance Line";
	
	public static final String PARAM_START="Start of the open range";
	public static final String PARAM_END="End of the open range";
		
//	RISK and PROFIT PARAMETERS
	public static final String SERIE_TAKE_PROFIT="Take profit limit";
	public static final String SERIE_STOP_LOSS="Stop loss limit";
	
	public static final String PARAM_RISK="Risk";
	public static final String PARAM_PROFIT_RISK_FACTOR="Profit/Risk Factor";
	
	
	public OpenRange() {
		super();
	}

	public OpenRange(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new OpenRange();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "Open Range";
	}

	@Override
	public void createParameters() {
		
//		Start
		this.parameters.add(new IbChartParameter(this, PARAM_START,ParameterType.DOUBLE, 0.4, 0.1, 1, 2));
//		End
		this.parameters.add(new IbChartParameter(this, PARAM_END,ParameterType.DOUBLE, 0.1, 0, 1, 2));
		
				
//		Risk & Profit: Risk
		this.parameters.add(new IbChartParameter(this, PARAM_RISK,ParameterType.DOUBLE, 0.0005, 0.0001, 0.004, 4));
		
//		Risk & Profit: Risk
		this.parameters.add(new IbChartParameter(this, PARAM_PROFIT_RISK_FACTOR,ParameterType.DOUBLE, 2, 1, 5, 2));

		
		

	}
	
	
	@Override
	public void createSeries() {
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MAX_RES_LINE,RendererType.MAIN,true,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_MIN_RES_LINE,RendererType.MAIN,true,true,50, 44, 189));
		
		
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_TAKE_PROFIT,RendererType.MAIN,false,true,50, 44, 89));
		this.series.add(new IbChartSerie(this,this.name+" "+SERIE_STOP_LOSS,RendererType.MAIN,false,true,50, 44, 189));
		
		super.createSeries();
		
	}
	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=1;
		
		return validAtPosition;
	}
	
	

	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		
//		Step 1: Read the parameters
		long[] times=getTimeArrayFromBar(bars);
		double[] close=getDataFromBars(bars, DataType.CLOSE);
		
	
		double risk = this.getChartParameter(PARAM_RISK).getValue();
		double profitRisk_Factor = this.getChartParameter(PARAM_PROFIT_RISK_FACTOR).getValue();
		
		double start = this.getChartParameter(PARAM_START).getValue();
		double end = start + (1-start)*this.getChartParameter(PARAM_END).getValue();
		
		
		ExBar firstBar=bars.get(0);
		Calendar currentDay=BarUtils.getCurrentDayOf(firstBar.getTimeInMs());
		Calendar nextDay=BarUtils.addOneDayTo(currentDay);
		
		double[] signal=new double[bars.size()];
		double[] maxResLine=new double[bars.size()];
		double[] minResLine=new double[bars.size()];
		double[] takeProfit=new double[bars.size()];
		double[] stopLoss=new double[bars.size()];
		
		for(int i=0;i<bars.size();i++){
			maxResLine[i] 	= Double.NaN;
			minResLine[i] 	= Double.NaN;
			takeProfit[i] 	= Double.NaN;
			stopLoss[i] 	= Double.NaN;
		}
		
		long dayInMs = nextDay.getTimeInMillis() - currentDay.getTimeInMillis();
		
		boolean startOpenRange = false;
		boolean endOpenRange = false;
		boolean isTrading = false;
		
		double triggerBuyLimit = 0;
		double triggerSellLimit = 0;
		double range = 0;
		double targetProfit = 0; 
		
		double profitLimit = 0;
		double stopLossLimit = 0;
		
		int i = 0;
		for(ExBar bar:bars){
			
			if(i>0){
				signal[i]=signal[i-1];
				takeProfit[i]=takeProfit[i-1];
				stopLoss[i]=stopLoss[i-1];
				maxResLine[i]=maxResLine[i-1];
				minResLine[i]=minResLine[i-1];
			}
			
//			The bar belong to the next day
			if(bar.getTimeInMs() >= nextDay.getTimeInMillis()){
				while(bar.getTimeInMs() >= nextDay.getTimeInMillis())
					nextDay=BarUtils.addOneDayTo(nextDay);
				startOpenRange = false;
				endOpenRange = false;
				isTrading = false;
				
				signal[i] = 0;
				takeProfit[i] = Double.NaN;
				stopLoss[i] = Double.NaN;
				maxResLine[i]=Double.NaN;
				minResLine[i]=Double.NaN;
				
				i++;
				continue;
			}
			
			double barPosInDay = 1 - ((double)(nextDay.getTimeInMillis() - bar.getTimeInMs()))/((double)dayInMs);
			
//			System.out.println("barPosInDay: "+barPosInDay);
//			System.out.println("start: "+start);
//			System.out.println("end: "+end);
			
			
//			Save the open Range
			if(barPosInDay >= start && barPosInDay <= end){
				if(!startOpenRange){
					startOpenRange = true;
					
					maxResLine[i] = bar.getClose();
					minResLine[i] = bar.getClose();
				}
				else{
					if(bar.getClose() > maxResLine[i]){
						maxResLine[i] = bar.getClose();
					}
					if(bar.getClose() < minResLine[i]){
						minResLine[i] = bar.getClose();
					}
				}
			}
//			Start of the trading
			else if(barPosInDay > end && !isTrading){
				if(!endOpenRange && i>0){
					triggerBuyLimit = maxResLine[i-1];
					triggerSellLimit = minResLine[i-1];
					range = risk;
					targetProfit = range * profitRisk_Factor;
					endOpenRange = true;
					maxResLine[i]=Double.NaN;
					minResLine[i]=Double.NaN;
				}
				
				if(bar.getClose() > triggerBuyLimit){
					
					
						signal[i] = 1;
						profitLimit = triggerBuyLimit + targetProfit;
						stopLossLimit = triggerBuyLimit - risk;
						isTrading = true;
						takeProfit[i] = profitLimit;
						stopLoss[i] = stopLossLimit;
						
				}
				if(bar.getClose() < triggerSellLimit){
					maxResLine[i]=Double.NaN;
					minResLine[i]=Double.NaN;
						signal[i] = -1;
						profitLimit = triggerSellLimit - targetProfit;
						stopLossLimit = triggerSellLimit + risk;
						isTrading = true;
						takeProfit[i] = profitLimit;
						stopLoss[i] = stopLossLimit;
				}
					
			}
			
			
//			Is trading
			if(isTrading){
				if(signal[i]>0){
					if(bar.getClose() < stopLossLimit || bar.getClose() > profitLimit){
						signal[i] = 0;
						takeProfit[i] = Double.NaN;
						stopLoss[i] = Double.NaN;
					}
				}
				if(signal[i]<0){
					if(bar.getClose() < profitLimit || bar.getClose() > stopLossLimit){
						signal[i] = 0;
						takeProfit[i] = Double.NaN;
						stopLoss[i] = Double.NaN;
					}
				}
			}
			
			
			i++;
		}
		
		
		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, signal,getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_MAX_RES_LINE, reset, times, maxResLine, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_MIN_RES_LINE, reset, times, minResLine, getValidAtPosition());
		
		refreshSerieValues(this.name+" "+SERIE_TAKE_PROFIT, reset, times, takeProfit, getValidAtPosition());
		refreshSerieValues(this.name+" "+SERIE_STOP_LOSS, reset, times, stopLoss, getValidAtPosition());
		
		
	}
	
	
	
	
	

	

}
