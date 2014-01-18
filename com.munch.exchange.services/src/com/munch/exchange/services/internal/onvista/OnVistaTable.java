package com.munch.exchange.services.internal.onvista;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OnVistaTable {
	
	public static final int bufferSize=1000;
	
	
	//private static String quote_url="http://finance.yahoo.com/d/quotes.csv?s=";
	
	
	public OnVistaTable() {
	
	}
	
	public String createUrl(){
		return "http://www.onvista.de/rohstoffe/kursliste.html?ID_NOTATION=24877915&RANGE=24M";	
	}
	
	public String getHtmlPage(){
		
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
			return null;
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
		OnVistaTable j=new OnVistaTable();
		String html=j.getHtmlPage();
		System.out.println(html);
		
		Document doc = Jsoup.parse(html);
		System.out.println(doc.getAllElements().size());
		Elements table=doc.getElementsByTag("table");
		for(Element e:doc.getElementsByTag("tr")){
			System.out.println(e.nodeName()+": "+e.text());
		}
		
		

	}

}
