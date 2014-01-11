package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class Estimation extends XmlParameterElement {
	
	
	
	public static final String FIELD_CurrentQtr="CurrentQtr";
	public static final String FIELD_NextQtr="NextQtr";
	public static final String FIELD_CurrentYear="CurrentYear";
	public static final String FIELD_NextYear="NextYear";
	
	
	private float CurrentQtr=Float.NaN;
	private float NextQtr=Float.NaN;
	
	private float CurrentYear=Float.NaN;
	private float NextYear=Float.NaN;
	
	private String tagName="";
	
	public Estimation(String tagName){
		super();
		this.tagName=tagName;
	}
	public Estimation(String tagName,Element childElement){
		super();
		this.tagName=tagName;
		this.init(childElement);
	}
	//
	
	
	public boolean update(Estimation other){
		boolean isUpdated=false;
		if(!this.getTagName().equals(other.getTagName()))
			return isUpdated;
		
		//System.out.println(this);
		//System.out.println(other);
		
		if(   !Float.isNaN(other.CurrentQtr) &&  this.CurrentQtr!=other.CurrentQtr){
			this.setCurrentQtr(other.CurrentQtr);isUpdated=true;
		}
		if( !Float.isNaN(other.NextQtr) &&this.NextQtr!=other.NextQtr){
			this.setNextQtr(other.NextQtr);isUpdated=true;
		}
		if( !Float.isNaN(other.CurrentYear) &&this.CurrentYear!=other.CurrentYear){
			this.setCurrentYear(other.CurrentYear);isUpdated=true;
		}
		if( !Float.isNaN(other.NextYear) &&this.NextYear!=other.NextYear){
			this.setNextYear(other.NextYear);isUpdated=true;
		}
		
		//System.out.println("IsUpdated: "+isUpdated);
		
		return isUpdated;
		
	}
	

	@Override
	public String getTagName() {
		if(!tagName.isEmpty())return this.tagName;
		return super.getTagName();
	}

	public float getCurrentQtr() {
		return CurrentQtr;
	}

	public void setCurrentQtr(float currentQtr) {
		changes.firePropertyChange(FIELD_CurrentQtr, this.CurrentQtr,
				this.CurrentQtr = currentQtr);
	}

	public float getNextQtr() {
		return NextQtr;
	}

	public void setNextQtr(float nextQtr) {
		changes.firePropertyChange(FIELD_NextQtr, this.NextQtr,
				this.NextQtr = nextQtr);
	}

	public float getCurrentYear() {
		return CurrentYear;
	}

	public void setCurrentYear(float currentYear) {
		changes.firePropertyChange(FIELD_CurrentYear, this.CurrentYear,
				this.CurrentYear = currentYear);
	}

	public float getNextYear() {
		return NextYear;
	}

	public void setNextYear(float nextYear) {
		changes.firePropertyChange(FIELD_NextYear, this.NextYear,
				this.NextYear = nextYear);
	}
	
	

	@Override
	public String toString() {
		return "Estimation [CurrentQtr=" + CurrentQtr + ", NextQtr=" + NextQtr
				+ ", CurrentYear=" + CurrentYear + ", NextYear=" + NextYear
				+ ", tagName=" + tagName + "]";
	}
	@Override
	protected void initAttribute(Element rootElement) {
		this.setCurrentQtr(Float.parseFloat(rootElement.getAttribute(FIELD_CurrentQtr)));
		this.setNextQtr(Float.parseFloat(rootElement.getAttribute(FIELD_NextQtr)));
		this.setCurrentYear(Float.parseFloat(rootElement.getAttribute(FIELD_CurrentYear)));
		this.setNextYear(Float.parseFloat(rootElement.getAttribute(FIELD_NextYear)));

	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_CurrentQtr,String.valueOf(this.getCurrentQtr()));
		rootElement.setAttribute(FIELD_NextQtr,String.valueOf(this.getNextQtr()));
		rootElement.setAttribute(FIELD_CurrentYear,String.valueOf(this.getCurrentYear()));
		rootElement.setAttribute(FIELD_NextYear,String.valueOf(this.getNextYear()));
	}

	@Override
	protected void appendChild(Element rootElement,Document doc) {}

}
