package com.munch.exchange.server.ejb.ib;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ib.controller.ApiController;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController.IConnectionHandler;
import com.munch.exchange.model.core.ib.ExContract;
import com.munch.exchange.server.ejb.ib.collectors.TopMktDataMsgSenderCollector;

/**
 * Session Bean implementation class ConnectionBean
 */
@Singleton
@LocalBean
@Startup
public class ConnectionBean implements IConnectionHandler,ILogger{
	
	public static ConnectionBean INSTANCE;
	
	
	private static final Logger log = Logger.getLogger(ConnectionBean.class.getName());
	
	private final ApiController m_controller = new ApiController( this, this, this);
	
	@PersistenceContext
	private EntityManager em;
	
	//@Resource(mappedName = "java:jboss/DefaultJMSConnectionFactory")
	@Resource(mappedName =Constants.JMS_CONNECTION_FACTORY)
	private ConnectionFactory connectionFactory;
	
	//@Resource(lookup="java:/jms/topic/demoTopic")
	@Resource(lookup=Constants.JMS_TOPIC_MARKET_DATA)
	private Topic destination;
	
	
    /**
     * Default constructor. 
     */
	@Inject
    public ConnectionBean() {
    	log.info("Connection Bean is started");
    	INSTANCE=this;
    }
    
    @PostConstruct
    private void startup() {
    	log.info("Startup Bean is starting");
    	log.info("Try to connect again");
    	m_controller.connect( 	Constants.IB_CONNECTION_HOST,
    							Constants.IB_CONNECTION_PORT,
    							Constants.IB_CONNECTION_ID);
    }
    
    @PreDestroy
    private void shutdown() {
    	log.info("Startup Bean is shuting down!");
    	m_controller.disconnect();
    }
    
    public ApiController controller() 		{ return m_controller; }
	
    

	@Override
	public void connected() {
		log.info("Connected!");
		//Start the Message sender collector
		TopMktDataMsgSenderCollector.INSTANCE.init(em,connectionFactory,destination);
	}

	@Override
	public void disconnected() {
		log.info("Disconnected!");
		TopMktDataMsgSenderCollector.INSTANCE.clearAll();
	}

	@Override
	public void accountList(ArrayList<String> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		log.warning(id + " " + errorCode + " " + errorMsg);
	}

	@Override
	public void show(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(String valueOf) {
		//log.info(valueOf);	
	}

}
