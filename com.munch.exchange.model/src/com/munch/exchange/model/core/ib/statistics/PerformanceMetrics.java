package com.munch.exchange.model.core.ib.statistics;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

public class PerformanceMetrics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8798544814449688139L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne(mappedBy="performanceMetrics",cascade=CascadeType.ALL)
	private TradeStatistics tradeStatistics;
	
	@OneToOne(mappedBy="performanceMetrics",cascade=CascadeType.ALL)
	private TimeStatistics timeStatistics;
	
	@OneToOne(mappedBy="performanceMetrics",cascade=CascadeType.ALL)
	private StabilityStatistics stabilityStatistics;
	
	@OneToOne(mappedBy="performanceMetrics",cascade=CascadeType.ALL)
	private RevenueStatistics revenueStatistics;
	
}
