package com.munch.exchange.services.ejb.interfaces;


public interface IIBContractProvider extends ContractInfoBeanRemote{
	
	//Initialization
	void init();
	
	//Close the service
	void close();
	
	//Search
	//List<IbContract> searchContracts(String symbol, SecType secType);
	
	
	//JPA Methodes
	//List<IbContract> getAll();
	/*
	public IbContract create(IbContract contract);
	public IbContract update(IbContract contract);
	public void remove(int id);
	public IbContract getContract(int id);
	
	//Commission
	public IbCommission getCommission(IbContract contract);
	*/
	
}
