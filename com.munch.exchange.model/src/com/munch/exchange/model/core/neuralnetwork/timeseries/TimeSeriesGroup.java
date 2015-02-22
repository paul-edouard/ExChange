package com.munch.exchange.model.core.neuralnetwork.timeseries;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.xml.XmlParameterElement;

public class TimeSeriesGroup extends XmlParameterElement {
	
	static final String ROOT="ROOT";
	
	static final String FIELD_Name="Name";
	static final String FIELD_Level="Level";
	static final String FIELD_ReferencedRateUUID="ReferencedRateUUID";
	
	private String name;
	private int level;
	private String referencedRateUUID="";
	private TimeSeriesGroup parent;
	
	
	LinkedList<TimeSeriesGroup> subGroups=new LinkedList<TimeSeriesGroup>();
	LinkedList<TimeSeries> timeSeriesList=new LinkedList<TimeSeries>();
	
	public TimeSeriesGroup(TimeSeriesGroup parent, boolean addToParentchildren){
		this.parent=parent;
		this.level=0;
		
		if(this.parent!=null)
			this.level=this.parent.level+1;
		
		if(this.parent!=null && addToParentchildren){
			this.parent.getSubGroups().add(this);
			
			if(!this.parent.getName().equals(ROOT) &&
					!this.parent.getReferencedRateUUID().isEmpty()){
				this.referencedRateUUID=this.parent.getReferencedRateUUID();
			}
			
		}
		
	}
	
	private TimeSeriesGroup searchRoot(){
		TimeSeriesGroup s_parent=this.parent;
		TimeSeriesGroup root=this;
		while(s_parent!=null){
			root=s_parent;
			s_parent=s_parent.parent;
		}
		return root;
	}
	
	private boolean isBranched(ExchangeRate rate){
		TimeSeriesGroup root=searchRoot();
		for(TimeSeriesGroup c:root.getSubGroups()){
			if(c.getReferencedRateUUID().equals(rate.getUUID()))
				return true;
		}
		
		return false;
	}
	
	public void addNewBranch(ExchangeRate rate){
		
		if(isBranched(rate))return;
		
		TimeSeriesGroup root=searchRoot();
		//Base
		TimeSeriesGroup baseGroup=new TimeSeriesGroup(root, true);
		baseGroup.setName(rate.getFullName());
		baseGroup.setReferencedRateUUID(rate.getUUID());
		
		//Rate
		
		if(rate instanceof Stock){
			TimeSeriesGroup rateGroup=new TimeSeriesGroup(baseGroup, true);
			
		}
		
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
	

	
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
	changes.firePropertyChange(FIELD_Level, this.level, this.level = level);}
	

	public String getReferencedRateUUID() {
		return referencedRateUUID;
	}

	public void setReferencedRateUUID(String referencedRateUUID) {
	changes.firePropertyChange(FIELD_ReferencedRateUUID, this.referencedRateUUID, this.referencedRateUUID = referencedRateUUID);}
	

	public LinkedList<TimeSeriesGroup> getSubGroups() {
		return subGroups;
	}

	public LinkedList<TimeSeries> getTimeSeriesList() {
		return timeSeriesList;
	}

	/***********************************
	 *		       XML                 *
	 ***********************************/

	@Override
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setLevel(Integer.parseInt(rootElement.getAttribute(FIELD_Level)));
		this.setReferencedRateUUID(rootElement.getAttribute(FIELD_ReferencedRateUUID));
		
		subGroups.clear();
		timeSeriesList.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		TimeSeriesGroup group=new TimeSeriesGroup(this,false);
		TimeSeries serie=new TimeSeries();
		if(childElement.getTagName().equals(group.getTagName())){
			group.init(childElement);
			subGroups.add(group);
		}
		else if(childElement.getTagName().equals(serie.getTagName())){
			serie.init(childElement);
			timeSeriesList.add(serie);
		}
		

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_Level,String.valueOf(this.getLevel()));
		rootElement.setAttribute(FIELD_ReferencedRateUUID,this.getReferencedRateUUID());

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(TimeSeriesGroup group:subGroups){
			rootElement.appendChild(group.toDomElement(doc));
		}
		
		for(TimeSeries serie:timeSeriesList){
			rootElement.appendChild(serie.toDomElement(doc));
		}

	}
	
	/***********************************
	 *		       XML                 *
	 ***********************************/


}
