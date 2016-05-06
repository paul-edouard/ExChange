package com.munch.exchange.services.ejb.providers;

import java.util.HashSet;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarListener;
import com.munch.exchange.services.ejb.interfaces.IIBRealTimeBarProvider;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataListener;
import com.munch.exchange.services.ejb.messages.Constants;

import javafx.scene.layout.BackgroundRepeat;

public class IBRealTimeBarProvider implements IIBRealTimeBarProvider,
		MessageListener {
	
	private static final Logger log = Logger.getLogger(IBRealTimeBarProvider.class.getName());
	 
	
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private JMSContext jmsContext;
	private Context context;
	private JMSConsumer consumer;
	
	private HashSet<IIBRealTimeBarListener> listeners=new HashSet<>();
	

	@Override
	public void onMessage(Message arg0) {
		try {
			ObjectMessage msg=(ObjectMessage) arg0;
			
			int contractId=msg.getIntProperty(IbTopMktData.CONTRACT_ID);
			String whatToShowString=msg.getStringProperty(IbTopMktData.WHAT_TO_SHOW);
			
			log.info("Contract: "+contractId);
			log.info("whatToShowString: "+whatToShowString);
			log.info("Bar: "+msg.getObject().getClass().getSimpleName());
			
//			ExBar bar=(ExBar) msg.getObject();
//			bar.setRealTime(true);
//			
//			for(IIBRealTimeBarListener listener:listeners){
//				if(listener.getContractId()!=contractId)continue;
//				listener.realTimeBarChanged(bar, BarUtils.getWhatToShowFromString(whatToShowString));
//			}
			
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		try {
			context = new InitialContext(Constants.getContextProperties());
			
			connectionFactory = (ConnectionFactory) context.lookup(Constants.DEFAULT_CONNECTION_FACTORY);
	        destination = (Destination) context.lookup(Constants.REAL_TIME_BAR);
	        
	        jmsContext=connectionFactory.createContext(Constants.DEFAULT_USERNAME, Constants.DEFAULT_PASSWORD);
	        jmsContext.setClientID(Constants.REAL_TIME_BAR_CLIENT_ID);
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
		log.info("Close Real time bar consumer");
		if(consumer!=null)
			consumer.close();
		if(jmsContext!=null)
			jmsContext.close();
	}

	@Override
	public void addIbRealTimeBarListener(IIBRealTimeBarListener listener) {
		if(consumer==null)init();
		this.listeners.add(listener);
	}

	@Override
	public void removeRealTimeBarListener(IIBRealTimeBarListener listener) {
		this.listeners.remove(listener);
	}

}
