package com.munch.exchange.services.ejb.beans;

import java.util.List;

import javax.ejb.Remote;

import com.munch.exchange.model.core.ib.ExContract;

@Remote
public interface ContractInfoBeanRemote {
	
	void searchContractInfo(String symbol, String Sectype);
	
	List<ExContract> searchContractExchange(String symbol, String exchange);
	

}
