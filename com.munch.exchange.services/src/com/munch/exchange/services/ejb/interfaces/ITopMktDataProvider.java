package com.munch.exchange.services.ejb.interfaces;

import com.munch.exchange.model.core.ib.ExTopMktData;

public interface ITopMktDataProvider {
	
	void init();
	
	
	//Market Data registering
	void registerTopMktData(ExTopMktData topMktData);
	void unregisterTopMktData(ExTopMktData topMktData);
	
	

}
