package com.munch.exchange.model.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlElementIF;

public class QuotePoint extends ParameterElement implements XmlElementIF {
	
	static final String DateStr="date";
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
	
	
	
	public static String dateToString(Calendar date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return format.format(date.getTime());
	}
	

	@Override
	public String toString() {
		return "QuotePoint [date=" + dateToString(date) + ", averageDailyVolume="
				+ averageDailyVolume + ", change=" + change + ", daysLow="
				+ daysLow + ", daysHigh=" + daysHigh + ", yearLow=" + yearLow
				+ ", yearHigh=" + yearHigh + ", marketCapitalization="
				+ marketCapitalization + ", lastTradePrice=" + lastTradePrice
				+ ", volume=" + volume + ", lastTradeDate=" + dateToString(lastTradeDate)
				+ "]";
	}

	@Override
	public Element toDomElement(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Element Root) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTagName() {
		return "quote";
	}

}
