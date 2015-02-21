package com.munch.exchange.model.core.chart;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.xml.XmlParameterElement;

public abstract class ChartIndicator extends XmlParameterElement {
	
	static final String FIELD_Name="Name";
	static final String FIELD_IsActivated="IsActivated";
	
	protected String name;
	private boolean isActivated=false;
	
	protected LinkedList<ChartSerie> chartSeries=new LinkedList<ChartSerie>();
	protected LinkedList<ChartParameter> chartParameters=new LinkedList<ChartParameter>();
	
	
	public ChartIndicator() {
		super();
		createSeries();
		createParameters();
	}

	public abstract void compute(HistoricalData hisData);
	
	public abstract void createSeries();
	
	public abstract void createParameters();
	
	
	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);
	}
	
	
	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
	changes.firePropertyChange(FIELD_IsActivated, this.isActivated, this.isActivated = isActivated);}
	
	
	public ChartParameter getChartParameter(String paramName){
		for(ChartParameter param:chartParameters){
			if(param.getName().equals(paramName)){
				return param;
			}
		}
		return null;
	}
	
	public ChartSerie getChartSerie(String serieName){
		for(ChartSerie serie:chartSeries){
			if(serie.getName().equals(serieName)){
				return serie;
			}
		}
		return null;
	}
	

	public LinkedList<ChartSerie> getChartSeries() {
		return chartSeries;
	}

	public LinkedList<ChartParameter> getChartParameters() {
		return chartParameters;
	}

	/***********************************
	 *		       XML                 *
	 ***********************************/
	
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setActivated(rootElement.getAttribute(FIELD_IsActivated).equals("true"));
		
		
		chartSeries.clear();
		chartParameters.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		ChartSerie serie=new ChartSerie();
		ChartParameter param=new ChartParameter();
		if(childElement.getTagName().equals(serie.getTagName())){
			serie.init(childElement);
			chartSeries.add(serie);
		}
		else if(childElement.getTagName().equals(param.getTagName())){
			param.init(childElement);
			chartParameters.add(param);
		}
		
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_IsActivated,String.valueOf(this.isActivated()));
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(ChartSerie serie:chartSeries){
			rootElement.appendChild(serie.toDomElement(doc));
		}
		
		for(ChartParameter param:chartParameters){
			rootElement.appendChild(param.toDomElement(doc));
		}
	}



}
