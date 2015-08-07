package com.munch.exchange.services.ejb.providers;

import java.util.Enumeration;
import java.util.HashMap;
//import java.util.logging.Logger;


import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.ib.ExTopMktData;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataProvider;
import com.munch.exchange.services.ejb.messages.Constants;

public class IBTopMktDataProvider implements IIBTopMktDataProvider, MessageListener{
	
	
	
	private static final Logger log = Logger.getLogger(IBTopMktDataProvider.class.getName());
	 
	
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private JMSContext jmsContext;
	private Context context;
	private JMSConsumer consumer;
	
	private HashMap<Integer, ExTopMktData> topMktDataMap=new HashMap<Integer, ExTopMktData>();
	

	@Override
	public void init() {
		
		try {
			context = new InitialContext(Constants.getContextProperties());
			
			//log.info("Attempting to acquire connection factory \"" + Constants.DEFAULT_CONNECTION_FACTORY + "\"");
	        connectionFactory = (ConnectionFactory) context.lookup(Constants.DEFAULT_CONNECTION_FACTORY);
	        //log.info("Found connection factory \"" + Constants.DEFAULT_CONNECTION_FACTORY + "\" in JNDI");
	        
	        //log.info("Attempting to acquire destination \"" + Constants.TOP_MARKET_DATA + "\"");
	        destination = (Destination) context.lookup(Constants.TOP_MARKET_DATA);
	        //log.info("Found destination \"" + Constants.TOP_MARKET_DATA + "\" in JNDI");
	        
	        jmsContext=connectionFactory.createContext(Constants.DEFAULT_USERNAME, Constants.DEFAULT_PASSWORD);
	        jmsContext.setClientID(Constants.DEFAULT_CLIENT_ID);
	        consumer=jmsContext.createDurableConsumer((Topic) destination,
        			"abo",
        			"",
        			true);
        	consumer.setMessageListener(this);
        	
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void close() {
		log.info("Close message consumer");
		consumer.close();
		jmsContext.close();
	}
	
	

	@Override
	public void registerTopMktData(ExTopMktData topMktData) {
		if(!topMktDataMap.containsKey(topMktData.getContractId()))
			topMktDataMap.put(topMktData.getContractId(), topMktData);
	}

	@Override
	public void unregisterTopMktData(ExTopMktData topMktData) {
		if(topMktDataMap.containsKey(topMktData.getContractId()))
			topMktDataMap.remove(topMktData.getContractId());

	}

	@Override
	public void onMessage(Message arg0) {
		try {
			TextMessage msg=(TextMessage) arg0;
			/*
			int contractId=msg.getIntProperty(ExTopMktData.CONTRACT_ID);
			if(contractId==0)return;
			ExTopMktData topMktData=topMktDataMap.get(contractId);
			if(topMktData==null)return;
			*/
			
			@SuppressWarnings("unchecked")
			Enumeration<String> propList= msg.getPropertyNames();
			String content="";
			while(propList.hasMoreElements()){
				String prop=propList.nextElement();
				String value=msg.getStringProperty(prop);
				//topMktData.setValue(prop, value);
				content+=prop+"="+value+", ";
			}
			
			System.out.println(content);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}
