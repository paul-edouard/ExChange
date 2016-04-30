package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;

import com.munch.exchange.model.core.ib.bar.IbBar.DataType;

public class ExBar implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5840961632782416913L;
	
	public static enum DataType {
		HIGH, LOW, OPEN, CLOSE, WAP, VOLUME, TIME;
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
		this.low = Math.min(bar.low, this.low);
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
			default:return 0;
		}
		
	}


	public long getTime() {
		return time;
	}
	
	public long getTimeInMs() {
		if(type==BarType.TIME)
			return time*1000;
		
		return time;
	}



	public void setTime(long time) {
		this.time = time;
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



	
	
	
	

}
