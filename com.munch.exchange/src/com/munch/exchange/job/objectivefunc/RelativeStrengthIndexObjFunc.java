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

	public static final String RSI__Buy_Signal = "RSI_ Buy Signal";
	public static final String RSI_Sell_Signal = "RSI_ Sell Signal";

	public static final String RSI_Profit = "RSI_ Profit";

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
		
		upperLimit=0;
		lowerLimit=0;
		
		
		// Init the series
		rsiSeries = new XYSeries(RSI);

		profitSeries = new XYSeries(RSI_Profit);
		buySignalSeries = new XYSeries(RSI__Buy_Signal);
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
		
		RSI[0]=0;
		double RS=0;
		double EMA_U=0;
		double EMA_D=0;
		
		for(i=1;i<noneZeroHisList.size();i++){
			EMA_U=EMA_U+alpha*(U[i]-EMA_U);
			EMA_D=EMA_D+alpha*(D[i]-EMA_D);
			RS=EMA_U/EMA_D;
			RSI[i]=100-100/(1+RS);
		}
		
		
		int pos=1;
		double u_max,u_min,l_max,l_min;
		for( i=period[0];i<period[1];i++){
			rsiSeries.add(pos, RSI[i]);
			
			u_max=upperMax*100;
			u_min=upperMax*100*upperMinFac;
			
			l_min=(1-lowerMin)*100;
			l_max=(1-lowerMin*lowerMaxFac)*100;
			
			upperBandSeries.add(pos, (u_max+u_min)/2, u_min, u_max);
			lowerBandSeries.add(pos, (l_max+l_min)/2, l_min, l_max);
			
			
			
			pos++;
		}
		
		
		
		
		
		
		
		profit = profit / noneZeroHisList.get((int) period[0]).get(field);

		return maxProfit - profit;
		
	}
	
}
