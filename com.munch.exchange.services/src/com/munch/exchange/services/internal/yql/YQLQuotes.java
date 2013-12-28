package com.munch.exchange.services.internal.yql;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.munch.exchange.model.core.Parameter;
import com.munch.exchange.model.core.QuotePoint;
import com.munch.exchange.services.internal.yql.json.JSONArray;
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
		else
			return this.getResult().getJSONObject("quote");
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
		
		return point;
	}
	
	
	/*
	 * Quote Data
	 */
	public float getOpen(){
		return this.getCurrent().getFloat("Open");
	}
	public float getPriceEPSEstimateCurrentYear(){
		return  this.getCurrent().getFloat("PriceEPSEstimateCurrentYear");
	}
	public float getBookValue(){
		return  this.getCurrent().getFloat("BookValue");
	}
	public float getBid(){
		return  this.getCurrent().getFloat("Bid");
	}
	public float getDaysHigh(){
		return  this.getCurrent().getFloat("DaysHigh");
	}
	public float getChangeFromFiftydayMovingAverage(){
		return  this.getCurrent().getFloat("ChangeFromFiftydayMovingAverage");
	}
	public float getOrderBookRealtime(){
		return  this.getCurrent().getFloat("OrderBookRealtime");
	}
	public String getErrorIndicationreturnedforsymbolchangedinvalid(){
		return  this.getCurrent().getString("ErrorIndicationreturnedforsymbolchangedinvalid");
	}
	public String getDaysRange(){
		return  this.getCurrent().getString("DaysRange");
	}
	public String getMoreInfo(){
		return  this.getCurrent().getString("MoreInfo");
	}
	public float getAnnualizedGain(){
		return  this.getCurrent().getFloat("AnnualizedGain");
	}
	public String getChange_PercentChange(){
		return  this.getCurrent().getString("Change_PercentChange");
	}
	public String getDaysRangeRealtime(){
		return  this.getCurrent().getString("DaysRangeRealtime");
	}
	public float getLowLimit(){
		return  this.getCurrent().getFloat("LowLimit");
	}
	public String getPercentChangeFromTwoHundreddayMovingAverage(){
		return  this.getCurrent().getString("PercentChangeFromTwoHundreddayMovingAverage");
	}
	public float getChangeFromTwoHundreddayMovingAverage(){
		return  this.getCurrent().getFloat("ChangeFromTwoHundreddayMovingAverage");
	}
	public float getDividendYield(){
		return  this.getCurrent().getFloat("DividendYield");
	}
	public float getEPSEstimateCurrentYear(){
		return  this.getCurrent().getFloat("EPSEstimateCurrentYear");
	}
	public String getLastTradeDate(){
		return  this.getCurrent().getString("LastTradeDate");
	}
	public float getTwoHundreddayMovingAverage(){
		return  this.getCurrent().getFloat("TwoHundreddayMovingAverage");
	}
	public float getAskRealtime(){
		return  this.getCurrent().getFloat("AskRealtime");
	}
	public String getPercentChange(){
		return  this.getCurrent().getString("PercentChange");
	}
	public String getDividendPayDate(){
		return  this.getCurrent().getString("DividendPayDate");
	}
	public String getYearRange(){
		return  this.getCurrent().getString("YearRange");
	}
	public String getsymbol(){
		return  this.getCurrent().getString("symbol");
	}
	public String getPercentChangeFromFiftydayMovingAverage(){
		return  this.getCurrent().getString("PercentChangeFromFiftydayMovingAverage");
	}
	public float getChange(){
		return  this.getCurrent().getFloat("Change");
	}
	public float getHoldingsGainPercent(){
		return  this.getCurrent().getFloat("HoldingsGainPercent");
	}
	public String getNotes(){
		return  this.getCurrent().getString("Notes");
	}
	public float getHoldingsGain(){
		return  this.getCurrent().getFloat("HoldingsGain");
	}
	public float getYearHigh(){
		return  this.getCurrent().getFloat("YearHigh");
	}
	public String getSymbol(){
		return  this.getCurrent().getString("Symbol");
	}
	public String getAfterHoursChangeRealtime(){
		return  this.getCurrent().getString("AfterHoursChangeRealtime");
	}
	public String getHoldingsGainPercentRealtime(){
		return  this.getCurrent().getString("HoldingsGainPercentRealtime");
	}
	public String getMarketCapitalization(){
		return  this.getCurrent().getString("MarketCapitalization");
	}
	public float getBidRealtime(){
		return  this.getCurrent().getFloat("BidRealtime");
	}
	public float getLastTradePriceOnly(){
		return  this.getCurrent().getFloat("LastTradePriceOnly");
	}
	public float getPERatio(){
		return  this.getCurrent().getFloat("PERatio");
	}
	public float getEPSEstimateNextQuarter(){
		return  this.getCurrent().getFloat("EPSEstimateNextQuarter");
	}
	public float getMarketCapRealtime(){
		return  this.getCurrent().getFloat("MarketCapRealtime");
	}
	public long getAverageDailyVolume(){
		return  this.getCurrent().getLong("AverageDailyVolume");
	}
	public String getPercentChangeFromYearLow(){
		return  this.getCurrent().getString("PercentChangeFromYearLow");
	}
	public String getTickerTrend(){
		return  this.getCurrent().getString("TickerTrend");
	}
	public float getChangeFromYearHigh(){
		return  this.getCurrent().getFloat("ChangeFromYearHigh");
	}
	public String getLastTradeWithTime(){
		return  this.getCurrent().getString("LastTradeWithTime");
	}
	public float getPERatioRealtime(){
		return  this.getCurrent().getFloat("PERatioRealtime");
	}
	public String getStockExchange(){
		return  this.getCurrent().getString("StockExchange");
	}
	public float getPreviousClose(){
		return  this.getCurrent().getFloat("PreviousClose");
	}
	public float getFiftydayMovingAverage(){
		return  this.getCurrent().getFloat("FiftydayMovingAverage");
	}
	public String getLastTradeTime(){
		return  this.getCurrent().getString("LastTradeTime");
	}
	public float getDaysLow(){
		return  this.getCurrent().getFloat("DaysLow");
	}
	public float getPriceEPSEstimateNextYear(){
		return  this.getCurrent().getFloat("PriceEPSEstimateNextYear");
	}
	public String getDaysValueChange(){
		return  this.getCurrent().getString("DaysValueChange");
	}
	public float getHighLimit(){
		return  this.getCurrent().getFloat("HighLimit");
	}
	public String getTradeDate(){
		return  this.getCurrent().getString("TradeDate");
	}
	public float getOneyrTargetPrice(){
		return  this.getCurrent().getFloat("OneyrTargetPrice");
	}
	public float getChangeRealtime(){
		return  this.getCurrent().getFloat("ChangeRealtime");
	}
	public String getExDividendDate(){
		return  this.getCurrent().getString("ExDividendDate");
	}
	public float getYearLow(){
		return  this.getCurrent().getFloat("YearLow");
	}
	public float getEPSEstimateNextYear(){
		return  this.getCurrent().getFloat("EPSEstimateNextYear");
	}
	public long getVolume(){
		return  this.getCurrent().getLong("Volume");
	}
	public float getPricePaid(){
		return  this.getCurrent().getFloat("PricePaid");
	}
	public float getAsk(){
		return  this.getCurrent().getFloat("Ask");
	}
	public String getHoldingsValue(){
		return  this.getCurrent().getString("HoldingsValue");
	}
	public String getChangeFromYearLow(){
		return  this.getCurrent().getString("ChangeFromYearLow");
	}
	public float getDividendShare(){
		return  this.getCurrent().getFloat("DividendShare");
	}
	public String getEBITDA(){
		return  this.getCurrent().getString("EBITDA");
	}
	public String getPercebtChangeFromYearHigh(){
		return  this.getCurrent().getString("PercebtChangeFromYearHigh");
	}
	public String getHoldingsGainRealtime(){
		return  this.getCurrent().getString("HoldingsGainRealtime");
	}
	public float getPEGRatio(){
		return  this.getCurrent().getFloat("PEGRatio");
	}
	public String getName(){
		return  this.getCurrent().getString("Name");
	}
	public String getCommission(){
		return  this.getCurrent().getString("Commission");
	}
	public String getDaysValueChangeRealtime(){
		return  this.getCurrent().getString("DaysValueChangeRealtime");
	}
	public String getLastTradeRealtimeWithTime(){
		return  this.getCurrent().getString("LastTradeRealtimeWithTime");
	}
	public String getChangePercentRealtime(){
		return  this.getCurrent().getString("ChangePercentRealtime");
	}
	public String getHoldingsValueRealtime(){
		return  this.getCurrent().getString("HoldingsValueRealtime");
	}
	public float getEarningsShare(){
		return  this.getCurrent().getFloat("EarningsShare");
	}
	public float getPriceBook(){
		return  this.getCurrent().getFloat("PriceBook");
	}
	public float getShortRatio(){
		return  this.getCurrent().getFloat("ShortRatio");
	}
	public float getSharesOwned(){
		return  this.getCurrent().getFloat("SharesOwned");
	}
	public String getChangeinPercent(){
		return  this.getCurrent().getString("ChangeinPercent");
	}
	public float getPriceSales(){
		return  this.getCurrent().getFloat("PriceSales");
	}
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		
		YQLQuotes quote=new YQLQuotes("DAI.DE");
	//	quote.addSymbol("YHOO");
	//	quote.addSymbol("DAI.DE");//PAH3.DE
	//	quote.addSymbol("CCC3.DE");
		
	//	System.out.println(quote.getResult().toString(1));
		System.out.println(quote.getCurrent().toString(1));
		
		System.out.println("Date:"+quote.getLastTradeDate());
		System.out.println("Time:"+quote.getLastTradeTime());
		
		System.out.println(quote.getCurrentQuotePoint());
		
		
	}
	
	
}
