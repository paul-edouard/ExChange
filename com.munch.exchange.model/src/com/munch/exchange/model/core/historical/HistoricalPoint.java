package com.munch.exchange.model.core.historical;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class HistoricalPoint extends XmlParameterElement {
	
	
	static final String LowStr="low";
	static final String OpenStr="open";
	static final String AdjCloseStr="adj_close";
	static final String CloseStr="close";
	static final String HighStr="high";
	static final String VolumeStr="volume";
	static final String DateStr="date";
	
	private float low,open,adj_close,high,close=0;
	private long volume=0;
	private Calendar date=Calendar.getInstance();
	
	
	
	
	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public float getAdjClose() {
		return adj_close;
	}

	public void setAdjClose(float adj_close) {
		this.adj_close = adj_close;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	
	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
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
		this.setAdjClose(Float.valueOf(Root.getAttribute(AdjCloseStr)));
		this.setDate(DateTool.StringToDate(Root.getAttribute(DateStr)));
		this.setHigh(Float.valueOf(Root.getAttribute(HighStr)));
		this.setLow(Float.valueOf(Root.getAttribute(LowStr)));
		this.setOpen(Float.valueOf(Root.getAttribute(OpenStr)));
		this.setClose(Float.valueOf(Root.getAttribute(CloseStr)));
		this.setVolume(Long.valueOf(Root.getAttribute(VolumeStr)));
	}
	
	protected void initChild(Element childElement){}
	
	
	protected void setAttribute(Element e){
		e.setAttribute(AdjCloseStr,String.valueOf(this.getAdjClose()));
		e.setAttribute(DateStr,DateTool.dateToString( this.getDate()));
		e.setAttribute(HighStr,String.valueOf(this.getHigh()));
		e.setAttribute(LowStr,String.valueOf(this.getLow()));
		e.setAttribute(OpenStr,String.valueOf(this.getOpen()));
		e.setAttribute(VolumeStr,String.valueOf(this.getVolume()));
		e.setAttribute(CloseStr,String.valueOf(this.getClose()));
	}
	
	protected void appendChild(Element rootElement){}
	
	
	

}
