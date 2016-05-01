package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarContainer;

@Remote
public interface HistoricalDataBeanRemote {
	
	
	//JPA Methodes
	public List<BarContainer> getAllBarContainers(IbContract exContract);
	
	
//	Time Bars
	public ExBar getFirstTimeBar(BarContainer container,BarSize size);
	public ExBar getLastTimeBar(BarContainer container,BarSize size);
	
	public List<ExBar> getAllTimeBars(BarContainer container,BarSize size);
	public List<ExBar> getTimeBarsFromTo(BarContainer container,BarSize size,long from, long to);
	
	
	public void removeBarsFromTo(BarContainer container,BarSize size,long from, long to);
		
//	Range Bars
	public ExBar getFirstRangeBar(BarContainer container, double range);
	public ExBar getLastRangeBar(BarContainer container, double range);
	
	public List<ExBar> getAllRangeBars(BarContainer container,double range);
	public List<ExBar> getRangeBarsFromTo(BarContainer container,double range,long from, long to);
	
	
}
