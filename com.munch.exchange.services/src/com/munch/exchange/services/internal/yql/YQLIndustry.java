package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YQLIndustry extends YQLTable {
	
	private static String table="yahoo.finance.industry";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private String id="";
	
	public YQLIndustry(String id){
		this.id=id;
	}
	

	@Override
	protected String createUrl() {
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable()
							+" where id="+id;
			
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
		YQLIndustry industry=new YQLIndustry("851");
		System.out.println(industry.getResult().toString(1));
	}

}
