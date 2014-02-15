package com.munch.exchange.services.internal.fred;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.client.RestTemplate;

public enum FredContext {

	INSTANCE;

	static final String FRED_REST_TEMPLATE_ID = "fredRestTemplate";

	private final ApplicationContext context = new FileSystemXmlApplicationContext(
			"resources/spring/application-context.xml");

	private RestTemplate restTemplate = null;

	private static Logger logger = Logger
			.getLogger(FredContext.class);
	
	
	@Before
	private void setUp() throws Exception {
		restTemplate = (RestTemplate) context.getBean(FRED_REST_TEMPLATE_ID);
	}

	@After
	public void tearDown() throws Exception {
		restTemplate = null;
	}
	
	
	public RestTemplate getRestTemplate(){
		if(restTemplate==null){
			try {
				setUp();
			} catch (Exception e) {
				logger.info("Error by initializing the Fred context");
				e.printStackTrace();
			}
		}
		
		return restTemplate;
	}

}
