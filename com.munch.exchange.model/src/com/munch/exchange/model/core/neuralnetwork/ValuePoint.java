package com.munch.exchange.model.core.neuralnetwork;

import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ValuePoint extends XmlParameterElement implements Comparable<ValuePoint> {
	
	static final String FIELD_Date="Date";
	static final String FIELD_Value="Value";
	static final String FIELD_NextValueDate="NextValueDate";
	static final String FIELD_MetaData="MetaData";
	
	
	private Calendar date=Calendar.getInstance();
	private Calendar nextValueDate=null;
	private double value=0;
	private String metaData=null;
	
	public ValuePoint(Calendar date,double value){
		this.date=date;
		this.value=value;
	}
	
	public ValuePoint(){
		
	}
	
	
	public Calendar getNextValueDate() {
		return nextValueDate;
	}

	public void setNextValueDate(Calendar nextValueDate) {
	changes.firePropertyChange(FIELD_NextValueDate, this.nextValueDate, this.nextValueDate = nextValueDate);}
	

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
	changes.firePropertyChange(FIELD_Date, this.date, this.date = date);}
	

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
	changes.firePropertyChange(FIELD_Value, this.value, this.value = value);}
	
	
	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
	changes.firePropertyChange(FIELD_MetaData, this.metaData, this.metaData = metaData);
	}
	

	@Override
	public String toString() {
		return "ValuePoint [date=" + DateTool.dateToString(date) + ", nextValueDate=" + DateTool.dateToString(nextValueDate)
				+ ", value=" + value + ", metaData=" + metaData + "]";
	}

	@Override
	
	protected void initAttribute(Element rootElement) {
		this.setDate(DateTool.StringToDate(rootElement.getAttribute(FIELD_Date)));
		this.setValue(Double.valueOf(rootElement.getAttribute(FIELD_Value)));
		if(rootElement.hasAttribute(FIELD_MetaData)){
			this.setMetaData(rootElement.getAttribute(FIELD_MetaData));
		}
	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Date,DateTool.dateToString( this.getDate()));
		rootElement.setAttribute(FIELD_Value,String.valueOf( this.getValue()));
		rootElement.setAttribute(FIELD_MetaData,String.valueOf( this.getMetaData()));
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {}

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
		
		if (!(obj instanceof ValuePoint)) {
			return false;
		}
		ValuePoint other = (ValuePoint) obj;
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
	public int compareTo(ValuePoint o) {
		 if( this.date.getTimeInMillis() < o.date.getTimeInMillis() )   
			 return -1;        
		 if( this.date.getTimeInMillis() >  o.date.getTimeInMillis())  
			 return 1; 
		return 0;
	}

}
