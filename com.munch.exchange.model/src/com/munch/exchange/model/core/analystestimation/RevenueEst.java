package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class RevenueEst extends XmlParameterElement {
	
	
	private Estimation YearAgoSales=new Estimation();
	private Estimation NoofAnalysts=new Estimation();
	private Estimation AvgEstimate=new Estimation();
	private Estimation LowEstimate=new Estimation();
	private Estimation HighEstimate=new Estimation();
	private Estimation SalesGrowth=new Estimation();

	

	@Override
	protected void initAttribute(Element rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAttribute(Element rootElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendChild(Element rootElement) {
		// TODO Auto-generated method stub

	}

}
