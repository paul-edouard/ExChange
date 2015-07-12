package com.munch.exchange.services.ejb.messages.test;


import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class SimplifiedChatTopicDurableClient implements MessageListener {
	
	private static final Logger log = Logger.getLogger(SimplifiedChatTopicDurableClient.class.getName());
	 
	
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private Context context;
	
	// Set up all the default values
   // private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "java:/jms/topic/demoTopic";
   // private static final String DEFAULT_MESSAGE_COUNT = "1";
    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "Password1!";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://localhost:8080";
 
	
	public static void main(String[] args) throws JMSException, NamingException{
		new SimplifiedChatTopicDurableClient();
	}
	
	public SimplifiedChatTopicDurableClient() throws JMSException, NamingException{
		
		// Set up the context for the JNDI lookup
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        
        //log.info("User: \"" + System.getProperty("username", DEFAULT_USERNAME));
        env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
        env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
        context = new InitialContext(env);
        
        // Perform the JNDI lookups
        String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
        log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
        connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
        log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

        String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
        log.info("Attempting to acquire destination \"" + destinationString + "\"");
        destination = (Destination) context.lookup(destinationString);
        log.info("Found destination \"" + destinationString + "\" in JNDI");
        
       // connectionFactory.createContext(DEFAULT_USERNAME, DEFAULT_PASSWORD)
        
        try{
        	JMSContext jmsContext=connectionFactory.createContext(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        	
        	
        	@SuppressWarnings("resource")
        	Scanner scanner = new Scanner(System.in);
        	
        	System.out.println("Dein Name: ");
        	String name = scanner.nextLine();
        	
        	System.out.println("Dein Chatraum: ");
        	String room = scanner.nextLine();
        	
        	System.out.println("Dein Client ID: ");
        	String clientID = scanner.nextLine();
        	
        	jmsContext.setClientID(clientID);
        	
        	System.out.println("Dein Abonnement: ");
        	String aboName = scanner.nextLine();
        	
        	JMSProducer producer=jmsContext.createProducer();
        	
        	JMSConsumer consumer=jmsContext.createDurableConsumer((Topic) destination,
        			aboName,
        			"ROOM = '"+room+"'",
        			true);
        	consumer.setMessageListener(this);
        	
        	while(true){
        		String in=scanner.nextLine();
        		if(in.equals("exit"))return;
        		TextMessage message=jmsContext.createTextMessage(in);
        		message.setStringProperty("ROOM", room);
        		message.setStringProperty("NAME", name);
        		producer.send(destination, message);
        	}
        	
        	
        }
        catch(Exception ex){
        	ex.printStackTrace();
        }
		
		
	}
	

	@Override
	public void onMessage(Message message) {
		try{
			String room=message.getStringProperty("ROOM");
			String name=message.getStringProperty("NAME");
			
			System.out.println(room+": "+name+": "+((TextMessage) message).getText());
			
		}catch(JMSException ex){
			ex.printStackTrace();
		}

	}

}
