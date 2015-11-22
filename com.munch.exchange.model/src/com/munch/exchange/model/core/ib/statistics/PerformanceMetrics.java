package com.munch.exchange.model.core.ib.statistics;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartSerie;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;


/**
 * 
 * http://www.fxstreet.com/education/learning-center/unit-3/
 * 
 * Chapter 02 Performance Metrics
 * 
 * 
    Trade Statistics
    Revenue Statistics
    Time Statistics
    Stability Statistics
    Reading a Performance Chart

 * 
 * @author paul-edouard
 *
 */

@Entity
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

	@OneToOne
	@JoinColumn(name="CHART_SIGNAL_ID")
	private IbChartSignal chartSignal;
	
	
	public PerformanceMetrics() {
		super();
	}
	
	
	public void calculateMetricsForSignal(List<IbBar> bars, IbChartSerie signal,
			IbChartSerie buy,IbChartSerie sell,IbChartSerie profit){
		//TODO implementation of metrics calculation
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TradeStatistics getTradeStatistics() {
		return tradeStatistics;
	}

	public void setTradeStatistics(TradeStatistics tradeStatistics) {
		this.tradeStatistics = tradeStatistics;
	}

	public TimeStatistics getTimeStatistics() {
		return timeStatistics;
	}

	public void setTimeStatistics(TimeStatistics timeStatistics) {
		this.timeStatistics = timeStatistics;
	}

	public StabilityStatistics getStabilityStatistics() {
		return stabilityStatistics;
	}

	public void setStabilityStatistics(StabilityStatistics stabilityStatistics) {
		this.stabilityStatistics = stabilityStatistics;
	}

	public RevenueStatistics getRevenueStatistics() {
		return revenueStatistics;
	}

	public void setRevenueStatistics(RevenueStatistics revenueStatistics) {
		this.revenueStatistics = revenueStatistics;
	}

	
	public IbChartSignal getChartSignal() {
		return chartSignal;
	}
	

	public void setChartSignal(IbChartSignal chartSignal) {
		this.chartSignal = chartSignal;
	}
	
	
	
}
