package com.munch.exchange.server.ejb.ib.topmktdata;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.server.ejb.ib.ConnectionBean;

public enum TopMktDataMsgSenderCollector {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(TopMktDataMsgSenderCollector.class.getName());
	
	
	private HashMap<Integer, TopMktDataMsgSender> senders=new HashMap<>();
	
	
	private ConnectionFactory connectionFactory;
	
	private Topic marketDataDestination;
	
	private Topic realTimeBarDestination;
	
	
	
	public void init(EntityManager em,ConnectionFactory connectionFactory,Topic marketDatadestination, 
			Topic realTimeBardestination){
		this.connectionFactory=connectionFactory;
		this.marketDataDestination=marketDatadestination;
		this.realTimeBarDestination=realTimeBardestination;
		
//		log.info("Initialization");
		List<IbContract> list=em.createNamedQuery("IbContract.getAll", IbContract.class).getResultList();
		for(IbContract contract : list){
			//log.info(contract.toString());
			addSender(contract);
		}
	}
	
	public boolean contains(IbContract contract){
		return senders.containsKey(contract.getId());
	}
	
	public Set<Integer> getIds(){
		return senders.keySet();
	}
	
	public void addSender(IbContract contract){
		TopMktDataMsgSender sender=new TopMktDataMsgSender(contract,connectionFactory,
				marketDataDestination, realTimeBarDestination);
		ConnectionBean.INSTANCE.controller().reqTopMktData(contract.getNewContract(), "", false, sender);
		senders.put(contract.getId(), sender);
	}
	
	public boolean removeSender(IbContract contract){
		return removeSender(contract.getId());
	}
	
	public boolean removeSender(int id){
		if(senders.containsKey(id)){
			ConnectionBean.INSTANCE.controller().cancelTopMktData(senders.get(id));
			senders.remove(id);
			return true;
		}
		
		return false;
	}
	
	public void clearAll(){
		for(TopMktDataMsgSender sender : senders.values()){
			ConnectionBean.INSTANCE.controller().cancelTopMktData(sender);
		}
		senders.clear();
	}
	
	public void flushSecondBars(long currentSecond){
		for(TopMktDataMsgSender sender : senders.values()){
			sender.flushSecondBar(currentSecond);
		}
	}
	
	public List<ExBar> searchLoadedSecondBars(IbContract contract, WhatToShow whatToShow){
		for(TopMktDataMsgSender sender : senders.values()){
			if(sender.getContract().getId()!=contract.getId())continue;
			
			return sender.getLoadedBars(whatToShow);
		}
		
		return null;
	}
	
	

}
