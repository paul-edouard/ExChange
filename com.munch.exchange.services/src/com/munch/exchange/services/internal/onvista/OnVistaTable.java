package com.munch.exchange.services.internal.onvista;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OnVistaTable {
	
	public static final int bufferSize=1000;
	
	
	//private static String quote_url="http://finance.yahoo.com/d/quotes.csv?s=";
	
	
	public OnVistaTable() {
	
	}
	
	public String createUrl(){
		return "http://www.onvista.de/rohstoffe/kursliste.html?ID_NOTATION=24877915&RANGE=6M";	
	}
	
	public Element getHtmlPage(){
		
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
		
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=factory.newDocumentBuilder();
		
		Document doc=builder.parse(dataIn);
		return doc.getDocumentElement();
		///BufferedReader reader = new BufferedReader(new Read
		//return slurp(dataIn,bufferSize);
		
		
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
		System.out.println(j.getHtmlPage());
		
		
		

	}

}
