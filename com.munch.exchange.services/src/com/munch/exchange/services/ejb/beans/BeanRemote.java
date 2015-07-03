package com.munch.exchange.services.ejb.beans;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.munch.exchange.services.ejb.interfaces.ContractInfoBeanRemote;


public class BeanRemote<T> {
	
	public static final String APP_NAME="com.munch.exchange.server";
	public static final String MODULE_NAME="com.munch.exchange.server.ejb";
	public static final String DISTINCT_NAME="";
	
	public static Context context=null;
	
	private T instance=null;
	private String beanName;
	
	
	public BeanRemote(String beanName,Class<?> viewClass) {
		super();
		this.beanName = beanName;
		this.instance=doLookUp(beanName, viewClass);
	}

	

	public static StudentDAORemote doLookUp() throws NamingException{
		final Hashtable jndiProperties = new Hashtable();
		jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	    
		
        final Context context = new InitialContext(jndiProperties);
        
        System.out.println("context: "+context);
       
        final String appName = "ServerDemoProj";
       
        final String moduleName = "ServerTestEJB";
       
        final String distinctName = "";
       
        final String beanName = "StudentDAO";
       
        final String viewClassName = "com.munch.exchange.services.ejb.beans.StudentDAORemote";
       
        return (StudentDAORemote) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
	}
	
	
	
	public T getService() {
		return instance;
	}



	public static ContractInfoBeanRemote doLookUpContractInfo() throws NamingException{
		
        
        //System.out.println("context: "+context+"Remote Class: ");
        
        final String appName = "com.munch.exchange.server";
       
        final String moduleName = "com.munch.exchange.server.ejb";
       
        final String distinctName = "";
       
        final String beanName = "ContractInfoBean";
       
        final String viewClassName = "com.munch.exchange.services.ejb.beans.ContractInfoBeanRemote";
       
        return (ContractInfoBeanRemote) getJndiContext().lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
	}
	
	
	@SuppressWarnings("unchecked")
	public T doLookUp(String beanName,Class<?> viewClass){
		
	
		String viewClassName = viewClass.getName();
		System.out.println(viewClassName);
		try {
			//return (T)  getJndiContext().lookup("ejb:" + APP_NAME + "/" + MODULE_NAME + "/" + DISTINCT_NAME + "/" + beanName + "!" + viewClassName);
			return (T)  getJndiContext().lookup( APP_NAME + "/" + MODULE_NAME + "/" + DISTINCT_NAME + "/" + beanName + "!" + viewClassName);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static Context getJndiContext() throws NamingException{
		
	
		if(context==null){
			@SuppressWarnings("rawtypes")
			
			
			
			
			/*
			Hashtable jndiProperties = new Hashtable();
			jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			context = new InitialContext(jndiProperties);
			*/
			// Configure  EJB Client properties for the InitialContext
			Properties ejbClientContextProps = new Properties();
			
			ejbClientContextProps.put("org.jboss.ejb.client.scoped.context", false);
			ejbClientContextProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			ejbClientContextProps.put("endpoint.name", "client");
			
			
			ejbClientContextProps.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED","false");
			ejbClientContextProps.put("remote.connections","default");
			ejbClientContextProps.put("remote.connection.default.host","localhost");
			ejbClientContextProps.put("remote.connection.default.port","8080");
			
			ejbClientContextProps.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS","false");
			
			
			ejbClientContextProps.put("remote.connection.default.username","admin");
			ejbClientContextProps.put("remote.connection.default.password","1SAMPRAS..");
			
			// Property to enable scoped EJB client context which will be tied to the JNDI context
			
			
			 Properties jndiProps = new Properties();
			  jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
			  jndiProps.put(Context.PROVIDER_URL,"http-remoting://localhost:8080");
			  // username
			  jndiProps.put(Context.SECURITY_PRINCIPAL, "admin");
			  // password
			  jndiProps.put(Context.SECURITY_CREDENTIALS, "1SAMPRAS..");
			  // This is an important property to set if you want to do EJB invocations via the remote-naming project
			  jndiProps.put("jboss.naming.client.ejb.context", true);
			  // create a context passing these properties
			  Context ctx = new InitialContext(jndiProps);
			  // lookup the bean     Foo
			  //beanRemoteInterface = (Foo) ctx.lookup("myapp/myejbmodule/FooBean!org.myapp.ejb.Foo");
			  context = ctx;
			
			//context = new InitialContext(ejbClientContextProps);
			
			System.out.println("Hallo");
			
		}
        return context;
	}
	
	

}
