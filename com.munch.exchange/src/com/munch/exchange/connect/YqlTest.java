package com.munch.exchange.connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.munch.exchange.connect.json.JSONObject;
import com.munch.exchange.connect.json.JSONTokener;

public class YqlTest {
	
	public static void main(String[] args) {
		
		
		String baseUrl = "http://query.yahooapis.com/v1/public/yql?q=";
		String query = "select * from yahoo.finance.historicaldata where symbol = \"DTE.DE\" and startDate = \"2013-09-11\" and endDate = \"2013-12-20\"";
		String fullUrlStr;
		URL fullUrl;
		try {
			fullUrlStr = baseUrl + URLEncoder.encode(query, "UTF-8") + "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
			fullUrl = new URL(fullUrlStr);
			
			InputStream is = fullUrl.openStream();

			JSONTokener tok = new JSONTokener(is);
			JSONObject result = new JSONObject(tok);
			
			System.out.println(result);
			//result.get(key)
			
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

		
	

	}

}
