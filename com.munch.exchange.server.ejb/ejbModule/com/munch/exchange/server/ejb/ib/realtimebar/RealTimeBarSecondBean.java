package com.munch.exchange.server.ejb.ib.realtimebar;

import java.util.Calendar;
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

import com.munch.exchange.model.core.ib.bar.BarUtils;
import com.munch.exchange.server.ejb.ib.topmktdata.TopMktDataMsgSenderCollector;

/**
 * Session Bean implementation class RealTimeBarSecondBean
 */
@Singleton
@LocalBean
@Startup
public class RealTimeBarSecondBean {
	
	private static final Logger log = Logger.getLogger(RealTimeBarSecondBean.class.getName());
	
	private static final String TIMER_STRING	=	"Second Bar timout";
	
	private static final long TIMER_INTERVALL	=	1000;
	private static final long TIMER_OFFSET		=	5*1000;
	
	
	@Resource
	TimerService timerService;
	

    /**
     * Default constructor. 
     */
    public RealTimeBarSecondBean() {
    }
    
    @PostConstruct
    private void startup() {
    	log.info("Real Time Bar Second Bean is starting!");
    	
    	//Current Date + 5s
    	Calendar currentTime=Calendar.getInstance();
    	currentTime.set(Calendar.MILLISECOND, 0);
    	Date date=new Date();
    	date.setTime(currentTime.getTimeInMillis()+TIMER_OFFSET);
    	
    	//TODO reset the timer
    	timerService.createTimer(date, TIMER_INTERVALL, TIMER_STRING);
    }
    
    @PreDestroy
    private void shutdown() {
    	log.info("Real Time Bar Second Bean is shuting down!");
    	Collection<Timer> timers=timerService.getTimers();
    	for(Timer timer:timers){
    		if(TIMER_STRING.equals(timer.getInfo())){
    			timer.cancel();
    		}
    	}
    }
    
    
    @Timeout
    public void sendTimerMessage(Timer timer){
    	
		Calendar currentSecond=Calendar.getInstance();
		currentSecond.set(Calendar.MILLISECOND, 0);

		if (!(timer.getInfo() instanceof String))
			return;

		String info = (String) timer.getInfo();
		if (!info.equals(TIMER_STRING))
			return;
		
//		Flush all the second bars
		TopMktDataMsgSenderCollector.INSTANCE.flushSecondBars(currentSecond.getTimeInMillis());
		
		// TextMessage textMessage=session.createTextMessage();

//		String msgStr = timer.getInfo().toString() + ": " + BarUtils.msFormat(time);
//		log.info("New Timer Message: " + msgStr + " with info: " + info);
    	
   
    }

}
