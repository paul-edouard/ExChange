package com.munch.exchange.server.ejb.ib.realtimebar;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.ib.controller.Types.WhatToShow;
import com.munch.exchange.model.core.ib.IbTopMktData;
import com.munch.exchange.model.core.ib.bar.ExBar;
import com.munch.exchange.server.ejb.ib.Constants;

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
//		List<ExBar> bars =new LinkedList<ExBar>();
		//Reset the new data
		if(propMap.containsKey(IbTopMktData.ASK)){
			double value=Double.parseDouble(propMap.get(IbTopMktData.ASK));
			ExBar barAsk=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.ASK,
					recievedDate,
					value);
			if(barAsk==null)return;
			ExBar barBid=RealTimeBarCollector.INSTANCE.getBar(contractId, WhatToShow.BID);
			double midValue=value;
			if(barBid.getClose()>0)
				midValue=(barAsk.getClose()+barBid.getClose())/2;
			ExBar barMid=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.MIDPOINT,
					recievedDate,
					midValue);
			
			
			RealTimeBarCollector.INSTANCE.sendUpdatedBar(barAsk, contractId,WhatToShow.ASK);
			RealTimeBarCollector.INSTANCE.sendUpdatedBar(barMid, contractId,WhatToShow.MIDPOINT);
			//log.info("Contract: "+contractId+", Ask: "+barAsk+", Mid: "+barMid);
			
			
		}
		else if(propMap.containsKey(IbTopMktData.BID)){
			double value=Double.parseDouble(propMap.get(IbTopMktData.BID));
			
			ExBar barBid=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.BID,
					recievedDate,
					value);
			if(barBid==null)return;
			ExBar barAsk=RealTimeBarCollector.INSTANCE.getBar(contractId, WhatToShow.ASK);
			double midValue=value;
			if(barAsk.getClose()>0)
				midValue=(barAsk.getClose()+barBid.getClose())/2;
			ExBar barMid=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.MIDPOINT,
					recievedDate,
					midValue);
			
			
			RealTimeBarCollector.INSTANCE.sendUpdatedBar(barBid, contractId, WhatToShow.BID);
			RealTimeBarCollector.INSTANCE.sendUpdatedBar(barMid, contractId, WhatToShow.MIDPOINT);
			
			
			//log.info("Contract: "+contractId+", Bid: "+barBid+", Mid: "+barMid);
			
			
		}
		else if(propMap.containsKey(IbTopMktData.LAST)){
			double value=Double.parseDouble(propMap.get(IbTopMktData.LAST));
			
			ExBar bar=RealTimeBarCollector.INSTANCE.updateBar(contractId,
					WhatToShow.TRADES,
					recievedDate,
					value);
//			bars.add(bar);
			if(bar==null)return;
			RealTimeBarCollector.INSTANCE.sendUpdatedBar(bar, contractId, WhatToShow.TRADES);
			//log.info("Contract: "+contractId+", Tade: "+bar);
			
		}
		
	}

}
