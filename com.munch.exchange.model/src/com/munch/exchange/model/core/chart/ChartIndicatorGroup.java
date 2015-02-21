package com.munch.exchange.model.core.chart;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.chart.trend.ChartSimpleMovingAverage;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ChartIndicatorGroup extends XmlParameterElement{
	
	static final String ROOT="ROOT";
	
	static final String FIELD_Name="Name";
	
	private String name;
	
	LinkedList<ChartIndicatorGroup> subGroups=new LinkedList<ChartIndicatorGroup>();
	LinkedList<ChartIndicator> indicators=new LinkedList<ChartIndicator>();
	
	
	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);
	}
	

	public LinkedList<ChartIndicatorGroup> getSubGroups() {
		return subGroups;
	}

	public LinkedList<ChartIndicator> getIndicators() {
		return indicators;
	}

	/***********************************
	 *		       XML                 *
	 ***********************************/
	
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		
		//subGroups.clear();
		//indicators.clear();
	}
	
	

	@Override
	public String getTagName() {
		return this.name;
	}

	@Override
	protected void initChild(Element childElement) {
		
		for(ChartIndicatorGroup group:subGroups){
			if(childElement.getTagName().equals(group.getTagName())){
				group.init(childElement);
			}
		}
		
		for(ChartIndicator indicator:indicators){
			if(childElement.getTagName().equals(indicator.getTagName())){
				indicator.init(childElement);
			}
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		
		
		for(ChartIndicatorGroup group:subGroups){
			rootElement.appendChild(group.toDomElement(doc));
		}
		
		for(ChartIndicator indicator:indicators){
			rootElement.appendChild(indicator.toDomElement(doc));
		}
		
	}
	
	
	/***********************************
	 *		       STATIC              *
	 ***********************************/
	
	public static ChartIndicatorGroup createRoot(){
		ChartIndicatorGroup root=new ChartIndicatorGroup();
		root.setName(ROOT);
		
		//TREND
		ChartIndicatorGroup trend=new ChartIndicatorGroup();
		trend.setName("Trend");
		root.getSubGroups().add(trend);
		
		trend.getIndicators().add(new ChartSimpleMovingAverage());
		
		
		
		return root;
	}
	
	/*
	public static void addNewIndicators(ChartIndicatorGroup group){
		//TODO
	}
	*/
	
	
	
}
