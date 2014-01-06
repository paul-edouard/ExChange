package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EarningsEst extends XmlParameterElement {
	
	static final String FIELD_NoofAnalysts="NoofAnalysts";
	static final String FIELD_YearAgoEPS="YearAgoEPS";
	static final String FIELD_AvgEstimate="AvgEstimate";
	static final String FIELD_LowEstimate="LowEstimate";
	static final String FIELD_HighEstimate="HighEstimate";
	
	
	private Estimation NoofAnalysts=new Estimation(FIELD_NoofAnalysts);
	private Estimation YearAgoEPS=new Estimation(FIELD_YearAgoEPS);
	private Estimation AvgEstimate=new Estimation(FIELD_AvgEstimate);
	private Estimation LowEstimate=new Estimation(FIELD_LowEstimate);
	private Estimation HighEstimate=new Estimation(FIELD_HighEstimate);
	
	public Estimation getNoofAnalysts() {
		return NoofAnalysts;
	}

	public void setNoofAnalysts(Estimation noofAnalysts) {
		changes.firePropertyChange(FIELD_NoofAnalysts, this.NoofAnalysts,
				this.NoofAnalysts = noofAnalysts);
	}

	public Estimation getYearAgoEPS() {
		return YearAgoEPS;
	}

	public void setYearAgoEPS(Estimation yearAgoEPS) {
		changes.firePropertyChange(FIELD_YearAgoEPS, this.YearAgoEPS,
				this.YearAgoEPS = yearAgoEPS);
	}

	public Estimation getAvgEstimate() {
		return AvgEstimate;
	}

	public void setAvgEstimate(Estimation avgEstimate) {
		changes.firePropertyChange(FIELD_AvgEstimate, this.AvgEstimate,
				this.AvgEstimate = avgEstimate);
	}

	public Estimation getLowEstimate() {
		return LowEstimate;
	}

	public void setLowEstimate(Estimation lowEstimate) {
		changes.firePropertyChange(FIELD_LowEstimate, this.LowEstimate,
				this.LowEstimate = lowEstimate);
	}

	public Estimation getHighEstimate() {
		return HighEstimate;
	}

	public void setHighEstimate(Estimation highEstimate) {
		changes.firePropertyChange(FIELD_HighEstimate, this.HighEstimate,
				this.HighEstimate = highEstimate);
	}

	@Override
	public String toString() {
		return "EarningsEst [NoofAnalysts=" + NoofAnalysts + ", YearAgoEPS="
				+ YearAgoEPS + ", AvgEstimate=" + AvgEstimate
				+ ", LowEstimate=" + LowEstimate + ", HighEstimate="
				+ HighEstimate + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_NoofAnalysts)){
			this.setNoofAnalysts(new Estimation(FIELD_NoofAnalysts,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_YearAgoEPS)){
			this.setYearAgoEPS(new Estimation(FIELD_YearAgoEPS,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_AvgEstimate)){
			this.setAvgEstimate(new Estimation(FIELD_AvgEstimate,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_LowEstimate)){
			this.setLowEstimate(new Estimation(FIELD_LowEstimate,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_HighEstimate)){
			this.setHighEstimate(new Estimation(FIELD_HighEstimate,childElement));
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getNoofAnalysts().toDomElement(doc));
		rootElement.appendChild(this.getYearAgoEPS().toDomElement(doc));
		rootElement.appendChild(this.getAvgEstimate().toDomElement(doc));
		rootElement.appendChild(this.getLowEstimate().toDomElement(doc));
		rootElement.appendChild(this.getHighEstimate().toDomElement(doc));

	}

}
