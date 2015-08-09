package com.munch.exchange.services.ejb.beans;

import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;

import com.ib.controller.NewContractDetails;
import com.ib.controller.Types.SecType;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;



public class ContractInfoMB {
	
	private static final Logger log = Logger.getLogger(ContractInfoMB.class.getName());
	
	public void test() throws NamingException{
		
		
		log.info("Look for Contract Service!");
		
		//ContractInfoBeanRemote contractInfoBeanRemote=BeanRemote.doLookUpContractInfo();
		
		
		BeanRemote<ContractInfoBeanRemote> beanRemote;
		beanRemote=new BeanRemote<ContractInfoBeanRemote>("ContractInfoBean",ContractInfoBeanRemote.class);
		
		ContractInfoBeanRemote contractInfoBeanRemote=beanRemote.getService();
		
		//List<ExContract> list=contractInfoBeanRemote.searchContract("EUR",SecType.CASH);
		log.info("Request started!");
		List<IbContract> list=contractInfoBeanRemote.searchContract("BMW",SecType.STK);
		//List<ExContract> list=contractInfoBeanRemote.searchContractExchange("IBM","SMART");
		/*
		for(ExContract contract: list){
			System.out.println(contract);
			contractInfoBeanRemote.create(contract);
		}
		*/
		//List<ExContract> list=contractInfoBeanRemote.getAllContracts();
		for(IbContract contract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			System.out.println(contract);
			System.out.println(contract.getSecType().getClass());
			contractInfoBeanRemote.create(contract);
			return;
		}
		
		
	}
	
	
	
	
	public static void main(String[] args) {
		//new StudentMB().test();
		ContractInfoMB contractInfoMB=new ContractInfoMB();
		try {
			contractInfoMB.test();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
