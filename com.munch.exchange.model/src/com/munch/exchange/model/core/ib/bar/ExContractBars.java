package com.munch.exchange.model.core.ib.bar;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	
	
	@OneToMany(mappedBy="root")
	private List<ExBar> allBars;
	

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

	

}
