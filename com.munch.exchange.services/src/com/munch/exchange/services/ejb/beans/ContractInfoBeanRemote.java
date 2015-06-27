package com.munch.exchange.services.ejb.beans;

import javax.ejb.Remote;

@Remote
public interface ContractInfoBeanRemote {
	
	void searchContractInfo(String symbol, String Sectype);

}
