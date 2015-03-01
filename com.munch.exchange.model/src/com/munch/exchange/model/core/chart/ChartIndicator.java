package com.munch.exchange.model.core.chart;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.historical.HistoricalData;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;
import com.munch.exchange.model.xml.XmlParameterElement;

public abstract class ChartIndicator extends XmlParameterElement {
	
	private static Logger logger = Logger.getLogger(ChartIndicator.class);
	
	static final String FIELD_Name="Name";
	static final String FIELD_IsActivated="IsActivated";
	static final String FIELD_IsDirty="IsDirty";
	
	protected String name;
	private boolean isActivated=false;
	private boolean isDirty=false;
	
	protected LinkedList<ChartSerie> chartSeries=new LinkedList<ChartSerie>();
	protected LinkedList<ChartParameter> chartParameters=new LinkedList<ChartParameter>();
	
	private ChartIndicatorGroup parent;
	private TimeSeries series;
	
	public ChartIndicator(TimeSeries series){
		super();
		this.series=series;
		
		initName();
		createSeries();
		createParameters();
	}
	
	
	public ChartIndicator(ChartIndicatorGroup parent) {
		super();
		
		this.parent=parent;
		if(this.parent!=null)
		this.parent.getIndicators().add(this);
		
		initName();
		createSeries();
		createParameters();
		
	}
	
	public abstract void initName();

	public abstract void compute(HistoricalData hisData);
	
	public abstract void createSeries();
	
	public abstract void createParameters();
	
	public void resetDefault(){
		for(ChartParameter p:chartParameters){
			p.resetDefault();
		}
	}
	
	public String toCsvString(){
		String csv=this.getClass().getSimpleName();
		for(ChartParameter param:chartParameters){
			csv+=";"+String.valueOf(param.getValue());
		}
		return csv;
	}
	
	
	
	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/	
	
	
	
	public String getName() {
		return name;
	}

	public void setDirty(boolean isDirty) {
		changes.firePropertyChange(FIELD_IsDirty, this.isDirty, this.isDirty = isDirty);
		if(this.parent!=null)
			this.parent.setDirty(isDirty);
	}
	
	public void setParent(ChartIndicatorGroup parent) {
		this.parent = parent;
	}
	

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);
	}
	
	
	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
	changes.firePropertyChange(FIELD_IsActivated, this.isActivated, this.isActivated = isActivated);
	for(ChartSerie serie:chartSeries){
		serie.setActivated(serie.isMain());
	}
	
	}
	
	
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
	
	public ChartSerie getMainChartSerie(){
		for(ChartSerie serie:chartSeries){
			if(serie.isMain())return serie;
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
		
		
		//chartSeries.clear();
		//chartParameters.clear();
	}

	@Override
	protected void initChild(Element childElement) {
		ChartSerie serie=new ChartSerie(this);
		ChartParameter param=new ChartParameter(this);
		if(childElement.getTagName().equals(serie.getTagName())){
			serie.init(childElement);
			for(ChartSerie s:chartSeries){
				if(s.getName().equals(serie.getName())){
					s.init(childElement);
				}
			}
			
		}
		else if(childElement.getTagName().equals(param.getTagName())){
			param.init(childElement);
			for(ChartParameter p:chartParameters){
				if(p.getName().equals(param.getName())){
					p.init(childElement);
				}
			}
			
			
			//chartParameters.add(param);
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
