package com.munch.exchange.services.ejb.messages;

import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Logger;

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

import com.munch.exchange.model.core.ib.IbTopMktData;

public class TopMktDataClient implements MessageListener {
	
	
	private static final Logger log = Logger.getLogger(TopMktDataClient.class.getName());
	 
	
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private Context context;
	private JMSConsumer consumer;
	
	
	public TopMktDataClient(){
		try {
			context = new InitialContext(Constants.getContextProperties());
			
			//log.info("Attempting to acquire connection factory \"" + Constants.DEFAULT_CONNECTION_FACTORY + "\"");
	        connectionFactory = (ConnectionFactory) context.lookup(Constants.DEFAULT_CONNECTION_FACTORY);
	        log.info("Found connection factory \"" + Constants.DEFAULT_CONNECTION_FACTORY + "\" in JNDI");
	        
	        //log.info("Attempting to acquire destination \"" + Constants.TOP_MARKET_DATA + "\"");
	        destination = (Destination) context.lookup(Constants.TOP_MARKET_DATA);
	        log.info("Found destination \"" + Constants.TOP_MARKET_DATA + "\" in JNDI");
	        
	        JMSContext jmsContext=connectionFactory.createContext(Constants.DEFAULT_USERNAME, Constants.DEFAULT_PASSWORD);
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
 	public void onMessage(Message arg0) {
		try {
			TextMessage msg=(TextMessage) arg0;
			Enumeration<String> propList= msg.getPropertyNames();
			String content="";
			while(propList.hasMoreElements()){
				String prop=propList.nextElement();
				String value=msg.getStringProperty(prop);
				content+=prop+"="+value+", ";
			}
			
			//System.out.println(content);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void readIn(){
		System.out.println("Type exit to close!");
		
		@SuppressWarnings("resource")
    	Scanner scanner = new Scanner(System.in);
		while(true){
    		String in=scanner.nextLine();
    		if(in.equals("exit"))return;
    	}
	}
	
	
	public static void main(String[] args) throws JMSException, NamingException{
		TopMktDataClient client=new TopMktDataClient();
		client.readIn();
	}

}
