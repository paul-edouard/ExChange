package com.munch.exchange.services.ejb.providers;

import java.util.List;

import org.jboss.security.authorization.resources.EJBResource;

import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.services.ejb.beans.BeanRemote;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;
import com.munch.exchange.services.ejb.interfaces.IContractProvider;

public class ContractProvider implements IContractProvider {
	
	
	BeanRemote<ContractInfoBeanRemote> beanRemote;
	

	public ContractProvider() {
		//System.setProperty("jboss.ejb.client.properties.file.path", "C:\\Users\\paul-edouard\\git\\ExChange\\com.munch.exchange.services\\src\\jboss-ejb-client.properties");
		
		
		
		/*
		List<ExContract> list=getAll();
		for(ExContract contract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			System.out.println(contract);
			System.out.println(contract.getSecType().getClass());
		}
		*/
		
	}

	@Override
	public List<ExContract> getAll() {
		return beanRemote.getService().getAllContracts();
	}

	@Override
	public void init() {
		beanRemote=new BeanRemote<ContractInfoBeanRemote>("ContractInfoBean",ContractInfoBeanRemote.class);
		
	}

}
