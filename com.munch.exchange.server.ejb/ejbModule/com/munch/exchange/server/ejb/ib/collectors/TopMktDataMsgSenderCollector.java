package com.munch.exchange.server.ejb.ib.collectors;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.server.ejb.ib.ConnectionBean;
import com.munch.exchange.server.ejb.ib.adapter.TopMktDataMsgSender;

public enum TopMktDataMsgSenderCollector {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(TopMktDataMsgSenderCollector.class.getName());
	
	
	private HashMap<Integer, TopMktDataMsgSender> senders=new HashMap<>();
	
	
	private ConnectionFactory connectionFactory;
	
	private Topic destination;
	
	
	
	public void init(EntityManager em,ConnectionFactory connectionFactory,Topic destination){
		this.connectionFactory=connectionFactory;
		this.destination=destination;
		
		log.info("Initialization");
		List<ExContract> list=em.createNamedQuery("ExContract.getAll", ExContract.class).getResultList();
		for(ExContract contract : list){
			log.info(contract.toString());
			addSender(contract);
		}
	}
	
	
	public void addSender(ExContract contract){
		TopMktDataMsgSender sender=new TopMktDataMsgSender(contract,connectionFactory,destination);
		ConnectionBean.INSTANCE.controller().reqTopMktData(contract.getNewContract(), "", false, sender);
		
		//log.info("Top Market Data Requested!");
		
		senders.put(contract.getId(), sender);
	}
	
	public void clearAll(){
		senders.clear();
	}
	

}
