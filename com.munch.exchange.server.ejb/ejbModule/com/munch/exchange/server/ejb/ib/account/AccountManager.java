package com.munch.exchange.server.ejb.ib.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.munch.exchange.server.ejb.ib.ConnectionBean;



public enum AccountManager {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(AccountManager.class.getName());
	
	
//	private ArrayList<String> acctCodeList = new ArrayList<String>();
	
	private HashMap<String, AccountListener> accountListeners=new HashMap<>();
	
	

	public void AccountUpdate(String accountCode){
		
	}
	

	
	public void show(String string) {
		log.info(string);
		
	}
	

	public void createAccountListeners(ArrayList<String> acctCodeList) {
		
		for (String account : acctCodeList) {
			show("Create account listner for: " + account);
			AccountListener listner=new AccountListener(account);
			ConnectionBean.INSTANCE.controller().reqAccountUpdates(true, account, listner);
//			break;
		}
	}
	
	public void removeAllAccountListeners() {
		
		for(String account :this.accountListeners.keySet()){
			AccountListener listner=this.accountListeners.get(account);
			
		}
		
		accountListeners.clear();
		
	}
	
	
	
	

}
