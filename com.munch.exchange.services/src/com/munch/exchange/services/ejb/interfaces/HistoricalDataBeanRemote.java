package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExContractBars;
import com.munch.exchange.model.core.ib.bar.ExSecondeBar;

@Remote
public interface HistoricalDataBeanRemote {
	
	
	//JPA Methodes
	public List<ExContractBars> getAllExContractBars(ExContract exContract);
	
	public ExBar getFirstBar(ExContractBars exContractBars,Class<? extends ExBar> exBarClass);
	
	//public ExSecondeBar getFirstSecondeBar(ExContractBars exContractBars);
	
	
	public ExBar getLastBar(ExContractBars exContractBars,Class<? extends ExBar> exBarClass);
	
	
}
