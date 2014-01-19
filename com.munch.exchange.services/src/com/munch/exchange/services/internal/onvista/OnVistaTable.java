package com.munch.exchange.services.internal.onvista;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.tool.DateTool;

public class OnVistaTable {
	
	public static final int bufferSize=1000;
	
	
	private Calendar startDate;
	private String interval="Y1";
	private String notationId="";
	
	
	//private static String quote_url="http://finance.yahoo.com/d/quotes.csv?s=";
	
	LinkedList<HistoricalPoint> plist=null;
	
	public OnVistaTable(String notationId, Calendar startDate, String interval) {
		this.notationId=notationId;
		this.startDate=startDate;
		this.interval=interval;
	}
	
	public OnVistaTable(String notationId, Calendar startDate) {
		this.notationId=notationId;
		this.startDate=startDate;
	}
	
	public String createUrl(){
		//return "http://www.onvista.de/rohstoffe/kursliste.html?ID_NOTATION=24877915&RANGE=24M";
		//http://www.onvista.de/onvista/times+sales/popup/historische-kurse/?notationId=8381868&dateStart=01.01.2014&interval=M1
		
		//String url="http://www.onvista.de/onvista/boxes/historicalquote/export.csv?";
		String url="http://www.onvista.de/onvista/times+sales/popup/historische-kurse/?";
		url+="notationId="+this.notationId;
		url+="&dateStart="+DateTool.OnVistadateToDayString(this.startDate);
		url+="&interval="+this.interval;
		url+="&assetName=Dollarkurs&exchange=außerbörslich";
		//System.out.println("Url: "+url);
		
		return url;
		
		
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
	
	
	public HistoricalPoint createHistoricalPoint(String line){
		
			
		//System.out.println(line);
		
			HistoricalPoint point=new HistoricalPoint();
			
			String[] data=line.split(";");
			if(data.length!=6 || data[0].equals("Datum"))return null;
			
			for(int i=1;i<6;i++){
				data[i]=data[i].replace(".", "").replace(",", ".");
			}
			point.setDate(DateTool.OnVistaStringToDay(data[0]));
			
			point.setOpen(Float.parseFloat(data[1]));
			point.setLow(Float.parseFloat(data[2]));
			point.setHigh(Float.parseFloat(data[3]));
			point.setClose(Float.parseFloat(data[4]));
			point.setVolume(Long.parseLong(data[5]));
			
			//System.out.println(point);
			
			return point;
		
	}
	
	public LinkedList<HistoricalPoint> getHisPointList(){
		if(plist!=null)return plist;
		
		plist=new LinkedList<HistoricalPoint>();
		
		String html=this.getHtmlPage();
	//	System.out.println(html);
		if(html==null || html.isEmpty())
			return plist;
		
		
		Document doc = Jsoup.parse(html);
		for(Element e:doc.getAllElements()){
			if(e.nodeName().equals("tr")){
				HistoricalPoint point=createHistoricalPoint(e.text().replace(" ", ";"));
				if(point!=null){
					plist.add(point);
				}
			}
		}
		/*
		String[] lines=html.split("\n");
		
		for(int i=0;i<lines.length;i++){
			//System.out.println(e.nodeName()+": "+e.text());
			HistoricalPoint point=createHistoricalPoint(lines[i]);
			if(point!=null){
				plist.add(point);
			}
			
		}
		*/
		return plist;
	}
	

	public static void main(String[] args) {
		
		//24877915
		OnVistaTable j=new OnVistaTable("24877915", DateTool.OnVistaStringToDay("01.01.2013"));
		//OnVistaTable j=new OnVistaTable("8381868", DateTool.OnVistaStringToDay("01.01.2013"));
		//System.out.println(j.getHtmlPage());
		
		for(HistoricalPoint p: j.getHisPointList()){
			System.out.println(p);
		}
		
		
		
		

	}

}
