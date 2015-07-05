package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.ExContract;

@Remote
public interface ContractInfoBeanRemote {
	
	//Search metodes
	
	//void searchContractInfo(String symbol, String Sectype);
	//List<ExContract> searchContractExchange(String symbol, String exchange);
	List<ExContract> searchContract(String symbol,SecType secType);
	
	//JPA Methodes
	
	public ExContract create(ExContract contract);
	public ExContract update(ExContract contract);
	public void remove(int id);
	public ExContract getContract(int id);
	public List<ExContract> getAllContracts();
	

}
