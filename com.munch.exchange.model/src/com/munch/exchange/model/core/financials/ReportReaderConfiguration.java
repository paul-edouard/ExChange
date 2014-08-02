package com.munch.exchange.model.core.financials;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class ReportReaderConfiguration extends XmlParameterElement {
	
	
	static final String FIELD_Website="Website";
	
	
	
	private String website;
	

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
	changes.firePropertyChange(FIELD_Website, this.website, this.website = website);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setWebsite((rootElement.getAttribute(FIELD_Website)));
		
		
	}

	@Override
	protected void initChild(Element childElement) {
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_Website,this.getWebsite());
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub
		
	}

}
