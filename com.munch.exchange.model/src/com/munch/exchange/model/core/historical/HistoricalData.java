package com.munch.exchange.model.core.historical;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;

public class HistoricalData extends DatePointList<HistoricalPoint>  {
	
	private static Logger logger = Logger.getLogger(HistoricalData.class);
	
	public static final String FIELD_Last_HisPoint_From_Quote="Last_HisPoint_From_Quote";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5430509341617898712L;
	
	private HistoricalPoint lastHisPointFromQuote=null;
	
	
	public HistoricalPoint getLastHisPointFromQuote() {
		return lastHisPointFromQuote;
	}

	public void setLastHisPointFromQuote(HistoricalPoint lastHisPointFromQuote) {
	changes.firePropertyChange(FIELD_Last_HisPoint_From_Quote, this.lastHisPointFromQuote, this.lastHisPointFromQuote = lastHisPointFromQuote);
	}
	



	@Override
	protected DatePoint createPoint() {
		return new HistoricalPoint();
	}
	
	
	
	public TimeSeries getTimeSeries(String field, int numberOfDays){
		 TimeSeries series = new TimeSeries(field);
		 
		 int maxDays=this.size();
		 for(int i=numberOfDays;i>0;i--){
			 if(maxDays-i>=0){
				 HistoricalPoint point=(HistoricalPoint)this.get(maxDays-i);
				 series.add(new Day(point.getDate().getTime()),point.get(field));
			 }
		 }
		 
		 if(lastHisPointFromQuote!=null && 
					lastHisPointFromQuote.getDate().get(Calendar.DAY_OF_YEAR)> this.getLast().getDate().get(Calendar.DAY_OF_YEAR) ){
			 series.add(new Day(lastHisPointFromQuote.getDate().getTime()),lastHisPointFromQuote.get(field));
		}
		 
		 
		 return series;
	}
	
	public XYSeries getXYSeries(String field, int numberOfDays){
		
		 XYSeries series = new XYSeries(field);
		 LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		 
		 int maxDays=pointList.size();
		 if(numberOfDays>maxDays)numberOfDays=maxDays;
		 
		 for(int pos=0;pos<numberOfDays;pos++){
			 HistoricalPoint point=pointList.get(maxDays-numberOfDays+pos);
			 series.add(pos+1,point.get(field));
		 }
		 
		 return series;
	}
	
	
	public XYSeries getMovingAvg(String field, int numberOfDays,int averageLength){
		
		 XYSeries series = new XYSeries("Moving Avg: "+field+ ", "+averageLength +"Days");
		 LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		 
		 int maxDays=pointList.size();
		 if(numberOfDays>maxDays)numberOfDays=maxDays;
		
		 for(int pos=0;pos<numberOfDays;pos++){
			 int current=maxDays-numberOfDays+pos;
			 if(current-averageLength+1<0)continue;
			 
			 float sum=0;
			 for(int i=current-averageLength+1;i<=current;i++){
				 sum+=pointList.get(i).get(field);
			 }
			 series.add(pos+1,sum/averageLength); 
		 }
		 
		 return series;
		 
		
	}
	
	
	public float calculateMovAvgProfit(int from, int downTo, String field, int MovAvgDays,float buyLimit, float sellLimit, double penalty){
		
		int pastDays=2+MovAvgDays;
		
		LinkedList<HistoricalPoint> pList=getNoneEmptyPointsFromPeriod(from,downTo+pastDays);
		if(pList.isEmpty() || pList.size()<=pastDays){return 0;}
		
		logger.info("pList.size: "+pList.size());
		
		float profit=0;
		HistoricalPoint point=null;
		HistoricalPoint last=null;
		int lastZeroDiff=-1;
		
		boolean bought=false;
		boolean firstBuy=false;
		
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
			
			logger.info("**  Pos:" + String.valueOf((i-pastDays+1))+ ", by: "+bought);
			
			point=pList.get(i);
			last=pList.get(i-1);
			//baforelast=pList.get(i-2);
			
			//Calculate Moving average
				
			//Get the last 3 values	
			float curVal=avgMap.get(i);
			float lastVal=avgMap.get(i-1);
			float beforeLastVal=avgMap.get(i-2);
			
			logger.info("Average:" + curVal+ ", last: "+lastVal+", beforeLastVal: "+beforeLastVal);
			
			//Calculate the diff
			float diff=curVal-lastVal;
			float lastDiff=lastVal-beforeLastVal;
				
			//Save the last zero diff position
			if((diff>=0 && lastDiff<0) || (diff<=0 && lastDiff>0) || lastZeroDiff<0){
				lastZeroDiff=i-1;
			}
			
			float diffFromLastZero=(curVal-avgMap.get(lastZeroDiff))/avgMap.get(lastZeroDiff);
			logger.info("diffFromLastZero: " + diffFromLastZero);
			
			//buy is on the current profit have to be added
			if(bought)
				profit+=point.get(field)-last.get(field);
			
			logger.info("profit:" + profit);
			
			logger.info("diff:" + diff+", buyLimit: "+buyLimit+", sellLimit: "+sellLimit);
			
			//Test if the rate have to be bought
			if(diff> 0 && diffFromLastZero>buyLimit && bought==false){
				bought=true;
				logger.info("-------->>>>>> BUY" );
				profit=profit-((float)penalty)*pList.get(i).get(field);
			}
			//Test if the rate have to be sold
			else if(diff<0 && diffFromLastZero<(-sellLimit) && bought==true){
				bought=false;
				logger.info("-------->>>>>> SELL" );
				profit=profit-((float)penalty)*pList.get(i).get(field);
			}
		}
		
		profit=profit/pList.get(pastDays).get(field);
		
		return profit;
	}
	
	
	public float calculateMaxProfit(int from, int downTo, String field){
		float maxProfitPercent=0;
		
		LinkedList<HistoricalPoint> pList=getNoneEmptyPointsFromPeriod(from,downTo);
		if(pList.isEmpty() || pList.size()==1){
			logger.info("Plist is empty");
			return 0;
		}
		
		HistoricalPoint lastPoint=null;
		for(HistoricalPoint point:pList){
			if(lastPoint!=null){
				float diff=point.get(field)-lastPoint.get(field);
				if(diff>0)
					maxProfitPercent+=diff;
			}
			lastPoint=point;
			
		}
		
		return maxProfitPercent/pList.getFirst().get(field);
	}
	
	public float calculateKeepAndOld(int from, int downTo, String field){
		
		float keepAndOld=0;
		LinkedList<HistoricalPoint> pList=getNoneEmptyPointsFromPeriod(from,downTo);
		if(pList.isEmpty() || pList.size()==1)return 0;
		
		keepAndOld=(pList.getLast().get(field)-pList.getFirst().get(field))/pList.getFirst().get(field);
		
		return keepAndOld;
		
	}
	
	
	/**
	 * return the none empty points
	 * @return
	 */
	private  LinkedList<HistoricalPoint> getNoneEmptyPoints(){
		LinkedList<HistoricalPoint> pointList =new LinkedList<HistoricalPoint>();
		for(DatePoint point : this){
			 HistoricalPoint HistPoint=(HistoricalPoint)point;
			 if(HistPoint.getVolume()>0){
				 pointList.add(HistPoint);
			 }
		}
		if(lastHisPointFromQuote!=null && 
				lastHisPointFromQuote.getDate().get(Calendar.DAY_OF_YEAR)> this.getLast().getDate().get(Calendar.DAY_OF_YEAR) ){
			 pointList.add(lastHisPointFromQuote);
		}
		
		return pointList;
	}
	
	private LinkedList<HistoricalPoint> getNoneEmptyPointsFromPeriod(int from, int downTo){
		
		LinkedList<HistoricalPoint> pointList =new LinkedList<HistoricalPoint>();
		LinkedList<HistoricalPoint> totalList=getNoneEmptyPoints();
		
		int start=totalList.size()-from-downTo;
		int end=totalList.size()-from;
		
		if(start<0)start=0;
		if(end<0)return pointList;
		if(start>end)return pointList;
		
		for(int i=start;i<=end;i++){
			if(i==totalList.size())break;
			pointList.add(totalList.get(i));
		}
		
		return pointList;
	}
	
	
	

}
