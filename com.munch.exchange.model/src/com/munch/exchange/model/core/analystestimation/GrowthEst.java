package com.munch.exchange.model.core.analystestimation;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class GrowthEst extends XmlParameterElement {
	
	
	
	HashMap<String, Float> PEGRatio=new HashMap<String, Float>();
	HashMap<String, Float> PriceEarnings=new HashMap<String, Float>();
	HashMap<String, Float> Past5Years=new HashMap<String, Float>();
	HashMap<String, Float> ThisYear=new HashMap<String, Float>();
	HashMap<String, Float> CurrentQtr=new HashMap<String, Float>();
	HashMap<String, Float> Next5Years=new HashMap<String, Float>();
	HashMap<String, Float> NextYear=new HashMap<String, Float>();
	HashMap<String, Float> NextQtr=new HashMap<String, Float>();
	
	
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
