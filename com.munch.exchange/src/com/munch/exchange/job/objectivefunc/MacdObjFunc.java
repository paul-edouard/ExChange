package com.munch.exchange.job.objectivefunc;

import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public class MacdObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
			
	/** a constant required by Java serialization */
	private static final long serialVersionUID = 1;
	private static Logger logger = Logger.getLogger(MacdObjFunc.class);
	
	public static final String Macd_Profit="MACD Profit";
	public static final String Macd_Buy_And_Sell="MACD Buy & Sell";
	
	public static final String Macd_Buy_Signal="MACD Buy Signal";
	public static final String Macd_Sell_Signal="MACD Sell Signal";
	
	public static final String Macd_EMA_Fast="MACD EMA fast";
	public static final String Macd_EMA_Slow="MACD EMA slow";
	
	
	//SERIES
	private XYSeries timeSeries;
	
	private XYSeries macdEmaFastSeries;
	private XYSeries macdEmaSlowSeries;
	private XYSeries macdSeries;
	private XYSeries macdSignalSeries;
	
	//the profit series
	private XYSeries profitSeries=new XYSeries(Macd_Profit);
	private XYSeries bySellSeries=new XYSeries(Macd_Buy_And_Sell);
	
	private XYSeries buySignalSeries=new XYSeries(Macd_Buy_Signal);
	private XYSeries sellSignalSeries=new XYSeries(Macd_Sell_Signal);
	
	//ALPHA
	private double fastAlpha=0;
	private double slowAlpha=0;
	private double signalAlpha=0;
	
	//Period
	private int[] period;
	
	//Max Profit from the given period
	private double maxProfit;
	//Profit
	private double profit;
	
	//the penalty for bay or sell
	private double penalty;
	
	public MacdObjFunc(XYSeries timeSeries,int[] period,double maxProfit, double penalty){
		this.timeSeries=timeSeries;
		this.period=period;
		this.maxProfit=maxProfit;
		this.penalty=penalty;
	}
	
	
	
	public XYSeries getMacdEmaFastSeries() {
		return macdEmaFastSeries;
	}



	public XYSeries getMacdEmaSlowSeries() {
		return macdEmaSlowSeries;
	}



	public XYSeries getMacdSeries() {
		return macdSeries;
	}



	public XYSeries getMacdSignalSeries() {
		return macdSignalSeries;
	}



	public XYSeries getProfitSeries() {
		return profitSeries;
	}



	public XYSeries getBySellSeries() {
		return bySellSeries;
	}
	
	
	public double getProfit() {
		return profit;
	}

	

	public XYSeries getBuySignalSeries() {
		return buySignalSeries;
	}



	public XYSeries getSellSignalSeries() {
		return sellSignalSeries;
	}



	@Override
	public double compute(double[] x, Random r) {
		if(x.length!=3)return Double.MAX_VALUE;
		
		profitSeries.clear();
		bySellSeries.clear();
		
		buySignalSeries.clear();
		sellSignalSeries.clear();
		
		fastAlpha=x[0];
		slowAlpha=x[1];
		signalAlpha=x[2];
		
		//Calculate the EMAs
		macdEmaFastSeries=calculateEMA(this.timeSeries,fastAlpha,Macd_EMA_Fast);
		macdEmaSlowSeries=calculateEMA(this.timeSeries,slowAlpha,Macd_EMA_Slow);
		macdSeries=MacdObjFunc.getDiffSeries(macdEmaFastSeries,macdEmaSlowSeries,"MACD");
		macdSignalSeries=calculateEMA(macdSeries,signalAlpha,"MACD Signal");
		
		//Calculate the Profit for the period:
		profit=0;
		boolean bought=false;
		
		//XYSeries r_macdSeries=reduceSerieToPeriod(macdSeries,period);
		//XYSeries r_macdSignalSeries=reduceSerieToPeriod(macdSignalSeries,period);
		//XYSeries r_timeSeries=reduceSerieToPeriod(timeSeries,period);
		
		for(int i=1;i<timeSeries.getItemCount();i++){
			if(i<period[0]|| i>=period[1])continue;
			
			XYDataItem item_macd=  macdSeries.getDataItem(i);
			//XYDataItem item_macd_last=  macdSeries.getDataItem(i-1);
			
			XYDataItem item_signal=  macdSignalSeries.getDataItem(i);
			//XYDataItem item_signal_last=  macdSignalSeries.getDataItem(i-1);
			
			XYDataItem item_time=  timeSeries.getDataItem(i);
			XYDataItem item_time_last=  timeSeries.getDataItem(i-1);
			
			//buy is on the current profit have to be added
			if(bought){
				profit+=item_time.getYValue()-item_time_last.getYValue();
				bySellSeries.add((i-period[0]+1), 1);
			}
			else{
				bySellSeries.add((i-period[0]+1), -1);
			}
			
			
			
			//Test if the rate have to be bought
			if(item_macd.getYValue()>item_signal.getYValue() && bought==false){
				
				bought=true;
			//	logger.info("-------->>>>>> BUY" );
				profit=profit-((float)penalty)*item_time.getYValue();
				buySignalSeries.add((i-period[0]+1),item_time.getYValue());
			}
			//Test if the rate have to be sold
			else if(item_macd.getYValue()<=item_signal.getYValue() && bought==true){	
				bought=false;
			//	logger.info("-------->>>>>> SELL" );
				profit=profit-((float)penalty)*item_time.getYValue();
				sellSignalSeries.add((i-period[0]+1),item_time.getYValue());
			}
			
			profitSeries.add((i-period[0]+1), profit/timeSeries.getDataItem(period[0]).getYValue());
			
		}
		
		profit=profit/timeSeries.getDataItem(period[0]).getYValue();
		return maxProfit-profit;
		
		
	}
	
	public static XYSeries calculateEMA(XYSeries input,double alpha, String serieName){
		XYSeries series = new XYSeries(serieName);
		
		double EMA= input.getDataItem(0).getYValue();
		series.add(1,EMA);
		
		for(int i=1;i<input.getItemCount();i++){
			XYDataItem item=  input.getDataItem(i);
			EMA=EMA+alpha*(item.getYValue()-EMA);
			series.add(i+1,EMA);
		}
		 
		return series;
	}
	
	public static XYSeries getDiffSeries(XYSeries series1, XYSeries series2,String name){
		XYSeries r_series =new XYSeries(name);
		if(series1.getItemCount()!=series2.getItemCount())return r_series;
		
		for(int i=0;i<series1.getItemCount();i++){
			XYDataItem item1=  series1.getDataItem(i);
			XYDataItem item2=  series2.getDataItem(i);
			r_series.add(item1.getXValue(), item1.getYValue()-item2.getYValue());
		}
		
		return r_series;
		
	}
	
	/**
	 * this function reduce the series to the given period
	 * 
	 * @param series
	 * @param period
	 * @return
	 */
	public static XYSeries  reduceSerieToPeriod(XYSeries series, int[] period){
		XYSeries r_series =new XYSeries(series.getKey());
		
		XYSeries sub_series;
		try {
			sub_series = series.createCopy(period[0], period[1]-1);
			int pos=1;
			for(Object obj:sub_series.getItems()){
				if(obj instanceof XYDataItem){
					XYDataItem item = (XYDataItem) obj;
					r_series.add(pos, item.getYValue());
				}
				pos++;
			}
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//logger.info("Moving Average Size: "+r_series.getItemCount());
		
		return 	r_series;
	}
	
	public static void main(String[] args) {
		double t=9.0;
		double alpha=1-2/(t+1);
		
		System.out.println(alpha);
		
		
	}
	

}
