package com.munch.exchange.model.core.ib.bar;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


import com.ib.controller.Types.BarSize;

public class BarUtils {
	
	
	public static TimeBarSize convert(BarSize size){
		if(size==BarSize._1_secs || size==BarSize._5_secs || 
				size==BarSize._10_secs || size==BarSize._15_secs || 
				size==BarSize._30_secs){
			 return TimeBarSize.SECOND;
		}
		
		return TimeBarSize.MINUTE;
	}
	
	public static BarSize convert(TimeBarSize timeBarSize){
		switch (timeBarSize) {
		case MINUTE:
			return BarSize._1_min;
		case SECOND:
			return BarSize._1_secs;
		default:
			return BarSize._1_min;
		}
	}
	
	public static LinkedList<ExBar> convertTimeBars(List<ExBar> bars, BarSize originSize, BarSize targetSize){
		LinkedList<ExBar> convertedBars=new LinkedList<ExBar>();
		if(bars==null || bars.isEmpty())return convertedBars;
		
		if(originSize==targetSize){
			convertedBars.addAll(bars);
			return convertedBars;
		}
		
		long originInterval=getIntervallInSec(originSize);
		long targetInterval=getIntervallInSec(targetSize);
		
		
		ExBar converted=null;
		for(ExBar bar:bars){
			if(bar.getTime()%targetInterval==0 && converted!=null){
				converted.integrateData(bar);
				convertedBars.add(converted);
				converted=null;
			}
			else if(bar.getTime()%targetInterval==originInterval ){
				converted=new ExBar(bar);
				converted.setTime(bar.getTime()-originInterval+targetInterval);
			}
			else if(converted!=null){
				converted.integrateData(bar);
			}
			else{
				converted=new ExBar(bar);
				if(bar.getTime()%targetInterval>0)
					converted.setTime(bar.getTime()-bar.getTime()%targetInterval+targetInterval);
			}
		}
		
		if(converted!=null){
			converted.setCompleted(false);
			convertedBars.add(converted);
		}
		//for(IbBar bar:convertedBars)
		//	System.out.println("Intervall: "+bar.getTime()%targetInterval);
		
		
		return convertedBars;
	}
	
	public static long getIntervallInSec(BarSize size){
		switch (size) {
		
		case _1_secs:
			return 1;
		case _5_secs:
			return 5;
		case _10_secs:
			return 10;
		case _15_secs:
			return 15;
		case _30_secs:
			return 30;
			
		case _1_min:
			return 60;
		case _2_mins:
			return 2*60;
		case _3_mins:
			return 3*60;
		case _5_mins:
			return 5*60;
		case _10_mins:
			return 10*60;
		case _15_mins:
			return 15*60;
		case _20_mins:
			return 20*60;
		case _30_mins:
			return 30*60;
		
		case _1_hour:
			return 3600L;
		case _4_hours:
			return 4L*3600L;
		
		case _1_day:
			return 3600L*24L;
		case _1_week:
			return 7L*3600L*24L;
		
		default:
			return Long.MAX_VALUE;
		}
	}
	
	public static LinkedList<ExBar> convertToRangeBars(LinkedList<LinkedList<ExBar>> barBlocks, double range){
		LinkedList<ExBar> convertedBars=new LinkedList<ExBar>();
		for(LinkedList<ExBar> barBlock : barBlocks){
			if(barBlock.isEmpty())continue;
			convertedBars.addAll(convertToRangeBars(barBlock, range));
		}
		return convertedBars;
	}
	
	public static LinkedList<ExBar> convertToRangeBars(List<ExBar> barBlock, double range){
		LinkedList<ExBar> convertedBars=new LinkedList<ExBar>();
		if(barBlock==null || barBlock.isEmpty())return convertedBars;
		
//		Time Open 000, Low 250, High 500, close 750
//		Time of range bar in ms!!
		
		ExBar firstBar=barBlock.get(0);
		ExBar rangeBar=createNewRangeBar(firstBar.getTimeInMs(), firstBar.getOpen());
//		rangeBar.setVolume(firstBar.getVolume()/4+rangeBar.getVolume());
		
		
		for(ExBar bar:barBlock){
			long timeInMs=bar.getTimeInMs();
			long volumeRatio=bar.getVolume()/4;
			
//			Integrate Open Price
			rangeBar=integrateTimeValue(timeInMs, bar.getOpen(),volumeRatio , range, rangeBar, convertedBars);
			
//			Integrate Low Price
			rangeBar=integrateTimeValue(timeInMs+250, bar.getLow(),volumeRatio , range, rangeBar, convertedBars);
			
//			Integrate High Price
			rangeBar=integrateTimeValue(timeInMs+500, bar.getHigh(),volumeRatio , range, rangeBar, convertedBars);
			
//			Integrate Close Price
			rangeBar=integrateTimeValue(timeInMs+750, bar.getClose(),volumeRatio , range, rangeBar, convertedBars);
		}
		
		convertedBars.add(rangeBar);
		
		return convertedBars;
		
	}
		
	private static ExBar integrateTimeValue(long timeInMs, double price, long volume, double range, ExBar rangeBar, LinkedList<ExBar> convertedBars){
		
		rangeBar.setVolume(volume+rangeBar.getVolume());
		
		if(price < (rangeBar.getHigh()-range)){
			rangeBar.setLow(rangeBar.getHigh()-range);
			rangeBar.setClose(rangeBar.getHigh()-range);
			rangeBar.setCompleted(true);
			
			convertedBars.add(rangeBar);
			return createNewRangeBar(timeInMs, price);
		}
		
		if(price > (rangeBar.getLow()+range)){
			rangeBar.setHigh(rangeBar.getLow()+range);
			rangeBar.setClose(rangeBar.getLow()+range);
			rangeBar.setCompleted(true);
			
			convertedBars.add(rangeBar);
			return createNewRangeBar(timeInMs, price);
		}
		
		rangeBar.setLow(Math.min(price, rangeBar.getLow()));
		rangeBar.setHigh(Math.max(price, rangeBar.getHigh()));
		
		return rangeBar;
	}
	
	private static ExBar createNewRangeBar(long timeInMs, double openPrice){
		ExBar rangeBar=new ExBar(BarType.RANGE);
		rangeBar.setTime(timeInMs);
		rangeBar.setOpen(openPrice);
		rangeBar.setHigh(openPrice);
		rangeBar.setLow(openPrice);
		rangeBar.setCompleted(false);
		return rangeBar;
	}
	
	
	public static LinkedList<LinkedList<ExBar>> splitBarListInDayBlocks(List<ExBar> bars){
		
		LinkedList<LinkedList<ExBar>> blocks=new LinkedList<LinkedList<ExBar>>();
		if(bars.size()==0)return blocks;
		
		ExBar firstBar=bars.get(0);
		Calendar currentDay=getCurrentDayOf(firstBar.getTimeInMs());
		Calendar nextDay=addOneDayTo(currentDay);
		
		
		LinkedList<ExBar> dayBlock=new LinkedList<ExBar>();
		
		for(ExBar bar:bars){
			if(bar.getTimeInMs() >= nextDay.getTimeInMillis()){
				blocks.add(dayBlock);
				dayBlock=new LinkedList<ExBar>();
				dayBlock.add(bar);
				
				while(bar.getTimeInMs() >= nextDay.getTimeInMillis())
					nextDay=addOneDayTo(nextDay);
				continue;
			}
			
			dayBlock.add(bar);
		}
		
		if(!dayBlock.isEmpty()){
			blocks.add(dayBlock);
		}
		
		return blocks;
		
	}
	
	public static Calendar getCurrentDayOf(long dateInMs){
		Calendar date=Calendar.getInstance();
		date.setTimeInMillis(dateInMs);
		
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MINUTE, 0);
		if(date.get(Calendar.HOUR_OF_DAY)<23)
			date.add(Calendar.DAY_OF_YEAR, -1);
		date.set(Calendar.HOUR_OF_DAY, 23);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//		System.out.println(sdf.format(date.getTime()));
		
		return date;
	}
	
	public static Calendar addOneDayTo(Calendar date){
		
		Calendar nextDay=Calendar.getInstance();
		nextDay.setTimeInMillis(date.getTimeInMillis());
		nextDay.add(Calendar.DAY_OF_YEAR, 1);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//		System.out.println(sdf.format(nextDay.getTime()));

		return nextDay;
	}
	
	

}
