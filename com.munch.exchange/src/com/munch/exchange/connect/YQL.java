package com.munch.exchange.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.munch.exchange.connect.json.JSONObject;
import com.munch.exchange.connect.json.JSONTokener;

public class YQL {
	
	protected static String URL="http://query.yahooapis.com/v1/public/yql?q=";
	
	protected static String getDateString(Calendar date){
		
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date.getTime());
	}
	
	protected static JSONObject getJSONObject(String url){
		
		JSONObject result=null;
		try {
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
