package com.munch.exchange.model.core.quote;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public class QuotePoint extends DatePoint {
	
	
	
	static final String FIELD_Average_Daily_Volume="averageDailyVolume";
	static final String FIELD_Change="change";
	static final String FIELD_Days_Low="daysLow";
	static final String FIELD_Days_High="daysHigh";
	static final String FIELD_Year_Low="yearLow";
	static final String FIELD_Year_High="yearHigh";
	static final String FIELD_Market_Capitalization="marketCapitalization";
	static final String FIELD_Last_Trade_Price="lastTradePrice";
	static final String FIELD_Volume="volume";
	static final String FIELD_Last_Trade_Date="lastTradeDate";
	
	
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
	

	


	public long getAverageDailyVolume() {
		return averageDailyVolume;
	}


	public void setAverageDailyVolume(long averageDailyVolume) {
		changes.firePropertyChange(FIELD_Average_Daily_Volume, this.averageDailyVolume, this.averageDailyVolume = averageDailyVolume);
	}


	public float getChange() {
		return change;
	}


	public void setChange(float change) {
		changes.firePropertyChange(FIELD_Change, this.change, this.change = change);
		this.change = change;
	}


	public float getDaysLow() {
		return daysLow;
	}


	public void setDaysLow(float daysLow) {
		changes.firePropertyChange(FIELD_Days_Low, this.daysLow, this.daysLow = daysLow);
	}


	public float getDaysHigh() {
		return daysHigh;
	}


	public void setDaysHigh(float daysHigh) {
		changes.firePropertyChange(FIELD_Days_High, this.daysHigh, this.daysHigh = daysHigh);
	}


	public float getYearLow() {
		return yearLow;
	}


	public void setYearLow(float yearLow) {
		changes.firePropertyChange(FIELD_Year_Low, this.yearLow, this.yearLow = yearLow);
	}


	public float getYearHigh() {
		return yearHigh;
	}


	public void setYearHigh(float yearHigh) {
		changes.firePropertyChange(FIELD_Year_High, this.yearHigh, this.yearHigh = yearHigh);
		//this.yearHigh = yearHigh;
	}


	public String getMarketCapitalization() {
		return marketCapitalization;
	}


	public void setMarketCapitalization(String marketCapitalization) {
		changes.firePropertyChange(FIELD_Market_Capitalization, this.marketCapitalization, this.marketCapitalization = marketCapitalization);
		//this.marketCapitalization = marketCapitalization;
	}


	public float getLastTradePrice() {
		return lastTradePrice;
	}


	public void setLastTradePrice(float lastTradePrice) {
		changes.firePropertyChange(FIELD_Last_Trade_Date, this.lastTradePrice, this.lastTradePrice = lastTradePrice);
		//this.lastTradePrice = lastTradePrice;
	}


	public long getVolume() {
		return volume;
	}


	public void setVolume(long volume) {
		changes.firePropertyChange(FIELD_Volume, this.volume, this.volume = volume);
		//this.volume = volume;
	}


	public Calendar getLastTradeDate() {
		return lastTradeDate;
	}


	public void setLastTradeDate(Calendar lastTradeDate) {
		changes.firePropertyChange(FIELD_Last_Trade_Date, this.lastTradeDate, this.lastTradeDate = lastTradeDate);
		//this.lastTradeDate = lastTradeDate;
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
		
		this.setAverageDailyVolume(Long.parseLong(Root.getAttribute(FIELD_Average_Daily_Volume)));
		this.setChange(Float.valueOf(Root.getAttribute(FIELD_Change)));
		this.setDaysHigh(Float.valueOf(Root.getAttribute(FIELD_Days_High)));
		this.setDaysLow(Float.valueOf(Root.getAttribute(FIELD_Days_Low)));
		this.setLastTradeDate(DateTool.StringToDate(Root.getAttribute(FIELD_Last_Trade_Date)));
		this.setLastTradePrice(Float.valueOf(Root.getAttribute(FIELD_Last_Trade_Price)));
		this.setMarketCapitalization(Root.getAttribute(FIELD_Market_Capitalization));
		this.setVolume(Long.parseLong(Root.getAttribute(FIELD_Volume)));
		this.setYearHigh(Float.valueOf(Root.getAttribute(FIELD_Year_High)));
		this.setYearLow(Float.valueOf(Root.getAttribute(FIELD_Year_Low)));
		
		super.initAttribute(Root);
	}
	
	
	
	protected void setAttribute(Element e){
		
		e.setAttribute(FIELD_Average_Daily_Volume,String.valueOf(this.getAverageDailyVolume()));
		e.setAttribute(FIELD_Change,String.valueOf(this.getChange()));
		e.setAttribute(FIELD_Days_High,String.valueOf(this.getDaysHigh()));
		e.setAttribute(FIELD_Days_Low,String.valueOf(this.getDaysLow()));
		e.setAttribute(FIELD_Last_Trade_Date,String.valueOf(this.getLastTradeDate()));
		e.setAttribute(FIELD_Last_Trade_Price,String.valueOf(this.getLastTradePrice()));
		e.setAttribute(FIELD_Market_Capitalization,String.valueOf(this.getMarketCapitalization()));
		e.setAttribute(FIELD_Volume,String.valueOf(this.getVolume()));
		e.setAttribute(FIELD_Year_High,String.valueOf(this.getYearHigh()));
		e.setAttribute(FIELD_Year_Low,String.valueOf(this.getYearLow()));
		
		super.setAttribute(e);
	}

	
}
