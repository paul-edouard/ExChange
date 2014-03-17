package com.munch.exchange.model.core.historical;

import java.util.Calendar;
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
		 
		 if(lastHisPointFromQuote!=null && !this.isEmpty() &&
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
	
	
	
	
	
	public float calculateMaxProfit(int from, int downTo, String field){
		float maxProfitPercent=0;
		
		LinkedList<HistoricalPoint> pList=getPointsFromPeriod(from,downTo,getNoneEmptyPoints());
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
		LinkedList<HistoricalPoint> pList=getPointsFromPeriod(from,downTo,getNoneEmptyPoints());
		if(pList.isEmpty() || pList.size()==1)return 0;
		
		keepAndOld=(pList.getLast().get(field)-pList.getFirst().get(field))/pList.getFirst().get(field);
		
		return keepAndOld;
		
	}
	
	
	/**
	 * return the none empty points
	 * @return
	 */
	public  LinkedList<HistoricalPoint> getNoneEmptyPoints(){
		LinkedList<HistoricalPoint> pointList =new LinkedList<HistoricalPoint>();
		for(DatePoint point : this){
			 HistoricalPoint HistPoint=(HistoricalPoint)point;
			 if(HistPoint.getVolume()>0){
				 pointList.add(HistPoint);
			 }
		}
		if(lastHisPointFromQuote!=null && !this.isEmpty() &&
				lastHisPointFromQuote.getDate().get(Calendar.DAY_OF_YEAR)> this.getLast().getDate().get(Calendar.DAY_OF_YEAR) ){
			//logger.info("Last Quote Added");
			 pointList.add(lastHisPointFromQuote);
		}
		/*
		else{
			logger.info("Last Quote no Added");
			if(lastHisPointFromQuote==null)
				logger.info("Last Quote is null");
		}
		*/
		
		return pointList;
	}
	
	public static LinkedList<HistoricalPoint> getPointsFromPeriod(int from, int downTo,LinkedList<HistoricalPoint> basis){
		
		LinkedList<HistoricalPoint> pointList =new LinkedList<HistoricalPoint>();
		LinkedList<HistoricalPoint> totalList=basis;
		
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
