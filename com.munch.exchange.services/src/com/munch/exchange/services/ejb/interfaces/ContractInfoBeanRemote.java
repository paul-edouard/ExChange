package com.munch.exchange.services.ejb.interfaces;

import java.util.List;

import javax.ejb.Remote;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;

@Remote
public interface ContractInfoBeanRemote {
	
	//Search methodes
	List<IbContract> searchContracts(String symbol,SecType secType);
	
	//JPA Methodes
	public IbContract create(IbContract contract);
	public IbContract update(IbContract contract);
	public void remove(int id);
	public IbContract getContract(int id);
	public List<IbContract> getAllContracts();
	
	//Commission
	public IbCommission getCommission(IbContract contract);
	public IbCommission update(IbCommission commission);
	
}
