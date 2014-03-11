package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.munch.exchange.services.internal.yql.json.JSONObject;


public class YQLIsin extends YQLTable {
	
	//http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.isin%20where%20symbol%20in%20(%22US9843321061%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
	
	private static String table="yahoo.finance.isin";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	private String isin="";
	
	public YQLIsin(String isin){
		this.isin=isin;
	}
	
	
	protected String createUrl(){
		try {
		String baseUrl=YQL.URL;
		String query = "select * from "+this.getTable()
						+" where symbol in (\""+isin+"\")";
		
		System.out.println("Query"+query);
		
		return baseUrl + URLEncoder.encode(query, "UTF-8") +this.getFormat();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}
	
			
	@Override
	protected String getTable() {
		return table;
	}

	@Override
	protected String getFormat() {
		return format;
	}

	@Override
	public boolean hasValidResult() {
		
		if(this.getResult()==null)return false;
		
		if(!this.getResult().has("stock"))return false;
		
		if(this.getStock()==null)return false;
		
		return true;
	}
	
	private JSONObject getStock(){
		Object obj=this.getResult().get("stock");
		if(obj instanceof JSONObject){
			return (JSONObject) obj;
		}
		
		return null;
	}
	
	public String getYahooSymbol(){
		JSONObject stock=this.getStock();
		if(stock==null)return "";
		if(!stock.has("Isin"))return "";
		
		Object symb_obj=stock.get("Isin");
		if(symb_obj instanceof String )
			return (String) symb_obj;
		else
			return "";
		
	}
	
	
	public static void main(String[] args) {
		
		YQLIsin isin=new YQLIsin("US9843321061");
		//quote.addSymbol("YHOO");
		System.out.println(isin.getYahooSymbol());
		
	}
	
	

}
