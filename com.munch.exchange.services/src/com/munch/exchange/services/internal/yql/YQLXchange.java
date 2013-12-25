package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YQLXchange extends YQLTable {
	
	private static String table="yahoo.finance.xchange";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	public YQLXchange(String symbol){
		this.symbol=symbol;
	}

	
	protected String createUrl(){
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable()
							+" where pair in (\""+this.symbol+"\")";
			
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
	
	public static void main(String[] args) {
		YQLXchange xchange=new YQLXchange("EURUSD");
		System.out.println(xchange.getResult().toString(1));
	}

}
