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
import com.munch.exchange.parts.composite.NMAWComposite;
import com.munch.exchange.parts.composite.RateChartEMAComposite;

public class NMAWObjFunc extends OptimizationModule implements
IObjectiveFunction<double[]> {
	
	
	public static final String NMAW = "NMAW";
	public static final String NMAW_GD = "NMAW GD";
	public static final String NMAW_DITF = "NMAW DITF";
	public static final String NMAW_UpperBand = "NMAW Upper Band";
	public static final String NMAW_LowerBand = "NMAW Lower Band";

	public static final String NMAW_Buy_Signal = "NMAW Buy Signal";
	public static final String NMAW_Sell_Signal = "NMAW Sell Signal";

	public static final String NMAW_Profit = "NMAW Profit";

	private static final long serialVersionUID = 1;
	
	private static Logger logger = Logger.getLogger(NMAWObjFunc.class);

	// History point List
	private LinkedList<HistoricalPoint> noneZeroHisList = new LinkedList<HistoricalPoint>();

	// The history fled to optimize
	private String field;
	// the penalty for bay or sell
	private double penalty;

	// Period
	private int[] period = new int[2];

	//Parameters
	private int period1;
	private double facPeriod1;	//facPeriod1=period1/maxPeriod1
	
	private int period2;
	
	private double lamda;	//lamda=period1/period2
	private double facLamda;
	
	private int aroonPeriod;
	private double facAroonPeriod;
	
	
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
	private XYSeries nmawSeries;
	private XYSeries nmawGDSeries;

	private XYSeries profitSeries;
	private XYSeries ditfSeries;
	
	private XYSeries buySignalSeries;
	private XYSeries sellSignalSeries;

	public NMAWObjFunc(String field, double penalty,
			LinkedList<HistoricalPoint> pList,  double maxProfit) {
		this.field = field;
		this.penalty = penalty;
		this.noneZeroHisList = pList;

		this.maxProfit = maxProfit;
	}

	public void setPeriod(int[] period) {
		this.period = period;
	}


	public XYSeries getNmawSeries() {
		return nmawSeries;
	}
	


	public XYSeries getNmawGDSeries() {
		return nmawGDSeries;
	}

	public XYSeries getDitfSeries() {
		return ditfSeries;
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
	
	/*
	 * calculate the weigth moving average
	 */
	private double[] calculateGDW(double[] kurs,int period){
		double[] gd=new double[kurs.length];
		
		double sum=0;
		for(int j=0;j<period;j++){
			sum+=(period-j);
		}
		
		for(int i=0;i<kurs.length;i++){
			gd[i]=0;
			if(kurs[i]==0)continue;

			for(int j=0;j<period;j++){
				if(i-j<0)continue;
				gd[i]+=(period-j)*kurs[i-j];
			}
			
			gd[i]=gd[i]/sum;
		}
		
		return gd;
	}
	
	private double[] calculateNMAW(double[] kurs,int gPeriode, int kPeriode){
		double[] ma1=calculateGDW(kurs,gPeriode);
		double[] ma2=calculateGDW(ma1,kPeriode);
		
		double calcLamda=gPeriode/kPeriode;
		//logger.info("Calc Lamda: "+calcLamda);
		double alpha= calcLamda*((double)gPeriode-1)/((double)gPeriode-calcLamda);
		//logger.info("alpha: "+alpha);
		
		double nmaw[]=new double[kurs.length];
		
		for(int i=0;i<ma1.length;i++){
			nmaw[i]=(alpha+1)*ma1[i] - alpha*ma2[i];
		}
		
		return nmaw;
	}
	
	private double[] calculateAroonUp(double[] kurs, int period){
		double[] aroonUp=new  double[kurs.length];
		
		for(int i=0;i<kurs.length;i++){
			if(kurs[i]==0)continue;
			double up=Double.NEGATIVE_INFINITY;
			double posUp=0;
			for(int j=0;j<=period;j++){
				if(i-j<0)continue;
				if(kurs[i-j]>up){
					up=kurs[i-j];
					posUp=j;
				}
			}
			
			aroonUp[i]=100*(period-posUp)/period;
			
		}
		return aroonUp;
	}
	
	private double[] calculateAroonDown(double[] kurs, int period){
		double[] aroonDown=new  double[kurs.length];
		
		for(int i=0;i<kurs.length;i++){
			if(kurs[i]==0)continue;
			double down=Double.POSITIVE_INFINITY;
			double posDown=0;
			for(int j=0;j<=period;j++){
				if(i-j<0)continue;
				if(kurs[i-j]<down){
					down=kurs[i-j];
					posDown=j;
				}
			}
			
			aroonDown[i]=100*(period-posDown)/period;
			
		}
		return aroonDown;
	}
	
	private double[] calculateAroonOsz(double[] kurs, int period){
		double[] aroonOsz=new  double[kurs.length];
		double[] aroonUp=calculateAroonUp(kurs,period);
		double[] aroonDown=calculateAroonDown(kurs,period);
		
		for(int i=0;i<kurs.length;i++){
			aroonOsz[i]=aroonUp[i]-aroonDown[i];
		}
		return aroonOsz;
	}
	
	private double[] calculateDITF(double[] data){
		double[] ditf=new  double[data.length];
		
		for(int i=0;i<data.length;i++){
			//if(data[i]==0)continue;
			
			double val1= 0.05*data[i];
			double val2=2*val1;
			double itf=(Math.exp(val2)-1)/(Math.exp(val2)+1);
			ditf[i]=itf*100;
		}
		
		return ditf;
	}
	

	@Override
	public double compute(double[] x, Random r) {
		if (x.length < 3)
			return 0;
		
		facPeriod1=x[0];
		facLamda=x[1];
		facAroonPeriod=x[2];
		
		period1=(int) (facPeriod1*NMAWComposite.maxPeriod1+NMAWComposite.minPeriod1);
		lamda=facLamda*NMAWComposite.maxLamda+NMAWComposite.minLamda;
		period2=(int) (period1/lamda);
		aroonPeriod=(int) (facAroonPeriod*NMAWComposite.maxAroonPeriod+NMAWComposite.minAroonPeriod);
		
		if(period2<2)period2=2;
		
		//logger.info("Period 2: "+period2);
		
		int maxPeriod=Math.max(period1, Math.max(period2,aroonPeriod));

		// Init Profit
		profit = 0;
		bought = false;
		isUpper=false;
		isLower=false;
		
		upperLimit=0;
		lowerLimit=0;
		
		double lastBuyPrice=noneZeroHisList.getFirst().get(field);
		
		// Init the series
		nmawSeries = new XYSeries(NMAW);
		nmawGDSeries = new XYSeries(NMAW_GD);
		ditfSeries = new XYSeries(NMAW_DITF);

		profitSeries = new XYSeries(NMAW_Profit);
		buySignalSeries = new XYSeries(NMAW_Buy_Signal);
		sellSignalSeries = new XYSeries(NMAW_Sell_Signal);

		
		//Calculate the Kurs
		double[] Kurs= new double[noneZeroHisList.size()];
		double[] close= new double[noneZeroHisList.size()];
	
		int i=0;
		for(HistoricalPoint point:noneZeroHisList){
			
			if(i<period[0]-maxPeriod || i>period[1]){
				Kurs[i]=0;i++;
				continue;
			}
			
			Kurs[i]=(point.getHigh()+point.getLow()+point.getClose())/3;
			close[i]=point.getClose();
			i++;
		}
		
		
		//calculate the NMAW
		double[] nmaw=calculateNMAW(Kurs,period1,period2);
		double[] gd=calculateGDW(Kurs, period1);
		
		//Calculate Aroon Osz
		double[] aroonOsz=calculateAroonOsz(nmaw,aroonPeriod);
		
		//Calculate DITF
		double[] ditf=calculateDITF(aroonOsz);
		
		//Create the Band Series
		int pos=1;
		
		for( i=period[0];i<period[1];i++){
			nmawSeries.add(pos, nmaw[i]);
			nmawGDSeries.add(pos, gd[i]);
			ditfSeries.add(pos, ditf[i]);
			
			//Buy
			if(!bought){
				
				if(ditf[i]>0){
					lastBuyPrice=close[i];
					bought=true;
					profit=profit-((float)penalty)*lastBuyPrice;
					buySignalSeries.add(pos,lastBuyPrice);
				}
			}
			//Sell
			else{
				profit+=close[i]-lastBuyPrice;
				lastBuyPrice=close[i];
				
				if(ditf[i]<0 ){
					bought=false;
					profit=profit-((float)penalty)*close[i];
					sellSignalSeries.add(pos,close[i]);
				}
			}
			
			
			profitSeries.add(pos, profit/noneZeroHisList.get((int) period[0]).get(field));
			
			
			pos++;
		}
		
		
		
		profit = profit / noneZeroHisList.get((int) period[0]).get(field);

		return maxProfit - profit;
		
	}
	
	
}