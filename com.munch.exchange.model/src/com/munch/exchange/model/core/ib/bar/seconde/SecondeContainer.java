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
import com.munch.exchange.model.core.ib.bar.BarContainerInterface;

@Entity
public class SecondeContainer implements Serializable, BarContainerInterface {

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
	
	private long lastShortTermAskBarTime=0;
	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeBidBar> secondeBidBars;
	
	private long lastShortTermBidBarTime=0;

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeMidPointBar> secondeMidPointBars;
	
	private long lastShortTermMidPointBarTime=0;

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<SecondeTradesBar> secondeTradesBars;
	
	private long lastShortTermTradesBarTime=0;

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
	
	public long getLastShortTermAskBarTime() {
		return lastShortTermAskBarTime;
	}

	public void setLastShortTermAskBarTime(long lastShortTermAskBarTime) {
		this.lastShortTermAskBarTime = lastShortTermAskBarTime;
	}

	public long getLastShortTermBidBarTime() {
		return lastShortTermBidBarTime;
	}

	public void setLastShortTermBidBarTime(long lastShortTermBidBarTime) {
		this.lastShortTermBidBarTime = lastShortTermBidBarTime;
	}

	public long getLastShortTermMidPointBarTime() {
		return lastShortTermMidPointBarTime;
	}

	public void setLastShortTermMidPointBarTime(long lastShortTermMidPointBarTime) {
		this.lastShortTermMidPointBarTime = lastShortTermMidPointBarTime;
	}

	public long getLastShortTermTradesBarTime() {
		return lastShortTermTradesBarTime;
	}

	public void setLastShortTermTradesBarTime(long lastShortTermTradesBarTime) {
		this.lastShortTermTradesBarTime = lastShortTermTradesBarTime;
	}
	
	

}
