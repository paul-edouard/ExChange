package com.munch.exchange.services.ejb.beans;

import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;

import com.ib.controller.NewContractDetails;
import com.munch.exchange.model.core.ib.ExContract;



public class ContractInfoMB {
	
	private static final Logger log = Logger.getLogger(ContractInfoMB.class.getName());
	
	public void test() throws NamingException{
		
		
		log.info("Look for Contract Service!");
		
		ContractInfoBeanRemote contractInfoBeanRemote=Utils.doLookUpContractInfo();
		//contractInfoBeanRemote.searchContractInfo("BMW", "");
		log.info("Request started!");
		//List<ExContract> list=contractInfoBeanRemote.searchContractExchange("BMW","TRQXDE");
		//List<ExContract> list=contractInfoBeanRemote.searchContractExchange("IBM","SMART");
		/*
		for(ExContract contract: list){
			System.out.println(contract);
			contractInfoBeanRemote.create(contract);
		}
		*/
		List<ExContract> list=contractInfoBeanRemote.getAllContracts();
		for(ExContract contract: list){
			//System.out.println(contract.getSecIdType().getApiString());
			System.out.println(contract);
			System.out.println(contract.getSecType().getClass());
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
