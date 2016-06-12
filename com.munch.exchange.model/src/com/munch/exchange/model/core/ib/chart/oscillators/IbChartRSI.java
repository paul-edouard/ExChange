package com.munch.exchange.model.core.ib.chart.oscillators;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.RelativeStrengthIndex;
import com.munch.exchange.model.analytic.indicator.trend.FractalAdaptiveMovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
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
public class IbChartRSI extends IbChartIndicator {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7599234527907676636L;
	
	public static final String RSI_Serie="Relativ Strength Index";
	public static final String MA_RSI_Serie="Moving Average Relativ Strength Index";
	public static final String RSI_UPPER_LINE_Serie="Relativ Strength Index Upper Line";
	public static final String RSI_LOWER_LINE_Serie="Relativ Strength Index Lower Line";
	public static final String RSI_ACTIVATION_Serie="Relativ Strength Index Activation";
	
	
	
	public static final String PERIOD="Period";
	public static final String MA_PERIOD="Moving Average Period";
	public static final String SIGNAL_LINE_POSITION="Signal Line Position";
	
	public static final String PRICE="Price";

	
	
	public IbChartRSI() {
		super();
	}
	
	
	public IbChartRSI(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new IbChartRSI();
		c.copyData(this);
		return c;
	}
	
	
	
	@Override
	public void initName() {
		this.name="RSI: Relativ Strength Index";
	}


	@Override
	public void createSeries() {
		
//		RSI
		this.series.add(new IbChartSerie(this,RSI_Serie,RendererType.PERCENT,true,true,50, 44, 89));
		
//		MA RSI
		this.series.add(new IbChartSerie(this,MA_RSI_Serie,RendererType.PERCENT,false,false,150, 44, 89));
		
//		RSI: UPPER LINE
		this.series.add(new IbChartSerie(this,RSI_UPPER_LINE_Serie,RendererType.PERCENT,false,true,240, 0, 0));
		
//		RSI: LOWER LINE
		this.series.add(new IbChartSerie(this,RSI_LOWER_LINE_Serie,RendererType.PERCENT,false,true,240, 0, 0));
		
//		RSI: LOWER LINE
		this.series.add(new IbChartSerie(this,RSI_ACTIVATION_Serie,RendererType.SECOND,false,false,240, 100, 0));
		
	}


	@Override
	public void createParameters() {
		

		
//		SIGNAL PERIOD
		IbChartParameter param=new IbChartParameter(this, PERIOD,ParameterType.INTEGER, 14, 1, 200, 0);
		this.parameters.add(param);
		
//		3MA: PERIOD FACTOR MIDDLE
		this.parameters.add(new IbChartParameter(this, SIGNAL_LINE_POSITION,ParameterType.DOUBLE, 0.3, 0.01, 0.49, 2));
		
//		MOVING AVERAGE PERIOD
		this.parameters.add(new IbChartParameter(this, MA_PERIOD,ParameterType.INTEGER, 14, 1, 200, 0));
				
//		PRICE
		IbChartParameter price=new IbChartParameter(this, PRICE,DataType.CLOSE.name(),DataType.toStringArray());
		this.parameters.add(price);
		
	}


	@Override
	protected void computeSeriesPointValues(List<ExBar> bars, boolean reset) {
		double[] prices=getPrices(bars);
		long[] times=BarUtils.getTimeArray(bars);
		
		int period=this.getChartParameter(PERIOD).getIntegerValue();
		int ma_period = this.getChartParameter(MA_PERIOD).getIntegerValue();
		double line_pos = this.getChartParameter(SIGNAL_LINE_POSITION).getValue();
		int validAtPosition=period;
		
		
//		RSI
		double[] RSI=RelativeStrengthIndex.compute(prices,period);
		
//		POSITION & ACTIVATION
		double[] UP_POS = new double[RSI.length];
		double[] LO_POS = new double[RSI.length];
		double[] ACT = new double[RSI.length];
		
		
		for(int i = 0; i< RSI.length; i++){
			UP_POS[i] = (1 - line_pos)*100;
			LO_POS[i] = line_pos*100;
			
			if(RSI[i] > UP_POS[i]){
				ACT[i] = -1.0;
			}
			else if(RSI[i] < LO_POS[i]){
				ACT[i] = 1.0;
			}
			
			
		}
		
		
		refreshSerieValues(RSI_Serie, 			reset, times, RSI, 									validAtPosition);
		refreshSerieValues(MA_RSI_Serie, 		reset, times, MovingAverage.EMA(RSI, ma_period), 	validAtPosition);
		
		refreshSerieValues(RSI_UPPER_LINE_Serie,reset, times, UP_POS, 								validAtPosition);
		refreshSerieValues(RSI_LOWER_LINE_Serie,reset, times, LO_POS, 								validAtPosition);
		refreshSerieValues(RSI_ACTIVATION_Serie,reset, times, ACT, 									validAtPosition);
		
		
		
	}
	
	
	
	
	private double[] getPrices(List<ExBar> bars){
		
		String priceLabel=this.getChartParameter(PRICE).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	


}
