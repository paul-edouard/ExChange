package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YQLSectors extends YQLTable {
	
	private static String table="yahoo.finance.sectors";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	@Override
	protected String createUrl() {
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable();
			
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
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		
		YQLSectors sectors=new YQLSectors();
		//quote.addSymbol("YHOO");
		System.out.println(sectors.getResult().toString(1));
		
	}

}
