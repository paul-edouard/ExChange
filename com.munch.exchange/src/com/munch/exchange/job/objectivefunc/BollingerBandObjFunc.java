package com.munch.exchange.job.objectivefunc;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.limit.Limit;
import com.munch.exchange.model.core.limit.LimitRange;
import com.munch.exchange.model.core.limit.LimitRange.LimitRangeType;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;

public class BollingerBandObjFunc extends OptimizationModule implements
		IObjectiveFunction<double[]> {
	
			
	public static final String BollingerBand_MovingAverage_Upper="Bollinger Band Moving Average Upper";
	public static final String BollingerBand_MovingAverage_Lower="Bollinger Band Moving Average Lower";
	public static final String BollingerBand_MovingAverage="Bollinger Band Moving Average";
	
	public static final String BollingerBand_UpperBand_Max="Bollinger Band upper Band Max";
	public static final String BollingerBand_UpperBand_Min="Bollinger Band upper Band Min";
	
	public static final String BollingerBand_LowerBand_Max="Bollinger Band lower Band Max";
	public static final String BollingerBand_LowerBand_Min="Bollinger Band lower Band Min";
	
	public static final String BollingerBand_UpperBand_Dev="Bollinger Band upper Band Deviation";
	public static final String BollingerBand_LowerBand_Dev="Bollinger Band lower Band Deviation";
			
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
	private double bandDamperUpper;
	private double bandFactorLower;
	private double bandDamperLower;
	private double maxBandFactor;
	
	private boolean isUpper;
	private boolean isLower;
	
	private boolean bought=false;
	
	//Max Profit from the given period
	private double maxProfit;
	private double profit;
	
	private double daySellUpLimit=0;
	private double daySellDownLimit=0;
	
	private double dayBuyUpLimit=0;
	private double dayBuyDownLimit=0;
	
	private boolean daySellUpLimitIsActivated=false;
	private boolean daySellDownLimitIsActivated=false;
	
	private boolean dayBuyUpLimitIsActivated=false;
	private boolean dayBuyDownLimitIsActivated=false;
	
	
	//Series
	private XYSeries movingAverageUpperSeries;
	private XYSeries movingAverageLowerSeries;
	private XYSeries movingAverageSeries;
	private XYSeries upperBandMaxSeries;
	private XYSeries upperBandMinSeries;
	private XYSeries lowerBandMaxSeries;
	private XYSeries lowerBandMinSeries;
	
	private YIntervalSeries upperBandDevSeries;
	private YIntervalSeries lowerBandDevSeries;
	
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



	public XYSeries getUpperBandMaxSeries() {
		return upperBandMaxSeries;
	}



	public XYSeries getLowerBandMaxSeries() {
		return lowerBandMaxSeries;
	}



	public XYSeries getUpperBandMinSeries() {
		return upperBandMinSeries;
	}



	public XYSeries getLowerBandMinSeries() {
		return lowerBandMinSeries;
	}
	
	



	public double getProfit() {
		return profit;
	}



	public XYSeries getMovingAverageSeries() {
		return movingAverageSeries;
	}



	public double getDaySellUpLimit() {
		return daySellUpLimit;
	}



	public double getDaySellDownLimit() {
		return daySellDownLimit;
	}



	public double getDayBuyUpLimit() {
		return dayBuyUpLimit;
	}



	public double getDayBuyDownLimit() {
		return dayBuyDownLimit;
	}

	

	public boolean isDaySellUpLimitIsActivated() {
		return daySellUpLimitIsActivated;
	}



	public boolean isDaySellDownLimitIsActivated() {
		return daySellDownLimitIsActivated;
	}



	public boolean isDayBuyUpLimitIsActivated() {
		return dayBuyUpLimitIsActivated;
	}



	public boolean isDayBuyDownLimitIsActivated() {
		return dayBuyDownLimitIsActivated;
	}
	
	



	public YIntervalSeries getUpperBandDevSeries() {
		return upperBandDevSeries;
	}



	public YIntervalSeries getLowerBandDevSeries() {
		return lowerBandDevSeries;
	}



	public double getMaxProfit() {
		return maxProfit;
	}
	
	public String getLimitStr(){
		if(bought){
			return "Sell: " +this.getSellLimitStr();
		}
		else{
			return "Buy: " + this.getBuyLimitStr();
		}
	}
	
	public LimitRange getLimitRange(){
		if(bought){
			Limit upper=new Limit(this.isDaySellUpLimitIsActivated(), this.getDaySellUpLimit());
			Limit lower=new Limit(this.isDaySellDownLimitIsActivated(), this.getDaySellDownLimit());
			
			return new LimitRange(upper, lower, LimitRangeType.SELL);
		}
		else{
			Limit upper=new Limit(this.isDayBuyUpLimitIsActivated(), this.getDayBuyUpLimit());
			Limit lower=new Limit(this.isDayBuyDownLimitIsActivated(), this.getDayBuyDownLimit());
			
			return new LimitRange(upper, lower, LimitRangeType.BUY);
		}
		
		
	}
	
	
	public String getSellLimitStr(){
		String daySellLimits_str="["+
				String.format("%,.3f",this.getDaySellUpLimit())+
				((this.isDaySellUpLimitIsActivated())?"*":"")+
				"-"+
				String.format("%,.3f",this.getDaySellDownLimit())+
				((this.isDaySellDownLimitIsActivated())?"*":"")+
				"]";
		
		return daySellLimits_str;
	}
	
	public String getBuyLimitStr(){
		String dayBuyLimits_str="["+
				String.format("%,.3f",this.getDayBuyUpLimit())+
				((this.isDayBuyUpLimitIsActivated())?"*":"")+
				"-"+
				String.format("%,.3f",this.getDayBuyDownLimit())+
				((this.isDayBuyDownLimitIsActivated())?"*":"")+
				"]";
		return dayBuyLimits_str;
	}
	

	public double compute(ExchangeRate rate){
		double[] g=rate.getOptResultsMap().get(Type.BILLINGER_BAND).getResults().getFirst().getDoubleArray();
		return compute(g,null);
		
	}

	@Override
	public double compute(double[] x, Random r) {
		if(x.length<6)return 0;
		
		numberOfDaysUpper=Math.round(x[0]*maxNumberOfDays);
		if(numberOfDaysUpper<1)
			numberOfDaysUpper=1;
		bandFactorUpper=maxBandFactor*x[1];
		bandDamperUpper=maxBandFactor*x[2]/2;
		
		numberOfDaysLower=Math.round(x[3]*maxNumberOfDays);
		if(numberOfDaysLower<1)
			numberOfDaysLower=1;
		bandFactorLower=maxBandFactor*x[4];
		bandDamperLower=maxBandFactor*x[5]/2;
		
		long numberOfDays=Math.max(numberOfDaysUpper, numberOfDaysLower);
		
		//Init Profit
		profit=0;
		bought=false;
		isUpper=false;
		isLower=false;
		
		//boolean isReadyToBuy=false;
		//boolean isReadyToSell=false;
		
		//logger.info("Number of days: "+numberOfDaysUpper);
		//logger.info("Band factor: "+bandFactorUpper);
		
		//Init the series
		movingAverageUpperSeries=new XYSeries(BollingerBand_MovingAverage_Upper);
		movingAverageLowerSeries=new XYSeries(BollingerBand_MovingAverage_Lower);
		movingAverageSeries=new XYSeries(BollingerBand_MovingAverage);
		upperBandMaxSeries=new XYSeries(BollingerBand_UpperBand_Max);
		upperBandMinSeries=new XYSeries(BollingerBand_UpperBand_Min);
		lowerBandMaxSeries=new XYSeries(BollingerBand_LowerBand_Max);
		lowerBandMinSeries=new XYSeries(BollingerBand_LowerBand_Min);
		upperBandDevSeries=new YIntervalSeries(BollingerBand_UpperBand_Dev);
		lowerBandDevSeries=new YIntervalSeries(BollingerBand_LowerBand_Dev);
		
		profitSeries=new XYSeries(BollingerBand_Profit);
		buySignalSeries=new XYSeries(BollingerBand_Buy_Signal);
		sellSignalSeries=new XYSeries(BollingerBand_Sell_Signal);
		
		//Moving Average needed period
		int[] n_period=new int[2];
		n_period[0]=period[0]-(int)numberOfDays;
		n_period[1]=period[1];
		
		//Fields
		String upperField=DatePoint.FIELD_High;
		String lowerField=DatePoint.FIELD_Low;
		
		//get the Day list
		LinkedList<HistoricalPoint> pList=HistoricalData.getPointsFromPeriod(n_period,this.noneZeroHisList );
		if(pList.isEmpty() || pList.size()<=numberOfDays){return 0;}
		
		double lastBuyPrice=pList.getFirst().get(field);
		
		//Calculate the moving average
		for(int i=(int)numberOfDays;i<pList.size();i++){
			
			if((i-numberOfDays+1)>=0){
				
				/////////////////////////
				//        Middle       //
				/////////////////////////
				double avg=0;
				for(int j=i- (int)numberOfDaysUpper+1;j<=i;j++){
					avg+=pList.get(j).get(field);
				}
				avg=avg/numberOfDaysUpper;
				
				movingAverageSeries.add((i-numberOfDays+1), avg);
				/////////////////////////
				//        Upper        //
				/////////////////////////
				
				double avgUpperMax=0;
				double avgUpperMin=0;
				
				for(int j=i- (int)numberOfDaysUpper+1;j<=i;j++){
					avgUpperMax+=pList.get(j).get(upperField);
					avgUpperMin+=pList.get(j).get(lowerField);
				}
				avgUpperMax=avgUpperMax/numberOfDaysUpper;
				avgUpperMin=avgUpperMin/numberOfDaysUpper;
				
				
				double stdDevUpperMax=0;
				double stdDevUpperMin=0;
				for(int j=i- (int)numberOfDaysUpper+1;j<=i;j++){
					stdDevUpperMax+=Math.pow(pList.get(j).get(upperField)-avgUpperMax, 2);
					stdDevUpperMin+=Math.pow(pList.get(j).get(lowerField)-avgUpperMin, 2);
				}
				stdDevUpperMax=Math.sqrt(stdDevUpperMax/numberOfDaysUpper);
				stdDevUpperMin=Math.sqrt(stdDevUpperMin/numberOfDaysUpper);
				
				double bandUpperMax=avgUpperMax+(bandFactorUpper+bandDamperUpper)*stdDevUpperMax;
				double bandUpperMin=avgUpperMin+(bandFactorUpper-bandDamperUpper)*stdDevUpperMin;
				
				movingAverageUpperSeries.add((i-numberOfDays+1), avgUpperMax);
				upperBandMaxSeries.add(      (i-numberOfDays+1), bandUpperMax);
				upperBandMinSeries.add(      (i-numberOfDays+1), bandUpperMin);
				upperBandDevSeries.add(      (i-numberOfDays+1), (bandUpperMax+bandUpperMin)/2,
						                     bandUpperMin, bandUpperMax);
				
				/////////////////////////
				//        Lower        //
				/////////////////////////
				
				double avgLowerMax=0;
				double avgLowerMin=0;
				
				for(int j=i- (int)numberOfDaysLower+1;j<=i;j++){
					avgLowerMax+=pList.get(j).get(upperField);
					avgLowerMin+=pList.get(j).get(lowerField);
				}
				avgLowerMax=avgLowerMax/numberOfDaysLower;
				avgLowerMin=avgLowerMin/numberOfDaysLower;
				
				double stdDevLowerMax=0;
				double stdDevLowerMin=0;
				
				for(int j=i- (int)numberOfDaysLower+1;j<=i;j++){
					stdDevLowerMax+=Math.pow(pList.get(j).get(upperField)-avgLowerMax, 2);
					stdDevLowerMin+=Math.pow(pList.get(j).get(lowerField)-avgLowerMin, 2);
				}
				stdDevLowerMax=Math.sqrt(stdDevLowerMax/numberOfDaysLower);
				stdDevLowerMin=Math.sqrt(stdDevLowerMin/numberOfDaysLower);
				
				double bandLowerMax=avgLowerMax-(bandFactorLower-bandDamperLower)*stdDevLowerMax;
				double bandLowerMin=avgLowerMin-(bandFactorLower+bandDamperLower)*stdDevLowerMin;
				
				movingAverageLowerSeries.add((i-numberOfDays+1), avgLowerMax);
				lowerBandMaxSeries.add(      (i-numberOfDays+1), bandLowerMax);
				lowerBandMinSeries.add(      (i-numberOfDays+1), bandLowerMin);
				lowerBandDevSeries.add(      (i-numberOfDays+1), (bandLowerMin+bandLowerMax)/2,
						                      bandLowerMin, bandLowerMax);
				
				/////////////////////////
				//        Profit       //
				/////////////////////////
				
				//double soldPrice=pList.get(i).get(field);
				
				//Buy Signal
				if(!bought){
					//The curve goes under
					if( pList.get(i).get(lowerField)<bandLowerMin){
						
						lastBuyPrice=bandLowerMin;
						
						bought=true;
						profit=profit-((float)penalty)*lastBuyPrice;
						buySignalSeries.add((i-numberOfDays+1),lastBuyPrice);
					}
					//The curve goes upper
					else if(isLower  &&  pList.get(i).get(upperField)>bandLowerMax){
						
						lastBuyPrice=bandLowerMax;
						
						bought=true;
						profit=profit-((float)penalty)*lastBuyPrice;
						buySignalSeries.add((i-numberOfDays+1),lastBuyPrice);
						
					}
						
					
				}
				
				//Sell Signal
				else if(bought){
					//upper the sell limit
					if( pList.get(i).get(upperField)>bandUpperMax ){
						profit+=bandUpperMax-lastBuyPrice;
						
						bought=false;
						profit=profit-((float)penalty)*bandUpperMax;
						sellSignalSeries.add((i-numberOfDays+1),bandUpperMax);
						
					}
					//under the sell limit
					else if(isUpper && pList.get(i).get(lowerField)<bandUpperMin){
						profit+=bandUpperMin-lastBuyPrice;
						
						bought=false;
						profit=profit-((float)penalty)*bandUpperMin;
						sellSignalSeries.add((i-numberOfDays+1),bandUpperMin);
						
					}
					else{
						profit+=pList.get(i).get(field)-lastBuyPrice;
						lastBuyPrice=pList.get(i).get(field);
					}
					
				}
				
				
				//Set Lower and Upper
				isUpper=(pList.get(i).get(upperField)<bandUpperMax
						&& pList.get(i).get(lowerField)>bandUpperMin);
				
				isLower=(pList.get(i).get(upperField)<bandLowerMax
						&& pList.get(i).get(lowerField)>bandLowerMin);
				
				profitSeries.add((i-numberOfDays+1), profit/pList.get((int)numberOfDays).get(field));
				
				if(bought){
					dayBuyUpLimitIsActivated=false;
					dayBuyDownLimitIsActivated=false;
					daySellUpLimitIsActivated=pList.get(i).get(upperField)<bandUpperMax;
					daySellDownLimitIsActivated=pList.get(i).get(lowerField)>bandUpperMin;
					
				}
				else{
					daySellUpLimitIsActivated=false;
					daySellDownLimitIsActivated=false;
					
					dayBuyUpLimitIsActivated=pList.get(i).get(upperField)<bandLowerMax;
					dayBuyDownLimitIsActivated=pList.get(i).get(lowerField)>bandLowerMin;
				}
				
				/*
				private boolean daySellUpLimitIsActivated=false;
				private boolean daySellDownLimitIsActivated=false;
				
				private boolean dayBuyUpLimitIsActivated=false;
				private boolean dayBuyDownLimitIsActivated=false;
				*/
				
				
				//Save the limits
				daySellUpLimit=bandUpperMax;
				daySellDownLimit=bandUpperMin;
				
				dayBuyUpLimit=bandLowerMax;
				dayBuyDownLimit=bandLowerMin;
				
			}
			
		}
		
		profit=profit/pList.get((int)numberOfDays).get(field);
		
		return maxProfit-profit;
	}

}
