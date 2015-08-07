package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.ExContractBars;

public interface IIBHistoricalDataProvider extends HistoricalDataBeanRemote {
	
	
	//Initialization
	void init();
	
	
	
	
}
