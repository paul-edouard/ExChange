package com.munch.exchange.model.core.ib.bar;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.ExContract;




@Entity
public class ExContractBars extends ExBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONTRACT_ID")
	private ExContract contract;
	
	
	@OneToMany(mappedBy="root",cascade=CascadeType.ALL)
	private List<ExBar> allBars;
	
	
	
	public ExContractBars(ExContract contract, WhatToShow whatToShow) {
		super();
		this.contract = contract;
		this.setType(whatToShow);
		this.setTime(new Date().getTime());
	}
	
	public ExContractBars() {
		super();
	}
	

	public ExContract getContract() {
		return contract;
	}

	public void setContract(ExContract contract) {
		this.contract = contract;
	}
	

	public List<ExBar> getAllBars() {
		return allBars;
	}

	public void setAllBars(List<ExBar> allBars) {
		this.allBars = allBars;
	}
	
	@Override
	public long getIntervall() {
		return Long.MAX_VALUE;
	}
	
	
	
	
	
}
