package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.IbContract;

public interface IIBContractProvider {
	
	//Initialization
	void init();
	
	//Search
	List<IbContract> searchContracts(String symbol, SecType secType);
	
	
	//JPA Methodes
	List<IbContract> getAll();
	public IbContract create(IbContract contract);
	public IbContract update(IbContract contract);
	public void remove(int id);
	public IbContract getContract(int id);
	
	
}
