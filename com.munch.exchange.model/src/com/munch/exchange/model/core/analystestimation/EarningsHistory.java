package com.munch.exchange.model.core.analystestimation;

import java.util.LinkedList;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class EarningsHistory extends XmlParameterElement {
	
	
	
	
	private LinkedList<EstimationPoint> Difference=new LinkedList<EstimationPoint>();
	private LinkedList<EstimationPoint> Surprise=new LinkedList<EstimationPoint>();
	private LinkedList<EstimationPoint> EPSEst=new LinkedList<EstimationPoint>();
	private LinkedList<EstimationPoint> EPSActual=new LinkedList<EstimationPoint>();
	
	
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
