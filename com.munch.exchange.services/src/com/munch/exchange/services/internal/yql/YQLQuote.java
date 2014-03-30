package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;

public class YQLQuote extends YQLTable {
	
	
	private static String table="yahoo.finance.quotes";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private List<String> symbols;
	private JSONArray quote;
	
	public YQLQuote(){
		this.symbols=new LinkedList<String>();
	}
	
	public YQLQuote(List<String> symbols){
		this.symbols=symbols;
	}
	
	public YQLQuote(String symbol){
		this.symbols=new LinkedList<String>();
		this.symbols.add(symbol);
	}
	
	public void addSymbol(String symbol){
		this.symbols.add(symbol);
	}
	
	private String createSymbolsString(){
		String ret="";
		for(String symbol:symbols){
			ret+="\""+symbol+"\",";
		}
		ret=ret.substring(0, ret.lastIndexOf(","));
		return ret;
	}
	
	protected String createUrl(){
		try {
		String baseUrl=YQL.URL;
		String query = "select * from "+this.getTable()
						+" where symbol in ("+createSymbolsString()+")";
		
		//System.out.println("Query"+query);
		
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
		return this.getQuote()!=null;
	}
	
	private JSONArray getQuote(){
		if(quote!=null)return quote;
		
		JSONObject obj=this.getResult();
		if(obj==null)return null;
		
		if(!obj.has("quote"))return null;
		
		Object a_obj=obj.get("quote");
		
		if(a_obj instanceof JSONArray){
			return (JSONArray)a_obj;
		}
		
		return null;
	}
	
	public HashMap<String, QuotePoint> getQuoteMap(){
		HashMap<String, QuotePoint> map=new HashMap<String, QuotePoint>();
		
		JSONArray array=this.getQuote();
		if(array==null)return map;
		
		for(int i=0;i<array.length();i++){
			Object obj=array.get(i);
			if(obj instanceof JSONObject){
				JSONObject j_obj=(JSONObject) obj;
				QuotePoint point=JSONToQuotePoint(j_obj);
				try {
					String symbol=j_obj.getString("symbol");
					map.put(symbol, point);
				
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		
		return map;
	}
	
	
	private QuotePoint JSONToQuotePoint(JSONObject obj){
		QuotePoint point=new QuotePoint();
		
			point.setAverageDailyVolume(this.getAverageDailyVolume(obj));
			point.setChange(this.getChange(obj));
			point.setDaysHigh(this.getDaysHigh(obj));
			point.setDaysLow(this.getDaysLow(obj));
			point.setLastTradeDate(this.getLastDateTime(obj));
			point.setLastTradePrice(this.getLastTradePriceOnly(obj));
			point.setMarketCapitalization(this.getMarketCapitalization(obj));
			point.setVolume(this.getVolume(obj));
			point.setYearHigh(this.getYearHigh(obj));
			point.setYearLow(this.getYearLow(obj));
			
			point.setDate(point.getLastTradeDate());
			
			
		return point;
	}
	
	private float getYearLow(JSONObject JSONobj) {
		
		if(!JSONobj.has("YearLow"))return Float.NaN;
		
		
		try {
			
			Object obj=JSONobj.get("YearLow");
			if(obj.toString().equals("null"))
				return Float.NaN;
			
			return JSONobj.getFloat("YearLow");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	private float getYearHigh(JSONObject JSONobj) {
		
		if(!JSONobj.has("YearHigh"))return Float.NaN;
		
		//if(!(this.getCurrent().get("YearHigh") instanceof Float))
		//	return Float.NaN;
		
		try {
			
			Object obj=JSONobj.get("YearHigh");
			if(obj.toString().equals("null"))
				return Float.NaN;
			
			return JSONobj.getFloat("YearHigh");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	private long getVolume(JSONObject JSONobj) {
		
		if(!JSONobj.has("Volume"))return 0;
		
		
		try {
			
			Object obj=JSONobj.get("Volume");
			if(obj.toString().equals("null"))
				return 0;
			
			return JSONobj.getLong("Volume");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private String getMarketCapitalization(JSONObject JSONobj) {
		
		
		if(!JSONobj.has("MarketCapitalization")){
			return "";
		}
		
		try {
			Object obj=JSONobj.get("MarketCapitalization");
			if(!(obj instanceof String))return "";
			
			return JSONobj.getString("MarketCapitalization");
			
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private float getLastTradePriceOnly(JSONObject obj) {
		try {
			return obj.getFloat("LastTradePriceOnly");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	private String getLastTradeDate(JSONObject obj) {
		try {
			
			
			if(!obj.has("LastTradeDate"))return "";
			Object t=obj.get("LastTradeDate");
			if(t instanceof String){
			return obj.getString("LastTradeDate");
			}
			else
				return "";
			
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private String getLastTradeTime(JSONObject obj) {
		try {
			if(!obj.has("LastTradeTime"))return "";
			Object t=obj.get("LastTradeTime");
			if(t instanceof String){
			return obj.getString("LastTradeTime");
			}
			else
				return "";
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private Calendar getLastDateTime(JSONObject obj){
		SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy-h:mma");
		Calendar date=Calendar.getInstance();
		if(this.getLastTradeDate(obj).isEmpty() || this.getLastTradeTime(obj).isEmpty())return date;
		
		try {
			Date d=format.parse(this.getLastTradeDate(obj)+"-"+this.getLastTradeTime(obj));
			date.setTime(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return date;
	}
	
	private float getDaysLow(JSONObject JSONobj) {
		
		if(!JSONobj.has("DaysLow"))return Float.NaN;
		
		
		try {
			
			Object obj=JSONobj.get("DaysLow");
			if(obj.toString().equals("null"))
				return Float.NaN;
			
			return JSONobj.getFloat("DaysLow");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	private float getDaysHigh(JSONObject JSONobj) {
		
		if(!JSONobj.has("DaysHigh"))return Float.NaN;
		
		
		try {
			
			Object obj=JSONobj.get("DaysHigh");
			if(obj.toString().equals("null"))
				return Float.NaN;
			
			
			return JSONobj.getFloat("DaysHigh");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	private long getAverageDailyVolume(JSONObject JSONobj) {
		try {
			return JSONobj.getLong("AverageDailyVolume");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private float getChange(JSONObject JSONobj) {
		if(JSONobj==null)return Float.NaN;
		try {
			
			Object obj=JSONobj.get("Change");
			if(obj.toString().equals("null"))
				return Float.NaN;
			
			
			if(obj instanceof String){
				String obj_str=(String) obj;
				if(obj_str.equals("null"))
					return Float.NaN;
			}
			
			return JSONobj.getFloat("Change");
		} catch (JSONException e) {
			//System.out.println( "After Exeption: "+this.getCurrent().get("Change"));
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	
	public static void main(String[] args) {
		
		//YQLQuotes quote=new YQLQuotes("YHOO");
		YQLQuote quote=new YQLQuote("DAI.DE");
		//YQLQuotes quote=new YQLQuotes("GCJ14.CMX");
		//287639=X
		//GCJ14.CMX
		quote.addSymbol("YHOO");
	//	quote.addSymbol("DAI.DE");//PAH3.DE
	//	quote.addSymbol("CCC3.DE");
		
	//	System.out.println(quote.getResult().toString(1));
		System.out.println(quote.getResult().toString(1));
		
		HashMap<String, QuotePoint> map=quote.getQuoteMap();
		
		for(String key:map.keySet()){
			System.out.println("key: "+key+", Point: "+map.get(key));
		}
		
		
		//System.out.println("Stock Exchange:"+quote.getStockExchange());
		
		
		//System.out.println(quote.getCurrentQuotePoint());
		
		
	}

}
