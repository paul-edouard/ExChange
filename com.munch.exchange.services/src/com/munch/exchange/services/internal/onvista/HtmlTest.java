package com.munch.exchange.services.internal.onvista;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class HtmlTest {
	
	public static final int bufferSize=1000;
	
	
	public static String getHtmlPage(String urlPath){
		
		try {
		
		URL url = new URL(urlPath);
		
		URLConnection connection = url.openConnection();
		
		/*
		connection.setRequestProperty("REQUEST_METHOD", "GET");

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(true);

		connection.setRequestProperty("Content-Type", "multipart-formdata");
		*/
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
	
	
	public String getPDFContentFromFile(String filename){
		try {
			PDDocument document = PDDocument.load(filename);
			PDFTextStripper s = new PDFTextStripper();
			String content = s.getText(document);
			
			return content;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return "Error by reading the file: "+filename;
		}
	}
	
	public static LinkedList<String> findPdfFiles(String input){
		LinkedList<String> pdfList=new LinkedList<String>();
		String searchStr=".pdf";
		String[] tockens=input.split("\n");
		for(int i=0;i<tockens.length;i++){
			if(!tockens[i].contains(searchStr))continue;
			
			String[] splits=tockens[i].split("\"");
			
			for(int j=0;j<splits.length;j++){
				if(splits[j].contains(searchStr)){
					//System.out.println(splits[j]);
					pdfList.add(splits[j]);
					
				}
			}
		}
		return pdfList;
		
	}
	
	private static String getPDF(String input,String searchStr){
		String returnStr="";
		String[] tockens=input.split("\n");
		for(int i=0;i<tockens.length;i++){
			if(!tockens[i].contains(searchStr))continue;
			
			String[] splits=tockens[i].split("\"");
			
			for(int j=0;j<splits.length;j++){
				if(splits[j].contains(searchStr)){
					//System.out.println(splits[j]);
					returnStr+=splits[j];
					
				}
			}
		}
		return returnStr;
	}
	
	
	public static void main(String[] args) {
		
		//System.out.println(HtmlTest.getHtmlPage("https://www.destatis.de/DE/ZahlenFakten/Indikatoren/Konjunkturindikatoren/Arbeitsmarkt/arb110.html"));
		//String text=(HtmlTest.getHtmlPage("http://www.heidelbergcement.com/global/de/company/investor_relations/financial_publications/financial_reports.htm"));
		//System.out.println("PDF: "+HtmlTest.getPDF(text,"Q22014"));
		
		//String text=(HtmlTest.getHtmlPage("http://www.daimler.com/investor-relations"));
		//System.out.println("PDF:\n"+HtmlTest.getPDF(text,"Q2_2014"));
		
		String text=(HtmlTest.getHtmlPage("http://www.jenoptik.com/de-zwischenberichte-pdf"));
		System.out.println("PDF:\n"+HtmlTest.getPDF(text,"2014-1.pdf"));
		
		
		

	}
	

}
