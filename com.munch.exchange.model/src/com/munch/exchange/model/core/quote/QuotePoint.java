package com.munch.exchange.model.core.quote;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class QuotePoint extends XmlParameterElement {
	
	static final String DateStr="date";
	
	static final String AverageDailyVolumeStr="averageDailyVolume";
	static final String ChangeStr="change";
	static final String DaysLowStr="daysLow";
	static final String DaysHighStr="daysHigh";
	static final String YearLowStr="yearLow";
	static final String YearHighStr="yearHigh";
	static final String MarketCapitalizationStr="marketCapitalization";
	static final String LastTradePriceStr="lastTradePrice";
	static final String VolumeStr="volume";
	static final String LastTradeDateStr="lastTradeDate";
	
	private Calendar date=Calendar.getInstance();
	
	private long averageDailyVolume;
	private float change;
	private float daysLow;
	private float daysHigh;
	private float yearLow;
	private float yearHigh;
	
	private String marketCapitalization;
	private float lastTradePrice;
	private long volume;
	
	private Calendar lastTradeDate=Calendar.getInstance();
	
	
	public Calendar getDate() {
		return date;
	}
	

	public void setDate(Calendar date) {
		this.date = date;
	}


	public long getAverageDailyVolume() {
		return averageDailyVolume;
	}

	public void setAverageDailyVolume(long averageDailyVolume) {
		this.averageDailyVolume = averageDailyVolume;
	}

	public float getChange() {
		return change;
	}

	public void setChange(float change) {
		this.change = change;
	}

	public float getDaysLow() {
		return daysLow;
	}

	public void setDaysLow(float daysLow) {
		this.daysLow = daysLow;
	}

	public float getDaysHigh() {
		return daysHigh;
	}

	public void setDaysHigh(float daysHigh) {
		this.daysHigh = daysHigh;
	}

	public float getYearLow() {
		return yearLow;
	}

	public void setYearLow(float yearLow) {
		this.yearLow = yearLow;
	}

	public float getYearHigh() {
		return yearHigh;
	}

	public void setYearHigh(float yearHigh) {
		this.yearHigh = yearHigh;
	}

	public String getMarketCapitalization() {
		return marketCapitalization;
	}

	public void setMarketCapitalization(String marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}

	public float getLastTradePrice() {
		return lastTradePrice;
	}

	public void setLastTradePrice(float lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public Calendar getLastTradeDate() {
		return lastTradeDate;
	}

	public void setLastTradeDate(Calendar lastTradeDate) {
		this.lastTradeDate = lastTradeDate;
	}

	@Override
	public String toString() {
		return "QuotePoint [date=" + DateTool.dateToString(date) + ", averageDailyVolume="
				+ averageDailyVolume + ", change=" + change + ", daysLow="
				+ daysLow + ", daysHigh=" + daysHigh + ", yearLow=" + yearLow
				+ ", yearHigh=" + yearHigh + ", marketCapitalization="
				+ marketCapitalization + ", lastTradePrice=" + lastTradePrice
				+ ", volume=" + volume + ", lastTradeDate=" + DateTool.dateToString(lastTradeDate)
				+ "]";
	}
	
	
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	protected void initAttribute(Element Root){
		this.setDate(DateTool.StringToDate(Root.getAttribute(DateStr)));
		this.setAverageDailyVolume(Long.parseLong(Root.getAttribute(AverageDailyVolumeStr)));
		this.setChange(Float.valueOf(Root.getAttribute(ChangeStr)));
		this.setDaysHigh(Float.valueOf(Root.getAttribute(DaysHighStr)));
		this.setDaysLow(Float.valueOf(Root.getAttribute(DaysLowStr)));
		this.setLastTradeDate(DateTool.StringToDate(Root.getAttribute(LastTradeDateStr)));
		this.setLastTradePrice(Float.valueOf(Root.getAttribute(LastTradePriceStr)));
		this.setMarketCapitalization(Root.getAttribute(MarketCapitalizationStr));
		this.setVolume(Long.parseLong(Root.getAttribute(VolumeStr)));
		this.setYearHigh(Float.valueOf(Root.getAttribute(YearHighStr)));
		this.setYearLow(Float.valueOf(Root.getAttribute(YearLowStr)));
	}
	
	protected void initChild(Element childElement){}
	
	
	protected void setAttribute(Element e){
		e.setAttribute(DateStr,DateTool.dateToString( this.getDate()));
		e.setAttribute(AverageDailyVolumeStr,String.valueOf(this.getAverageDailyVolume()));
		e.setAttribute(ChangeStr,String.valueOf(this.getChange()));
		e.setAttribute(DaysHighStr,String.valueOf(this.getDaysHigh()));
		e.setAttribute(DaysLowStr,String.valueOf(this.getDaysLow()));
		e.setAttribute(LastTradeDateStr,String.valueOf(this.getLastTradeDate()));
		e.setAttribute(LastTradePriceStr,String.valueOf(this.getLastTradePrice()));
		e.setAttribute(MarketCapitalizationStr,String.valueOf(this.getMarketCapitalization()));
		e.setAttribute(VolumeStr,String.valueOf(this.getVolume()));
		e.setAttribute(YearHighStr,String.valueOf(this.getYearHigh()));
		e.setAttribute(YearLowStr,String.valueOf(this.getYearLow()));
	}
	
	protected void appendChild(Element rootElement){}

	
}
