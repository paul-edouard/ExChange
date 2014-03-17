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
			
	/** a constant required by Java serialization */
	private static final long serialVersionUID = 1;
	private static Logger logger = Logger.getLogger(MovingAverageObjFunc.class);
	 
	//The history fled to optimize
	private String field;
	//the penalty for bay or sell
	private double penalty;
	//the profit series
	private XYSeries profitSeries;
	//History point List
	private LinkedList<HistoricalPoint> noneZeroHisList=new LinkedList<HistoricalPoint>();
	//Max Profit from the given period
	private float maxProfit;
	
	//Period
	private int from;
	private int downTo;
	
	
	public MovingAverageObjFunc(String field, double penalty,
			XYSeries profitSeries, LinkedList<HistoricalPoint> pList,
			float maxProfit, int from, int downTo) {
		super();
		this.field = field;
		this.penalty = penalty;
		this.profitSeries = profitSeries;
		this.noneZeroHisList = pList;
		this.maxProfit = maxProfit;
		
		this.from = from;
		this.downTo = downTo;
	}



	@Override
	public double compute(double[] x, Random r) {
		profitSeries.clear();
		
		if(x.length<3)return 0;
		
		int MovAvgDays=(int)x[0];
		float buyLimit=(float)x[1];
		float sellLimit=(float)x[2];
		
		int pastDays=2+MovAvgDays;
		
		LinkedList<HistoricalPoint> pList=HistoricalData.getPointsFromPeriod(from,downTo+pastDays,this.noneZeroHisList );
		if(pList.isEmpty() || pList.size()<=pastDays){return 0;}
		
		float profit=0;
		HistoricalPoint point=null;
		HistoricalPoint last=null;
		
		boolean bought=false;
		//boolean firstBuy=false;
		
		//Calculate the moving average
		HashMap<Integer,Float> avgMap=new HashMap<Integer,Float>();
		for(int i=0;i<pList.size();i++){
			
			if((i-MovAvgDays+1)>=0){
				point=pList.get(i);
				float avg=0;
				for(int j=i-MovAvgDays+1;j<=i;j++){
					avg+=pList.get(j).get(field);
				}
				avg=avg/MovAvgDays;
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
				
			//buy is on the current profit have to be added
			if(bought)
				profit+=point.get(field)-last.get(field);
			
			profitSeries.add((i-pastDays+1), profit);
			//logger.info("profit:" + profit+", Diff2: "+diff2);
			
			//Test if the rate have to be bought
			if(diff2>buyLimit && bought==false){
				
				bought=true;
			//	logger.info("-------->>>>>> BUY" );
				profit=profit-((float)penalty)*pList.get(i).get(field);
			}
			//Test if the rate have to be sold
			else if(diff2<(-sellLimit) && bought==true){	
				bought=false;
			//	logger.info("-------->>>>>> SELL" );
				profit=profit-((float)penalty)*pList.get(i).get(field);
			}
		}
		
		profit=profit/pList.get(pastDays).get(field);
		
		logger.info("Profit: " +profit);
		
		return maxProfit-profit;
		
		
	}

}
