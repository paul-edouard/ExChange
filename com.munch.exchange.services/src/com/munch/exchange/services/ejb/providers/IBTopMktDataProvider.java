package com.munch.exchange.services.ejb.providers;

import java.util.Enumeration;
import java.util.HashMap;
//import java.util.logging.Logger;


import java.util.HashSet;

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

import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataListener;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataProvider;
import com.munch.exchange.services.ejb.messages.Constants;

public class IBTopMktDataProvider implements IIBTopMktDataProvider, MessageListener{
	
	
	
	private static final Logger log = Logger.getLogger(IBTopMktDataProvider.class.getName());
	 
	
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private JMSContext jmsContext;
	private Context context;
	private JMSConsumer consumer;
	
	private HashMap<Integer, IbTopMktData> topMktDataMap=new HashMap<Integer, IbTopMktData>();
	private HashSet<IIBTopMktDataListener> ibTopMktDataListeners=new HashSet<>();
	

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
	public void registerTopMktData(IbTopMktData topMktData) {
		if(!topMktDataMap.containsKey(topMktData.getContractId()))
			topMktDataMap.put(topMktData.getContractId(), topMktData);
	}

	@Override
	public void unregisterTopMktData(IbTopMktData topMktData) {
		if(topMktDataMap.containsKey(topMktData.getContractId()))
			topMktDataMap.remove(topMktData.getContractId());

	}

	@Override
	public void onMessage(Message arg0) {
		try {
			TextMessage msg=(TextMessage) arg0;
			
			@SuppressWarnings("unchecked")
			Enumeration<String> propList= msg.getPropertyNames();
			HashMap<String, String> propMap=new HashMap<>();
			while(propList.hasMoreElements()){
				String prop=propList.nextElement();
				String value=msg.getStringProperty(prop);
				propMap.put(prop, value);
			}
			reactOnNewIbTopMktData(propMap);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private void reactOnNewIbTopMktData(HashMap<String, String> propMap){
		if( !propMap.containsKey(IbTopMktData.CONTRACT_ID))return;
		
		int contractId=Integer.parseInt(propMap.get(IbTopMktData.CONTRACT_ID));
		
		if( !topMktDataMap.containsKey(contractId))
			topMktDataMap.put(contractId, new IbTopMktData(contractId));
		
		//Reset the new data
		IbTopMktData ibTopMktData=topMktDataMap.get(contractId);
		if(propMap.containsKey(IbTopMktData.ASK)){
			ibTopMktData.setAsk(Double.parseDouble(propMap.get(IbTopMktData.ASK)));
		}
		else if(propMap.containsKey(IbTopMktData.ASK_SIZE)){
			ibTopMktData.setAskSize(Integer.parseInt(propMap.get(IbTopMktData.ASK_SIZE)));
		}
		else if(propMap.containsKey(IbTopMktData.BID)){
			ibTopMktData.setBid(Double.parseDouble(propMap.get(IbTopMktData.BID)));
		}
		else if(propMap.containsKey(IbTopMktData.BID_SIZE)){
			ibTopMktData.setBidSize(Integer.parseInt(propMap.get(IbTopMktData.BID_SIZE)));
		}
		else if(propMap.containsKey(IbTopMktData.CLOSE)){
			ibTopMktData.setClose(Double.parseDouble(propMap.get(IbTopMktData.CLOSE)));
		}
		else if(propMap.containsKey(IbTopMktData.LAST)){
			ibTopMktData.setLast(Double.parseDouble(propMap.get(IbTopMktData.LAST)));
		}
		else if(propMap.containsKey(IbTopMktData.LAST_TIME)){
			ibTopMktData.setLastTime(Long.parseLong(propMap.get(IbTopMktData.LAST_TIME)));
		}
		else if(propMap.containsKey(IbTopMktData.VOLUME)){
			ibTopMktData.setVolume(Integer.parseInt(propMap.get(IbTopMktData.VOLUME)));
		}
		
		
		//Send the new top mkt data
		for(IIBTopMktDataListener listener:ibTopMktDataListeners){
			listener.ibTopMktDataChanged(ibTopMktData);
		}
		
		
	}
	
	
	@Override
	public void addIbTopMktDataListener(IIBTopMktDataListener listener) {
		this.ibTopMktDataListeners.add(listener);
	}

	@Override
	public void removeIbTopMktDataListener(IIBTopMktDataListener listener) {
		this.ibTopMktDataListeners.remove(listener);
	}

	

}
