package com.munch.exchange.model.core.chart;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.analytic.indicator.signals.SimpleDerivate;
import com.munch.exchange.model.core.chart.signals.ChartSimpleDerivate;
import com.munch.exchange.model.core.chart.trend.ChartAdaptiveMovingAverage;
import com.munch.exchange.model.core.chart.trend.ChartDoubleLinearWeigthedMovingAverage;
import com.munch.exchange.model.core.chart.trend.ChartSimpleMovingAverage;
import com.munch.exchange.model.xml.XmlParameterElement;

public class ChartIndicatorGroup extends XmlParameterElement{
	
	static final String ROOT="ROOT";
	
	static final String FIELD_Name="Name";
	public static final String FIELD_IsDirty="IsDirty";
	
	private String name;
	private boolean isDirty=false;
	
	LinkedList<ChartIndicatorGroup> subGroups=new LinkedList<ChartIndicatorGroup>();
	LinkedList<ChartIndicator> indicators=new LinkedList<ChartIndicator>();
	
	private ChartIndicatorGroup parent;
	
	public ChartIndicatorGroup(ChartIndicatorGroup parent,String name){
		this.parent=parent;
		this.name=name;
		
		if(this.parent!=null)
			this.parent.getSubGroups().add(this);
	}
	
	
	
	




	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/	
	
	public void setDirty(boolean isDirty) {
	changes.firePropertyChange(FIELD_IsDirty, this.isDirty, this.isDirty = isDirty);
	if(this.parent!=null)
		this.parent.setDirty(isDirty);
	}
	
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
		return this.name.replace(" ", "_");
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
	
	
	
	
	
	
	
	
}
