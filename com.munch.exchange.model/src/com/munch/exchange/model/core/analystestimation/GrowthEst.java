package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlHashMap;
import com.munch.exchange.model.xml.XmlParameterElement;

public class GrowthEst extends XmlParameterElement {
	
	static final String FIELD_PEGRatio="PEGRatio";
	static final String FIELD_PriceEarnings="PriceEarnings";
	static final String FIELD_Past5Years="Past5Years";
	static final String FIELD_ThisYear="ThisYear";
	static final String FIELD_CurrentQtr="CurrentQtr";
	static final String FIELD_Next5Years="Next5Years";
	static final String FIELD_NextYear="NextYear";
	static final String FIELD_NextQtr="NextQtr";
	
	XmlHashMap<String, Float> PEGRatio=new XmlHashMap<String, Float>(FIELD_PEGRatio,String.class,Float.class);
	XmlHashMap<String, Float> PriceEarnings=new XmlHashMap<String, Float>(FIELD_PriceEarnings,String.class,Float.class);
	XmlHashMap<String, Float> Past5Years=new XmlHashMap<String, Float>(FIELD_Past5Years,String.class,Float.class);
	XmlHashMap<String, Float> ThisYear=new XmlHashMap<String, Float>(FIELD_ThisYear,String.class,Float.class);
	XmlHashMap<String, Float> CurrentQtr=new XmlHashMap<String, Float>(FIELD_CurrentQtr,String.class,Float.class);
	XmlHashMap<String, Float> Next5Years=new XmlHashMap<String, Float>(FIELD_Next5Years,String.class,Float.class);
	XmlHashMap<String, Float> NextYear=new XmlHashMap<String, Float>(FIELD_NextYear,String.class,Float.class);
	XmlHashMap<String, Float> NextQtr=new XmlHashMap<String, Float>(FIELD_NextQtr,String.class,Float.class);
	
	
	public boolean update(GrowthEst other){
		boolean isUpdated=false;
		if(!this.getTagName().equals(other.getTagName()))
			return isUpdated;
		
		if(this.getPEGRatio().update(other.getPEGRatio()))
			isUpdated=true;
		if(this.getPriceEarnings().update(other.getPriceEarnings()))
			isUpdated=true;
		if(this.getPast5Years().update(other.getPast5Years()))
			isUpdated=true;
		if(this.getThisYear().update(other.getThisYear()))
			isUpdated=true;
		if(this.getCurrentQtr().update(other.getCurrentQtr()))
			isUpdated=true;
		if(this.getNext5Years().update(other.getNext5Years()))
			isUpdated=true;
		if(this.getNextYear().update(other.getNextYear()))
			isUpdated=true;
		if(this.getNextQtr().update(other.getNextQtr()))
			isUpdated=true;
		
		
		return isUpdated;
		
	}
	
	
	public XmlHashMap<String, Float> getPEGRatio() {
		return PEGRatio;
	}

	public void setPEGRatio(XmlHashMap<String, Float> pEGRatio) {
		changes.firePropertyChange(FIELD_PEGRatio, this.PEGRatio,
				this.PEGRatio = pEGRatio);
	}

	public XmlHashMap<String, Float> getPriceEarnings() {
		return PriceEarnings;
	}

	public void setPriceEarnings(XmlHashMap<String, Float> priceEarnings) {
		changes.firePropertyChange(FIELD_PriceEarnings, this.PriceEarnings,
				this.PriceEarnings = priceEarnings);
	}

	public XmlHashMap<String, Float> getPast5Years() {
		return Past5Years;
	}

	public void setPast5Years(XmlHashMap<String, Float> past5Years) {
		changes.firePropertyChange(FIELD_Past5Years, this.Past5Years,
				this.Past5Years = past5Years);
	}

	public XmlHashMap<String, Float> getThisYear() {
		return ThisYear;
	}

	public void setThisYear(XmlHashMap<String, Float> thisYear) {
		changes.firePropertyChange(FIELD_ThisYear, this.ThisYear,
				this.ThisYear = thisYear);
	}

	public XmlHashMap<String, Float> getCurrentQtr() {
		return CurrentQtr;
	}

	public void setCurrentQtr(XmlHashMap<String, Float> currentQtr) {
		changes.firePropertyChange(FIELD_CurrentQtr, this.CurrentQtr,
				this.CurrentQtr = currentQtr);
	}

	public XmlHashMap<String, Float> getNext5Years() {
		return Next5Years;
	}

	public void setNext5Years(XmlHashMap<String, Float> next5Years) {
		changes.firePropertyChange(FIELD_Next5Years, this.Next5Years,
				this.Next5Years = next5Years);
	}

	public XmlHashMap<String, Float> getNextYear() {
		return NextYear;
	}

	public void setNextYear(XmlHashMap<String, Float> nextYear) {
		changes.firePropertyChange(FIELD_NextYear, this.NextYear,
				this.NextYear = nextYear);
	}

	public XmlHashMap<String, Float> getNextQtr() {
		return NextQtr;
	}

	public void setNextQtr(XmlHashMap<String, Float> nextQtr) {
		changes.firePropertyChange(FIELD_NextQtr, this.NextQtr,
				this.NextQtr = nextQtr);
	}

	
	
	@Override
	public String toString() {
		return "GrowthEst [PEGRatio=" + PEGRatio + ", PriceEarnings="
				+ PriceEarnings + ", Past5Years=" + Past5Years + ", ThisYear="
				+ ThisYear + ", CurrentQtr=" + CurrentQtr + ", Next5Years="
				+ Next5Years + ", NextYear=" + NextYear + ", NextQtr="
				+ NextQtr + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		XmlHashMap<String, Float> PEGRatio=new XmlHashMap<String, Float>(FIELD_PEGRatio,String.class,Float.class);
		XmlHashMap<String, Float> PriceEarnings=new XmlHashMap<String, Float>(FIELD_PriceEarnings,String.class,Float.class);
		XmlHashMap<String, Float> Past5Years=new XmlHashMap<String, Float>(FIELD_Past5Years,String.class,Float.class);
		XmlHashMap<String, Float> ThisYear=new XmlHashMap<String, Float>(FIELD_ThisYear,String.class,Float.class);
		XmlHashMap<String, Float> CurrentQtr=new XmlHashMap<String, Float>(FIELD_CurrentQtr,String.class,Float.class);
		XmlHashMap<String, Float> Next5Years=new XmlHashMap<String, Float>(FIELD_Next5Years,String.class,Float.class);
		XmlHashMap<String, Float> NextYear=new XmlHashMap<String, Float>(FIELD_NextYear,String.class,Float.class);
		XmlHashMap<String, Float> NextQtr=new XmlHashMap<String, Float>(FIELD_NextQtr,String.class,Float.class);
		
		
		if(childElement.getTagName().equals(PEGRatio.getTagName())){
			PEGRatio.init(childElement);
			this.setPEGRatio(PEGRatio);
		}
		else if(childElement.getTagName().equals(PriceEarnings.getTagName())){
			PriceEarnings.init(childElement);
			this.setPriceEarnings(PriceEarnings);
		}
		else if(childElement.getTagName().equals(Past5Years.getTagName())){
			Past5Years.init(childElement);
			this.setPast5Years(Past5Years);
		}
		else if(childElement.getTagName().equals(ThisYear.getTagName())){
			ThisYear.init(childElement);
			this.setThisYear(ThisYear);
		}
		else if(childElement.getTagName().equals(CurrentQtr.getTagName())){
			CurrentQtr.init(childElement);
			this.setCurrentQtr(CurrentQtr);
		}
		else if(childElement.getTagName().equals(Next5Years.getTagName())){
			Next5Years.init(childElement);
			this.setNext5Years(Next5Years);
		}
		else if(childElement.getTagName().equals(NextYear.getTagName())){
			NextYear.init(childElement);
			this.setNextYear(NextYear);
		}
		else if(childElement.getTagName().equals(NextQtr.getTagName())){
			NextQtr.init(childElement);
			this.setNextQtr(NextQtr);
		}
		

	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getPEGRatio().toDomElement(doc));
		rootElement.appendChild(this.getPriceEarnings().toDomElement(doc));
		rootElement.appendChild(this.getPast5Years().toDomElement(doc));
		rootElement.appendChild(this.getThisYear().toDomElement(doc));
		rootElement.appendChild(this.getCurrentQtr().toDomElement(doc));
		rootElement.appendChild(this.getNext5Years().toDomElement(doc));
		rootElement.appendChild(this.getNextYear().toDomElement(doc));
		rootElement.appendChild(this.getNextQtr().toDomElement(doc));

	}

}
