package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YQLCashFlow extends YQLTable {

	private static String table="yahoo.finance.cashflow";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private String timeframe="quarterly";
	
	public YQLCashFlow(String symbol){
		this.symbol=symbol;
	}

	public void setTimeFrameToQuaterly(){
		this.timeframe="quarterly";
	}
	public void setTimeFrameToAnnual(){
		this.timeframe="annual";
	}
	
	@Override
	protected String createUrl(){
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable()
							+" where symbol=\""+this.symbol+"\""
							+" and timeframe=\""+this.timeframe+"\"";
			
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
		YQLCashFlow cashFlow=new YQLCashFlow("yhoo");
		cashFlow.setTimeFrameToAnnual();
		System.out.println(cashFlow.getResult().toString(1));
	}

}
