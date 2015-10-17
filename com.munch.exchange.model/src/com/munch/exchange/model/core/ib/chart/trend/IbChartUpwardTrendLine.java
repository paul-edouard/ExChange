package com.munch.exchange.model.core.ib.chart.trend;

import java.util.List;

import javax.persistence.Entity;

import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.chart.IbChartIndicator;
import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;

@Entity
public class IbChartUpwardTrendLine extends IbChartIndicator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1932238979675913940L;
	
	public IbChartUpwardTrendLine(){
		super();
	}
	
	public IbChartUpwardTrendLine(IbChartIndicatorGroup group) {
		super(group);
	}
	

	@Override
	public void initName() {
		this.name="Upward Trend Line";
	}

	@Override
	public void createSeries() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void compute(List<IbBar> bars) {
		// TODO Auto-generated method stub

	}

	@Override
	public void computeLast(List<IbBar> bars) {
		// TODO Auto-generated method stub

	}

}
