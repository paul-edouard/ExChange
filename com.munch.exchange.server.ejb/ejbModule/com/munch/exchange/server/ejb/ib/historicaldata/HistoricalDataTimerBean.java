package com.munch.exchange.server.ejb.ib.historicaldata;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.munch.exchange.server.ejb.ib.Constants;

/**
 * Session Bean implementation class HistoricalDataTimerBean
 */

@Singleton
@LocalBean
@Startup
public class HistoricalDataTimerBean {
	
	private static final Logger log = Logger.getLogger(HistoricalDataTimerBean.class.getName());
	
	private static final String TIMER_STRING	=	"Historical Data timout";
	public static final String TIME_STRING		=	"TIME";
	
	private static final long TIMER_INTERVALL	=	11*60*1000;
//	private static final long TIMER_INTERVALL	=	10*1000;
	private static final long TIMER_OFFSET		=	10*60*1000;
//	private static final long TIMER_OFFSET		=	10*1000;
	
	
	@Resource(mappedName =Constants.JMS_CONNECTION_FACTORY)
	private ConnectionFactory connectionFactory;
	
	@Resource(lookup=Constants.JMS_TOPIC_HISTORICAL_DATA)
	private Topic destination;
	
	@Resource
	TimerService timerService;
	
	
	private Connection connection;
	
	private Session session;
	
	private MessageProducer msgProducer;
	
	private long lastMsgSend=0;

    /**
     * Default constructor. 
     */
    public HistoricalDataTimerBean() {
    }
    
    
    @PostConstruct
    private void startup() {
    	log.info("Historical Data Timer Bean is starting!");
    	
    	try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			msgProducer=session.createProducer(destination);
			
		} catch (JMSException e) {
			e.printStackTrace();
			log.warning(e.toString());
		}
    	
    	
    	//Current Date + 5s
    	Date date=new Date();
    	date.setTime(date.getTime()+TIMER_OFFSET);
    	
    	//TODO reset the timer
    	timerService.createTimer(date, TIMER_INTERVALL, TIMER_STRING);
    	
    }
    
    
    @PreDestroy
    private void shutdown() {
    	log.info("Historical Data Timer Bean is shuting down!");
    	Collection<Timer> timers=timerService.getTimers();
    	for(Timer timer:timers){
    		if(TIMER_STRING.equals(timer.getInfo())){
    			timer.cancel();
    		}
    	}
    	
    	while(HistoricalDataLoaders.INSTANCE!=null && HistoricalDataLoaders.INSTANCE.isLoading()){
//    		log.info("Wait of the ");
    		try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
    }
    
    @Timeout
    public void sendTimerMessage(Timer timer){
    	try {
    		if(!(timer.getInfo() instanceof String))return;
    		
    		String info=(String)timer.getInfo();
    		if(!info.equals(TIMER_STRING))return;
    		
			TextMessage textMessage=session.createTextMessage();
			long time=new Date().getTime();
			if(lastMsgSend>0 && (time-lastMsgSend)<TIMER_INTERVALL/2)return;
			lastMsgSend=time;
			
			String msgStr=timer.getInfo().toString()+": "+new Date().getTime();
			log.info("New Timer Message: "+msgStr+ " with info: "+info);
			textMessage.setText(msgStr);
			textMessage.setLongProperty(TIME_STRING,time );
			msgProducer.send(textMessage);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warning(e.toString());
		}
    	
    }
    
    

}
