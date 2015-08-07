package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.ExContract;

public interface IIBContractProvider {
	
	//Initialization
	void init();
	
	//Search
	List<ExContract> searchContracts(String symbol, SecType secType);
	
	
	//JPA Methodes
	List<ExContract> getAll();
	public ExContract create(ExContract contract);
	public ExContract update(ExContract contract);
	public void remove(int id);
	public ExContract getContract(int id);
	
	
}
