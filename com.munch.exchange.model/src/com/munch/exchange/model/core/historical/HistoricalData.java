package com.munch.exchange.model.core.historical;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jfree.data.time.Day;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLC;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.YIntervalSeries;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;
import com.munch.exchange.model.core.historical.HistoricalPoint.Type;
import com.munch.exchange.model.core.neuralnetwork.ValuePoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;

public class HistoricalData extends DatePointList<HistoricalPoint>  {
	
	private static Logger logger = Logger.getLogger(HistoricalData.class);
	
	public static final String FIELD_Last_HisPoint_From_Quote="Last_HisPoint_From_Quote";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5430509341617898712L;
	
	private HistoricalPoint lastHisPointFromQuote=null;
	
	private LinkedList<HistoricalPoint> noneEmptyPoints=null;
	
	private Set<String> usedInSet =new HashSet<String>();
	
	
	public HistoricalPoint getLastHisPointFromQuote() {
		return lastHisPointFromQuote;
	}

	public void setLastHisPointFromQuote(HistoricalPoint lastHisPointFromQuote) {
		noneEmptyPoints=null;
		changes.firePropertyChange(FIELD_Last_HisPoint_From_Quote, this.lastHisPointFromQuote, this.lastHisPointFromQuote = lastHisPointFromQuote);
	}
	

	public synchronized boolean isUsed() {
		return !usedInSet.isEmpty();
	}

	public void addUsedClass(Class<?> clazz) {
		usedInSet.add(clazz.getName());
	}
	
	public void removeUsedClass(Class<?> clazz){
		usedInSet.remove(clazz.getName());
	}
	

	@Override
	protected DatePoint createPoint() {
		return new HistoricalPoint();
	}
	
	
	public TimeSeries getTimeSeries (String field, int numberOfDays){
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
	
	public OHLCSeries getPosOHLCSeries( String key,int[] period){
		
		OHLCSeries series = new OHLCSeries(key);
		LinkedList<HistoricalPoint> pointList= getPointsFromPeriod(period,getNoneEmptyPoints());
		
		long pos=1;
		Calendar date=Calendar.getInstance();
		date.setTimeInMillis(pos);
		Millisecond current=new Millisecond(date.getTime());
		
		for(HistoricalPoint point:pointList){
			// if(point.get(Type.CLOSE)>point.get(Type.OPEN)){
				 series.add(current,point.get(Type.OPEN), point.get(Type.HIGH), point.get(Type.LOW), point.get(Type.CLOSE));
			 //}
			 //current=(Day)current.next();
			 pos++;
			 date.setTimeInMillis(pos);
			 current =new Millisecond(date.getTime());
		}
		 
		 return series;
	}
	
	public OHLCSeries getNegOHLCSeries( String key, int[] period){
		
		OHLCSeries series = new OHLCSeries(key);
		LinkedList<HistoricalPoint> pointList= getPointsFromPeriod(period,getNoneEmptyPoints());
		//Date d=pointList.getFirst().getDate().getTime();
		
		//pointList.getFirst().getDate().add(Calendar.DAY_OF_YEAR, amount);
		long pos=1;
		Calendar date=Calendar.getInstance();
		date.setTimeInMillis(pos);
		Millisecond current=new Millisecond(date.getTime());
		
		for(HistoricalPoint point:pointList){
			 if(point.get(Type.CLOSE)<=point.get(Type.OPEN)){
				 series.add(current,point.get(Type.OPEN), point.get(Type.HIGH), point.get(Type.LOW), point.get(Type.CLOSE));
			 }
			 //current=(Day)current.next();
			 pos++;
			 date.setTimeInMillis(pos);
			 current =new Millisecond(date.getTime());
		}
		 
		 return series;
	}
	
	
	
	public YIntervalSeries getYIntervalSeries( String serie_name, int[] period){
		
		YIntervalSeries inter_series = new YIntervalSeries(serie_name);
		LinkedList<HistoricalPoint> pointList= getPointsFromPeriod(period,getNoneEmptyPoints());
		
		int pos=1;
		 for(HistoricalPoint point:pointList){
			 inter_series.add(pos,	point.get(HistoricalPoint.FIELD_Close),
					 				point.get(HistoricalPoint.FIELD_Low),
					 				point.get(HistoricalPoint.FIELD_High));
			 pos++;
		 }
		
		
		return inter_series;
	}
	
	public double[] getPrices(Type type){
		LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		double[] prices=new double[pointList.size()];
		int pos=0;
		 for(HistoricalPoint point:pointList){
			 prices[pos]=point.getDouble(type);
			 pos++;
		 }
		 
		 return prices;
		
	}
	
	public ValuePointList getPricesAsValuePointList(Type type){
		LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		ValuePointList list=new ValuePointList();
		for(HistoricalPoint h_point:pointList){
			ValuePoint point=new ValuePoint(h_point.getDate(), h_point.getDouble(type));
			list.add(point);
		}
		
		return list;
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
	/**
	 * Info: http://en.wikipedia.org/wiki/Relative_strength_index
	 * 
	 * @param alpha
	 * @param serieName
	 * @return
	 */
	public XYSeries getRSI(float alpha, String serieName){
		
		XYSeries series = new XYSeries(serieName);
		LinkedList<HistoricalPoint> pointList= getNoneEmptyPoints();
		
		float[] U= new float[pointList.size()];
		float[] D= new float[pointList.size()];
		float[] RS= new float[pointList.size()];
		float[] RSI= new float[pointList.size()];
		
		HistoricalPoint previous=null;
		int i=0;
		for(HistoricalPoint point:pointList){
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
		
		RS[0]=0;
		RSI[0]=0;
		float EMA_U=0;
		float EMA_D=0;
		
		for(i=1;i<pointList.size();i++){
			EMA_U=EMA_U+alpha*(U[i]-EMA_U);
			EMA_D=EMA_D+alpha*(D[i]-EMA_D);
			RS[i]=EMA_U/EMA_D;
			RSI[i]=100-100/(1+RS[i]);
		}
		
		for(i=0;i<pointList.size();i++){
			series.add(i+1,RSI[i]);
		}
		
		return series;
		
	}
	
	
	
	
	
	public float calculateMaxProfit(int[] period, String field){
		LinkedList<HistoricalPoint> pList=getPointsFromPeriod(period,getNoneEmptyPoints());
		return calculateMaxProfit(pList,field);
	}
	
	public float calculateMaxProfit(String field){
		return calculateMaxProfit(getNoneEmptyPoints(),field);
	}
	
	private float calculateMaxProfit(LinkedList<HistoricalPoint> pList , String field){
		float maxProfitPercent=0;
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
	
	
	
	public float calculateMaxProfit(Calendar startdate, String field){
		int[] period=calculatePeriod(startdate);
		return calculateMaxProfit(period,field);
	}
	
	
	public float calculateKeepAndOld(int[] period, String field){
		
		float keepAndOld=0;
		LinkedList<HistoricalPoint> pList=getPointsFromPeriod(period,getNoneEmptyPoints());
		if(pList.isEmpty() || pList.size()==1)return 0;
		
		keepAndOld=(pList.getLast().get(field)-pList.getFirst().get(field))/pList.getFirst().get(field);
		
		return keepAndOld;
		
	}
	
	public float calculateKeepAndOld(Calendar startdate, String field){
		int[] period=calculatePeriod(startdate);
		return calculateKeepAndOld(period,field);
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
	
	
	public int[] calculatePeriod(Calendar startdate){
		int[] period=new int[2];
		
		period[1]=this.getNoneEmptyPoints().size();
		
		int pos=0;
		for(HistoricalPoint point:this.getNoneEmptyPoints()){
			if(point.getDate().after(startdate))break;
			pos++;
		}
		period[0]=pos;
		
		return period;
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
