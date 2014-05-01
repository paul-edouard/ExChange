package com.munch.exchange.job.objectivefunc;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;

public class BollingerBandObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
	
			
	public static final String BollingerBand_MovingAverage_Upper="Bollinger Band Moving Average Upper";
	public static final String BollingerBand_MovingAverage_Lower="Bollinger Band Moving Average Lower";
	
	public static final String BollingerBand_UpperBand="Bollinger Band upper Band";
	public static final String BollingerBand_LowerBand="Bollinger Band lower Band";
			
	
	public static final String BollingerBand_Buy_Signal="Bollinger Band Buy Signal";
	public static final String BollingerBand_Sell_Signal="Bollinger Band Sell Signal";
	
	public static final String BollingerBand_Profit="Bollinger Band Profit";
	
	
	private static final long serialVersionUID = 1;
	private static Logger logger = Logger.getLogger(BollingerBandObjFunc.class);
	
	
	//History point List
	private LinkedList<HistoricalPoint> noneZeroHisList=new LinkedList<HistoricalPoint>();
	
	//The history fled to optimize
	private String field;
	//the penalty for bay or sell
	private double penalty;
	
	//Period
	private int[] period=new int[2];
	
	//Number of days
	private long numberOfDaysUpper;
	private long numberOfDaysLower;
	private double maxNumberOfDays;
	
	//Band factor
	private double bandFactorUpper;
	private double bandFactorLower;
	private double maxBandFactor;
	
	private boolean isUpper;
	private boolean isLower;
	
	
	//Max Profit from the given period
	private double maxProfit;
	private double profit;
	
	//Series
	private XYSeries movingAverageUpperSeries;
	private XYSeries movingAverageLowerSeries;
	private XYSeries upperBandSeries;
	private XYSeries lowerBandSeries;
	
	private XYSeries profitSeries;
	private XYSeries buySignalSeries;
	private XYSeries sellSignalSeries;
	
	
	public BollingerBandObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList,
			double maxNumberOfDays, double maxBandFactor, double maxProfit){
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;
		
		this.maxNumberOfDays=maxNumberOfDays;
		this.maxBandFactor=maxBandFactor;
		
		this.maxProfit=maxProfit;
			
	}
	
	
			
	public void setPeriod(int[] period) {
		this.period = period;
	}
	
	



	public XYSeries getMovingAverageUpperSeries() {
		return movingAverageUpperSeries;
	}
	



	public XYSeries getMovingAverageLowerSeries() {
		return movingAverageLowerSeries;
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



	public XYSeries getUpperBandSeries() {
		return upperBandSeries;
	}



	public XYSeries getLowerBandSeries() {
		return lowerBandSeries;
	}



	@Override
	public double compute(double[] x, Random r) {
		if(x.length<4)return 0;
		
		numberOfDaysUpper=Math.round(x[0]*maxNumberOfDays);
		if(numberOfDaysUpper<1)
			numberOfDaysUpper=1;
		bandFactorUpper=maxBandFactor*x[1];
		
		numberOfDaysLower=Math.round(x[2]*maxNumberOfDays);
		if(numberOfDaysLower<1)
			numberOfDaysLower=1;
		bandFactorLower=maxBandFactor*x[3];
		
		long numberOfDays=Math.max(numberOfDaysUpper, numberOfDaysLower);
		
		//Init Profit
		profit=0;
		boolean bought=false;
		isUpper=false;
		isLower=false;
		
		//logger.info("Number of days: "+numberOfDaysUpper);
		//logger.info("Band factor: "+bandFactorUpper);
		
		//Init the series
		movingAverageUpperSeries=new XYSeries(BollingerBand_MovingAverage_Upper);
		movingAverageLowerSeries=new XYSeries(BollingerBand_MovingAverage_Lower);
		upperBandSeries=new XYSeries(BollingerBand_UpperBand);
		lowerBandSeries=new XYSeries(BollingerBand_LowerBand);
		
		profitSeries=new XYSeries(BollingerBand_Profit);
		buySignalSeries=new XYSeries(BollingerBand_Buy_Signal);
		sellSignalSeries=new XYSeries(BollingerBand_Sell_Signal);
		
		//Moving Average needed period
		int[] n_period=new int[2];
		n_period[0]=period[0]-(int)numberOfDays;
		n_period[1]=period[1];
		
		//get the Day list
		LinkedList<HistoricalPoint> pList=HistoricalData.getPointsFromPeriod(n_period,this.noneZeroHisList );
		if(pList.isEmpty() || pList.size()<=numberOfDays){return 0;}
		
		//Calculate the moving average
		for(int i=(int)numberOfDays;i<pList.size();i++){
			
			if((i-numberOfDays+1)>=0){
				/////////////////////////
				//        Upper        //
				/////////////////////////
				double avgUpper=0;
				for(int j=i- (int)numberOfDaysUpper+1;j<=i;j++){
					avgUpper+=pList.get(j).get(field);
				}
				avgUpper=avgUpper/numberOfDaysUpper;
				
				
				double stdDevUpper=0;
				for(int j=i- (int)numberOfDaysUpper+1;j<=i;j++){
					stdDevUpper+=Math.pow(pList.get(j).get(field)-avgUpper, 2);
				}
				stdDevUpper=stdDevUpper/numberOfDaysUpper;
				stdDevUpper=Math.sqrt(stdDevUpper);
				
				double bandUpper=avgUpper+bandFactorUpper*stdDevUpper;
				
				movingAverageUpperSeries.add((i-numberOfDays+1), avgUpper);
				upperBandSeries.add(         (i-numberOfDays+1), bandUpper);
				
				/////////////////////////
				//        Lower        //
				/////////////////////////
				double avgLower=0;
				for(int j=i- (int)numberOfDaysLower+1;j<=i;j++){
					avgLower+=pList.get(j).get(field);
				}
				avgLower=avgLower/numberOfDaysLower;
				
				
				double stdDevLower=0;
				for(int j=i- (int)numberOfDaysLower+1;j<=i;j++){
					stdDevLower+=Math.pow(pList.get(j).get(field)-avgLower, 2);
				}
				stdDevLower=stdDevLower/numberOfDaysLower;
				stdDevLower=Math.sqrt(stdDevLower);
				
				double bandLower=avgLower+bandFactorLower*stdDevLower;
				
				movingAverageLowerSeries.add((i-numberOfDays+1), avgLower);
				lowerBandSeries.add(         (i-numberOfDays+1), bandLower);
				
				/////////////////////////
				//        Profit       //
				/////////////////////////
				if(bought){
					profit+=pList.get(i).get(field)-pList.get(i-1).get(field);
				}
				
				//Sell Signal
				if(isUpper && pList.get(i).get(field)<bandUpper && bought){
					bought=false;
					profit=profit-((float)penalty)*pList.get(i).get(field);
					sellSignalSeries.add((i-numberOfDays+1),pList.get(i).get(field));
				}
				
				//Buy Signal
				if(isLower && pList.get(i).get(field)>bandLower && !bought){
					bought=true;
					profit=profit-((float)penalty)*pList.get(i).get(field);
					buySignalSeries.add((i-numberOfDays+1),pList.get(i).get(field));
				}
				
				//Set Lower and Upper
				isUpper=pList.get(i).get(field)>bandUpper;
				isLower=pList.get(i).get(field)<bandLower;
				
				profitSeries.add((i-numberOfDays+1), profit/pList.get((int)numberOfDays).get(field));
				
			}
			
		}
		
		profit=profit/pList.get((int)numberOfDays).get(field);
		
		return maxProfit-profit;
	}

}
