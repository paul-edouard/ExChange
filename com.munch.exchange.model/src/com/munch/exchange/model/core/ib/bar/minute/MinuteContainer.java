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

@Entity
public class MinuteContainer implements Serializable{
	

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

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteBidBar> minuteBidBars;
	
	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteMidPointBar> minuteMidPointBars;

	@OneToMany(mappedBy="container",cascade=CascadeType.ALL)
	private List<MinuteTradesBar> minuteTradesBars;

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
	
	
	

	
	
}
