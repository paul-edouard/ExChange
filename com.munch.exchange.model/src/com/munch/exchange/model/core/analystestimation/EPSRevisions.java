package com.munch.exchange.model.core.analystestimation;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EPSRevisions extends XmlParameterElement {
	
	private Estimation UpLast7Days=new Estimation();
	private Estimation UpLast30Days=new Estimation();
	private Estimation DownLast30Days=new Estimation();
	private Estimation DownLast90Days=new Estimation();
	
	

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
