package com.munch.exchange.server.ejb.ib.adapter;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;

import com.ib.controller.NewTickType;
import com.ib.controller.ApiController.TopMktDataAdapter;
import com.ib.controller.Types.MktDataType;
import com.munch.exchange.model.core.ib.ExContract;

public class TopMktDataMsgSender extends TopMktDataAdapter{
	
	private static final Logger log = Logger.getLogger(TopMktDataMsgSender.class.getName());
	
	
	
	ConnectionFactory connectionFactory;
	Topic destination;
	
	private Connection connection;
	private Session session;
	private MessageProducer msgProducer;
	
	private ExContract contract;
	
	private double m_bid;
	private double m_ask;
	private double m_last;
	private long m_lastTime;
	private int m_bidSize;
	private int m_askSize;
	private double m_close;
	private int m_volume;
	private boolean m_frozen;
	
	
	public TopMktDataMsgSender(ExContract contract,ConnectionFactory connectionFactory,Topic destination){
		this.contract=contract;
		this.connectionFactory=connectionFactory;
		this.destination=destination;
		
		try {
			connection=connectionFactory.createConnection();
			session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			msgProducer=session.createProducer(destination);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMessage("Hallo");
		
	}
	
	private String createMsgText(){
		String txt="Bid: "+m_bid+", Ask: "+m_ask;
		txt+=", Close: "+m_close+", ";
		
		return txt;
	}
	
	private void sendMessage(String name){
		
		if(destination==null || msgProducer==null || connectionFactory==null){
			log.info("Smth is null!");
			return;
		}
		
		
		try {
			TextMessage msg=session.createTextMessage();
			String txt=createMsgText();
			msg.setText(txt);
			msg.setStringProperty("NAME", name);
			msgProducer.send(msg);
			//log.info("Message send: "+txt);
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override public void tickPrice( NewTickType tickType, double price, int canAutoExecute) {
		switch( tickType) {
			case BID:
				m_bid = price;
				break;
			case ASK:
				m_ask = price;
				break;
			case LAST:
				m_last = price;
				break;
			case CLOSE:
				m_close = price;
				break;
		}
		sendMessage("Tick Price");
		//m_model.fireTableDataChanged(); // should use a timer to be more efficient
	}

	@Override public void tickSize( NewTickType tickType, int size) {
		switch( tickType) {
			case BID_SIZE:
				m_bidSize = size;
				break;
			case ASK_SIZE:
				m_askSize = size;
				break;
			case VOLUME:
				m_volume = size;
				break;
		}
		sendMessage("Tick Size");
		//m_model.fireTableDataChanged();
	}
	
	@Override public void tickString(NewTickType tickType, String value) {
		switch( tickType) {
			case LAST_TIMESTAMP:
				m_lastTime = Long.parseLong( value) * 1000;
				break;
		}
	}
	
	@Override public void marketDataType(MktDataType marketDataType) {
		m_frozen = marketDataType == MktDataType.Frozen;
		sendMessage("Market Data Type");
		//m_model.fireTableDataChanged();
	}
	
	
	
}
