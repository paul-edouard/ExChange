package com.munch.exchange.model.core.chart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.chart.ChartParameter.ParameterType;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ChartSerie extends XmlParameterElement{
	
	
	static final String FIELD_Name="Name";
	static final String FIELD_Values="Values";
	static final String FIELD_ValidAtPosition="ValidAtPosition";
	static final String FIELD_IsMain="IsMain";
	static final String FIELD_IsActivated="IsActivated";
	static final String FIELD_Color="Color";
	static final String FIELD_ColorRed="ColorRed";
	static final String FIELD_ColorGreen="ColorGreen";
	static final String FIELD_ColorBlue="ColorBlue";
	static final String FIELD_RendererType="Type";
	
	
	
	private String name;
	private double[] values;
	private int validAtPosition=0;
	private boolean isMain=false;
	private boolean isActivated=false;
	private int[] color=new int[3];
	private RendererType rendererType;
	
	private ChartIndicator parent;
	
	
	public enum RendererType { NONE(-1), MAIN(0), SECOND(1), PERCENT(2), ERROR(3), DEVIATION(4), DEVIATION_PERCENT(5);
		
		private int val;
	
	private RendererType(int val) {
		this.val = val;
	}
	public int getValue() {
		return this.val;
	}
	
	public static RendererType fromString(String input){
		try {
			int invalue=Integer.parseInt(input);
			if(invalue==0)
				return MAIN;
			else if(invalue==1)
				return SECOND;
			else if(invalue==2)
				return PERCENT;
			else if(invalue==3)
				return ERROR;
			else if(invalue==4)
				return DEVIATION;
			else if(invalue==5)
				return DEVIATION_PERCENT;
			
		} catch (Exception e) {
			return NONE;
		}
		return NONE;
		
	}
	
	public static String toString(RendererType type){
		switch (type) {
		case MAIN:
			return String.valueOf(0);
		case SECOND:
			return String.valueOf(1);
		case PERCENT:
			return String.valueOf(2);
		case ERROR:
			return String.valueOf(3);
		case DEVIATION:
			return String.valueOf(4);
		case DEVIATION_PERCENT:
			return String.valueOf(5);
		default:
			return String.valueOf(-1);
		}
	}
	
	
	};
	
	
	public ChartSerie(ChartIndicator parent){
		this.parent=parent;
	}
	
	public ChartSerie(ChartIndicator parent,String name,RendererType type,boolean isMain, boolean isActivated,int[] color ){
		this.name=name;
		
		this.isMain=isMain;
		this.isActivated=isActivated;
		this.color=color;
		this.rendererType=type;
		
		this.parent=parent;
	
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
	
	
	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		changes.firePropertyChange(FIELD_Values, this.values, this.values = values);
	}
	

	public int getValidAtPosition() {
		return validAtPosition;
	}

	public void setValidAtPosition(int validAtPosition) {
		changes.firePropertyChange(FIELD_ValidAtPosition, this.validAtPosition,this.validAtPosition = validAtPosition);
	}
	

	public boolean isMain() {
		return isMain;
	}

	public void setMain(boolean isMain) {
	changes.firePropertyChange(FIELD_IsMain, this.isMain, this.isMain = isMain);}
	

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
	changes.firePropertyChange(FIELD_IsActivated, this.isActivated, this.isActivated = isActivated);}
	

	public int[] getColor() {
		return color;
	}

	public void setColor(int[] color) {
	changes.firePropertyChange(FIELD_Color, this.color, this.color = color);}
	

	public RendererType getRendererType() {
		return rendererType;
	}

	public void setRendererType(RendererType type) {
	changes.firePropertyChange(FIELD_RendererType, this.rendererType, this.rendererType = type);
	}
	

	/***********************************
	 *		       XML                 *
	 ***********************************/
	
	
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setValidAtPosition(Integer.parseInt(rootElement.getAttribute(FIELD_ValidAtPosition)));
		
		this.setMain(rootElement.getAttribute(FIELD_IsMain).equals("true"));
		this.setActivated(rootElement.getAttribute(FIELD_IsActivated).equals("true"));
		
		color[0]=Integer.parseInt(rootElement.getAttribute(FIELD_ColorRed));
		color[1]=Integer.parseInt(rootElement.getAttribute(FIELD_ColorGreen));
		color[2]=Integer.parseInt(rootElement.getAttribute(FIELD_ColorBlue));
		
		this.setRendererType(RendererType.fromString(rootElement.getAttribute(FIELD_RendererType)));
		
	}

	@Override
	protected void initChild(Element childElement) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_ValidAtPosition,String.valueOf(this.getValidAtPosition()));
		
		rootElement.setAttribute(FIELD_IsMain,String.valueOf(this.isMain()));
		rootElement.setAttribute(FIELD_IsActivated,String.valueOf(this.isActivated()));
		
		rootElement.setAttribute(FIELD_ColorRed,String.valueOf(color[0]));
		rootElement.setAttribute(FIELD_ColorGreen,String.valueOf(color[1]));
		rootElement.setAttribute(FIELD_ColorBlue,String.valueOf(color[2]));
		
		rootElement.setAttribute(FIELD_RendererType,RendererType.toString(this.getRendererType()));

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		// TODO Auto-generated method stub

	}

}
