package com.munch.exchange.services.ejb.providers;

import java.util.List;

import org.jboss.security.authorization.resources.EJBResource;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;

public class IBContractProvider implements IIBContractProvider {
	
	
	BeanRemote<ContractInfoBeanRemote> beanRemote;
	

	public IBContractProvider() {
	}

	@Override
	public List<IbContract> getAll() {
		return beanRemote.getService().getAllContracts();
	}

	@Override
	public void init() {
		beanRemote=new BeanRemote<ContractInfoBeanRemote>("ContractInfoBean",ContractInfoBeanRemote.class);
		
	}

	@Override
	public List<IbContract> searchContracts(String symbol, SecType secType) {
		return beanRemote.getService().searchContract(symbol, secType);
	}

	@Override
	public IbContract create(IbContract contract) {
		return beanRemote.getService().create(contract);
	}

	@Override
	public IbContract update(IbContract contract) {
		return beanRemote.getService().update(contract);
	}

	@Override
	public void remove(int id) {
		beanRemote.getService().remove(id);
	}

	@Override
	public IbContract getContract(int id) {
		return beanRemote.getService().getContract(id);
	}

}
