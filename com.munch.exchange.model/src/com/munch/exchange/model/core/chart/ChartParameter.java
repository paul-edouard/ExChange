package com.munch.exchange.model.core.chart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.neuralnetwork.PeriodType;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ChartParameter extends XmlParameterElement {
	
	static final String FIELD_Name="Name";
	static final String FIELD_Type="Type";
	static final String FIELD_Value="Value";
	
	static final String FIELD_MinValue="MinValue";
	static final String FIELD_MaxValue="MaxValue";
	static final String FIELD_ScalarFactor="ScalarFactor";
	
	private ParameterType type;
	private String name;
	private double value;
	private double minValue;
	private double maxValue;
	private int scalarFactor;
	
	
	public enum ParameterType {DOUBLE(1), INTEGER(2), NONE(0);
		
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

	
	public ChartParameter(){}
			
	
	public ChartParameter(String name,ParameterType type,  double val, double minValue, double maxValue, int  scalarFac){
		this.value=val;
		this.maxValue=maxValue;
		this.minValue=minValue;
		this.type=type;
		this.name=name;
		
		this.scalarFactor=scalarFac;
	}
	
	
	
	
	
	

	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);
	}
	
	
	
	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
	changes.firePropertyChange(FIELD_Type, this.type, this.type = type);}
	

	public double getValue() {
		return value;
	}
	
	public int getIntegerValue() {
		return (int) value;
	}
	

	public void setValue(double value) {
	changes.firePropertyChange(FIELD_Value, this.value, this.value = value);}
	

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
	changes.firePropertyChange(FIELD_MinValue, this.minValue, this.minValue = minValue);}
	

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
	changes.firePropertyChange(FIELD_MaxValue, this.maxValue, this.maxValue = maxValue);}
	

	public int getScalarFactor() {
		return scalarFactor;
	}

	public void setScalarFactor(int scalarFactor) {
	changes.firePropertyChange(FIELD_ScalarFactor, this.scalarFactor, this.scalarFactor = scalarFactor);
	}
	

	/***********************************
	 *		       XML                 *
	 ***********************************/
	
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		
		this.setType(ParameterType.fromString(rootElement.getAttribute(FIELD_Type)));
		this.setValue(Double.parseDouble(rootElement.getAttribute(FIELD_Value)));
		this.setMinValue(Double.parseDouble(rootElement.getAttribute(FIELD_MinValue)));
		this.setMaxValue(Double.parseDouble(rootElement.getAttribute(FIELD_MaxValue)));
		this.setScalarFactor(Integer.parseInt(rootElement.getAttribute(FIELD_ScalarFactor)));
		

	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		
		rootElement.setAttribute(FIELD_Type,ParameterType.toString(this.getType()));
		rootElement.setAttribute(FIELD_Value,String.valueOf(this.getValue()));
		rootElement.setAttribute(FIELD_MinValue,String.valueOf(this.getMinValue()));
		rootElement.setAttribute(FIELD_MaxValue,String.valueOf(this.getMaxValue()));
		rootElement.setAttribute(FIELD_ScalarFactor,String.valueOf(this.getScalarFactor()));
		

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}


}
