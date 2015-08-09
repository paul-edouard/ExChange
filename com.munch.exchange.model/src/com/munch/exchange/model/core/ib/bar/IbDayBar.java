package com.munch.exchange.model.core.ib.bar;

import javax.persistence.Entity;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;



@Entity
public class IbDayBar extends IbBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = 999914877339044430L;
	

	
	public IbDayBar() {
		super();
		this.setSize(BarSize._1_day);
	}


	public IbDayBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_day);
	}

	
	@Override
	public long getIntervall() {
		return 1000*60*60*24;
	}

}
