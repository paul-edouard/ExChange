package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignal;
import com.munch.exchange.model.core.ib.chart.signals.IbChartSignalOptimizedParameters;

@Remote
public interface ChartIndicatorBeanRemote {
	
	//Chart Indicator Group
	public void update(IbChartIndicatorGroup group);
	public void removeGroup(int id);
	public IbChartIndicatorGroup getGroup(int id);
	
	//Chart Signal
	public List<IbChartSignalOptimizedParameters> updateOptimizedParameters(IbChartSignal signal);
	public IbChartSignal getSignal(int id);
	
	

}
