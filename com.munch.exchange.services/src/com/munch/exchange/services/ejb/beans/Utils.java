package com.munch.exchange.services.ejb.beans;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class Utils {
	
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

}
