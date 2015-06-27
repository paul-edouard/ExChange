package com.munch.exchange.server.ejb.ib;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import apidemo.ApiDemo;

import com.ib.controller.ApiController.IContractDetailsHandler;
import com.ib.controller.Types.SecType;
import com.ib.controller.NewContract;
import com.ib.controller.NewContractDetails;
import com.munch.exchange.services.ejb.beans.ContractInfoBeanRemote;

/**
 * Session Bean implementation class ContractInfoBean
 */
@Stateless
@LocalBean
public class ContractInfoBean implements ContractInfoBeanRemote, IContractDetailsHandler {
	
	private static final Logger log = Logger.getLogger(ContractInfoBean.class.getName());
	

    /**
     * Default constructor. 
     */
    public ContractInfoBean() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void searchContractInfo(String symbol, String Sectype) {
		NewContract m_contract = new NewContract();
		m_contract.symbol(symbol);
		m_contract.secType(SecType.STK);
		
		ConnectionBean.INSTANCE.controller().reqContractDetails(m_contract, this);
		
	}

	@Override
	public void contractDetails(ArrayList<NewContractDetails> list) {
		if (list.size() == 0) {
			log.warning("No matching contracts were found");
			return;
		}
		int i=1;
		for(NewContractDetails details:list){
			log.info("Found Contract: "+i+"\n"+details.toString());
			i++;
		}
		
	}

}
