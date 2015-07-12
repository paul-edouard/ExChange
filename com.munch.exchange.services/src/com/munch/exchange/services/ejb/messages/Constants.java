package com.munch.exchange.services.ejb.messages;

import java.util.Properties;

import javax.naming.Context;

public class Constants {
	
	 public static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
	 public static final String DEFAULT_USERNAME = "jmsuser";
	 public static final String DEFAULT_PASSWORD = "Password1!";
	 public static final String DEFAULT_CLIENT_ID = "12345";
	 private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
	 private static final String PROVIDER_URL = "http-remoting://localhost:8080";
	 
	 
	 //Destinations
	 private static final String DEFAULT_DESTINATION = "java:/jms/topic/demoTopic";
	 public static final String TOP_MARKET_DATA = "java:/jms/topic/MktData";
	 
	 public static Properties getContextProperties(){
		 final Properties env = new Properties();
	        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
	        env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
	        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
	        
	        env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
	        env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
	        return env;
	 }
	 
	 
	 
}
