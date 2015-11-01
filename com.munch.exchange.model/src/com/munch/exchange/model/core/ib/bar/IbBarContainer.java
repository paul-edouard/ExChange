package com.munch.exchange.model.core.ib.bar;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorFactory;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;




@Entity
public class IbBarContainer extends IbBar implements Copyable<IbBarContainer>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	
	@OneToMany(mappedBy="root",cascade=CascadeType.ALL)
	private List<IbBar> allBars;
	
	@OneToOne(mappedBy="container",cascade=CascadeType.ALL)
	private IbChartIndicatorGroup indicatorGroup;
	
	
	public IbBarContainer(IbContract contract, WhatToShow whatToShow) {
		super();
		this.contract = contract;
		this.setType(whatToShow);
		this.setTime(new Date().getTime());
	}
	
	public IbBarContainer() {
		super();
	}
	
	
	@Override
	public IbBarContainer copy() {
		IbBarContainer c=new IbBarContainer();
		c.copyData(this);
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
	

	public List<IbBar> getAllBars() {
		return allBars;
	}

	public void setAllBars(List<IbBar> allBars) {
		this.allBars = allBars;
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

	
	
	
	
}
