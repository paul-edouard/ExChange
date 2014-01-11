package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EPSTrends extends XmlParameterElement {
	
	static final String FIELD_CurrentEstimate="CurrentEstimate";
	static final String FIELD__7DaysAgo="_7DaysAgo";
	static final String FIELD__30DaysAgo="_30DaysAgo";
	static final String FIELD__60DaysAgo="_60DaysAgo";
	static final String FIELD__90DaysAgo="_90DaysAgo";
	
	
	private Estimation CurrentEstimate=new Estimation(FIELD_CurrentEstimate);
	private Estimation _7DaysAgo=new Estimation(FIELD__7DaysAgo);
	private Estimation _30DaysAgo=new Estimation(FIELD__30DaysAgo);
	private Estimation _60DaysAgo=new Estimation(FIELD__60DaysAgo);
	private Estimation _90DaysAgo=new Estimation(FIELD__90DaysAgo);
	
	
	public boolean update(EPSTrends other){
		boolean isUpdated=false;
		if(!this.getTagName().equals(other.getTagName()))
			return isUpdated;
		
		if(this.getCurrentEstimate().update(other.getCurrentEstimate()))
			isUpdated=true;
		if(this.get_7DaysAgo().update(other.get_7DaysAgo()))
			isUpdated=true;
		if(this.get_30DaysAgo().update(other.get_30DaysAgo()))
			isUpdated=true;
		if(this.get_60DaysAgo().update(other.get_60DaysAgo()))
			isUpdated=true;
		if(this.get_90DaysAgo().update(other.get_90DaysAgo()))
			isUpdated=true;
		
		
		return isUpdated;
		
	}
	
	public Estimation getCurrentEstimate() {
		return CurrentEstimate;
	}

	public void setCurrentEstimate(Estimation currentEstimate) {
		changes.firePropertyChange(FIELD_CurrentEstimate, this.CurrentEstimate,
				this.CurrentEstimate = currentEstimate);
	}

	public Estimation get_7DaysAgo() {
		return _7DaysAgo;
	}

	public void set_7DaysAgo(Estimation _7DaysAgo) {
	changes.firePropertyChange(FIELD__7DaysAgo, this._7DaysAgo, this._7DaysAgo = _7DaysAgo);}
	

	public Estimation get_30DaysAgo() {
		return _30DaysAgo;
	}

	public void set_30DaysAgo(Estimation _30DaysAgo) {
	changes.firePropertyChange(FIELD__30DaysAgo, this._30DaysAgo, this._30DaysAgo = _30DaysAgo);}
	

	public Estimation get_60DaysAgo() {
		return _60DaysAgo;
	}

	public void set_60DaysAgo(Estimation _60DaysAgo) {
	changes.firePropertyChange(FIELD__60DaysAgo, this._60DaysAgo, this._60DaysAgo = _60DaysAgo);}
	

	public Estimation get_90DaysAgo() {
		return _90DaysAgo;
	}

	public void set_90DaysAgo(Estimation _90DaysAgo) {
	changes.firePropertyChange(FIELD__90DaysAgo, this._90DaysAgo, this._90DaysAgo = _90DaysAgo);}
	

	@Override
	public String toString() {
		return "EPSTrends [CurrentEstimate=" + CurrentEstimate + ", _7DaysAgo="
				+ _7DaysAgo + ", _30DaysAgo=" + _30DaysAgo + ", _60DaysAgo="
				+ _60DaysAgo + ", _90DaysAgo=" + _90DaysAgo + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_CurrentEstimate)){
			this.setCurrentEstimate(new Estimation(FIELD_CurrentEstimate,childElement));
		}
		else if(childElement.getTagName().equals(FIELD__7DaysAgo)){
			this.set_7DaysAgo(new Estimation(FIELD__7DaysAgo,childElement));
		}
		else if(childElement.getTagName().equals(FIELD__30DaysAgo)){
			this.set_30DaysAgo(new Estimation(FIELD__30DaysAgo,childElement));
		}
		else if(childElement.getTagName().equals(FIELD__60DaysAgo)){
			this.set_60DaysAgo(new Estimation(FIELD__60DaysAgo,childElement));
		}
		else if(childElement.getTagName().equals(FIELD__90DaysAgo)){
			this.set_90DaysAgo(new Estimation(FIELD__90DaysAgo,childElement));
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getCurrentEstimate().toDomElement(doc));
		rootElement.appendChild(this.get_7DaysAgo().toDomElement(doc));
		rootElement.appendChild(this.get_30DaysAgo().toDomElement(doc));
		rootElement.appendChild(this.get_60DaysAgo().toDomElement(doc));
		rootElement.appendChild(this.get_90DaysAgo().toDomElement(doc));

	}

}
