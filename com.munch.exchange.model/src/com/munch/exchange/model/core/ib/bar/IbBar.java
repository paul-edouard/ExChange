package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.ib.controller.Bar;
import com.ib.controller.Formats;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;

@Entity
@Inheritance
@DiscriminatorColumn(name="BAR_TYPE")
public abstract  class IbBar implements Serializable,Comparable<IbBar>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyyMMdd HH:mm:ss"); // format for historical query
	
	public static enum DataType {
		HIGH, LOW, OPEN, CLOSE, WAP, VOLUME, TIME;
	}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Enumerated(EnumType.STRING)
	private WhatToShow type;
	
	/*
	@EmbeddedId
	private ExBarPK pk;
	*/
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	private IbBar parent;
	
	@Enumerated(EnumType.STRING)
	private BarSize size;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROOT_ID")
	private IbBarContainer root;
	
	@OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
	private List<IbBar> childBars;
	
	@Transient
	private boolean isRealTime=false;
	
	@Transient
	private boolean isCompleted=true;
	
	private  long time;
	private  double high;
	private  double low;
	private  double open;
	private  double close;
	private  double wap;
	private  long volume=0;
	private  int count=0;
	
	public IbBar(){
		
	}
	
	public IbBar( long time, double high, double low, double open, double close, double wap, long volume, int count) {
		this.setTime( time  );
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.wap = wap;
		this.volume = volume;
		this.count = count;
	}
	
	public IbBar(Bar bar){
		this.init(bar);
	}
	
	public void init(Bar bar){
		this.setTime( bar.time());
		this.high = bar.high();
		this.low = bar.low();
		this.open = bar.open();
		this.close = bar.close();
		this.wap = bar.wap();
		this.volume = bar.volume();
		this.count = bar.count();
	}
	
	public void copyData(IbBar bar){
		this.setTime( bar.time);
		this.high = bar.high;
		this.low = bar.low;
		this.open = bar.open;
		this.close = bar.close;
		this.wap = bar.wap;
		this.volume = bar.volume;
		this.count = bar.count;
		
		//this.size=bar.size;
		this.type=bar.type;
		this.id=bar.id;
		
		this.isRealTime=bar.isRealTime;
		this.isCompleted=bar.isCompleted;
	}
	
	
	public void integrateData(IbBar bar){
		//this.setTime( bar.time);
		this.high = Math.max(bar.high, this.high);
		this.low = Math.min(bar.low, this.low);
		//this.open = bar.open;
		this.close = bar.close;
		this.wap = bar.wap;
		this.volume += bar.volume;
		this.count += bar.count;
		
	}
	
	
	public void setRootAndParent(IbBarContainer root,IbBar parent){
		setParent(parent);
		setRoot(root);
		setType(root.getType());
		
		//parent.getChildBars().add(this);
		//root.getAllBars().add(this);
		
	}
	
	//public abstract long getIntervall();
	
	public double getData(DataType dataType){
		switch (dataType) {
			case HIGH:return high;
			case LOW:return low;
			case OPEN:return open;
			case CLOSE:return close;
			case WAP:return wap;
			case VOLUME:return (double)volume;
			case TIME:return (double)time;
			default:return 0;
		}
		
	}

	public long getIntervallInSec(){
		return getIntervallInSec(size);
	}
	
	public boolean isRealTime() {
		return isRealTime;
	}

	public void setRealTime(boolean isRealTime) {
		this.isRealTime = isRealTime;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public long getIntervallInMs(){
		return 1000L*getIntervallInSec();
	}
	
	/*
	public ExBarPK getPk() {
		return pk;
	}

	public void setPk(ExBarPK pk) {
		this.pk = pk;
	}
	*/

	public List<IbBar> getChildBars() {
		if(childBars==null || childBars.isEmpty()){
			childBars=new LinkedList<IbBar>();
		}
		return childBars;
	}

	public void setChildBars(List<IbBar> childBars) {
		this.childBars = childBars;
	}

	public IbBarContainer getRoot() {
		return root;
	}

	public void setRoot(IbBarContainer root) {
		this.root = root;
		setType(root.getType());
		
		//root.getAllBars().add(this);
	}

	public BarSize getSize() {
		return size;
	}

	public void setSize(BarSize size) {
		this.size = size;
	}

	public WhatToShow getType() {
		return type;
		//return pk.getType();
	}

	public void setType(WhatToShow type) {
		this.type = type;
		//this.pk.setType(type);
	}
	
	
	public long getId() {return id;}

	public void setId(long id) {this.id = id;}
	
	public IbBar getParent() {return parent;}

	public void setParent(IbBar parent) {this.parent = parent;}

	public long getTime() {
		return this.time;
		//return this.pk.getTime();
	}
	
	public long getTimeInMs() {
		return this.time*1000L;
		//return this.pk.getTime();
	}

	public void setTime(long time) {
		this.time = time;
		//this.pk.setTime(time);
	}
	
	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}
	
	public void setLow(double low) {
		this.low = low;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getWap() {
		return wap;
	}

	public void setWap(double wap) {
		this.wap = wap;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String formattedTime() {
		return Formats.fmtDate( this.time * 1000);
		//return Formats.fmtDate( this.pk.getTime() * 1000);
	}
	
	/** Format for query. */
	public static String format( long ms) {
		return FORMAT.format( new Date( ms) );
	}

	@Override
	public String toString() {
		return "ExBar [id=" + id + ", type=" + type + ", parent=" + parent
				+ ", size=" + size + ", root=" + root + ", time=" + time +", formated time="+this.formattedTime()+ ", high=" + high + ", low="
				+ low + ", open=" + open + ", close=" + close + ", wap=" + wap
				+ ", volume=" + volume + ", count=" + count + ", real time=" + isRealTime + "]";
	}
	
	@Override
	public int compareTo(IbBar o) {
		long diff=this.time-o.time;
		if(diff>0)return 1;
		else if(diff==0)return 0;
		return -1;
	}
	
//	+++++++++++++++++++++++++++++++++
//	++       STATIC FUNCTIONS      ++
//	+++++++++++++++++++++++++++++++++
	

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
	
	public static LinkedList<IbBar> convertIbBars(List<IbBar> bars, BarSize targetSize){
		LinkedList<IbBar> convertedBars=new LinkedList<IbBar>();
		if(bars==null || bars.isEmpty())return convertedBars;
		
		if(bars.get(0).getSize()==targetSize){
			convertedBars.addAll(bars);
			return convertedBars;
		}
		
		long targetInterval=getIntervallInSec(targetSize);
		long startInterval=bars.get(0).getIntervallInSec();
		
		IbBar converted=null;
		for(IbBar bar:bars){
			if(bar.getTime()%targetInterval==0 && converted!=null){
				converted.integrateData(bar);
				converted.setCompleted(true);
				convertedBars.add(converted);
				converted=null;
			}
			else if(bar.getTime()%targetInterval==startInterval ){
				converted=createNewInstance(targetSize);
				converted.copyData(bar);
				converted.setTime(bar.getTime()-startInterval+targetInterval);
				converted.setCompleted(true);
			}
			else if(converted!=null){
				converted.integrateData(bar);
				converted.setCompleted(true);
			}
			else{
				converted=createNewInstance(targetSize);
				converted.copyData(bar);
				converted.setCompleted(true);
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
	
	public static Class<? extends IbBar> searchCorrespondingBarClass(BarSize size){
		if(		size==BarSize._1_secs ||
				size==BarSize._5_secs ||
				size==BarSize._10_secs ||
				size==BarSize._15_secs ||
				size==BarSize._30_secs ){
			return IbSecondeBar.class;
		}
		else if(size==BarSize._1_min ||
				size==BarSize._2_mins ||
				size==BarSize._3_mins ||
				size==BarSize._5_mins ||
				size==BarSize._10_mins ||
				size==BarSize._15_mins ||
				size==BarSize._20_mins ||
				size==BarSize._30_mins ){
			return IbMinuteBar.class;
		}
		else if(size==BarSize._1_hour ||
				size==BarSize._4_hours ){
			return IbHourBar.class;
		}
		else{
			return IbDayBar.class;
		}
	}
	
	public static List<String> getAllBarSizesAsString(){
		List<String> barSizes=new LinkedList<String>();
		
		barSizes.add("1 minute");
		barSizes.add("2 minutes");
		barSizes.add("3 minutes");
		barSizes.add("5 minutes");
		barSizes.add("10 minutes");
		barSizes.add("15 minutes");
		barSizes.add("20 minutes");
		barSizes.add("30 minutes");
		
		barSizes.add("1 hour");
		barSizes.add("4 hours");
		
		barSizes.add("1 day");
		
		return barSizes;
		
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
		
		return BarSize._1_day;
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
	
 	public static IbBar createNewInstance(BarSize size){
		Class<? extends IbBar> ibBarClass=IbBar.searchCorrespondingBarClass(size);
		
		try {
			IbBar instance=ibBarClass.newInstance();  
			instance.setSize(size);
			//System.out.println("Target Size: "+size);
			return instance;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static LinkedList<LinkedList<IbBar>> splitBarListInWeekBlocks(List<IbBar> bars){
		
		LinkedList<LinkedList<IbBar>> blocks=new LinkedList<LinkedList<IbBar>>();
		if(bars.size()==0)return blocks;
		
		IbBar firstBar=bars.get(0);
		Calendar lastSunday=getLastSundayOfDate(firstBar.getTimeInMs());
		Calendar nextSunday=addOneWeekTo(lastSunday);
		
		
		LinkedList<IbBar> weekBlock=new LinkedList<IbBar>();
		
		for(IbBar bar:bars){
			if(bar.getTimeInMs() >= nextSunday.getTimeInMillis()){
				blocks.add(weekBlock);
				weekBlock=new LinkedList<IbBar>();
				weekBlock.add(bar);
				
				while(bar.getTimeInMs() >= nextSunday.getTimeInMillis())
					nextSunday=addOneWeekTo(nextSunday);
				continue;
			}
			
			weekBlock.add(bar);
		}
		
		if(!weekBlock.isEmpty()){
			blocks.add(weekBlock);
		}
		
		
		
		return blocks;
	}
	
	public static Calendar getLastSundayOfDate(long dateInMs){
		Calendar date=Calendar.getInstance();
		date.setTimeInMillis(dateInMs);
		
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.HOUR_OF_DAY, 6);
		
		
		while(date.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
			date.add(Calendar.DAY_OF_WEEK, -1);
		}
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//		System.out.println(sdf.format(date.getTime()));
	
		return date;
		
	}
	
	public static Calendar addOneWeekTo(Calendar date){
		
		Calendar nextSunday=Calendar.getInstance();
		nextSunday.setTimeInMillis(date.getTimeInMillis());
		nextSunday.add(Calendar.WEEK_OF_YEAR, 1);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//		System.out.println(sdf.format(nextSunday.getTime()));

		
		return nextSunday;
	}
	
 	
 	
	public static void main(String[] args){
		int a=13;
		int b=10;
		
		int c=a%b;
		
		System.out.println(c);
		
		
		Calendar date=Calendar.getInstance();
		Calendar lastSunday=IbBar.getLastSundayOfDate(date.getTimeInMillis());
		addOneWeekTo(lastSunday);
		
	}
	

}
