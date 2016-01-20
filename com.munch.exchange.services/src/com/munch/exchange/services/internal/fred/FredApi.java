package com.munch.exchange.services.internal.fred;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.munch.exchange.services.internal.yql.json.JSONObject;
import com.munch.exchange.services.internal.yql.json.JSONTokener;

public class FredApi {
	
//	protected static String URL="http://api.stlouisfed.org/fred/";
	protected static String URL="none";
	
	private static Logger logger = Logger.getLogger(FredApi.class);
	
	protected static JSONObject getJSONObject(String url){
		
		JSONObject result=null;
		try {
			
		//	logger.info("Testing URL: "+url);
			URL fullUrl = new URL(url);
			
			InputStream is = fullUrl.openStream();

			JSONTokener tok = new JSONTokener(is);
			result = new JSONObject(tok);
			
			is.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

}
