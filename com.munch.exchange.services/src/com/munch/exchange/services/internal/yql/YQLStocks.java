package com.munch.exchange.services.internal.yql;

public class YQLStocks extends YQLTable {
	
	private static String table="yahoo.finance.stocks";
	private static String format="&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	

	@Override
	protected String createUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

}
