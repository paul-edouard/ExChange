package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorFactory;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;




@Entity
public class BarContainer implements Copyable<BarContainer>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	
//	@OneToMany(mappedBy="root",cascade=CascadeType.ALL)
//	private List<IbBar> allBars;
	
	@OneToOne(mappedBy="container",cascade=CascadeType.ALL)
	private IbChartIndicatorGroup indicatorGroup;
	
	@Enumerated(EnumType.STRING)
	private WhatToShow type;
	
	
//	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
//	private List<MinuteAskBar> minuteAskBars;
	
	
	
	
	public BarContainer(IbContract contract, WhatToShow whatToShow) {
		super();
		this.contract = contract;
		this.setType(whatToShow);
//		this.setTime(new Date().getTime());
	}
	
	public BarContainer() {
		super();
	}
	
	
	@Override
	public BarContainer copy() {
		BarContainer c=new BarContainer();
		
		c.type=this.type;
		c.id=this.id;
		
		c.indicatorGroup=this.indicatorGroup.copy();
		c.indicatorGroup.setContainer(c);
		return c;
	}
	
	
	

	public IbContract getContract() {
		return contract;
	}

	public void setContract(IbContract contract) {
		this.contract = contract;
	}
	
	
	

	public IbChartIndicatorGroup getIndicatorGroup() {
		if(indicatorGroup==null){
			indicatorGroup=IbChartIndicatorFactory.createRoot();
			indicatorGroup.setContainer(this);
		}
		return indicatorGroup;
	}

	
	public void setIndicatorGroup(IbChartIndicatorGroup indicatorGroup) {
		this.indicatorGroup = indicatorGroup;
	}

	public WhatToShow getType() {
		return type;
	}

	public void setType(WhatToShow type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	
	
	
	
}
