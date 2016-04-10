package com.munch.exchange.model.core.ib.bar.minute;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ib.controller.Bar;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.bar.BarConversionInterface;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbMinuteBar;

@Entity
public class MinuteBidBar implements Serializable,Comparable<MinuteBidBar>,BarConversionInterface{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2975688434450506985L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTAINER_ID")
	private MinuteContainer container;
	
	
	private  long time;
	private  double high;
	private  double low;
	private  double open;
	private  double close;
	private  double wap;
	private  long volume=0;
	private  int count=0;
	
	public MinuteBidBar() {
		super();
	}
	

	public MinuteBidBar(long time, double high, double low, double open,
			double close, double wap, long volume, int count) {
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

	public MinuteBidBar(Bar bar){
		this.init(bar);
	}
	
	
	public void init(Bar bar){
		this.time= bar.time();
		this.high = bar.high();
		this.low = bar.low();
		this.open = bar.open();
		this.close = bar.close();
		this.wap = bar.wap();
		this.volume = bar.volume();
		this.count = bar.count();
	}
	
	@Override
	public IbBar toIbBar() {
		IbMinuteBar ibBar=new IbMinuteBar();
		ibBar.setTime(time);
		ibBar.setHigh(high);
		ibBar.setLow(low);
		ibBar.setOpen(open);
		ibBar.setClose(close);
		ibBar.setWap(wap);
		ibBar.setVolume(volume);
		ibBar.setCount(count);
		
		ibBar.setType(WhatToShow.BID);
		
		return ibBar;
	}


	@Override
	public int compareTo(MinuteBidBar o) {
		long diff=this.time-o.time;
		if(diff>0)return 1;
		else if(diff==0)return 0;
		return -1;
	}
	
	
}
