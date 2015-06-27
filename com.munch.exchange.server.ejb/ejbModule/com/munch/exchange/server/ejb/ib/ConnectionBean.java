package com.munch.exchange.server.ejb.ib;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.ib.controller.ApiController;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController.IConnectionHandler;

/**
 * Session Bean implementation class ConnectionBean
 */
@Singleton
@LocalBean
@Startup
public class ConnectionBean implements IConnectionHandler,ILogger{
	
	static ConnectionBean INSTANCE;
	
	private static final Logger log = Logger.getLogger(ConnectionBean.class.getName());
	
	private final ApiController m_controller = new ApiController( this, this, this);
	
	
	
    /**
     * Default constructor. 
     */
    public ConnectionBean() {
    	log.info("Connection Bean is started");
    	INSTANCE=this;
    }
    
    @PostConstruct
    private void startup() {
    	log.info("Startup Bean is starting");
    	log.info("Try to connect again");
    	m_controller.connect( "127.0.0.1", 7496, 1);
    }
    
    public ApiController controller() 		{ return m_controller; }
	
    

	@Override
	public void connected() {
		log.info("Connected!");
	}

	@Override
	public void disconnected() {
		log.info("Disconnected!");
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
