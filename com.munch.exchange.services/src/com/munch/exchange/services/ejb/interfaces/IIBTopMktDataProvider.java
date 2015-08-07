package com.munch.exchange.services.ejb.interfaces;

import com.munch.exchange.model.core.ib.ExTopMktData;

public interface IIBTopMktDataProvider {
	
	void init();
	void close();
	
	
	//Market Data registering
	void registerTopMktData(ExTopMktData topMktData);
	void unregisterTopMktData(ExTopMktData topMktData);
	
	

}
