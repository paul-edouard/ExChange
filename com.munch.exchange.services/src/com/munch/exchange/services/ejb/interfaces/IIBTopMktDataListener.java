package com.munch.exchange.services.ejb.interfaces;

import com.munch.exchange.model.core.ib.IbTopMktData;

public interface IIBTopMktDataListener {
	
	
	void ibTopMktDataChanged(IbTopMktData ibTopMktData);

}
