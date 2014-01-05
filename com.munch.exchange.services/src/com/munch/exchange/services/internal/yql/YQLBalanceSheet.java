package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.munch.exchange.model.core.financials.FinancialPoint;

public class YQLBalanceSheet extends YQLTable {

	private static String table="yahoo.finance.balancesheet";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private String timeframeType=FinancialPoint.PeriodeTypeNone;
	
	public YQLBalanceSheet(String symbol){
		this.symbol=symbol;
	}

	public String getTimeFrame(){
		return timeframeType;
	}

	public String getTimeframeType() {
		return timeframeType;
	}

	public void setTimeframeType(String timeframeType) {
		this.timeframeType = timeframeType;
	}

	public void setTimeFrameToQuaterly(){
		this.timeframeType=FinancialPoint.PeriodeTypeQuaterly;
	}
	public void setTimeFrameToAnnual(){
		this.timeframeType=FinancialPoint.PeriodeTypeAnnual;
	}
	
	@Override
	protected String createUrl(){
		try {
			String baseUrl=YQL.URL;
			String query = "select * from "+this.getTable()
							+" where symbol=\""+this.symbol+"\""
							+" and timeframe=\""+this.getTimeFrame()+"\"";
			
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
		YQLBalanceSheet balanceSheet=new YQLBalanceSheet("yhoo");
		balanceSheet.setTimeFrameToAnnual();
		System.out.println(balanceSheet.getResult().toString(1));
	}

}
