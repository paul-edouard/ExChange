package com.munch.exchange.services.internal.yql;


public class YQLKeystat extends YQLTable {
	
	private static String table="yahoo.finance.keystats";
	private static String format="&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	
	public YQLKeystat(String symbol){
		this.symbol=symbol;
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
		YQLKeystat keystat=new YQLKeystat("yhoo");
		System.out.println(keystat.getResult().toString(1));
	}

}
