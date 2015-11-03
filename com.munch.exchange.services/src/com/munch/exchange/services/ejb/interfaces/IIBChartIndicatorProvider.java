package com.munch.exchange.services.ejb.interfaces;

public interface IIBChartIndicatorProvider extends ChartIndicatorBeanRemote{
	
	//Initialization
	void init();
		
	//Close the service
	void close();

}
