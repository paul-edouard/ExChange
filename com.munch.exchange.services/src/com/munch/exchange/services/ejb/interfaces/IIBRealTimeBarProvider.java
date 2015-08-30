package com.munch.exchange.services.ejb.interfaces;

public interface IIBRealTimeBarProvider {
	
	void init();
	void close();
	
	//Listener
	void addIbRealTimeBarListener(IIBRealTimeBarListener listener);
	void removeRealTimeBarListener(IIBRealTimeBarListener listener);

}
