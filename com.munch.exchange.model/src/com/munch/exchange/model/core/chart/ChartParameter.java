package com.munch.exchange.model.core.chart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.neuralnetwork.PeriodType;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ChartParameter extends XmlParameterElement {
	
	
	private ParameterType type;
	private String name;
	private double value;
	private double minValue;
	private double maxValue;
	private int scalarFactor;
	
	//public enum ParameterType { DOUBLE, INTEGER, NONE};
	
	public enum ParameterType {
		DOUBLE(1),
		INTEGER(2),
		NONE(0);
		
		private int val;
		
		private ParameterType(int val) {
			this.val = val;
		}
		public int getValue() {
			return this.val;
		}
		
		public static ParameterType fromString(String input){
			try {
				int invalue=Integer.parseInt(input);
				if(invalue==1)
					return DOUBLE;
				else if(invalue==2)
					return INTEGER;
				
			} catch (Exception e) {
				return NONE;
			}
			return NONE;
			
		}
		
		public static String toString(ParameterType type){
			switch (type) {
			case DOUBLE:
				return String.valueOf(1);
			case INTEGER:
				return String.valueOf(2);
			default:
				return String.valueOf(0);
			}
		}
		
	}

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
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}

}
