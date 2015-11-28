package com.munch.exchange.model.core.ib.statistics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartPoint;
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
	
	
	public void calculateMetricsForSignal(List<IbBar> bars,
				HashMap<Long, IbChartPoint> signalMap, IbCommission commission,
				long volume){
		this.getTradeStatistics().calculate(bars, signalMap, commission, volume);
		this.getTimeStatistics().calculate(bars, signalMap, commission, volume);
		this.getStabilityStatistics().calculate(bars, signalMap, commission, volume);
		this.getRevenueStatistics().calculate(bars, signalMap, commission, volume);
		
		if(this.getRevenueStatistics().getMaximumDrawdown()>0){
			this.getStabilityStatistics().setProfitToDrawdownRatio(
					this.getRevenueStatistics().getNetProfit()/this.getRevenueStatistics().getMaximumDrawdown());
		}
		
		
		System.out.println(this);
		
	}
	
	
	public Object[] getChildren(){
		Object[] children =new Object[4];
		
		children[0]=this.getTradeStatistics();
		children[1]=this.getTimeStatistics();
		children[2]=this.getStabilityStatistics();
		children[3]=this.getRevenueStatistics();
		
		return children;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TradeStatistics getTradeStatistics() {
		if(tradeStatistics==null)
			tradeStatistics=new TradeStatistics();
		return tradeStatistics;
	}

	public void setTradeStatistics(TradeStatistics tradeStatistics) {
		this.tradeStatistics = tradeStatistics;
	}

	public TimeStatistics getTimeStatistics() {
		if(timeStatistics==null)
			timeStatistics=new TimeStatistics();
		return timeStatistics;
	}

	public void setTimeStatistics(TimeStatistics timeStatistics) {
		this.timeStatistics = timeStatistics;
	}

	public StabilityStatistics getStabilityStatistics() {
		if(stabilityStatistics==null)
			stabilityStatistics=new StabilityStatistics();
		return stabilityStatistics;
	}

	public void setStabilityStatistics(StabilityStatistics stabilityStatistics) {
		this.stabilityStatistics = stabilityStatistics;
	}

	public RevenueStatistics getRevenueStatistics() {
		if(revenueStatistics==null)
			revenueStatistics=new RevenueStatistics();
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


	@Override
	public String toString() {
		return "PerformanceMetrics [\ntradeStatistics:\n" + tradeStatistics
				+ "\ntimeStatistics:\n" + timeStatistics
				+ "\nstabilityStatistics:\n" + stabilityStatistics
				+ "\nrevenueStatistics:\n" + revenueStatistics + "]";
	}
	
	
	
}
