package com.munch.exchange.server.ejb.ib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ib.client.TagValue;
import com.ib.controller.ApiController.IContractDetailsHandler;
import com.ib.controller.Types.SecType;
import com.ib.controller.NewContract;
import com.ib.controller.NewContractDetails;
import com.munch.exchange.model.core.ib.IbCommission;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.server.ejb.ib.topmktdata.TopMktDataMsgSenderCollector;
import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;

/**
 * Session Bean implementation class ContractInfoBean
 */
@Stateless
@LocalBean
public class ContractInfoBean implements ContractInfoBeanRemote, IContractDetailsHandler {
	
	private static final Logger log = Logger.getLogger(ContractInfoBean.class.getName());
	
	private ArrayList<NewContractDetails> list;
	
	@PersistenceContext
	private EntityManager em;
	
	
    /**
     * Default constructor. 
     */
    public ContractInfoBean() {
        // TODO Auto-generated constructor stub
    }

    /*
	@Override
	public void searchContractInfo(String symbol, String Sectype) {
		NewContract m_contract = new NewContract();
		m_contract.symbol(symbol);
		m_contract.secType(SecType.STK);
		
		
		//log.info("reqContractDetails Started!");
		
		ConnectionBean.INSTANCE.controller().reqContractDetails(m_contract, this);
		
		//ConnectionBean.INSTANCE.controller().reqHistoricalData(contract, endDateTime, duration, durationUnit, barSize, whatToShow, rthOnly, handler);
		
		
		log.info("searchContractInfo Finished!");
		
	}
	*/

	@Override
	public void contractDetails(ArrayList<NewContractDetails> list) {
		
		
		this.list=list;
		
		if (list.size() == 0) {
			log.warning("No matching contracts were found");
			return;
		}
		
	}
	
	
	/*
	@Override
	public List<ExContract> searchContractExchange(String symbol, String exchange) {
		
	
		NewContract m_contract = new NewContract();
		m_contract.symbol(symbol);
		//m_contract.tradingClass(marketName);
		m_contract.secType(SecType.STK);
		m_contract.exchange(exchange);
		
		//log.info("reqContractDetails Started!");
		
		//Reset the list
		this.list=null;
		
		ConnectionBean.INSTANCE.controller().reqContractDetails(m_contract, this);
		
		//Wait of the answer
		waitForIbAnswer();
		
		
		log.info("searchContractInfo Finished!");
		
		List<ExContract> ouputList=new LinkedList<>();
		for(NewContractDetails details:list){
			ouputList.add(new ExContract(details));
		}
		return ouputList;
	}
	*/
	
	@Override
	public List<IbContract> searchContracts(String symbol,SecType secType) {
		
	
		NewContract m_contract = new NewContract();
		m_contract.symbol(symbol);
		//m_contract.tradingClass(marketName);
		m_contract.secType(secType);
		//m_contract.exchange(exchange);
		
		//log.info("reqContractDetails Started!");
		
		//Reset the list
		this.list=null;
		
		ConnectionBean.INSTANCE.controller().reqContractDetails(m_contract, this);
		
		//Wait of the answer
		waitForIbAnswer();
		
		
		log.info("searchContractInfo Finished!");
		
		List<IbContract> ouputList=new LinkedList<>();
		for(NewContractDetails details:list){
			if(details.secIdList()!=null){
				for(TagValue tagValue: details.secIdList()){
					log.info("Tag: "+tagValue.m_tag+", Value: "+tagValue.m_value);
				}
			}
			ouputList.add(new IbContract(details));
		}
		return ouputList;
	}
	
	
	
	private void waitForIbAnswer(){
		int i=0;
		while(i<100){
			if(this.list==null){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			else break;
			i++;
		}
		
		if(this.list==null){
			this.list=new ArrayList<NewContractDetails>();
		}
	}

	@Override
	public IbContract create(IbContract contract) {
		em.persist(contract);
		//Add message sender
		TopMktDataMsgSenderCollector.INSTANCE.addSender(contract);
		return contract;
	}

	@Override
	public IbContract update(IbContract contract) {
		em.merge(contract);
		return contract;
	}

	@Override
	public void remove(int id) {
		//Remove a message sender
		TopMktDataMsgSenderCollector.INSTANCE.removeSender(getContract(id));
		
		em.remove(getContract(id));
	}

	@Override
	public IbContract getContract(int id) {
		return em.find(IbContract.class, id);
	}

	@Override
	public List<IbContract> getAllContracts() {
		return em.createNamedQuery("IbContract.getAll", IbContract.class).getResultList();
	}

	@Override
	public IbCommission getCommission(IbContract contract) {
		IbContract c=getContract(contract.getId());
		return c.getCommission();
	}

	@Override
	public IbCommission update(IbCommission commission) {
		em.merge(commission);
		return commission;
	}

	

}
