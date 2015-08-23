package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;

public interface IIBHistoricalDataProvider extends HistoricalDataBeanRemote {
	
	
	//Initialization
	void init();
	
	
	
	
	
}
