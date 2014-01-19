package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.quote.QuotePoint;
import com.munch.exchange.model.xml.Parameter;
import com.munch.exchange.services.internal.yql.json.JSONArray;
import com.munch.exchange.services.internal.yql.json.JSONException;
import com.munch.exchange.services.internal.yql.json.JSONObject;


public class YQLQuotes  extends YQLTable {
	
	private static String table="yahoo.finance.quotes";
	private static String format="&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	
	private List<String> symbols;
	
	private int currentQuote=0;
	
	public YQLQuotes(){
		this.symbols=new LinkedList<String>();
	}
	
	public YQLQuotes(List<String> symbols){
		this.symbols=symbols;
	}
	
	public YQLQuotes(String symbol){
		this.symbols=new LinkedList<String>();
		this.symbols.add(symbol);
	}
	
	
	public void addSymbol(String symbol){
		this.symbols.add(symbol);
	}
	
	public void setCurrentQuote(int currentQuote) {
		this.currentQuote = currentQuote;
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
	
	private JSONArray getQuote(){
		if(this.symbols.size()>1)
		return this.getResult().getJSONArray("quote");
		else
			return null;
	}
	private JSONObject getCurrent(){
		if(this.symbols.size()>1)
			return this.getQuote().getJSONObject(currentQuote);
		else{
			if(this.getResult()==null)return null;
			if(this.getResult().has("quote")){
				Object q=this.getResult().get("quote");
				if(q instanceof JSONObject){
					return (JSONObject) q;
				}
			}
			return null;
		}
	}
	
	
	
	protected String getTable(){
		return table;
	}
	protected String getFormat(){
		return format;
	}
	
	
	private Parameter currentToParameter(){
		Parameter param=new Parameter("quote", Calendar.getInstance().getTimeInMillis());
		
		for(Object key:this.getCurrent().keySet()){
			if(key instanceof String){
				String keyStr=(String)key;
				Object value=this.getCurrent().get(keyStr);
				Parameter c=new Parameter(keyStr, value);
				param.addChild(c);
			}
		}
		return param;
	}
	
	private Calendar getLastDateTime(){
		SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy-h:mma");
		Calendar date=Calendar.getInstance();
		try {
			Date d=format.parse(this.getLastTradeDate()+"-"+this.getLastTradeTime());
			date.setTime(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return date;
	}
	
	
	public QuotePoint getCurrentQuotePoint(){
		if(!this.hasValidResult())return null;
		
		QuotePoint point=new QuotePoint();
		
		point.setAverageDailyVolume(this.getAverageDailyVolume());
		point.setChange(this.getChange());
		point.setDaysHigh(this.getDaysHigh());
		point.setDaysLow(this.getDaysLow());
		point.setLastTradeDate(this.getLastDateTime());
		point.setLastTradePrice(this.getLastTradePriceOnly());
		point.setMarketCapitalization(this.getMarketCapitalization());
		point.setParameter(this.currentToParameter());
		point.setVolume(this.getVolume());
		point.setYearHigh(this.getYearHigh());
		point.setYearLow(this.getYearLow());
		
		point.setDate(this.getLastDateTime());
		
		return point;
	}
	
	
	@Override
	public boolean hasValidResult() {
		if(this.getCurrent()==null)return false;
		
		if(!this.getCurrent().has("Name"))return false;
		
		if(!(this.getCurrent().get("Name") instanceof String))return false;
		
		
		return this.getChange()!=Float.NaN;
	}

	/*
	 * Quote Data
	 */
	public float getOpen() {
		try {
			return this.getCurrent().getFloat("Open");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getPriceEPSEstimateCurrentYear() {
		try {
			return this.getCurrent().getFloat("PriceEPSEstimateCurrentYear");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getBookValue() {
		try {
			return this.getCurrent().getFloat("BookValue");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getBid() {
		try {
			return this.getCurrent().getFloat("Bid");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getDaysHigh() {
		
		if(!this.getCurrent().has("DaysHigh"))return Float.NaN;
		
		//if(!(this.getCurrent().get("DaysHigh") instanceof Float))
		//	return Float.NaN;
		
		try {
			return this.getCurrent().getFloat("DaysHigh");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getChangeFromFiftydayMovingAverage() {
		try {
			return this.getCurrent()
					.getFloat("ChangeFromFiftydayMovingAverage");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getOrderBookRealtime() {
		try {
			return this.getCurrent().getFloat("OrderBookRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getErrorIndicationreturnedforsymbolchangedinvalid() {
		try {
			return this.getCurrent().getString(
					"ErrorIndicationreturnedforsymbolchangedinvalid");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getDaysRange() {
		try {
			return this.getCurrent().getString("DaysRange");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getMoreInfo() {
		try {
			return this.getCurrent().getString("MoreInfo");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getAnnualizedGain() {
		try {
			return this.getCurrent().getFloat("AnnualizedGain");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getChange_PercentChange() {
		try {
			return this.getCurrent().getString("Change_PercentChange");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getDaysRangeRealtime() {
		try {
			return this.getCurrent().getString("DaysRangeRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getLowLimit() {
		try {
			return this.getCurrent().getFloat("LowLimit");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getPercentChangeFromTwoHundreddayMovingAverage() {
		try {
			return this.getCurrent().getString(
					"PercentChangeFromTwoHundreddayMovingAverage");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getChangeFromTwoHundreddayMovingAverage() {
		try {
			return this.getCurrent().getFloat(
					"ChangeFromTwoHundreddayMovingAverage");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getDividendYield() {
		try {
			return this.getCurrent().getFloat("DividendYield");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getEPSEstimateCurrentYear() {
		try {
			return this.getCurrent().getFloat("EPSEstimateCurrentYear");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getLastTradeDate() {
		try {
			return this.getCurrent().getString("LastTradeDate");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getTwoHundreddayMovingAverage() {
		try {
			return this.getCurrent().getFloat("TwoHundreddayMovingAverage");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getAskRealtime() {
		try {
			return this.getCurrent().getFloat("AskRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getPercentChange() {
		try {
			return this.getCurrent().getString("PercentChange");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getDividendPayDate() {
		try {
			return this.getCurrent().getString("DividendPayDate");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getYearRange() {
		try {
			return this.getCurrent().getString("YearRange");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getsymbol() {
		try {
			return this.getCurrent().getString("symbol");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getPercentChangeFromFiftydayMovingAverage() {
		try {
			return this.getCurrent().getString(
					"PercentChangeFromFiftydayMovingAverage");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getChange() {
		if(this.getCurrent()==null)return Float.NaN;
		try {
			
			return this.getCurrent().getFloat("Change");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getHoldingsGainPercent() {
		try {
			return this.getCurrent().getFloat("HoldingsGainPercent");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getNotes() {
		try {
			return this.getCurrent().getString("Notes");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getHoldingsGain() {
		try {
			return this.getCurrent().getFloat("HoldingsGain");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getYearHigh() {
		
		if(!this.getCurrent().has("YearHigh"))return Float.NaN;
		
		//if(!(this.getCurrent().get("YearHigh") instanceof Float))
		//	return Float.NaN;
		
		try {
			return this.getCurrent().getFloat("YearHigh");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getSymbol() {
		try {
			return this.getCurrent().getString("Symbol");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getAfterHoursChangeRealtime() {
		try {
			return this.getCurrent().getString("AfterHoursChangeRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getHoldingsGainPercentRealtime() {
		try {
			return this.getCurrent().getString("HoldingsGainPercentRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getMarketCapitalization() {
		
		
		if(!this.getCurrent().has("MarketCapitalization")){
			return "";
		}
		
		try {
			Object obj=this.getCurrent().get("MarketCapitalization");
			if(!(obj instanceof String))return "";
			
			return this.getCurrent().getString("MarketCapitalization");
			
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getBidRealtime() {
		try {
			return this.getCurrent().getFloat("BidRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getLastTradePriceOnly() {
		try {
			return this.getCurrent().getFloat("LastTradePriceOnly");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getPERatio() {
		try {
			return this.getCurrent().getFloat("PERatio");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getEPSEstimateNextQuarter() {
		try {
			return this.getCurrent().getFloat("EPSEstimateNextQuarter");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getMarketCapRealtime() {
		try {
			return this.getCurrent().getFloat("MarketCapRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public long getAverageDailyVolume() {
		try {
			return this.getCurrent().getLong("AverageDailyVolume");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String getPercentChangeFromYearLow() {
		try {
			return this.getCurrent().getString("PercentChangeFromYearLow");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getTickerTrend() {
		try {
			return this.getCurrent().getString("TickerTrend");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getChangeFromYearHigh() {
		try {
			return this.getCurrent().getFloat("ChangeFromYearHigh");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getLastTradeWithTime() {
		try {
			return this.getCurrent().getString("LastTradeWithTime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getPERatioRealtime() {
		try {
			return this.getCurrent().getFloat("PERatioRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getStockExchange() {
		try {
			return this.getCurrent().getString("StockExchange");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getPreviousClose() {
		try {
			return this.getCurrent().getFloat("PreviousClose");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getFiftydayMovingAverage() {
		try {
			return this.getCurrent().getFloat("FiftydayMovingAverage");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getLastTradeTime() {
		try {
			return this.getCurrent().getString("LastTradeTime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getDaysLow() {
		
		if(!this.getCurrent().has("DaysLow"))return Float.NaN;
		
		//if(!(this.getCurrent().get("DaysLow") instanceof Float))
		//	return Float.NaN;
		
		try {
			return this.getCurrent().getFloat("DaysLow");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getPriceEPSEstimateNextYear() {
		try {
			return this.getCurrent().getFloat("PriceEPSEstimateNextYear");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getDaysValueChange() {
		try {
			return this.getCurrent().getString("DaysValueChange");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getHighLimit() {
		try {
			return this.getCurrent().getFloat("HighLimit");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getTradeDate() {
		try {
			return this.getCurrent().getString("TradeDate");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getOneyrTargetPrice() {
		try {
			return this.getCurrent().getFloat("OneyrTargetPrice");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getChangeRealtime() {
		try {
			return this.getCurrent().getFloat("ChangeRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getExDividendDate() {
		try {
			return this.getCurrent().getString("ExDividendDate");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getYearLow() {
		
		if(!this.getCurrent().has("YearLow"))return Float.NaN;
		
		//if(!(this.getCurrent().get("YearLow") instanceof Float))
		//	return Float.NaN;
		
		try {
			return this.getCurrent().getFloat("YearLow");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getEPSEstimateNextYear() {
		try {
			return this.getCurrent().getFloat("EPSEstimateNextYear");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public long getVolume() {
		
		if(!this.getCurrent().has("Volume"))return 0;
		
		//if(!(this.getCurrent().get("Volume") instanceof Float))
		//	return 0;
		
		
		try {
			return this.getCurrent().getLong("Volume");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public float getPricePaid() {
		try {
			return this.getCurrent().getFloat("PricePaid");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getAsk() {
		try {
			return this.getCurrent().getFloat("Ask");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getHoldingsValue() {
		try {
			return this.getCurrent().getString("HoldingsValue");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getChangeFromYearLow() {
		try {
			return this.getCurrent().getString("ChangeFromYearLow");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getDividendShare() {
		try {
			return this.getCurrent().getFloat("DividendShare");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getEBITDA() {
		try {
			return this.getCurrent().getString("EBITDA");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getPercebtChangeFromYearHigh() {
		try {
			return this.getCurrent().getString("PercebtChangeFromYearHigh");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getHoldingsGainRealtime() {
		try {
			return this.getCurrent().getString("HoldingsGainRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getPEGRatio() {
		try {
			return this.getCurrent().getFloat("PEGRatio");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getName() {
		
		try {
			return this.getCurrent().getString("Name");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getCommission() {
		try {
			return this.getCurrent().getString("Commission");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getDaysValueChangeRealtime() {
		try {
			return this.getCurrent().getString("DaysValueChangeRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getLastTradeRealtimeWithTime() {
		try {
			return this.getCurrent().getString("LastTradeRealtimeWithTime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getChangePercentRealtime() {
		try {
			return this.getCurrent().getString("ChangePercentRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getHoldingsValueRealtime() {
		try {
			return this.getCurrent().getString("HoldingsValueRealtime");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getEarningsShare() {
		try {
			return this.getCurrent().getFloat("EarningsShare");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getPriceBook() {
		try {
			return this.getCurrent().getFloat("PriceBook");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getShortRatio() {
		try {
			return this.getCurrent().getFloat("ShortRatio");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public float getSharesOwned() {
		try {
			return this.getCurrent().getFloat("SharesOwned");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}

	public String getChangeinPercent() {
		try {
			return this.getCurrent().getString("ChangeinPercent");
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public float getPriceSales() {
		try {
			return this.getCurrent().getFloat("PriceSales");
		} catch (JSONException e) {
			e.printStackTrace();
			return Float.NaN;
		}
	}
	
	
	public static void main(String[] args) {
			
		//YQLQuotes quote=new YQLQuotes("YHOO");
		YQLQuotes quote=new YQLQuotes("EURUSD=X");
		//YQLQuotes quote=new YQLQuotes("GCJ14.CMX");
		//287639=X
		//GCJ14.CMX
	//	quote.addSymbol("YHOO");
	//	quote.addSymbol("DAI.DE");//PAH3.DE
	//	quote.addSymbol("CCC3.DE");
		
	//	System.out.println(quote.getResult().toString(1));
		System.out.println(quote.getCurrent().toString(1));
		
		System.out.println("Date:"+quote.getLastTradeDate());
		System.out.println("Time:"+quote.getLastTradeTime());
		System.out.println("Name:"+quote.getName());
		System.out.println("StockExchange:"+quote.getStockExchange());
		
		//System.out.println("Stock Exchange:"+quote.getStockExchange());
		
		
		//System.out.println(quote.getCurrentQuotePoint());
		
		
	}
	
	
}
