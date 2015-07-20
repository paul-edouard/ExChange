package com.munch.exchange.model.core.ib.bar;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.ib.controller.Bar;
import com.ib.controller.Types.BarSize;



@Entity
public class ExHourBar extends ExBar{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5073661063360288597L;
	
	@OneToMany(mappedBy="parent")
	private List<ExMinuteBar> minuteBars;
	
	public ExHourBar(){
		super();
		this.setSize(BarSize._1_hour);
	}
	
	public ExHourBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_hour);
	}
	
	
	public List<ExMinuteBar> getMinuteBars() {
		return minuteBars;
	}

	public void setMinuteBars(List<ExMinuteBar> minuteBars) {
		this.minuteBars = minuteBars;
	}
	
	
	
	
}
