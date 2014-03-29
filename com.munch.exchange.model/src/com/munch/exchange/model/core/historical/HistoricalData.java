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
	
	private LinkedList<HistoricalPoint> noneEmptyPoints=null;
	
	
	public HistoricalPoint getLastHisPointFromQuote() {
		return lastHisPointFromQuote;
	}

	public void setLastHisPointFromQuote(HistoricalPoint lastHisPointFromQuote) {
		noneEmptyPoints=null;
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
	
	public XYSeries getXYSeries(String field, int[] period){
		
		 XYSeries series = new XYSeries(field);
		 LinkedList<HistoricalPoint> pointList= getPointsFromPeriod(period,getNoneEmptyPoints());
		 
		 int pos=1;
		 for(HistoricalPoint point:pointList){
			 series.add(pos,point.get(field));
			 pos++;
		 }
		 
		 return series;
	}
	
	public XYSeries getXYSeries(String field){
		
		 XYSeries series = new XYSeries(field);
		 LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		 
		 int pos=1;
		 for(HistoricalPoint point:pointList){
			 series.add(pos,point.get(field));
			 pos++;
		 }
		 
		 return series;
	}
	
	
	public XYSeries getMovingAvg(String field,int averageLength, String serieName){
		
		XYSeries series = new XYSeries(serieName);
		LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		 
		 
		int pos=1;
		float sum=0;
		LinkedList<HistoricalPoint> last=new LinkedList<HistoricalPoint>();
		
		for(HistoricalPoint point:pointList){
			last.add(point);
			if(last.size()==averageLength){
				sum=0;
				for(HistoricalPoint p:last){
					sum+=p.get(field);
				}
				series.add(pos,sum/averageLength);
				
				last.removeFirst();
			}
			else{
				series.add(pos,point.get(field));
			}
			
			
			pos++;
		}
		
		 return series;
		 
		
	}
	
	public XYSeries getEMA(String field,float alpha, String serieName){
		
		XYSeries series = new XYSeries(serieName);
		LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		
		float EMA=0;
		int pos=0;
		
		for(HistoricalPoint point:pointList){
			
			if(pos==0){
				EMA=point.get(field);
			}
			else{
				EMA=EMA+alpha*(point.get(field)-EMA);
			}
			series.add(pos+1,EMA);
			pos++;
		}
		 
		return series;
		
	}
	
	
	
	
	
	public float calculateMaxProfit(int[] period, String field){
		float maxProfitPercent=0;
		
		LinkedList<HistoricalPoint> pList=getPointsFromPeriod(period,getNoneEmptyPoints());
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
	
	public float calculateKeepAndOld(int[] period, String field){
		
		float keepAndOld=0;
		LinkedList<HistoricalPoint> pList=getPointsFromPeriod(period,getNoneEmptyPoints());
		if(pList.isEmpty() || pList.size()==1)return 0;
		
		keepAndOld=(pList.getLast().get(field)-pList.getFirst().get(field))/pList.getFirst().get(field);
		
		return keepAndOld;
		
	}
	
	
	/**
	 * return the none empty points
	 * @return
	 */
	public  LinkedList<HistoricalPoint> getNoneEmptyPoints(){
		if(noneEmptyPoints!=null && noneEmptyPoints.size()>1)return noneEmptyPoints;
		
		
		noneEmptyPoints =new LinkedList<HistoricalPoint>();
		for(DatePoint point : this){
			 HistoricalPoint HistPoint=(HistoricalPoint)point;
			 if(HistPoint.getVolume()>0){
				 noneEmptyPoints.add(HistPoint);
			 }
		}
		if(lastHisPointFromQuote!=null && !this.isEmpty() &&
				lastHisPointFromQuote.getDate().get(Calendar.DAY_OF_YEAR)> this.getLast().getDate().get(Calendar.DAY_OF_YEAR) ){
			//logger.info("Last Quote Added");
			noneEmptyPoints.add(lastHisPointFromQuote);
		}
		
		
		return noneEmptyPoints;
	}
	
	
	
	public static LinkedList<HistoricalPoint> getPointsFromPeriod(int[] period,LinkedList<HistoricalPoint> basis){
		
		LinkedList<HistoricalPoint> pointList =new LinkedList<HistoricalPoint>();
		LinkedList<HistoricalPoint> totalList=basis;
		
		//logger.info("Period: ["+period[0]+", "+period[1]+"]");
		
		int start=period[0];
		int end=period[1]-1;
		
		//logger.info("Start: ["+period[0]+", "+period[1]+"]");
		
		if(start<0)start=0;
		if(end<0)return pointList;
		if(start>end)return pointList;
		
		int pos=0;
		for(HistoricalPoint point:totalList){
			if(pos>=start && pos<=end){
				pointList.add(point);
			}
			pos++;
		}
		
		/*
		for(int i=start;i<=end;i++){
			if(i==totalList.size())break;
			pointList.add(totalList.get(i));
		}
		*/
		
		return pointList;
	}
	
	
	

}
