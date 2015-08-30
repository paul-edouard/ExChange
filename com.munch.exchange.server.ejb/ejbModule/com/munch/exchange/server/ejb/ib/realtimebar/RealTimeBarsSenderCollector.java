package com.munch.exchange.server.ejb.ib.realtimebar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.persistence.EntityManager;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.bar.IbBarContainer;
import com.munch.exchange.server.ejb.ib.ConnectionBean;
import com.munch.exchange.server.ejb.ib.historicaldata.HistoricalDataMsgDrivenBean;


public enum RealTimeBarsSenderCollector {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(RealTimeBarsSenderCollector.class.getName());
	
	
	private HashMap<Integer, List<RealTimeBarsSender>> senders=new HashMap<>();
	
	
	private ConnectionFactory connectionFactory;
	private Topic destination;
	
	
	public void init(EntityManager em,ConnectionFactory connectionFactory,Topic destination,List<IbContract> allContracts){
		this.connectionFactory=connectionFactory;
		this.destination=destination;
		
		log.info("Initialization");
		//List<IbContract> list=em.createNamedQuery("IbContract.getAll", IbContract.class).getResultList();
		for(IbContract contract : allContracts){
			//List<IbBarContainer> Allbars=HistoricalDataMsgDrivenBean.getBarsFrom(contract, em);
			addSender(contract, contract.getBars());
			
		}
	}
	
	
	public void addSender(IbContract contract,List<IbBarContainer> Allbars){
		List<RealTimeBarsSender> l=new LinkedList<RealTimeBarsSender>();
		for(IbBarContainer barcontainer:Allbars){
			WhatToShow whatToShow=barcontainer.getType();
			RealTimeBarsSender sender=new RealTimeBarsSender(contract, whatToShow, connectionFactory, destination);
			log.info("Try to request real time bar for: ");
			ConnectionBean.INSTANCE.controller().reqRealTimeBars(contract.getNewContract(), whatToShow, false, sender);
			l.add(sender);
		}
		senders.put(contract.getId(), l);
	}
	
	public boolean removeSender(IbContract contract){
		return removeSender(contract.getId());
	}
	
	public boolean removeSender(int id){
		if(senders.containsKey(id)){
			for(RealTimeBarsSender sender : senders.get(id)){
				ConnectionBean.INSTANCE.controller().cancelRealtimeBars(sender);
			}
			senders.remove(id);
			return true;
		}
		return false;
	}
	
	public void clearAll(){
		for(List<RealTimeBarsSender> l : senders.values()){
			for(RealTimeBarsSender sender:l){
				ConnectionBean.INSTANCE.controller().cancelRealtimeBars(sender);
			}
		}
		senders.clear();
	}

	
	

}
