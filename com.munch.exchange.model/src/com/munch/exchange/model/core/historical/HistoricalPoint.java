package com.munch.exchange.model.core.historical;

import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;

public class HistoricalPoint extends DatePoint {
	
	

	
	private float low,open,adj_close,high,close=0;
	private long volume=0;

	public enum Type { CLOSE, OPEN,
						HIGH, LOW ,
						MEDIAN_PRICE, TYPICAL_PRICE,
						WEIGHtED_CLOSE,NONE};
	
	public float get(Type type){
		switch (type) {
		case CLOSE:
			return this.getClose();
		case OPEN:
			return this.getOpen();
		case HIGH:
			return this.getHigh();			
		case LOW:
			return this.getLow();	
		case MEDIAN_PRICE:
			return (this.getLow()+this.getHigh())/2;	
		case TYPICAL_PRICE:
			return (this.getLow()+this.getClose()+this.getHigh())/3;
		case WEIGHtED_CLOSE:
			return (this.getLow()+2+this.getClose()+this.getHigh())/4;
		default:
			return 0;
		}
	}
	
	public double getDouble(Type type){
		return (double)get(type);
	}
	
						
	public float get(String field){
		if(field.equals(FIELD_Low))return this.getLow();
		else if(field.equals(FIELD_Open))return this.getOpen();
		else if(field.equals(FIELD_Adj_Close))return this.getAdjClose();
		else if(field.equals(FIELD_Close))return this.getClose();
		else if(field.equals(FIELD_High))return this.getHigh();
		else if(field.equals(FIELD_Volume))return this.getVolume();
		else return Float.NaN;
	}
	

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
		this.setHigh(Float.valueOf(Root.getAttribute(FIELD_High)));
		this.setLow(Float.valueOf(Root.getAttribute(FIELD_Low)));
		this.setOpen(Float.valueOf(Root.getAttribute(FIELD_Open)));
		this.setClose(Float.valueOf(Root.getAttribute(FIELD_Close)));
		this.setVolume(Long.valueOf(Root.getAttribute(FIELD_Volume)));
		
		super.initAttribute(Root);
	}

	
	protected void setAttribute(Element e){
		e.setAttribute(FIELD_Adj_Close,String.valueOf(this.getAdjClose()));
		e.setAttribute(FIELD_High,String.valueOf(this.getHigh()));
		e.setAttribute(FIELD_Low,String.valueOf(this.getLow()));
		e.setAttribute(FIELD_Open,String.valueOf(this.getOpen()));
		e.setAttribute(FIELD_Volume,String.valueOf(this.getVolume()));
		e.setAttribute(FIELD_Close,String.valueOf(this.getClose()));
		
		super.setAttribute(e);
	}
	
	

}
