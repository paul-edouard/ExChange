package com.munch.exchange.server.ejb.ib;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.munch.exchange.server.ejb.ib.account.AccountManager;
import com.munch.exchange.server.ejb.ib.realtimebar.RealTimeBarCollector;
import com.munch.exchange.server.ejb.ib.topmktdata.TopMktDataMsgSenderCollector;

/**
 * Session Bean implementation class ConnectionBean
 */
@Singleton
@LocalBean
@Startup
public class ConnectionBean implements IConnectionHandler, ILogger {

	public static ConnectionBean INSTANCE;

	private static final Logger log = Logger.getLogger(ConnectionBean.class
			.getName());

	private final ApiController m_controller = new ApiController(this, this,
			this);
	
	private boolean connected=false;
	
	
	@PersistenceContext
	private EntityManager em;

	@Resource(mappedName = Constants.JMS_CONNECTION_FACTORY)
	private ConnectionFactory connectionFactory;

	@Resource(lookup = Constants.JMS_TOPIC_MARKET_DATA)
	private Topic destination;

	@Resource(lookup = Constants.JMS_TOPIC_REAL_TIME_BAR)
	private Topic realTimeBardestination;
	
	
	/**
	 * Default constructor.
	 */
	@Inject
	public ConnectionBean() {
		log.info("Connection Bean is started");
		INSTANCE = this;
	}

	@PostConstruct
	private void startup() {
		log.info("Startup Bean is starting");
		log.info("Try to connect again");
		m_controller.connect(Constants.IB_CONNECTION_HOST,
				Constants.IB_CONNECTION_PORT, Constants.IB_CONNECTION_ID);

		// TODO remove
		log.info("Initialization of bars");

		RealTimeBarCollector.INSTANCE.init(connectionFactory,
				realTimeBardestination);

	}

	@PreDestroy
	private void shutdown() {
		log.info("Startup Bean is shuting down!");
		m_controller.disconnect();
	}

	public ApiController controller() {
		return m_controller;
	}

	// private List<IbContract> allContracts;
	@Override
	public void connected() {
		log.info("Connected!");
		
		connected=true;
		
		
		// Start the Message sender collector
		TopMktDataMsgSenderCollector.INSTANCE.init(em, connectionFactory,
				destination);
		// RealTimeBarsSenderCollector.INSTANCE.init(em, connectionFactory,
		// destination,allContracts);

	}

	@Override
	public void disconnected() {
		log.info("Disconnected!");
		
		connected=false;
		
		
		TopMktDataMsgSenderCollector.INSTANCE.clearAll();
		// RealTimeBarsSenderCollector.INSTANCE.clearAll();
	}

	@Override
	public void accountList(ArrayList<String> list) {
		show("Received account list");
		
		AccountManager.INSTANCE.createAccountListeners(list);
	}

	@Override
	public void error(Exception e) {
		e.printStackTrace();
		// TODO Auto-generated method stub

	}

	@Override
	public void message(int id, int errorCode, String errorMsg) {
		log.warning(id + " " + errorCode + " " + errorMsg);
	}

	@Override
	public void show(String string) {
		log.info(string);

	}
	
	
	

	public synchronized boolean isConnected() {
		return connected;
	}


	private String logStr = "";

	@Override
	public void log(String valueOf) {
		/*
		 * logStr+=valueOf; if(valueOf=="\n"){ log.info("LOG: "+logStr);
		 * logStr=""; }
		 */
	}

}
