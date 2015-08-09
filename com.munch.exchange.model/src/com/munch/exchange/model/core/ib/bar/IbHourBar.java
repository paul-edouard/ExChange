package com.munch.exchange.model.core.ib.bar;

import javax.persistence.Entity;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;



@Entity
public class IbHourBar extends IbBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5073661063360288597L;
	
	
	public IbHourBar(){
		super();
		this.setSize(BarSize._1_hour);
	}
	
	public IbHourBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_hour);
	}
	
	@Override
	public long getIntervall() {
		return 1000*60*60;
	}
	
	
}
