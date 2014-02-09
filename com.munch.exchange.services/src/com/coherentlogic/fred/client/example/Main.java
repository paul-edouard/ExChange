package com.coherentlogic.fred.client.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.coherentlogic.fred.client.core.domain.Seriess;

/**
 * A simple application which calls the seriesService.getSeries method and then
 * prints out the Seriess returned.
 *
 * NOTE: You will need to add your own apiKey in the application-context.xml
 * file, otherwise this won't work.
 *
 * @author <a href="support@coherentlogic.com">Support</a>
 */
public class Main {

    public static void main(String[] args) {
    	
    	/*
    	Bundle bundle = FrameworkUtil.getBundle(Main.class);
		URL url = FileLocator.find(bundle, new Path("resources/spring/application-context.xml"), null);
    	*/
    	
        // Note that the application-context.xml is based very closely on the
        // one we use in fred-client-core-it.
        ApplicationContext context = new FileSystemXmlApplicationContext ("resources/spring/application-context.xml");
      //      "src/main/resources/spring/application-context.xml");

        SeriesService seriesService =
            (SeriesService) context.getBean("seriesService");

        String seriesId = "GNPCA", // GNPCA / canada
            realtimeStartDate = "2001-01-20",
            realtimeEndDate = "2004-05-17";

        Seriess seriess = seriesService.getSeries(
            seriesId,
            realtimeStartDate,
            realtimeEndDate);

        System.err.println ("Seriess: " + seriess);
        
      //  context.
    }
}
