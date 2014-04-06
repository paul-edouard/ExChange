package com.munch.exchange.job.objectivefunc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;

public class MovingAverageObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
	
	public static final String Moving_Average_Buy_Signal="Moving Average Buy Signal";
	public static final String Moving_Average_Sell_Signal="Moving Average Sell Signal";
	
	public static final String Moving_Average_Profit="Moving Average Profit";
	public static final String Moving_Average_Buy_And_Sell="Moving Average Buy & Sell";
	
			
	/** a constant required by Java serialization */
	private static final long serialVersionUID = 1;
	private static Logger logger = Logger.getLogger(MovingAverageObjFunc.class);
	 
	//The history fled to optimize
	private String field;
	//the penalty for bay or sell
	private double penalty;
	
	//the profit series
	private XYSeries profitSeries=new XYSeries(Moving_Average_Profit);
	private XYSeries buySellSeries=new XYSeries(Moving_Average_Buy_And_Sell);
	
	private XYSeries buySignalSeries=new XYSeries(Moving_Average_Buy_Signal);
	private XYSeries sellSignalSeries=new XYSeries(Moving_Average_Sell_Signal);
	
	//History point List
	private LinkedList<HistoricalPoint> noneZeroHisList=new LinkedList<HistoricalPoint>();
	//Max Profit from the given period
	private float maxProfit;
	
	//Period
	private int from;
	private int downTo;
	
	//Moving Average Days
	private int movAvgDays;
	
	//MinMax Diff2
	private float diff2Max=Float.MIN_VALUE;
	private float diff2Min=Float.MIN_VALUE;
	
	
	
	public MovingAverageObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList,
			float maxProfit, int from, int downTo,int movAvgDays ) {
		super();
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;
		this.maxProfit = maxProfit;
		
		this.from = from;
		this.downTo = downTo;
		this.movAvgDays = movAvgDays;
	}

	

	public float getDiff2Max() {
		return diff2Max;
	}

	public float getDiff2Min() {
		return diff2Min;
	}
	
	
	public XYSeries getProfitSeries() {
		return profitSeries;
	}

	public XYSeries getBySellSeries() {
		return buySellSeries;
	}


	public XYSeries getBuySignalSeries() {
		return buySignalSeries;
	}



	public XYSeries getSellSignalSeries() {
		return sellSignalSeries;
	}



	@Override
	public double compute(double[] x, Random r) {
		profitSeries.clear();
		buySellSeries.clear();
		
		buySignalSeries.clear();
		sellSignalSeries.clear();
		
		if(x.length<2)return 0;
		
		//int movAvgDays=(int)x[0];
		float buyLimit=(float)x[0];
		float sellLimit=(float)x[1];
		
		int pastDays=2+movAvgDays;
		int[] period = new int[2];period[0]=from-pastDays;period[1]=downTo;
		
		LinkedList<HistoricalPoint> pList=HistoricalData.getPointsFromPeriod(period,this.noneZeroHisList );
		if(pList.isEmpty() || pList.size()<=pastDays){return 0;}
		
		float profit=0;
		HistoricalPoint point=null;
		HistoricalPoint last=null;
		
		boolean bought=false;
		//boolean firstBuy=false;
		
		//Calculate the moving average
		HashMap<Integer,Float> avgMap=new HashMap<Integer,Float>();
		for(int i=0;i<pList.size();i++){
			
			if((i-movAvgDays+1)>=0){
				point=pList.get(i);
				float avg=0;
				for(int j=i-movAvgDays+1;j<=i;j++){
					avg+=pList.get(j).get(field);
				}
				avg=avg/movAvgDays;
				avgMap.put(i, avg);
			}
			
		}
		
		
		for(int i=pastDays;i<pList.size();i++){
			
			//logger.info("**  Pos:" + String.valueOf((i-pastDays+1))+ ", by: "+bought);
			point=pList.get(i);
			last=pList.get(i-1);
				
			//Get the last 3 values	
			float curVal=avgMap.get(i);
			float lastVal=avgMap.get(i-1);
			float beforeLastVal=avgMap.get(i-2);
				
			//Calculate the diffs
			float diff=curVal-lastVal;
			float lastDiff=lastVal-beforeLastVal;
			float diff2=diff-lastDiff;
			
			//Save Min Max
			if(diff2>this.diff2Max)this.diff2Max=diff2;
			if(diff2<this.diff2Min)this.diff2Min=diff2;
				
			//buy is on the current profit have to be added
			if(bought){
				profit+=point.get(field)-last.get(field);
				buySellSeries.add((i-pastDays+1), 1);
			}
			else{
				buySellSeries.add((i-pastDays+1), -1);
			}
			
			profitSeries.add((i-pastDays+1), profit);
			
			//logger.info("profit:" + profit+", Diff2: "+diff2);
			
			//Test if the rate have to be bought
			if(diff2>buyLimit && bought==false){
				
				bought=true;
			//	logger.info("-------->>>>>> BUY" );
				profit=profit-((float)penalty)*pList.get(i).get(field);
				buySignalSeries.add((i-pastDays+1),point.get(field));
				
			}
			//Test if the rate have to be sold
			else if(diff2<(-sellLimit) && bought==true){	
				bought=false;
			//	logger.info("-------->>>>>> SELL" );
				profit=profit-((float)penalty)*pList.get(i).get(field);
				sellSignalSeries.add((i-pastDays+1),point.get(field));
			}
		}
		
		profit=profit/pList.get(pastDays).get(field);
		
		//logger.info("By Limit: "+buyLimit+", SellLimit: "+sellLimit+", Profit: " +profit);
		
		return maxProfit-profit;
		
		
	}

}
