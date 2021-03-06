package com.munch.exchange.model.core.ib.bar;

import javax.persistence.Entity;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;


@Entity
public class IbSecondeBar extends IbBar{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2670538272871591708L;

	public IbSecondeBar(){
		super();
		this.setSize(BarSize._1_secs);
	}
	
	public IbSecondeBar(long time, double high, double low, double open,
			double close, double wap, long volume, int count) {
		super(time, high, low, open, close, wap, volume, count);
		this.setSize(BarSize._1_secs);
	}

	public IbSecondeBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_secs);
	}

	

	
}
