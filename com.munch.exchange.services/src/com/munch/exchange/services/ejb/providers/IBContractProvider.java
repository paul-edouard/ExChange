package com.munch.exchange.services.ejb.providers;

import java.util.List;

import org.jboss.security.authorization.resources.EJBResource;

import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IIBContractProvider;

public class IBContractProvider implements IIBContractProvider {
	
	
	BeanRemote<ContractInfoBeanRemote> beanRemote;
	

	public IBContractProvider() {
	}

	@Override
	public List<IbContract> getAllContracts() {
		if(beanRemote==null)init();
		return beanRemote.getService().getAllContracts();
	}

	@Override
	public void init() {
		beanRemote=new BeanRemote<ContractInfoBeanRemote>("ContractInfoBean",ContractInfoBeanRemote.class);
	}

	@Override
	public List<IbContract> searchContracts(String symbol, SecType secType) {
		if(beanRemote==null)init();
		return beanRemote.getService().searchContracts(symbol, secType);
	}

	@Override
	public IbContract create(IbContract contract) {
		if(beanRemote==null)init();
		return beanRemote.getService().create(contract);
	}

	@Override
	public IbContract update(IbContract contract) {
		if(beanRemote==null)init();
		return beanRemote.getService().update(contract);
	}

	@Override
	public void remove(int id) {
		if(beanRemote==null)init();
		beanRemote.getService().remove(id);
	}

	@Override
	public IbContract getContract(int id) {
		if(beanRemote==null)init();
		return beanRemote.getService().getContract(id);
	}

	@Override
	public void close() {
		if(beanRemote!=null)
			beanRemote.CloseContext();
	}

	@Override
	public IbCommission getCommission(IbContract contract) {
		if(beanRemote==null)init();
		return beanRemote.getService().getCommission(contract);
	}

}
