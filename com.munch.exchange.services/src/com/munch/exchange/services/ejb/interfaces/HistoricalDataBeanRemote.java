package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.model.core.ib.bar.IbSecondeBar;

@Remote
public interface HistoricalDataBeanRemote {
	
	
	//JPA Methodes
	public List<IbBarContainer> getAllExContractBars(IbContract exContract);
	
	public IbBar getFirstBar(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass);
	
	//public ExSecondeBar getFirstSecondeBar(ExContractBars exContractBars);
	
	
	public IbBar getLastBar(IbBarContainer exContractBars,Class<? extends IbBar> exBarClass);
	
	
}
