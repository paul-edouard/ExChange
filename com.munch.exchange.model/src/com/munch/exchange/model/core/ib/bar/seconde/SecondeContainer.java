package com.munch.exchange.model.core.ib.bar.seconde;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.munch.exchange.model.core.ib.IbContract;

@Entity
public class SecondeContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8084987186181089505L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeAskBar> secondeAskBars;
	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeBidBar> secondeBidBars;

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeMidPointBar> secondeMidPointBars;

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeTradesBar> secondeTradesBars;

	public SecondeContainer() {
		super();
	}
	
	public SecondeContainer(IbContract contract) {
		super();
		this.contract=contract;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

}
