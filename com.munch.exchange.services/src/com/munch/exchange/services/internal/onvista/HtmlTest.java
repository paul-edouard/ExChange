package com.munch.exchange.services.internal.onvista;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class HtmlTest {
	
	public static final int bufferSize=1000;
	
	
	public static String getHtmlPage(String urlPath){
		
		try {
		
		URL url = new URL(urlPath);
		
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
			return null;
		}
	}
	
	
	public static String slurp(final InputStream is, final int bufferSize)
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
		
		System.out.println(HtmlTest.getHtmlPage("https://www.destatis.de/DE/ZahlenFakten/Indikatoren/Konjunkturindikatoren/Arbeitsmarkt/arb110.html"));
		
		
		
		

	}
	

}
