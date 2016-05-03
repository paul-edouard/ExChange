package com.munch.exchange.server.ejb.ib.topmktdata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.ib.controller.ApiController.TopMktDataAdapter;
import com.ib.controller.NewTickType;
import com.ib.controller.Types.MktDataType;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.IbTopMktData;

public class TopMktDataMsgSender extends TopMktDataAdapter implements PropertyChangeListener{
	
	private static final Logger log = Logger.getLogger(TopMktDataMsgSender.class.getName());
	
	public static final String MSG_HEADER="Top_Market_Data";
	
	ConnectionFactory connectionFactory;
	Topic destination;
	
	private Connection connection;
	private Session session;
	private MessageProducer msgProducer;
	
	private IbContract contract;
	private IbTopMktData topMktData;
	
	/*
	private double m_bid;
	private double m_ask;
	private double m_last;
	private long m_lastTime;
	private int m_bidSize;
	private int m_askSize;
	private double m_close;
	private int m_volume;
	private boolean m_frozen;
	*/
	
	public TopMktDataMsgSender(IbContract contract,ConnectionFactory connectionFactory,Topic destination){
		this.contract=contract;
		this.connectionFactory=connectionFactory;
		this.destination=destination;
		topMktData=new IbTopMktData(this.contract);
		//log.info(String.valueOf(topMktData.getContractId()));
		topMktData.addPropertyChangeListener(this);
		
		try {
			connection=connectionFactory.createConnection();
			session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			msgProducer=session.createProducer(destination);	
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		
		log.info("Sender activated for "+contract.toString());
	}
	
	/*
	private String createMsgText(){
		String txt="Bid: "+m_bid+", Ask: "+m_ask;
		txt+=", Close: "+m_close+", ";
		
		return txt;
	}
	*/
	
	private void sendMessage(String field, String value){
		
		if(destination==null || msgProducer==null || connectionFactory==null){
			log.info("Smth is null!");
			return;
		}
		
		
		try {
			TextMessage msg=session.createTextMessage();
			msg.setText(MSG_HEADER);
			msg.setIntProperty(IbTopMktData.CONTRACT_ID, topMktData.getContractId());
			//msg.setStringProperty(ExTopMktData.CONTRACT_ID, String.valueOf(topMktData.getContractId()));
			msg.setStringProperty(field, value);
			msgProducer.send(msg);
			//log.info("New msg!");
			
		} catch (JMSException e) {
			log.warning(e.toString());
			e.printStackTrace();
		}
	}
	
	@Override public void tickPrice( NewTickType tickType, double price, int canAutoExecute) {
		switch( tickType) {
			case BID:
				topMktData.setBid(price);
				break;
			case ASK:
				topMktData.setAsk(price);
				break;
			case LAST:
				topMktData.setLast(price);
				break;
			case CLOSE:
				topMktData.setClose(price);
				break;
			
			default:
				break;
		}
//		sendMessage("Tick Price");
//		log.info("Tick Price");
		//m_model.fireTableDataChanged(); // should use a timer to be more efficient
	}

	@Override public void tickSize( NewTickType tickType, int size) {
		switch( tickType) {
			case BID_SIZE:
				topMktData.setBidSize(size);
				break;
			case ASK_SIZE:
				topMktData.setAskSize(size);
				break;
			case VOLUME:
				topMktData.setVolume(size);
				break;
			default:
				break;
		}
//		log.info("Tick Size");
//		sendMessage("Tick Size");
		//m_model.fireTableDataChanged();
	}
	
	@Override public void tickString(NewTickType tickType, String value) {
		switch( tickType) {
			case LAST_TIMESTAMP:
				topMktData.setLastTime(Long.parseLong( value) * 1000);
				//Calendar cal=Calendar.getInstance();
				//log.info("Recieved Bar: "+IbBar.format(Long.parseLong( value) * 1000)+", current time: "+IbBar.format(cal.getTimeInMillis()));
				break;
			default:
				break;
		}
	}
	
	@Override public void marketDataType(MktDataType marketDataType) {
		boolean m_frozen = marketDataType == MktDataType.Frozen;
		topMktData.setFrozen(m_frozen);
		//sendMessage("Market Data Type");
		//m_model.fireTableDataChanged();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String field=evt.getPropertyName();
		String value=String.valueOf(evt.getNewValue());
		
//		log.info("field: "+field+", value: "+value);
		
		this.sendMessage(field, value);	
	}
	
	
	
}
