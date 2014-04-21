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
	private float profit;
	
	//Period
	private int from;
	private int downTo;
	
	//Moving Average Days
	private double movAvgMaxDayFac;
	private int movAvgMaxDays;
	
	private float movAvgSliderBuyFac=0;
	private float movAvgSliderSellFac=0;
	private double max=0;
	
	//MinMax Diff2
	private float diff2Max=Float.MIN_VALUE;
	private float diff2Min=Float.MIN_VALUE;
	
	
	
	public MovingAverageObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList,
			float maxProfit,int movAvgMaxDays) {
		super();
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;
		this.maxProfit = maxProfit;
		
		//this.from = from;
		//this.downTo = downTo;
		//this.movAvgMaxFac = movAvgMaxFac;
		this.movAvgMaxDays=movAvgMaxDays;
		
		calculateFactors();
		
	}
	
	public void setFromAndDownTo(int from, int downTo){
		this.from = from;
		this.downTo = downTo;
	}
	
	private void  calculateFactors(){
		for(int i=2;i<noneZeroHisList.size();i++){
				
			//Get the last 3 values	
			float curVal=noneZeroHisList.get(i).get(field);
			float lastVal=noneZeroHisList.get(i-1).get(field);
			float beforeLastVal=noneZeroHisList.get(i-2).get(field);
				
			//Calculate the diffs
			float diff=curVal-lastVal;
			float lastDiff=lastVal-beforeLastVal;
			float diff2=diff-lastDiff;
			
			//Save Min Max
			if(diff2>this.diff2Max)this.diff2Max=diff2;
			if(diff2<this.diff2Min)this.diff2Min=diff2;
		}
		
		//Set the slider Max Value	
		movAvgSliderBuyFac=100/diff2Max;
		movAvgSliderSellFac=-100/diff2Min;
		
		max=100 / Math.min(movAvgSliderBuyFac, movAvgSliderSellFac);
		
		movAvgMaxDayFac=((double)movAvgMaxDays)/max;
		
	}
	
	public double getMax(){
		return max;
	}

	
	
	
	public float getProfit() {
		return profit;
	}

	public double getMovAvgMaxDayFac() {
		return movAvgMaxDayFac;
	}

	public float getMovAvgSliderBuyFac() {
		return movAvgSliderBuyFac;
	}

	public float getMovAvgSliderSellFac() {
		return movAvgSliderSellFac;
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
		
		
		profitSeries=new XYSeries(Moving_Average_Profit);
		buySellSeries=new XYSeries(Moving_Average_Buy_And_Sell);
		
		buySignalSeries=new XYSeries(Moving_Average_Buy_Signal);
		sellSignalSeries=new XYSeries(Moving_Average_Sell_Signal);
		
		if(x.length<3)return 0;
		
		//int movAvgDays=(int)x[0];
		float buyLimit=(float)x[0];
		float sellLimit=(float)x[1];
		double dayInDoubles=x[2];
		int movAvgDays=Math.round((float) (dayInDoubles*movAvgMaxDayFac));
		if(movAvgDays==0)movAvgDays=1;
		//logger.info("movAvgDays: "+movAvgDays);
		
		int pastDays=2+movAvgDays;
		int[] period = new int[2];period[0]=from-pastDays;period[1]=downTo;
		
		LinkedList<HistoricalPoint> pList=HistoricalData.getPointsFromPeriod(period,this.noneZeroHisList );
		
		if(pList.isEmpty() || pList.size()<=pastDays){return 0;}
		
		profit=0;
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
			//if(diff2>this.diff2Max)this.diff2Max=diff2;
			//if(diff2<this.diff2Min)this.diff2Min=diff2;
				
			//buy is on the current profit have to be added
			if(bought){
				profit+=point.get(field)-last.get(field);
				buySellSeries.add((i-pastDays+1), 1);
			}
			else{
				buySellSeries.add((i-pastDays+1), -1);
			}
			
			
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
			
			
			profitSeries.add((i-pastDays+1), profit/pList.get(pastDays).get(field));
			
		}
		
		profit=profit/pList.get(pastDays).get(field);
		
		//logger.info("By Limit: "+buyLimit+", SellLimit: "+sellLimit+", Profit: " +profit);
		
		return maxProfit-profit;
		
		
	}

}
