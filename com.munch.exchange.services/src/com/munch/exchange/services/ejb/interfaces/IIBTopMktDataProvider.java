package com.munch.exchange.services.ejb.interfaces;

import com.munch.exchange.model.core.ib.IbTopMktData;

public interface IIBTopMktDataProvider {
	
	void init();
	void close();
	
	
	//Market Data registering
	void registerTopMktData(IbTopMktData topMktData);
	void unregisterTopMktData(IbTopMktData topMktData);
	
	//Top Mkt data Listerners
	void addIbTopMktDataListener(IIBTopMktDataListener listener);
	void removeIbTopMktDataListener(IIBTopMktDataListener listener);
	

}
