package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EPSTrends extends XmlParameterElement {
	
	private Estimation CurrentEstimate=new Estimation();
	private Estimation _7DaysAgo=new Estimation();
	private Estimation _30DaysAgo=new Estimation();
	private Estimation _60DaysAgo=new Estimation();
	private Estimation _90DaysAgo=new Estimation();
	
	
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
