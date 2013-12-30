package com.munch.exchange.model.core.historical;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class HistoricalPoint extends XmlParameterElement {
	
	
	static final String FIELD_Low="low";
	static final String FIELD_Open="open";
	static final String FIELD_Adj_Close="adj_close";
	static final String FIELD_Close="close";
	static final String FIELD_High="high";
	static final String FIELD_Volume="volume";
	static final String FIELD_Date="date";
	
	private float low,open,adj_close,high,close=0;
	private long volume=0;
	private Calendar date=Calendar.getInstance();
	
	
	
	
	

	

	public float getLow() {
		return low;
	}



	public void setLow(float low) {
		changes.firePropertyChange(FIELD_Low, this.low, this.low = low);
		//this.low = low;
	}



	public float getOpen() {
		return open;
	}



	public void setOpen(float open) {
		changes.firePropertyChange(FIELD_Open, this.open, this.open = open);
		//this.open = open;
	}



	public float getAdjClose() {
		return adj_close;
	}



	public void setAdjClose(float adj_close) {
		changes.firePropertyChange(FIELD_Adj_Close, this.adj_close, this.adj_close = adj_close);
		//this.adj_close = adj_close;
	}



	public float getHigh() {
		return high;
	}



	public void setHigh(float high) {
		changes.firePropertyChange(FIELD_High, this.high, this.high = high);
		//this.high = high;
	}



	public float getClose() {
		return close;
	}



	public void setClose(float close) {
		changes.firePropertyChange(FIELD_Close, this.close, this.close = close);
		//this.close = close;
	}



	public long getVolume() {
		return volume;
	}



	public void setVolume(long volume) {
		changes.firePropertyChange(FIELD_Volume, this.volume, this.volume = volume);
		//this.volume = volume;
	}



	public Calendar getDate() {
		return date;
	}



	public void setDate(Calendar date) {
		changes.firePropertyChange(FIELD_Date, this.date, this.date = date);
		//this.date = date;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof HistoricalPoint)) {
			return false;
		}
		HistoricalPoint other = (HistoricalPoint) obj;
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		return true;
	}



	@Override
	public String toString() {
		return "HistoricalPoint ["+"date=" + DateTool.dateToString(date) +", low=" + low + ", open=" + open
				+ ", adj_close=" + adj_close + ", high=" + high + ", close="
				+ close + ", volume=" + volume + "]";
	}

	
	
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	protected void initAttribute(Element Root){
		this.setAdjClose(Float.valueOf(Root.getAttribute(FIELD_Adj_Close)));
		this.setDate(DateTool.StringToDate(Root.getAttribute(FIELD_Date)));
		this.setHigh(Float.valueOf(Root.getAttribute(FIELD_High)));
		this.setLow(Float.valueOf(Root.getAttribute(FIELD_Low)));
		this.setOpen(Float.valueOf(Root.getAttribute(FIELD_Open)));
		this.setClose(Float.valueOf(Root.getAttribute(FIELD_Close)));
		this.setVolume(Long.valueOf(Root.getAttribute(FIELD_Volume)));
	}
	
	protected void initChild(Element childElement){}
	
	
	protected void setAttribute(Element e){
		e.setAttribute(FIELD_Adj_Close,String.valueOf(this.getAdjClose()));
		e.setAttribute(FIELD_Date,DateTool.dateToString( this.getDate()));
		e.setAttribute(FIELD_High,String.valueOf(this.getHigh()));
		e.setAttribute(FIELD_Low,String.valueOf(this.getLow()));
		e.setAttribute(FIELD_Open,String.valueOf(this.getOpen()));
		e.setAttribute(FIELD_Volume,String.valueOf(this.getVolume()));
		e.setAttribute(FIELD_Close,String.valueOf(this.getClose()));
	}
	
	protected void appendChild(Element rootElement){}
	
	
	

}
