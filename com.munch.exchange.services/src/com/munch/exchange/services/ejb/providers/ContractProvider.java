package com.munch.exchange.services.ejb.providers;

import java.util.List;

import org.jboss.security.authorization.resources.EJBResource;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IContractProvider;

public class ContractProvider implements IContractProvider {
	
	
	BeanRemote<ContractInfoBeanRemote> beanRemote;
	

	public ContractProvider() {
	}

	@Override
	public List<ExContract> getAll() {
		return beanRemote.getService().getAllContracts();
	}

	@Override
	public void init() {
		beanRemote=new BeanRemote<ContractInfoBeanRemote>("ContractInfoBean",ContractInfoBeanRemote.class);
		
	}

	@Override
	public List<ExContract> searchContracts(String symbol, SecType secType) {
		return beanRemote.getService().searchContract(symbol, secType);
	}

	@Override
	public ExContract create(ExContract contract) {
		return beanRemote.getService().create(contract);
	}

	@Override
	public ExContract update(ExContract contract) {
		return beanRemote.getService().update(contract);
	}

	@Override
	public void remove(int id) {
		beanRemote.getService().remove(id);
	}

	@Override
	public ExContract getContract(int id) {
		return beanRemote.getService().getContract(id);
	}

}
