package com.munch.exchange.job.objectivefunc;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.limit.Limit;
import com.munch.exchange.model.core.limit.LimitRange;
import com.munch.exchange.model.core.limit.LimitRange.LimitRangeType;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;


public class RelativeStrengthIndexObjFunc extends OptimizationModule implements
IObjectiveFunction<double[]> {
	
	
	public static final String RSI = "Relative Strength Index";
	public static final String RSI_UpperBand = "RSI Upper Band";
	public static final String RSI_LowerBand = "RSI Lower Band";

	public static final String RSI_Buy_Signal = "RSI Buy Signal";
	public static final String RSI_Sell_Signal = "RSI Sell Signal";

	public static final String RSI_Profit = "RSI Profit";

	private static final long serialVersionUID = 1;
	private static Logger logger = Logger.getLogger(RelativeStrengthIndexObjFunc.class);

	// History point List
	private LinkedList<HistoricalPoint> noneZeroHisList = new LinkedList<HistoricalPoint>();

	// The history fled to optimize
	private String field;
	// the penalty for bay or sell
	private double penalty;

	// Period
	private int[] period = new int[2];
	private int maxPastDays=100;

	//Parameters
	private double alpha=0.0714;
	
	private double upperMax=0.75;
	private double upperMinFac=0.5;
	
	//to calculate as 1-lowerMin
	private double lowerMin=0.75;
	private double lowerMaxFac=0.5;


	// Max Profit from the given period
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
	
	//Limits
	private double upperLimit=0;
	private double lowerLimit=0;
	private boolean isUpper;
	private boolean isLower;
	
	private boolean bought=false;
	
	// Series
	private XYSeries rsiSeries;
	
	private YIntervalSeries upperBandSeries;
	private YIntervalSeries lowerBandSeries;

	private XYSeries profitSeries;
	private XYSeries buySignalSeries;
	private XYSeries sellSignalSeries;

	public RelativeStrengthIndexObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList,  double maxProfit) {
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;

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
	

	public YIntervalSeries getUpperBandSeries() {
		return upperBandSeries;
	}

	public YIntervalSeries getLowerBandSeries() {
		return lowerBandSeries;
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

	public boolean isBought() {
		return bought;
	}
	
	
	public double getMaxProfit() {
		return maxProfit;
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
	
	public LimitRange getUpperLimitRange(){
		Limit upper=new Limit(this.isDaySellUpLimitIsActivated(), this.getDaySellUpLimit());
		Limit lower=new Limit(this.isDaySellDownLimitIsActivated(), this.getDaySellDownLimit());
		
		return new LimitRange(upper, lower, LimitRangeType.SELL);
	}
	
	public LimitRange getLowerLimitRange(){
		Limit upper=new Limit(this.isDayBuyUpLimitIsActivated(), this.getDayBuyUpLimit());
		Limit lower=new Limit(this.isDayBuyDownLimitIsActivated(), this.getDayBuyDownLimit());
		
		return new LimitRange(upper, lower, LimitRangeType.BUY);
	}
	
	
	public double compute(ExchangeRate rate){
		double[] g=rate.getOptResultsMap().get(Type.RELATIVE_STRENGTH_INDEX).getResults().getFirst().getDoubleArray();
		return compute(g,null);
		
	}
	

	@Override
	public double compute(double[] x, Random r) {
		if (x.length < 5)
			return 0;
		
		//logger.info("Start Compute");
		
		//Acc
		alpha=x[0];
		
		upperMax=x[1];
		upperMinFac=x[2];
		
		lowerMin=x[3];
		lowerMaxFac=x[4];
		

		// Init Profit
		profit = 0;
		bought = false;
		isUpper=false;
		isLower=false;
		
		upperLimit=0;
		lowerLimit=0;
		
		double lastBuyPrice=noneZeroHisList.getFirst().get(field);
		
		// Init the series
		rsiSeries = new XYSeries(RSI);

		profitSeries = new XYSeries(RSI_Profit);
		buySignalSeries = new XYSeries(RSI_Buy_Signal);
		sellSignalSeries = new XYSeries(RSI_Sell_Signal);

		upperBandSeries=new YIntervalSeries(RSI_UpperBand);
		lowerBandSeries=new YIntervalSeries(RSI_LowerBand);
		
		
		//Calculate the RSI
		double[] U= new double[noneZeroHisList.size()];
		double[] D= new double[noneZeroHisList.size()];
		double[] RSI= new double[noneZeroHisList.size()];
		
		HistoricalPoint previous=null;
		int i=0;
		for(HistoricalPoint point:noneZeroHisList){
			U[i]=D[i]=0;
			
			if(i<period[0]-maxPastDays || i>period[1]){i++;continue;}
			
			if(previous!=null && point.getClose()>previous.getClose()){
				U[i]=point.getClose()-previous.getClose();
			}
			else if (previous!=null && point.getClose()<previous.getClose()){
				D[i]=previous.getClose()-point.getClose();
			}
			
			i++;
			previous=point;
		}
		
		//Max And Min
		double u_max,u_min,l_max,l_min;
		//Tomorrow Limits
		double[] u_max_limit=new double[noneZeroHisList.size()];
		double[] u_min_limit=new double[noneZeroHisList.size()];
		double[] l_max_limit=new double[noneZeroHisList.size()];
		double[] l_min_limit=new double[noneZeroHisList.size()];
		u_max=upperMax*100;
		u_min=upperMax*100*upperMinFac;
		
		l_min=(1-lowerMin)*100;
		l_max=(1-lowerMin*lowerMaxFac)*100;
		
		RSI[0]=0;
		double RS=0;
		double EMA_U=0;
		double EMA_D=0;
		
		for(i=1;i<noneZeroHisList.size();i++){
			
			if(i<period[0]-maxPastDays || i>period[1])continue;
			
			EMA_U=EMA_U+alpha*(U[i]-EMA_U);
			EMA_D=EMA_D+alpha*(D[i]-EMA_D);
			RS=EMA_U/EMA_D;
			RSI[i]=100-100/(1+RS);
			
			float day_close=noneZeroHisList.get(i).getClose();
			u_max_limit[i]=day_close+calculateTomorrowDeltaToGetRSILimit(RSI[i], EMA_U, EMA_D, u_max);
			u_min_limit[i]=day_close+calculateTomorrowDeltaToGetRSILimit(RSI[i], EMA_U, EMA_D, u_min);
			
			l_max_limit[i]=day_close+calculateTomorrowDeltaToGetRSILimit(RSI[i], EMA_U, EMA_D, l_max);
			l_min_limit[i]=day_close+calculateTomorrowDeltaToGetRSILimit(RSI[i], EMA_U, EMA_D, l_min);
		}
		
		
		//Create the Band Series
		int pos=1;
		
		for( i=period[0];i<period[1];i++){
			rsiSeries.add(pos, RSI[i]);
				
			upperBandSeries.add(pos, (u_max+u_min)/2, u_min, u_max);
			lowerBandSeries.add(pos, (l_max+l_min)/2, l_min, l_max);
			
			if(i-1<0)continue;
			
			HistoricalPoint point=noneZeroHisList.get(i);
			
			//Buy Signal
			if(!bought){
				//The curve goes under
				if(point.getLow()<l_min_limit[i-1]){
					
					if(point.getOpen()<l_min_limit[i-1]){
						lastBuyPrice=point.getHigh();
					}
					else{
						lastBuyPrice=l_min_limit[i-1];
					}
					
					bought=true;
					profit=profit-((float)penalty)*lastBuyPrice;
					buySignalSeries.add(pos,lastBuyPrice);
				}
				//The curve goes upper
				else if(isLower  &&  point.getHigh()>l_max_limit[i-1]){
					if(point.getOpen()>l_max_limit[i-1]){
						lastBuyPrice=point.getOpen();
					}
					else{
						lastBuyPrice=l_max_limit[i-1];
					}
					
					bought=true;
					profit=profit-((float)penalty)*lastBuyPrice;
					buySignalSeries.add(pos,lastBuyPrice);
				}
			}
			//Sell Signal
			else if(bought){
				//upper the sell limit
				if( point.getHigh()>u_max_limit[i-1] ){
					double sellPrice=0;
					if(point.getOpen()>u_max_limit[i-1] ){
						sellPrice=point.getOpen();
					}
					else{
						sellPrice=u_max_limit[i-1];
					}
					profit+=sellPrice-lastBuyPrice;
					bought=false;
					profit=profit-((float)penalty)*sellPrice;
					sellSignalSeries.add(pos,sellPrice);
				}
				//under the sell limit
				else if(isUpper && point.getLow()<u_min_limit[i-1]){
					
					double sellPrice=0;
					if(point.getOpen()<u_min_limit[i-1] ){
						sellPrice=point.getOpen();
					}
					else{
						sellPrice=u_min_limit[i-1];
					}
					
					
					profit+=sellPrice-lastBuyPrice;
					
					bought=false;
					profit=profit-((float)penalty)*sellPrice;
					sellSignalSeries.add(pos,sellPrice);
				}
				else{
					profit+=point.get(field)-lastBuyPrice;
					lastBuyPrice=point.get(field);
				}
			}
			
			//Set Lower and Upper
			isUpper=point.getLow()>u_min_limit[i-1];
			isLower=point.getHigh()<l_max_limit[i-1];
			
			profitSeries.add(pos, profit/noneZeroHisList.get(period[0]).get(field));
			
			
			
			
			pos++;
		}
		
		if(period[1]-1>=0){
		HistoricalPoint point=noneZeroHisList.get(period[1]-1);
		
		if(bought){
			dayBuyUpLimitIsActivated=false;
			dayBuyDownLimitIsActivated=false;
			daySellUpLimitIsActivated=point.getHigh()<u_max_limit[period[1]-1];
			daySellUpLimit=u_max_limit[period[1]-1];
			daySellDownLimitIsActivated=point.getLow()>u_min_limit[period[1]-1];
			daySellDownLimit=u_min_limit[period[1]-1];
			
		}
		else{
			daySellUpLimitIsActivated=false;
			daySellDownLimitIsActivated=false;
			
			dayBuyUpLimitIsActivated=point.getHigh()<l_max_limit[period[1]-1];
			dayBuyUpLimit=l_max_limit[period[1]-1];
			dayBuyDownLimitIsActivated=point.getLow()>l_min_limit[period[1]-1];
			dayBuyDownLimit=l_min_limit[period[1]-1];
		}
		}
		
		profit = profit / noneZeroHisList.get((int) period[0]).get(field);

		return maxProfit - profit;
		
	}
	
	
	private double calculateTomorrowDeltaToGetRSILimit(double todayRsi,double EMA_U,double EMA_D,double tomorrowRsi){
		
		double delta=0;
		
		if(todayRsi<tomorrowRsi){
			double EMA_U_Tomorrow=EMA_D*(100/(100-tomorrowRsi)-1);
			delta=EMA_U+1/alpha*(EMA_U_Tomorrow-EMA_U);
		}
		else{
			double EMA_D_Tomorrow=EMA_U*1/((100/(100-tomorrowRsi)-1));
			delta=-(EMA_D+1/alpha*(EMA_D_Tomorrow-EMA_D));
		}
		
		return delta;
	}
	
	
}
