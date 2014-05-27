package com.munch.exchange.job.objectivefunc;

import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;
import org.goataa.impl.OptimizationModule;
import org.goataa.spec.IObjectiveFunction;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import com.munch.exchange.model.core.historical.HistoricalPoint;


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
					
					lastBuyPrice=l_min_limit[i-1];
					
					bought=true;
					profit=profit-((float)penalty)*lastBuyPrice;
					buySignalSeries.add(pos,lastBuyPrice);
				}
				//The curve goes upper
				else if(point.getLow()<l_max_limit[i-1]  &&  point.getHigh()>l_max_limit[i-1]){
					
					lastBuyPrice=l_max_limit[i-1];
					
					bought=true;
					profit=profit-((float)penalty)*lastBuyPrice;
					buySignalSeries.add(pos,lastBuyPrice);
				}
			}
			//Sell Signal
			else if(bought){
				//upper the sell limit
				if( point.getHigh()>u_max_limit[i-1] ){
					profit+=u_max_limit[i-1]-lastBuyPrice;
					
					bought=false;
					profit=profit-((float)penalty)*u_max_limit[i-1];
					sellSignalSeries.add(pos,u_max_limit[i-1]);
					
				}
				//under the sell limit
				else if(point.getHigh()>u_min_limit[i-1] && point.getLow()<u_min_limit[i-1]){
					profit+=u_min_limit[i-1]-lastBuyPrice;
					
					bought=false;
					profit=profit-((float)penalty)*u_min_limit[i-1];
					sellSignalSeries.add(pos,u_min_limit[i-1]);
					
				}
				else{
					profit+=point.get(field)-lastBuyPrice;
					lastBuyPrice=point.get(field);
				}
			}
			
			
			profitSeries.add(pos, profit/noneZeroHisList.get(period[0]).get(field));
			
			
			pos++;
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
