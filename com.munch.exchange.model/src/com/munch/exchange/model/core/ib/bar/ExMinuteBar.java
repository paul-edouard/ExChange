package com.munch.exchange.model.core.ib.bar;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;

@Entity
public class ExMinuteBar extends ExBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2310035118704239560L;
	
	public ExMinuteBar(){
		super();
		this.setSize(BarSize._1_min);
	}
	
	public ExMinuteBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_min);
		// TODO Auto-generated constructor stub
	}
	
	@OneToMany(mappedBy="parent")
	private List<ExBar> secondBars;


	public List<ExBar> getSecondBars() {
		return secondBars;
	}

	public void setSecondBars(List<ExBar> secondBars) {
		this.secondBars = secondBars;
	}
	
	
	
	
	
}
