package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;


public class YQLQuotes  extends YQLTable {
	
	private static String table="yahoo.finance.quotes";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private List<String> symbols;
	
	public YQLQuotes(){
		this.symbols=new LinkedList<String>();
	}
	
	public YQLQuotes(List<String> symbols){
		this.symbols=symbols;
	}
	
	public YQLQuotes(String symbol){
		this.symbols=new LinkedList<String>();
		this.symbols.add(symbol);
	}
	
	
	public void addSymbol(String symbol){
		this.symbols.add(symbol);
	}
	
	private String createSymbolsString(){
		String ret="";
		for(String symbol:symbols){
			ret+="\""+symbol+"\",";
		}
		ret=ret.substring(0, ret.lastIndexOf(","));
		return ret;
		
	}
	
	protected String createUrl(){
		try {
		String baseUrl=YQL.URL;
		String query = "select * from "+this.getTable()
						+" where symbol in ("+createSymbolsString()+")";
		
		//System.out.println("Query"+query);
		
		return baseUrl + URLEncoder.encode(query, "UTF-8") +this.getFormat();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}
	
	
	protected String getTable(){
		return table;
	}
	
	protected String getFormat(){
		return format;
	}
	
	public static void main(String[] args) {
		
		
		YQLQuotes quote=new YQLQuotes("TWTR");
		//quote.addSymbol("YHOO");
		System.out.println(quote.getResult().toString(1));
		
	}
	
	
}
