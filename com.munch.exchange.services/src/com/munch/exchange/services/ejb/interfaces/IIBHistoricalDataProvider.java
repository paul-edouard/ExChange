package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.munch.exchange.model.core.ib.bar.BarContainer;
import com.munch.exchange.model.core.ib.bar.ExBar;

public interface IIBHistoricalDataProvider extends HistoricalDataBeanRemote {
	
	
	//Initialization
	void init();
	
	//Close the service
	void close();
	
	
	public List<ExBar> getCorrespondingRangeBars(BarContainer container, List<ExBar> masterBars);
	
	
}
