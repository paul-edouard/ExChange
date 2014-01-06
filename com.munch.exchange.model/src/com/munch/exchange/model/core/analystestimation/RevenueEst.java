package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class RevenueEst extends XmlParameterElement {
	
	static final String FIELD_YearAgoSales="YearAgoSales";
	static final String FIELD_NoofAnalysts="NoofAnalysts";
	static final String FIELD_AvgEstimate="AvgEstimate";
	static final String FIELD_LowEstimate="LowEstimate";
	static final String FIELD_HighEstimate="HighEstimate";
	static final String FIELD_SalesGrowth="SalesGrowth";
	
	
	private Estimation YearAgoSales=new Estimation(FIELD_YearAgoSales);
	private Estimation NoofAnalysts=new Estimation(FIELD_NoofAnalysts);
	private Estimation AvgEstimate=new Estimation(FIELD_AvgEstimate);
	private Estimation LowEstimate=new Estimation(FIELD_LowEstimate);
	private Estimation HighEstimate=new Estimation(FIELD_HighEstimate);
	private Estimation SalesGrowth=new Estimation(FIELD_SalesGrowth);

	

	public Estimation getYearAgoSales() {
		return YearAgoSales;
	}

	public void setYearAgoSales(Estimation yearAgoSales) {
		changes.firePropertyChange(FIELD_YearAgoSales, this.YearAgoSales,
				this.YearAgoSales = yearAgoSales);
	}

	public Estimation getNoofAnalysts() {
		return NoofAnalysts;
	}

	public void setNoofAnalysts(Estimation noofAnalysts) {
		changes.firePropertyChange(FIELD_NoofAnalysts, this.NoofAnalysts,
				this.NoofAnalysts = noofAnalysts);
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

	public Estimation getSalesGrowth() {
		return SalesGrowth;
	}

	public void setSalesGrowth(Estimation salesGrowth) {
		changes.firePropertyChange(FIELD_SalesGrowth, this.SalesGrowth,
				this.SalesGrowth = salesGrowth);
	}

	
	@Override
	public String toString() {
		return "RevenueEst [YearAgoSales=" + YearAgoSales + ", NoofAnalysts="
				+ NoofAnalysts + ", AvgEstimate=" + AvgEstimate
				+ ", LowEstimate=" + LowEstimate + ", HighEstimate="
				+ HighEstimate + ", SalesGrowth=" + SalesGrowth + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {}

	@Override
	protected void initChild(Element childElement) {
		
		if(childElement.getTagName().equals(FIELD_YearAgoSales)){
			this.setYearAgoSales(new Estimation(FIELD_YearAgoSales,childElement));
		}
		else if(childElement.getTagName().equals(FIELD_NoofAnalysts)){
			this.setNoofAnalysts(new Estimation(FIELD_NoofAnalysts,childElement));
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
		else if(childElement.getTagName().equals(FIELD_SalesGrowth)){
			this.setSalesGrowth(new Estimation(FIELD_SalesGrowth,childElement));
		}

	}

	@Override
	protected void setAttribute(Element rootElement) {}

	@Override
	protected void appendChild(Element rootElement,Document doc) {
		
		rootElement.appendChild(this.getNoofAnalysts().toDomElement(doc));
		rootElement.appendChild(this.getYearAgoSales().toDomElement(doc));
		rootElement.appendChild(this.getAvgEstimate().toDomElement(doc));
		rootElement.appendChild(this.getLowEstimate().toDomElement(doc));
		rootElement.appendChild(this.getHighEstimate().toDomElement(doc));
		rootElement.appendChild(this.getSalesGrowth().toDomElement(doc));

	}

}
