package com.munch.exchange.services.ejb.beans;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class BeanRemote<T> {
	
	public static final String APP_NAME="com.munch.exchange.server";
	public static final String MODULE_NAME="com.munch.exchange.server.ejb";
	public static final String DISTINCT_NAME="";
	
	public static final String SERVER_CONTEXT_FACTORY="org.jboss.naming.remote.client.InitialContextFactory";
	public static final String SERVER_HOST="localhost";
	public static final String SERVER_PORT="8080";
	public static final String SERVER_USER="admin";
	public static final String SERVER_PASSWORD="1SAMPRAS..";
	
	
	
	public  static Context context=null;
	
	private T instance=null;
	private String beanName;
	private String lookUpName;
	
	
	public BeanRemote(String beanName,Class<?> viewClass) {
		super();
		this.beanName = beanName;
		this.instance=doLookUp(beanName, viewClass);
	}

	

//	public static StudentDAORemote doLookUp() throws NamingException{
//		final Hashtable jndiProperties = new Hashtable();
//		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
//	    
//		
//        final Context context = new InitialContext(jndiProperties);
//        
//        System.out.println("context: "+context);
//       
//        final String appName = "com.munch.exchange.server";
//       
//        final String moduleName = "ServerTestEJB";
//       
//        final String distinctName = "";
//       
//        final String beanName = "StudentDAO";
//       
//        final String viewClassName = "com.munch.exchange.services.ejb.beans.StudentDAORemote";
//       
//        return (StudentDAORemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
//	}
	
	
	
	public T getService() {
		
		//CloseContext();
		//System.out.println("Search Service!!!");
		//instance=reLookUp();
		//System.out.println("Rellok up!");
		return instance;
	}


/*
	public static ContractInfoBeanRemote doLookUpContractInfo() throws NamingException{
		
        
        //System.out.println("context: "+context+"Remote Class: ");
        
        final String appName = "com.munch.exchange.server";
       
        final String moduleName = "com.munch.exchange.server.ejb";
       
        final String distinctName = "";
       
        final String beanName = "ContractInfoBean";
       
        final String viewClassName = "com.munch.exchange.services.ejb.beans.ContractInfoBeanRemote";
       
        return (ContractInfoBeanRemote) getJndiContext().lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
	}
	*/
	
	@SuppressWarnings("unchecked")
	public T doLookUp(String beanName,Class<?> viewClass){
		
		String viewClassName = viewClass.getName();
		
		lookUpName=APP_NAME + "/" + MODULE_NAME + "/" + DISTINCT_NAME + "/" + beanName + "!" + viewClassName;
		
		//System.out.println(viewClassName);
		try {
			return (T)  getJndiContext().lookup( lookUpName);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public T reLookUp(){
		try {
			return (T)  getJndiContext().lookup( lookUpName);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void CloseContext() {
		if(context==null)return;
		try {
			context.close();
			context=null;
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Context getJndiContext() throws NamingException {

		if (context == null) {

			// Property to enable scoped EJB client context which will be tied
			// to the JNDI context
			Properties jndiProps = new Properties();
			
			jndiProps.put(Context.INITIAL_CONTEXT_FACTORY,SERVER_CONTEXT_FACTORY);
			
			jndiProps.put(Context.PROVIDER_URL,"http-remoting://"+SERVER_HOST+":"+SERVER_PORT);
			
			// username
			jndiProps.put(Context.SECURITY_PRINCIPAL, SERVER_USER);
			
			// password
			jndiProps.put(Context.SECURITY_CREDENTIALS, SERVER_PASSWORD);
			
			// This is an important property to set if you want to do EJB
			// invocations via the remote-naming project
			jndiProps.put("jboss.naming.client.ejb.context", true);
			// create a context passing these properties
			context = new InitialContext(jndiProps);
			
			System.out.println("Creation of the context: "+context.toString());

		}
		return context;
	}
	
	

}
