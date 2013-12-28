package com.munch.exchange.services.internal.yql;

import java.text.ParseException;
import java.util.Calendar;

import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;


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
	
	private JSONObject getStock(){
		JSONObject stock=this.getResult().getJSONObject("stock");
		return stock;
	}
	
	/*
	 * Common
	 */
	
	public String getSymbol(){
		return this.getStock().getString("symbol");
	}
	
	public Calendar getStartDate(){
		try {
			return getStock().getDate("start");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Calendar getEndDate(){
		try {
			return getStock().getDate("end");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Stock
	 */
	public boolean isStock(){
		if(this.getFundFamily()==null){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getSector(){
		try {
		return this.getStock().getString("Sector");
		} catch (JSONException e) {e.printStackTrace();return null;}
		
	}
	public String getIndustry(){
		try {
		return this.getStock().getString("Industry");
		} catch (JSONException e) {e.printStackTrace();return null;}
	}
	public Long getFullTimeEmployees(){
		try {
		return this.getStock().getLong("FullTimeEmployees");
		} catch (JSONException e) {e.printStackTrace();return null;}
	}
	
	/*
	 * Fund
	 */
	public boolean isFund(){
		if(this.getFundFamily()!=null){
			return true;
		}
		else{
			return false;
		}
	}
	public String getFundFamily(){
		try {
		return this.getStock().getString("FundFamily");
		} catch (JSONException e) {e.printStackTrace();return null;}
	}
	public String getCategory(){
		try {
		return this.getStock().getString("Category");
		} catch (JSONException e) {e.printStackTrace();return null;}
	}
	public String getNetAssets(){
		try {
		return this.getStock().getString("NetAssets");
		} catch (JSONException e) {e.printStackTrace();return null;}
	}
	public String getYeartoDateReturn(){
		try {
		return this.getStock().getString("Year-to-DateReturn");
		} catch (JSONException e) {e.printStackTrace();return null;}
	}
	
	public static void main(String[] args) {
		//YQLStocks stocks=new YQLStocks("CTYRX");
		YQLStocks stocks=new YQLStocks("YHO.DE");
		//YQLStocks stocks=new YQLStocks("YHOO");
		//YHO.DE
		//"YHOO"
		//AAPL
		//Fond  CTYRX
		System.out.println(stocks.getResult().toString(1));
		System.out.println(stocks.getEndDate().getTime());
		//System.out.println(stocks.getCategory());
		//System.out.println(stocks.isFund());
	}

}
