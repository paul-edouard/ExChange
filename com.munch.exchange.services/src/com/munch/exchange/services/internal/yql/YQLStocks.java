package com.munch.exchange.services.internal.yql;


public class YQLStocks extends YQLTable {
	
	private static String table="yahoo.finance.stocks";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	public YQLStocks(String symbol){
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
	
	public static void main(String[] args) {
		//YQLStocks stocks=new YQLStocks("APC.DE");
		YQLStocks stocks=new YQLStocks("CTYRX");
		
		//AAPL
		
		System.out.println(stocks.getResult().toString(1));
	}

}
