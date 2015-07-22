package com.munch.exchange.model.core.ib.bar;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;



@Entity
public class ExDayBar extends ExBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = 999914877339044430L;
	

	
	public ExDayBar() {
		super();
		this.setSize(BarSize._1_day);
	}


	public ExDayBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_day);
	}



}
