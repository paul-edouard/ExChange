package com.munch.exchange.model.core.ib.chart.signals.strategies;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.analytic.indicator.oscillators.MACD;
import com.munch.exchange.model.analytic.indicator.oscillators.RelativeStrengthIndex;
import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.analytic.indicator.trend.MovingAverage;
import com.munch.exchange.model.analytic.indicator.trend.StandardDeviation;
import com.munch.exchange.model.core.ib.IbCommission;
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
public class ReverseMaxProfit extends IbChartSignal {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5831915053085710634L;
	
	public static final String PARAM_PRICE="Price";


	public ReverseMaxProfit() {
		super();
	}

	public ReverseMaxProfit(IbChartIndicatorGroup group) {
		super(group);
	}
	
	@Override
	public IbChartIndicator copy() {
		IbChartIndicator c=new ReverseMaxProfit();
		c.copyData(this);
		return c;
	}
	
	@Override
	public void initName() {
		this.name= "Reverse Max Profit";
	}

	@Override
	public void createParameters() {
		this.parameters.add(new IbChartParameter(this, PARAM_PRICE,DataType.CLOSE.name(),DataType.toStringArray()));

	}
	
	

	
	@Override
	protected int getValidAtPosition() {
		int validAtPosition=0;
		
		return validAtPosition;
	}
	
	
	private double localMin;
	private int localMinIndex;
	
	private double localMax;
	private int localMaxIndex;
	
	@Override
	public void computeSignalPoint(List<ExBar> bars, boolean reset) {
		
//		Step 1: Read the parameters
		long[] times=BarUtils.getTimeArray(bars);
		double[] price=getPrices(bars,PARAM_PRICE);
		double[] signal=new double[price.length];
		
		signal[0] = 0;
		
		localMin = price[0];
		localMinIndex = 0;
		
		localMax = price[0];
		localMaxIndex = 0;
		
		long volume = this.getVolume();
		IbCommission commission = this.getCommission();
		
		signal[0] = 0.0;
		for(int i=1;i<price.length;i++){
			if(price[i]>localMax){
				resetMaxValues(price, i);
				if(signal[i] > 0)
					resetMinValues(price, i);
			}
			if(price[i] < localMin){
				resetMinValues(price, i);
				if(signal[i] < 0)
					resetMaxValues(price, i);
			}
			
			double MinMaxdiff = Math.abs(localMax-localMin)*volume;
			
			if(MinMaxdiff<commission.calculate(volume, price[i])){
				signal[i] = signal[i-1];
				continue;
			}
			
			if(localMaxIndex>=localMinIndex){
				resetFromToWith(signal, localMinIndex, localMaxIndex, 1.0);
				resetMinValues(price, i);
			}
			else{
				resetFromToWith(signal, localMaxIndex, localMinIndex, -1.0);
				resetMaxValues(price, i);
			}
			
		}
		
//		Clean the serie from one shoot
//		for(int i=2;i<price.length;i++){
//			if(Math.abs(signal[i-2]-signal[i-1])<2)continue;
//			if(Math.abs(signal[i-1]-signal[i])<2)continue;
//			
//			signal[i-1] = signal[i];
//			
//		}
		
		
		refreshSerieValues(this.getSignalSerie().getName(), reset, times, signal, 0);
		
	}
	
	private void resetMinValues(double[] array, int index){
		localMin = array[index];
		localMinIndex = index;
	}
	private void resetMaxValues(double[] array, int index){
		localMax = array[index];
		localMaxIndex = index;
	}
	
	private void resetFromToWith(double[] array, int from, int to, double value){
		for(int i=from;i<=to;i++){
			array[i] = value;
		}
	}
	
	private double[] getPrices(List<ExBar> bars, String paramName){
		
		String priceLabel=this.getChartParameter(paramName).getStringValue();
		double[] prices=BarUtils.barsToDoubleArray(bars, DataType.fromString(priceLabel));
		
		return prices;
	}

	

}
