package com.munch.exchange.services.ejb.interfaces;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.chart.IbChartIndicatorGroup;

@Remote
public interface ChartIndicatorBeanRemote {
	
	//Chart Indicator Group
	public IbChartIndicatorGroup update(IbChartIndicatorGroup group);
	public void removeGroup(int id);
	public IbChartIndicatorGroup getGroup(int id);
	
	

}
