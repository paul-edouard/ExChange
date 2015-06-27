package com.munch.exchange.services.ejb.beans;

import javax.naming.NamingException;


public class ContractInfoMB {
	
	public void test() throws NamingException{
		ContractInfoBeanRemote contractInfoBeanRemote=Utils.doLookUpContractInfo();
		contractInfoBeanRemote.searchContractInfo("BMW", "");
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
