package com.munch.exchange.server.ejb.ib;

public class Constants {
	
	//Interactive Broker Connection
	public static final String 	IB_CONNECTION_HOST="127.0.0.1";
	public static final int 	IB_CONNECTION_PORT=7496;
	public static final int 	IB_CONNECTION_ID=1;
	
	
	//Topics
	public static final String JMS_CONNECTION_FACTORY="java:jboss/DefaultJMSConnectionFactory";
	public static final String JMS_DEMO_TOPIC="java:/jms/topic/demoTopic";
	public static final String JMS_TOPIC_EXCHANGE="java:/jms/topic/exChangeTopic";
	public static final String JMS_TOPIC_MARKET_DATA="java:/jms/topic/MktData";
	

}
