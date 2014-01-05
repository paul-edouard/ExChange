package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class AnalystEstimation extends XmlParameterElement {
	
	
	
	
	private EarningsHistory earningsHistory=new EarningsHistory();
	private GrowthEst growthEst=new GrowthEst();
	private RevenueEst revenueEst=new RevenueEst();
	private EPSTrends ePSTrends=new EPSTrends();
	private EPSRevisions ePSRevisions=new EPSRevisions();
	private EarningsEst earningsEst=new EarningsEst();
	
	
	

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
