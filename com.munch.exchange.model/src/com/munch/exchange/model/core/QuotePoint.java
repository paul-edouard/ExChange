package com.munch.exchange.model.core;

import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.ParameterElement;
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
	
	@Override
	public String getTagName() {
		return "quote";
	}
	
	@Override
	public void init(Element Root){
		/*
		if(Root.getTagName().equals(this.getTagName())){
			
			this.setAdjClose(Float.valueOf(Root.getAttribute(AdjCloseStr)));
			setDateString(Root.getAttribute(DateStr));
			this.setHigh(Float.valueOf(Root.getAttribute(HighStr)));
			this.setLow(Float.valueOf(Root.getAttribute(LowStr)));
			this.setOpen(Float.valueOf(Root.getAttribute(OpenStr)));
			this.setClose(Float.valueOf(Root.getAttribute(CloseStr)));
			this.setVolume(Long.valueOf(Root.getAttribute(VolumeStr)));
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Parameter
					if(childElement.getTagName().equals(new Parameter().getTagName())){
						this.setParameter(new Parameter(childElement));
					}
					
				}
			}
			
			
		}
		*/
	}
	
	
	/**
	 * export the user map in a xml element
	 */
	@Override
	public Element toDomElement(Document doc){
		/*
		Element e=doc.createElement(this.getTagName());
			
		e.setAttribute(AdjCloseStr,String.valueOf(this.getAdjClose()));
		e.setAttribute(DateStr, this.getDateString());
		e.setAttribute(HighStr,String.valueOf(this.getHigh()));
		e.setAttribute(LowStr,String.valueOf(this.getLow()));
		e.setAttribute(OpenStr,String.valueOf(this.getOpen()));
		e.setAttribute(VolumeStr,String.valueOf(this.getVolume()));
		e.setAttribute(CloseStr,String.valueOf(this.getClose()));
		
		//Parameter
		e.appendChild(this.getParameter().toDomElement(doc));
	
		return e;
		*/
		return null;
	  }

	
}
