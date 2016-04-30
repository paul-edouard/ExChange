package com.munch.exchange.model.core.ib.bar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.bar.ExBar.DataType;

public class BarUtils {
	
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query
	
	
	public static String format( long ms) {
		return FORMAT.format( new Date( ms) );
	}
	
//	#########################################
//	##             CONVERTIONS             ##
//	#########################################
	
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
	
	public static long getIntervallInMs(BarSize size){
		return 1000L*getIntervallInSec(size);
	}
	
	public static List<String> getAllBarSizesAsString(){
		List<String> barSizes=new LinkedList<String>();
		
		barSizes.add("1 min");
		barSizes.add("2 mins");
		barSizes.add("3 mins");
		barSizes.add("5 mins");
		barSizes.add("10 mins");
		barSizes.add("15 mins");
		barSizes.add("20 mins");
		barSizes.add("30 mins");
		
		barSizes.add("1 hour");
		barSizes.add("4 hours");
		
		barSizes.add("1 day");
		
		return barSizes;
		
	}
	
	public static WhatToShow getWhatToShowFromString(String whatToShowStr){
		if(whatToShowStr.equals("TRADES")){return WhatToShow.TRADES;}
		else if(whatToShowStr.equals("MIDPOINT")){return WhatToShow.MIDPOINT;}
		else if(whatToShowStr.equals("BID")){return WhatToShow.BID;}
		else if(whatToShowStr.equals("ASK")){return WhatToShow.ASK;}
		else if(whatToShowStr.equals("BID_ASK")){return WhatToShow.BID_ASK;}
		else if(whatToShowStr.equals("HISTORICAL_VOLATILITY")){return WhatToShow.HISTORICAL_VOLATILITY;}
		else if(whatToShowStr.equals("OPTION_IMPLIED_VOLATILITY")){return WhatToShow.OPTION_IMPLIED_VOLATILITY;}
		else if(whatToShowStr.equals("YIELD_ASK")){return WhatToShow.YIELD_ASK;}
		else if(whatToShowStr.equals("YIELD_BID")){return WhatToShow.YIELD_BID;}
		else if(whatToShowStr.equals("YIELD_BID_ASK")){return WhatToShow.YIELD_BID_ASK;}
		else if(whatToShowStr.equals("YIELD_LAST")){return WhatToShow.YIELD_LAST;}
		else{return WhatToShow.TRADES;}
	}
	
	
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
	
	
	public static BarSize getBarSizeFromString(String sizeStr){
		if(sizeStr.equals("1 minute")){return BarSize._1_min;}
		else if(sizeStr.equals("2 minutes")){return BarSize._2_mins;}
		else if(sizeStr.equals("3 minutes")){return BarSize._3_mins;}
		else if(sizeStr.equals("5 minutes")){return BarSize._5_mins;}
		else if(sizeStr.equals("10 minutes")){return BarSize._10_mins;}
		else if(sizeStr.equals("15 minutes")){return BarSize._15_mins;}
		else if(sizeStr.equals("20 minutes")){return BarSize._20_mins;}
		else if(sizeStr.equals("30 minutes")){return BarSize._30_mins;}
		
		if(sizeStr.equals("1 hour")){return BarSize._1_hour;}
		else if(sizeStr.equals("4 hours")){return BarSize._4_hours;}
		
		if(sizeStr.equals("1 min")){return BarSize._1_min;}
		else if(sizeStr.equals("2 mins")){return BarSize._2_mins;}
		else if(sizeStr.equals("3 mins")){return BarSize._3_mins;}
		else if(sizeStr.equals("5 mins")){return BarSize._5_mins;}
		else if(sizeStr.equals("10 mins")){return BarSize._10_mins;}
		else if(sizeStr.equals("15 mins")){return BarSize._15_mins;}
		else if(sizeStr.equals("20 mins")){return BarSize._20_mins;}
		else if(sizeStr.equals("30 mins")){return BarSize._30_mins;}
		
		return BarSize._1_day;
	}
	
	
	
	public static long[] getTimeArray(List<ExBar> bars,int numberOfValues){
		int min=Math.min(bars.size(), numberOfValues);
		int last=bars.size()-min;
		long[] array=new long[min];
		ExBar[] barArray=bars.toArray(new ExBar[bars.size()]);
		for(int i=bars.size()-1;i>=last;i--){
			array[i-last]=barArray[i].getTimeInMs();
		}
		return array;
	}
	
	public static long[] getTimeArray(List<ExBar> bars){
		return getTimeArray(bars, bars.size());
	}
	
	public static double[] barsToDoubleArray(List<ExBar> bars,DataType dataType){
		return barsToDoubleArray(bars, dataType, bars.size());
	}
	
	public static double[] barsToDoubleArray(List<ExBar> bars,DataType dataType,int numberOfValues){
		
		int min=Math.min(bars.size(), numberOfValues);
		int last=bars.size()-min;
		double[] array=new double[min];
		ExBar[] barArray=bars.toArray(new ExBar[bars.size()]);
		
		for(int i=bars.size()-1;i>=last;i--){
			array[i-last]=barArray[i].getData(dataType);
		}
		return array;
	}
	
//	#########################################
//	##             RANGE BARs              ##
//	#########################################
	
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
	
	
//	#########################################
//	##             DAY BLOCKS              ##
//	#########################################	
	
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
	
	public static LinkedList<LinkedList<ExBar>> collectPercentageOfBlocks(LinkedList<LinkedList<ExBar>> allBlocks, int percentRequired){
		LinkedList<LinkedList<ExBar>> optBlocks=new LinkedList<LinkedList<ExBar>>();
		
		LinkedList<LinkedList<ExBar>> allBlocksCopy=new LinkedList<LinkedList<ExBar>>();
		allBlocksCopy.addAll(allBlocks);
		
		double numberOfBlocks=0;
		//int percentRequired=spinnerPercentOfData.getSelection();
		double numberOfRequired=  allBlocks.size()*(((double) percentRequired)/100.0);
		//logger.info("Percent of bars required: "+percentRequired);
		//logger.info("Number of required bars: "+numberOfRequired);
		
		Set<LinkedList<ExBar>> blocksToDelete=new HashSet<LinkedList<ExBar>>();
		
		while(numberOfBlocks<numberOfRequired ){
			
			if(allBlocks.isEmpty())break;
			
			Random rand = new Random();
			int index=rand.nextInt(allBlocks.size());
			LinkedList<ExBar> removedBlock=allBlocks.remove(index);
			blocksToDelete.add(removedBlock);
//			optBlocks.add(removedBlock);
			
			numberOfBlocks+=1;
		}
		
		for(LinkedList<ExBar> block:allBlocksCopy){
			if(blocksToDelete.contains(block)){
				optBlocks.add(block);
			}
		}
		
		
		//logger.info("Number of blocks: "+optBlocks.size());
		//logger.info("Number of bars: "+numberOfBars);
		
		return optBlocks;
	}

}
