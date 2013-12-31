package com.munch.exchange.model.core;

import java.util.Calendar;

import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public abstract class DatePoint extends XmlParameterElement implements Comparable<DatePoint>{
	
	static final String FIELD_Date="date";
	
	protected Calendar date=Calendar.getInstance();
	
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
		
		if (!(obj instanceof DatePoint)) {
			return false;
		}
		DatePoint other = (DatePoint) obj;
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
	public int compareTo(DatePoint o) {
		 if( this.date.getTimeInMillis() < o.date.getTimeInMillis() )   
			 return -1;        
		 if( this.date.getTimeInMillis() >  o.date.getTimeInMillis())  
			 return 1; 
		return 0;
	}

	@Override
	protected void initAttribute(Element rootElement) {
		this.setDate(DateTool.StringToDate(rootElement.getAttribute(FIELD_Date)));
	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Date,DateTool.dateToString( this.getDate()));
	}

	@Override
	protected void appendChild(Element rootElement) {}
	

}
