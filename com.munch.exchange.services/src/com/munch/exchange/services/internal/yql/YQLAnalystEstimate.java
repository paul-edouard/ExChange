package com.munch.exchange.services.internal.yql;

public class YQLAnalystEstimate extends YQLTable {
	
	private static String table="yahoo.finance.analystestimate";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	public YQLAnalystEstimate(String symbol){
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
		//AOI
		//YQLAnalystEstimate analystEstimate=new YQLAnalystEstimate("APPL");
		YQLAnalystEstimate analystEstimate=new YQLAnalystEstimate("PRLB");
		System.out.println(analystEstimate.getResult().toString(1));
	}

}
