package com.munch.exchange.services.internal.yql;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class YahooFinance {
	
	
	public static final int bufferSize=1000;
	
	
	private static String quote_url="http://finance.yahoo.com/d/quotes.csv?s=";
	private LinkedList<String> stocks=new LinkedList<String>();
	
	private String options="snd1l1yrk2";
	
	

	
	public YahooFinance(String stock) {
		super();
		stocks.add(stock);
	}
	
	public void addStock(String stock){
		stocks.add(stock);
	}
	
	
	public String createUrl(){
		String stocks_str="";
		for(String stock :stocks ){
			stocks_str+=stock+"+";
		}
		stocks_str=stocks_str.substring(0, stocks_str.lastIndexOf("+"));
		return quote_url+stocks_str+"&f="+options;
	}
	
	public String getCurrentQuotes(){
		
		try {
		
		URL url = new URL(createUrl());
		
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("REQUEST_METHOD", "GET");

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(true);

		connection.setRequestProperty("Content-Type", "multipart-formdata");
		
		//connection.getOutputStream();
		connection.connect();
		
		// Input
		DataInputStream dataIn = new DataInputStream(connection.getInputStream());
		
		///BufferedReader reader = new BufferedReader(new Read
		return slurp(dataIn,bufferSize);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			return "None!";
		}
	}
	
	
	private String slurp(final InputStream is, final int bufferSize)
	{
	  final char[] buffer = new char[bufferSize];
	  final StringBuilder out = new StringBuilder();
	  try {
	    final Reader in = new InputStreamReader(is, "UTF-8");
	    try {
	      for (;;) {
	        int rsz = in.read(buffer, 0, buffer.length);
	        if (rsz < 0)
	          break;
	        out.append(buffer, 0, rsz);
	      }
	    }
	    finally {
	      in.close();
	    }
	  }
	  catch (UnsupportedEncodingException ex) {
	    /* ... */
	  }
	  catch (IOException ex) {
	      /* ... */
	  }
	  return out.toString();
	}

	public static void main(String[] args) {
		YahooFinance j=new YahooFinance("GE");j.addStock("FDAX.EX");
		System.out.println(j.createUrl());
		
		System.out.println(j.getCurrentQuotes());

	}

}
