package com.munch.exchange.model.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;

public class HistoricalPoint extends ParameterElement implements XmlElementIF {
	
	
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
		return "HistoricalPoint ["+"date=" + getDateString() +", low=" + low + ", open=" + open
				+ ", adj_close=" + adj_close + ", high=" + high + ", close="
				+ close + ", volume=" + volume + "]";
	}

	public String getDateString(){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return format.format(date.getTime());
	}
	
	public void setDateString(String dateStr){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			Date d=format.parse(dateStr);
			if(d!=null){
				date.setTime(d);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	/**
	 * return the TAG Name used in the xml file
	 */
	@Override
	public String getTagName(){return "history_point";}
	
	/**
	 * initializes the users map from a xml element
	 */
	@Override
	public void init(Element Root){
		
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
	}
	
	
	/**
	 * export the user map in a xml element
	 */
	@Override
	public Element toDomElement(Document doc){
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
	  }

	

}
