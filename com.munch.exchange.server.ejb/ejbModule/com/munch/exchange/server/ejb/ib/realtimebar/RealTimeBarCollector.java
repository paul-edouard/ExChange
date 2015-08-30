package com.munch.exchange.server.ejb.ib.realtimebar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.Connection;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.model.core.ib.bar.IbMinuteBar;

public enum RealTimeBarCollector {
	
	INSTANCE;
	
	private static final Logger log = Logger.getLogger(RealTimeBarsSenderCollector.class.getName());
	
	
	private HashMap<Integer, HashMap<WhatToShow, IbBar>> barMap=new HashMap<>();
	
	
	//private ConnectionFactory connectionFactory;
	//private Topic destination;
	
	private Connection connection;
	private Session session;
	private MessageProducer msgProducer;
	
	private HashMap<WhatToShow, IbBar> getBarFromContract(int contractId){
		if(!barMap.containsKey(contractId)){
			barMap.put(contractId, new HashMap<WhatToShow, IbBar>());
		}
    	return barMap.get(contractId);
    }
	
	
	public void init(ConnectionFactory connectionFactory, Topic destination){
		//this.connectionFactory=connectionFactory;
		//this.destination=destination;
		
		try {
			connection=connectionFactory.createConnection();
			session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			msgProducer=session.createProducer(destination);	
		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}	
		
	}
	
	public synchronized void sendUpdatedBars(List<IbBar> bars, int contractId){
		
		if(session==null || msgProducer==null ){
    		return;
    	}
    	
		
		try {
    		//TextMessage msg=session.createTextMessage();
    		for(IbBar bar:bars){
    			ObjectMessage objMsg=session.createObjectMessage();
    			objMsg.setObject(bar);
    			objMsg.setIntProperty(IbTopMktData.CONTRACT_ID, contractId);
    			msgProducer.send(objMsg);
    			//log.info("Send new Msg: "+bar);
    		}
    	
    	} catch (JMSException e) {
    		log.warning(e.toString());
    		e.printStackTrace();
    	}
	}
	
	
	public IbBar getBar(int contractId,WhatToShow whatToShow){
		HashMap<WhatToShow, IbBar> contractBars=getBarFromContract(contractId);
		if(!contractBars.containsKey(whatToShow)){
			IbBar bar=new IbMinuteBar();
			bar.setType(whatToShow);
			contractBars.put(whatToShow, bar);
		}
		
		return contractBars.get(whatToShow);
	}
	
	
	public IbBar updateBar(int contractId,WhatToShow whatToShow,Calendar recievedDate,double value){
		IbBar bar=getBar(contractId, whatToShow);
		
		//log.info("Recieved Time: "+recievedDate.getTimeInMillis());
		//log.info("Intervall Time: "+bar.getIntervallInMs());
		//log.info("Modulo: "+recievedDate.getTimeInMillis()%bar.getIntervallInMs());
		
		long time=(recievedDate.getTimeInMillis()/bar.getIntervallInMs())*bar.getIntervallInSec();
		
		//log.info("Time: "+time);
		
		if(time==bar.getTime()){
			bar.setClose(value);
			if(bar.getClose()>bar.getHigh())
				bar.setHigh(bar.getClose());
			if(bar.getClose()<bar.getLow())
				bar.setLow(bar.getClose());
		}
		else{
			bar.setTime(time);
			if(bar.getClose()>0)
				bar.setOpen(bar.getClose());
			else
				bar.setOpen(value);
			bar.setClose(value);
			bar.setHigh(value);
			bar.setLow(value);
		}
		
		return bar;
	}
	
	

}
