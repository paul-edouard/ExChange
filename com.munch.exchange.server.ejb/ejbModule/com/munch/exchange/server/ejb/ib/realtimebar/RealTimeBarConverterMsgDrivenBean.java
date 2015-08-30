package com.munch.exchange.server.ejb.ib.realtimebar;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.model.core.ib.bar.IbBar;
import com.munch.exchange.server.ejb.ib.Constants;
import com.munch.exchange.server.ejb.ib.historicaldata.HistoricalDataMsgDrivenBean;
import com.munch.exchange.server.ejb.ib.topmktdata.TopMktDataMsgSender;
import com.munch.exchange.services.ejb.interfaces.IIBTopMktDataListener;

/**
 * Message-Driven Bean implementation class for: RealTimeBarConverter
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = Constants.JMS_TOPIC_MARKET_DATA),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
				@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
		},mappedName = Constants.JMS_TOPIC_MARKET_DATA)

public class RealTimeBarConverterMsgDrivenBean implements MessageListener {
	
	private static final Logger log = Logger.getLogger(RealTimeBarConverterMsgDrivenBean.class.getName());
	
	
	
	
    /**
     * Default constructor. 
     */
    public RealTimeBarConverterMsgDrivenBean() {
       
    }
	
   
    
    
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
    	Calendar recievedDate=Calendar.getInstance();
    	
    	
    	
    	
    	try {
    	
    	TextMessage msg = null;
    	if (message instanceof TextMessage){
    		msg = (TextMessage) message;
    		
    		@SuppressWarnings("unchecked")
			Enumeration<String> propList= msg.getPropertyNames();
			HashMap<String, String> propMap=new HashMap<>();
			while(propList.hasMoreElements()){
				String prop=propList.nextElement();
				String value=msg.getStringProperty(prop);
				propMap.put(prop, value);
			}
			reactOnNewIbTopMktData(propMap,recievedDate);
    	}
    	
    	} catch (JMSException e) {
			e.printStackTrace();
		}
        
    }
    
    private void reactOnNewIbTopMktData(HashMap<String, String> propMap,Calendar recievedDate){
    	
		if( !propMap.containsKey(IbTopMktData.CONTRACT_ID))return;
		
		int contractId=Integer.parseInt(propMap.get(IbTopMktData.CONTRACT_ID));
		List<IbBar> bars =new LinkedList<IbBar>();
		//Reset the new data
		if(propMap.containsKey(IbTopMktData.ASK)){
			double value=Double.parseDouble(propMap.get(IbTopMktData.ASK));
			IbBar barAsk=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.ASK,
					recievedDate,
					value);
			IbBar barBid=RealTimeBarCollector.INSTANCE.getBar(contractId, WhatToShow.BID);
			double midValue=value;
			if(barBid.getClose()>0)
				midValue=(barAsk.getClose()+barBid.getClose())/2;
			IbBar barMid=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.MIDPOINT,
					recievedDate,
					midValue);
			
			bars.add(barAsk);
			bars.add(barMid);
			RealTimeBarCollector.INSTANCE.sendUpdatedBars(bars, contractId);
			//log.info("Contract: "+contractId+", Ask: "+barAsk+", Mid: "+barMid);
			
			
		}
		else if(propMap.containsKey(IbTopMktData.BID)){
			double value=Double.parseDouble(propMap.get(IbTopMktData.BID));
			
			IbBar barBid=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.BID,
					recievedDate,
					value);
			IbBar barAsk=RealTimeBarCollector.INSTANCE.getBar(contractId, WhatToShow.ASK);
			double midValue=value;
			if(barAsk.getClose()>0)
				midValue=(barAsk.getClose()+barBid.getClose())/2;
			IbBar barMid=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.MIDPOINT,
					recievedDate,
					midValue);
			
			bars.add(barBid);
			bars.add(barMid);
			RealTimeBarCollector.INSTANCE.sendUpdatedBars(bars, contractId);
			
			
			//log.info("Contract: "+contractId+", Bid: "+barBid+", Mid: "+barMid);
			
			
		}
		else if(propMap.containsKey(IbTopMktData.LAST)){
			double value=Double.parseDouble(propMap.get(IbTopMktData.LAST));
			
			IbBar bar=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.TRADES,
					recievedDate,
					value);
			bars.add(bar);
			RealTimeBarCollector.INSTANCE.sendUpdatedBars(bars, contractId);
			//log.info("Contract: "+contractId+", Tade: "+bar);
			
		}
		
	}

}
