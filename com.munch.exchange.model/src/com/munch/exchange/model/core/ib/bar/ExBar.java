package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class ExBar implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5840961632782416913L;
	
	public static enum DataType {
		HIGH, LOW, OPEN, CLOSE, WAP, VOLUME, TIME, MEDIAN_PRICE, TYPICAL_PRICE,
		WEIGHTED_CLOSE;
		
		public static String[] toStringArray(){
			List<String> list=new LinkedList<String>();
			for(DataType type:DataType.values()){
				if(type==WAP || type==VOLUME || type==TIME)continue;
				list.add(type.name());
			}
			return list.toArray(new String[list.size()]);
		}
		
		public static DataType fromString(String string){
			if(string.equals(HIGH.name())){
				return HIGH;
			}
			else if(string.equals(LOW.name())){
				return LOW;
			}
			else if(string.equals(OPEN.name())){
				return OPEN;
			}
			else if(string.equals(CLOSE.name())){
				return CLOSE;
			}
			else if(string.equals(WAP.name())){
				return WAP;
			}
			else if(string.equals(VOLUME.name())){
				return VOLUME;
			}
			else if(string.equals(TIME.name())){
				return TIME;
			}
			else if(string.equals(MEDIAN_PRICE.name())){
				return MEDIAN_PRICE;
			}
			else if(string.equals(TYPICAL_PRICE.name())){
				return TYPICAL_PRICE;
			}
			else if(string.equals(WEIGHTED_CLOSE.name())){
				return WEIGHTED_CLOSE;
			}
			
			return CLOSE;
			
		}
		
		
	}
	
	private boolean isRealTime=false;

	private boolean isCompleted=true;
	
	private  long time;
	private  double high;
	private  double low;
	private  double open;
	private  double close;
	private  double wap;
	private  long volume=0;
	private  int count=0;
	
	private BarType type=BarType.TIME;
	
	public ExBar() {
		super();
	}
	
	public ExBar(BarType barType) {
		super();
		
		this.type=barType;
	}

	

	public ExBar(long time, double high, double low, double open, double close, double wap, long volume, int count) {
		super();
		this.time = time;
		this.high = high;
		this.low = low;
		this.open = open;
		this.close = close;
		this.wap = wap;
		this.volume = volume;
		this.count = count;
	}
	
	public ExBar(ExBar bar) {
		super();
		copyData(bar);
	}
	
	public void copyData(ExBar bar){
		this.time = bar.time;
		this.high = bar.high;
		this.low = bar.low;
		this.open = bar.open;
		this.close = bar.close;
		this.wap = bar.wap;
		this.volume = bar.volume;
		this.count = bar.count;
		
		this.isCompleted=bar.isCompleted;
	}
	
	
	public void integrateData(ExBar bar){
		//this.setTime( bar.time);
		this.high = Math.max(bar.high, this.high);
		if(this.low > 0)
			this.low = Math.min(bar.low, this.low);
		else
			this.low = bar.low;
		//this.open = bar.open;
		this.close = bar.close;
		this.wap = bar.wap;
		this.volume += bar.volume;
		this.count += bar.count;
		
	}
	
	public double getData(DataType dataType){
		switch (dataType) {
			case HIGH:return high;
			case LOW:return low;
			case OPEN:return open;
			case CLOSE:return close;
			case WAP:return wap;
			case VOLUME:return (double)volume;
			case TIME:return (double)time;
			case MEDIAN_PRICE:
				return (this.getLow()+this.getHigh())/2;	
			case TYPICAL_PRICE:
				return (this.getLow()+this.getClose()+this.getHigh())/3;
			case WEIGHTED_CLOSE:
				return (this.getLow()+2*this.getClose()+this.getHigh())/4;
				
			default:return 0;
		}
		
	}


	public long getTime() {
		return time;
	}
	
	public long getTimeInSec() {
		if(type==BarType.TIME)
			return time;
		
		return time/1000;
	}
	
	public long getTimeInMs() {
		if(type==BarType.TIME)
			return time*1000;
		
		return time;
	}



	public void setTime(long time) {
		this.time = time;
	}
	
	public void setTimeInMs(long time) {
		this.time = time/1000;
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



	public boolean isCompleted() {
		return isCompleted;
	}



	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public boolean isRealTime() {
		return isRealTime;
	}

	public void setRealTime(boolean isRealTime) {
		this.isRealTime = isRealTime;
	}

	@Override
	public String toString() {
		return "ExBar [isRealTime=" + isRealTime + ", isCompleted=" + isCompleted + ", time=" + BarUtils.format(getTimeInMs()) + ", high=" + high
				+ ", low=" + low + ", open=" + open + ", close=" + close + ", wap=" + wap + ", volume=" + volume
				+ ", count=" + count + ", type=" + type + "]";
	}



	
	
	
	

}
