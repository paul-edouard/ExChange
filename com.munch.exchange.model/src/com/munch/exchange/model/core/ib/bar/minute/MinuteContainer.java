package com.munch.exchange.model.core.ib.bar.minute;

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
public class MinuteContainer implements Serializable, BarContainerInterface{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1747876871224543669L;


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne
	@JoinColumn(name="CONTRACT_ID")
	private IbContract contract;
	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteAskBar> minuteAskBars;
	
	private long lastShortTermAskBarTime=0;
	private boolean longTermAskBarLoadingFinished=false;

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteBidBar> minuteBidBars;
	
	private long lastShortTermBidBarTime=0;
	private boolean longTermBidBarLoadingFinished=false;
	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteMidPointBar> minuteMidPointBars;
	
	private long lastShortTermMidPointBarTime=0;
	private boolean longTermMidPointBarLoadingFinished=false;

	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteTradesBar> minuteTradesBars;
	
	private long lastShortTermTradesBarTime=0;
	private boolean longTermTradesBarLoadingFinished=false;

	
	public MinuteContainer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MinuteContainer(IbContract contract) {
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

	public boolean isLongTermAskBarLoadingFinished() {
		return longTermAskBarLoadingFinished;
	}

	public void setLongTermAskBarLoadingFinished(boolean longTermAskBarLoadingFinished) {
		this.longTermAskBarLoadingFinished = longTermAskBarLoadingFinished;
	}

	public boolean isLongTermBidBarLoadingFinished() {
		return longTermBidBarLoadingFinished;
	}

	public void setLongTermBidBarLoadingFinished(boolean longTermBidBarLoadingFinished) {
		this.longTermBidBarLoadingFinished = longTermBidBarLoadingFinished;
	}

	public boolean isLongTermMidPointBarLoadingFinished() {
		return longTermMidPointBarLoadingFinished;
	}

	public void setLongTermMidPointBarLoadingFinished(boolean longTermMidPointBarLoadingFinished) {
		this.longTermMidPointBarLoadingFinished = longTermMidPointBarLoadingFinished;
	}

	public boolean isLongTermTradesBarLoadingFinished() {
		return longTermTradesBarLoadingFinished;
	}

	public void setLongTermTradesBarLoadingFinished(boolean longTermTradesBarLoadingFinished) {
		this.longTermTradesBarLoadingFinished = longTermTradesBarLoadingFinished;
	}
	
	
	

	
	
}
