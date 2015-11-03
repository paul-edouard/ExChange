package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;

@Remote
public interface HistoricalDataBeanRemote {
	
	
	//JPA Methodes
	public List<IbBarContainer> getAllExContractBars(IbContract exContract);
	
	
	public IbBar searchBarOfTime(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass,long time);
	
	public IbBar getFirstBar(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass);
	public long getFirstBarTime(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass);
	
	public IbBar getLastBar(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass);
	public long getLastBarTime(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass);
	
	
	public List<IbBar> getAllBars(IbBarContainer exContractBars,BarSize size);
	public List<IbBar> getBarsFromTo(IbBarContainer exContractBars,BarSize size,long from, long to);
	public List<IbBar> downloadLastBars(IbBarContainer exContractBars,BarSize size);
	
	
	
	void removeBar(long id);
	public IbBar getBar(long id);
	
	
	
}
