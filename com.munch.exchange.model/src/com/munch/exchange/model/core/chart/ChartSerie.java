package com.munch.exchange.model.core.chart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class ChartSerie extends XmlParameterElement{
	
	
	private String name;
	private double[] values;
	private int validAtPosition=0;
	private boolean isMain=false;
	private boolean icActivated=false;
	private SerieType type;
	
	
	
	public enum SerieType { MAIN, SECOND, PERCENT, ERROR, DEVIATION, DEVIATION_PERCENT};
	
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
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}

}
