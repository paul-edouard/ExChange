package com.munch.exchange.server.ejb.ib.realtimebar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import com.ib.controller.ApiController.IRealTimeBarHandler;
import com.ib.controller.Types.WhatToShow;
import com.ib.controller.Bar;
import com.munch.exchange.model.core.ib.IbContract;

public class RealTimeBarsSender implements IRealTimeBarHandler,
		PropertyChangeListener {
	
	private static final Logger log = Logger.getLogger(RealTimeBarsSender.class.getName());
	
	ConnectionFactory connectionFactory;
	Topic destination;
	
	private Connection connection;
	private Session session;
	private MessageProducer msgProducer;
	
	
	private IbContract contract;
	private WhatToShow whatToShow;
	
	public RealTimeBarsSender(IbContract contract,WhatToShow whatToShow, ConnectionFactory connectionFactory,Topic destination){
		this.contract=contract;
		this.whatToShow=whatToShow;
		this.connectionFactory=connectionFactory;
		this.destination=destination;
		
		log.info("RealTimeBars Sender activated for "+contract.getLongName());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void realtimeBar(Bar bar) {
		log.info("New Bar from "+contract.getLongName()+", "+whatToShow.name()+", "+bar);

	}

}
