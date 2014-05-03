package com.munch.exchange.job.objectivefunc;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.core.historical.HistoricalPoint;

public class ParabolicSARObjFunc extends OptimizationModule implements
IObjectiveFunction<double[]> {
	
	
	public static final String Parabolic_SAR_Bullish_Trend = "Parabolic SAR Bullish Trend";
	public static final String Parabolic_SAR_Bearish_Trend = "Parabolic SAR Bearish Trend";

	public static final String Parabolic_SAR_Buy_Signal = "Parabolic SAR Buy Signal";
	public static final String Parabolic_SAR_Sell_Signal = "Parabolic SAR Sell Signal";

	public static final String Parabolic_SAR_Profit = "Parabolic SAR Profit";

	private static final long serialVersionUID = 1;
	private static Logger logger = Logger.getLogger(ParabolicSARObjFunc.class);

	// History point List
	private LinkedList<HistoricalPoint> noneZeroHisList = new LinkedList<HistoricalPoint>();

	// The history fled to optimize
	private String field;
	// the penalty for bay or sell
	private double penalty;

	// Period
	private int[] period = new int[2];

	// Number of days
	private double accMaxIncrement = 10;
	private double maxAccMaxIncrement = 40;
	
	private double acceleratorStart=0.02;
	private double maxAcceleratorStart=0.5;
	
	private double acceleratorIncrement=0.02;
	private double maxAcceleratorIncrement=0.5;


	// Max Profit from the given period
	private double maxProfit;
	private double profit;
	
	//Limits
	private double stopLossLimit=0;
	private double startBuyLimit=0;

	// Series
	private XYSeries bullishTrendSeries;
	private XYSeries bearishTrendSeries;

	private XYSeries profitSeries;
	private XYSeries buySignalSeries;
	private XYSeries sellSignalSeries;

	public ParabolicSARObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList, double maxAcceleratorStart,
			double maxAcceleratorIncrement, double maxAccMaxIncrement, double maxProfit) {
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;

		this.maxAcceleratorStart = maxAcceleratorStart;
		this.maxAcceleratorIncrement = maxAcceleratorIncrement;
		this.maxAccMaxIncrement=maxAccMaxIncrement;

		this.maxProfit = maxProfit;

	}

	public void setPeriod(int[] period) {
		this.period = period;
	}


	public XYSeries getProfitSeries() {
		return profitSeries;
	}

	public XYSeries getBuySignalSeries() {
		return buySignalSeries;
	}

	public XYSeries getSellSignalSeries() {
		return sellSignalSeries;
	}
	
	public double getProfit() {
		return profit;
	}
	
	


	public double getStopLossLimit() {
		return stopLossLimit;
	}

	public double getStartBuyLimit() {
		return startBuyLimit;
	}

	public XYSeries getBullishTrendSeries() {
		return bullishTrendSeries;
	}

	public XYSeries getBearishTrendSeries() {
		return bearishTrendSeries;
	}

	@Override
	public double compute(double[] x, Random r) {
		if (x.length < 3)
			return 0;
		
		//logger.info("Start Compute");
		
		//Acc
		acceleratorStart=x[0]*maxAcceleratorStart;
		acceleratorIncrement=x[1]*maxAcceleratorIncrement;
		accMaxIncrement=Math.round(x[2]*maxAccMaxIncrement);
		if(accMaxIncrement<1)
			accMaxIncrement=1;

		// Init Profit
		profit = 0;
		boolean bought = false;
		
		stopLossLimit=0;
		startBuyLimit=0;
	
		//Rising
		//int periodLength=period[1]-period[0];
		
		// Init the series
		bullishTrendSeries = new XYSeries(Parabolic_SAR_Bullish_Trend);
		bearishTrendSeries = new XYSeries(Parabolic_SAR_Bearish_Trend);

		profitSeries = new XYSeries(Parabolic_SAR_Profit);
		buySignalSeries = new XYSeries(Parabolic_SAR_Buy_Signal);
		sellSignalSeries = new XYSeries(Parabolic_SAR_Sell_Signal);

		
		//Calculate the Parabolic SAR
		boolean isRising=true;
		
		double SAR=noneZeroHisList.getFirst().getLow();
		double EP=noneZeroHisList.getFirst().getHigh();
		double AF=acceleratorStart;
		
		double maxAcc=acceleratorIncrement*accMaxIncrement;
		
		int pos=-1;
		//int startPeriodPos=noneZeroHisList.size()-periodLength;
		
		//boolean isStarted=false;
		
		for(HistoricalPoint point : noneZeroHisList){
			
			pos++;
			if(pos<period[0]-250)continue;
			
			HistoricalPoint last=null;
			HistoricalPoint next=null;
			
			if(pos>0)last=noneZeroHisList.get(pos-1);
			if(pos<noneZeroHisList.size()-1)next=noneZeroHisList.get(pos+1);
			
			if( last==null || next==null)continue;
			
			
			if(isRising){
				if(pos>=period[0]-1 && pos<period[1]-1)
					profit+=next.get(field)-point.get(field);
				
				//Previous SAR + Previous AF(Previous EP + Previous SAR) = Current SAR
				SAR=SAR+AF*(EP-SAR);
				
				if(EP<point.getHigh()){
					EP=point.getHigh();
					AF+=acceleratorIncrement;
					if(AF>maxAcc)AF=maxAcc;
				}
				
				if( (/*SAR<last.getHigh() &&*/ SAR>last.getLow()) ||
					(/*SAR<point.getHigh() && */SAR>point.getLow())	){
					SAR=Math.min(point.getLow(),last.getLow());
				}
				
				stopLossLimit=SAR;
				startBuyLimit=0;
				
				
			}
			else{
				//Previous SAR - Previous AF(Previous SAR - Previous EP) = Current SAR
				SAR=SAR+AF*(EP-SAR);
				
				if(EP>point.getLow()){
					EP=point.getLow();
					AF+=acceleratorIncrement;
					if(AF>maxAcc)AF=maxAcc;
				}
				
				if( (SAR<last.getHigh() /*&& SAR>last.getLow()*/) ||
						(SAR<point.getHigh() /*&& SAR>point.getLow()*/)	){
						SAR=Math.max(point.getHigh(),last.getHigh());
				}
				
				stopLossLimit=0;
				startBuyLimit=SAR;
				
			}
			
			//New Trend
			if(isRising && next!=null){
				//Sell
				if(SAR>next.getLow()){
					if(pos>=period[0]-1 && pos<period[1]-1){
						profit=profit-((float)penalty)*SAR;
						sellSignalSeries.add(pos-period[0]+2,SAR);
					}
					AF=acceleratorStart;
					SAR=EP;
					EP=point.getLow();
					isRising=!isRising;
					
					
				}
			}
			//Buy
			else if( next!=null && SAR<next.getHigh()){
				if(pos>=period[0]-1 && pos<period[1]-1){
					profit=profit-((float)penalty)*SAR;
					buySignalSeries.add((pos-period[0]+2),SAR);
				}
				
				
				AF=acceleratorStart;
				SAR=EP;
				EP=point.getHigh();
				isRising=!isRising;
					
			}
			
			
			//Save the Trend
			if(pos>=period[0] && pos<period[1]){
				//logger.info("Add point");
				if(isRising){
					bullishTrendSeries.add(pos-period[0]+2, SAR);
					
				}
				else{
					bearishTrendSeries.add(pos-period[0]+2, SAR);
				}
			}
			if(pos>=period[0]-1 && pos<period[1]-1)
				profitSeries.add(pos-period[0]+2, profit/noneZeroHisList.get(period[0]).get(field));
			
			
		}
		
		
		
		profit = profit / noneZeroHisList.get((int) period[0]).get(field);

		return maxProfit - profit;
		
	}

}
