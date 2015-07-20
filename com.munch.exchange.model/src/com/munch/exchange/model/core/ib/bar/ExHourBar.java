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
	private List<ExBar> minuteBars;
	
	public ExHourBar(){
		super();
		this.setSize(BarSize._1_hour);
	}
	
	public ExHourBar(Bar bar) {
		super(bar);
		this.setSize(BarSize._1_hour);
	}
	
	
	public List<ExBar> getMinuteBars() {
		return minuteBars;
	}

	public void setMinuteBars(List<ExBar> minuteBars) {
		this.minuteBars = minuteBars;
	}
	
	
	
	
}
