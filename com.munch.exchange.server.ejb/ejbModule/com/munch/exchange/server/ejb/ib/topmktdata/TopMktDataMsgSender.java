package com.munch.exchange.server.ejb.ib.topmktdata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.ib.controller.ApiController.TopMktDataAdapter;
import com.ib.controller.NewTickType;
import com.ib.controller.Types.MktDataType;
import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.model.core.ib.bar.ExBar;

public class TopMktDataMsgSender extends TopMktDataAdapter implements PropertyChangeListener{
	
	private static final Logger log = Logger.getLogger(TopMktDataMsgSender.class.getName());
	
	public static final String MSG_HEADER="Top_Market_Data";
	
	private static final int MAX_NUMBER_OF_SAVE_BARS=3600;
	
	private ConnectionFactory connectionFactory;
	private Topic marketDataDestination;
	private Topic realTimeBarDestination;
	
	private Connection connection;
	private Session session;
	private MessageProducer marketDataMsgProducer;
	private MessageProducer realTimeBarMsgProducer;
	
	private IbContract contract;
	private IbTopMktData topMktData;
	
	private ExBar secondBidBar=new ExBar();
	private ExBar secondAskBar=new ExBar();
	private ExBar secondMidPointBar=new ExBar();
	
	
	private LinkedList<ExBar> lastSecondBidBars=new LinkedList<>();
	private LinkedList<ExBar> lastSecondAskBars=new LinkedList<>();
	private LinkedList<ExBar> lastSecondMidPointBars=new LinkedList<>();
	
	
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
	
	public TopMktDataMsgSender(IbContract contract,ConnectionFactory connectionFactory,
			Topic marketDataDestination, Topic realTimeBarDestination){
		this.contract=contract;
		this.connectionFactory=connectionFactory;
		this.marketDataDestination=marketDataDestination;
		this.realTimeBarDestination=realTimeBarDestination;
		topMktData=new IbTopMktData(this.contract);
		//log.info(String.valueOf(topMktData.getContractId()));
		topMktData.addPropertyChangeListener(this);
		
		try {
			connection=connectionFactory.createConnection();
			session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			marketDataMsgProducer=session.createProducer(marketDataDestination);
			realTimeBarMsgProducer=session.createProducer(realTimeBarDestination);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		
		log.info("Sender activated for "+contract.toString());
	}
	
	
	private void sendBar(ExBar bar, WhatToShow whatToShow){
		if(realTimeBarDestination==null || realTimeBarMsgProducer==null || connectionFactory==null){
			log.info("Smth is null!");
			return;
		}
		
		try {
    		//TextMessage msg=session.createTextMessage();
    		ObjectMessage objMsg=session.createObjectMessage();
    		objMsg.setObject(bar);
    		objMsg.setIntProperty(IbTopMktData.CONTRACT_ID, topMktData.getContractId());
    		objMsg.setStringProperty(IbTopMktData.WHAT_TO_SHOW, whatToShow.toString());
    		realTimeBarMsgProducer.send(objMsg);
//    		log.info("What to show: "+whatToShow.toString()+", new Bar: "+bar);
    		
    	
    	} catch (JMSException e) {
    		log.warning(e.toString());
    		e.printStackTrace();
    	}
		
		
	}
	
	private void sendMarketData(String field, String value){
		
		if(marketDataDestination==null || marketDataMsgProducer==null || connectionFactory==null){
			log.info("Smth is null!");
			return;
		}
		
		
		try {
			TextMessage msg=session.createTextMessage();
			msg.setText(MSG_HEADER);
			msg.setIntProperty(IbTopMktData.CONTRACT_ID, topMktData.getContractId());
			//msg.setStringProperty(ExTopMktData.CONTRACT_ID, String.valueOf(topMktData.getContractId()));
			msg.setStringProperty(field, value);
			marketDataMsgProducer.send(msg);
			//log.info("New msg!");
			
		} catch (JMSException e) {
			log.warning(e.toString());
			e.printStackTrace();
		}
	}
	
	public synchronized void flushSecondBar(long currentSecond){
//		log.info("\nflushSecondBar was called for contract: "+contract.toString());
//		No data recieved during the last 
		secondAskBar=flushSecondBar(secondAskBar, lastSecondAskBars, currentSecond, WhatToShow.ASK);
		secondBidBar=flushSecondBar(secondBidBar, lastSecondBidBars, currentSecond, WhatToShow.BID);
		secondMidPointBar=flushSecondBar(secondMidPointBar, lastSecondMidPointBars, currentSecond, WhatToShow.MIDPOINT);
		
//		log.info("End of flush\n");
	}
	
	private ExBar flushSecondBar(ExBar bar, LinkedList<ExBar> lastBars, long currentSecond, WhatToShow whatToShow){
		ExBar newBar=new ExBar();
		newBar.setTimeInMs(currentSecond);
		newBar.setCompleted(false);
		newBar.setRealTime(true);
		
		
		if(bar.getOpen()==0){
			if(lastBars.isEmpty())return newBar;
			
			ExBar last=lastBars.getLast();
			bar.setOpen(last.getClose());
			bar.setClose(last.getClose());
			bar.setHigh(last.getClose());
			bar.setLow(last.getClose());	
		}
		
//		Save the bar only if the time was set
		if(bar.getTime()!=0){
			bar.setCompleted(true);
			sendBar(bar, whatToShow);
			lastBars.addLast(bar);
			if(lastBars.size()> MAX_NUMBER_OF_SAVE_BARS)
				lastBars.removeFirst();
		}
		
		return newBar;
		
	}
	
	
	
	
	@Override public void tickPrice( NewTickType tickType, double price, int canAutoExecute) {
		switch( tickType) {
			case BID:
				topMktData.setBid(price);
				updatePrice(secondBidBar, price, WhatToShow.BID);
				break;
			case ASK:
				topMktData.setAsk(price);
				updatePrice(secondAskBar, price, WhatToShow.ASK);
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
	
	private synchronized void updatePrice(ExBar bar, double price, WhatToShow whatToShow){
		bar.setClose(price);
		
		if(bar.getOpen()==0){
			bar.setOpen(price);
			bar.setHigh(price);
			bar.setLow(price);
			updateMidPointBarPrice(bar, whatToShow);
			return;
		}
		
		if(bar.getHigh() < price){
			bar.setHigh(price);
			updateMidPointBarPrice(bar, whatToShow);
			return;
		}
		
		if(bar.getLow() > price)
			bar.setLow(price);
		
		updateMidPointBarPrice(bar, whatToShow);
	}
	
	private synchronized void updateMidPointBarPrice(ExBar bar, WhatToShow whatToShow){
		if(secondAskBar.getOpen()!=0 && secondBidBar.getOpen()!=0){
			secondMidPointBar.setClose((secondAskBar.getClose()+secondBidBar.getClose())/2);
			secondMidPointBar.setOpen((secondAskBar.getOpen()+secondBidBar.getOpen())/2);
			secondMidPointBar.setHigh((secondAskBar.getHigh()+secondBidBar.getHigh())/2);
			secondMidPointBar.setLow((secondAskBar.getLow()+secondBidBar.getLow())/2);
		}
		else if(secondAskBar.getOpen()!=0){
			secondMidPointBar.setClose(secondAskBar.getClose());
			secondMidPointBar.setOpen(secondAskBar.getOpen());
			secondMidPointBar.setHigh(secondAskBar.getHigh());
			secondMidPointBar.setLow(secondAskBar.getLow());
		}
		else if(secondBidBar.getOpen()!=0){
			secondMidPointBar.setClose(secondBidBar.getClose());
			secondMidPointBar.setOpen(secondBidBar.getOpen());
			secondMidPointBar.setHigh(secondBidBar.getHigh());
			secondMidPointBar.setLow(secondBidBar.getLow());
		}
		
		sendBar(bar, whatToShow);
		sendBar(secondMidPointBar, WhatToShow.MIDPOINT);
		
	}
	
	

	@Override public void tickSize( NewTickType tickType, int size) {
		switch( tickType) {
			case BID_SIZE:
				topMktData.setBidSize(size);
				updateSize(secondBidBar, size, WhatToShow.BID);
				break;
			case ASK_SIZE:
				topMktData.setAskSize(size);
				updateSize(secondAskBar, size, WhatToShow.ASK);
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
	
	private synchronized void updateSize(ExBar bar, double size, WhatToShow whatToShow){
		bar.setVolume((long) size + bar.getVolume());
		
		secondMidPointBar.setVolume((secondAskBar.getVolume()+secondBidBar.getVolume())/2);
		
		
//		sendBar(bar, whatToShow);
//		sendBar(secondMidPointBar, WhatToShow.MIDPOINT);
		
	}
	
	
	
	@Override public void tickString(NewTickType tickType, String value) {
		switch( tickType) {
			case LAST_TIMESTAMP:
				topMktData.setLastTime(Long.parseLong( value) * 1000);
//				Calendar cal=Calendar.getInstance();
//				log.info("Recieved Bar: "+BarUtils.format(Long.parseLong( value) * 1000)+", current time: "+BarUtils.format(cal.getTimeInMillis()));
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
		
		this.sendMarketData(field, value);	
	}
	
	
	
}
