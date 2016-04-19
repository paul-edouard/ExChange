package com.munch.exchange;

import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;


/**
 * Session Bean implementation class TestStartBean
 */
@Singleton
@LocalBean
@Startup
public class TestStartBean {
	
	private static final Logger log = Logger.getLogger(TestStartBean.class.getName());
	

    /**
     * Default constructor. 
     */
    public TestStartBean() {
        // TODO Auto-generated constructor stub
    	log.info("Hallo ich bin started coucou Paul endlich was neues!!!!  222222222222");
    }

}
