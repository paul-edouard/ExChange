package com.munch.exchange.services.ejb.interfaces;


public interface IIBHistoricalDataProvider extends HistoricalDataBeanRemote {
	
	
	//Initialization
	void init();
	
	//Close the service
	void close();
	
}
