package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;

@Remote
public interface HistoricalDataBeanRemote {
	
	
	//JPA Methodes
	public List<IbBarContainer> getAllBarContainers(IbContract exContract);
	
	
//	Time Bars
	public ExBar getFirstTimeBar(IbBarContainer container,BarSize size);
	public ExBar getLastTimeBar(IbBarContainer container,BarSize size);
	
	public List<ExBar> getAllTimeBars(IbBarContainer container,BarSize size);
	public List<ExBar> getTimeBarsFromTo(IbBarContainer container,BarSize size,long from, long to);
	
	
	public void removeBarsFromTo(IbBarContainer container,BarSize size,long from, long to);
		
//	Range Bars
	public ExBar getFirstRangeBar(IbBarContainer container, double range);
	public ExBar getLastRangeBar(IbBarContainer container, double range);
	
	public List<ExBar> getAllRangeBars(IbBarContainer container,double range);
	public List<ExBar> getRangeBarsFromTo(IbBarContainer container,double range,long from, long to);
	
	
}
