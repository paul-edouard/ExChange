package com.munch.exchange.model.core.ib.bar.seconde;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ib.controller.Bar;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.bar.BarConversionInterface;
import com.munch.exchange.model.core.ib.bar.BarPK;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;

@Entity
@IdClass(BarPK.class)
public class SecondeMidPointBar implements Serializable,Comparable<SecondeMidPointBar>,BarConversionInterface{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3293591978632718703L;

	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTAINER_ID")
	private SecondeContainer container;
	
	@Id
	private  long time;
	
	@Id
	private  long containerId;

	private  double high;
	private  double low;
	private  double open;
	private  double close;
	private  double wap;
	private  long volume=0;
	private  int count=0;
	
	public SecondeMidPointBar() {
		super();
	}
	

	public SecondeMidPointBar(long time, double high, double low, double open,
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

	public SecondeMidPointBar(Bar bar){
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
	public Bar toBar() {
		return new Bar(time, high, low, open, close, wap, volume, count);
	}


	@Override
	public int compareTo(SecondeMidPointBar o) {
		long diff=this.time-o.time;
		if(diff>0)return 1;
		else if(diff==0)return 0;
		return -1;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (containerId ^ (containerId >>> 32));
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecondeMidPointBar other = (SecondeMidPointBar) obj;
		if (containerId != other.containerId)
			return false;
		if (time != other.time)
			return false;
		return true;
	}
	
	@Override
	public void attachToContainer(Object container) {
		if(container instanceof SecondeContainer){
			SecondeContainer m_con=(SecondeContainer) container;
			this.containerId=m_con.getId();
			this.container=m_con;
		}
		
	}


	
	
	
	
	
}
