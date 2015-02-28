package com.munch.exchange.model.core.neuralnetwork.timeseries;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.chart.ChartIndicatorGroup;
import com.munch.exchange.model.core.financials.Financials;
import com.munch.exchange.model.core.financials.IncomeStatementPoint;
import com.munch.exchange.model.xml.XmlParameterElement;

public class TimeSeriesGroup extends XmlParameterElement {
	
	static final String ROOT="ROOT";
	static final String BASE="this";
	
	static final String FIELD_Name="Name";
	static final String FIELD_Level="Level";
	static final String FIELD_ReferencedRateUUID="ReferencedRateUUID";
	
	
	public static final String GROUP_RATE="Rate";
	public static final String GROUP_FINANCIAL="Financial";
	public static final String GROUP_INDICATOR="Indicator";
	public static final String GROUP_TARGET_OUTPUT="Target Output";
	
	
	
	private String name;
	private int level;
	private String referencedRateUUID="";
	private TimeSeriesGroup parent;
	
	
	LinkedList<TimeSeriesGroup> subGroups=new LinkedList<TimeSeriesGroup>();
	LinkedList<TimeSeries> timeSeriesList=new LinkedList<TimeSeries>();
	
	public TimeSeriesGroup(){
		
	}
	
	public TimeSeriesGroup(TimeSeriesGroup parent,String name, boolean addToParentchildren){
		this.parent=parent;
		this.name=name;
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
		TimeSeriesGroup baseGroup=new TimeSeriesGroup(root,rate.getFullName(), true);
		baseGroup.setReferencedRateUUID(rate.getUUID());
		
		//Stock
		if(rate instanceof Stock){
			addStockGroups((Stock) rate,baseGroup);
		}
		//Rate
		else{
			addRateGroups(rate, baseGroup);
		}
		
	}
	
	
	/***********************************
	 *	    GETTER AND SETTER          *
	 ***********************************/
	
	public TimeSeriesGroup createCopy(){
		TimeSeriesGroup copy=new TimeSeriesGroup();
		copy.name=this.name;
		copy.level=this.level;
		copy.referencedRateUUID=this.referencedRateUUID;
		
		for(TimeSeriesGroup child:subGroups){
			TimeSeriesGroup child_copy=child.createCopy();
			child_copy.parent=copy;
			copy.subGroups.add(child_copy);
		}
		for(TimeSeries series:timeSeriesList){
			TimeSeries series_copy=series.createCopy();
			series_copy.setParentGroup(copy);
			copy.timeSeriesList.add(series_copy);
		}
		
		return copy;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
	changes.firePropertyChange(FIELD_Name, this.name, this.name = name);
	}
	

	public TimeSeriesGroup getParent() {
		return parent;
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
	
	public LinkedList<TimeSeries> getAllTimeSeries(){
		LinkedList<TimeSeries> list=new LinkedList<TimeSeries>();
		list.addAll(timeSeriesList);
		for(TimeSeriesGroup subGroup:subGroups)
			list.addAll(subGroup.getAllTimeSeries());
			
		return list;
	}
	
	
	public void addTimeSeries(TimeSeries serie){
		timeSeriesList.add(serie);
		serie.setParentGroup(this);
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
		TimeSeriesGroup group=new TimeSeriesGroup(this,ROOT,false);
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
	 *		       STATIC              *
	 ***********************************/
	
	public static TimeSeriesGroup createRoot(Stock stock){
		TimeSeriesGroup root=new TimeSeriesGroup(null,ROOT, false);
		root.setName(ROOT);
		root.setLevel(0);
		//root.setReferencedRateUUID(stock.getUUID());
		
		//Base
		TimeSeriesGroup baseGroup=new TimeSeriesGroup(root,"this", true);
		baseGroup.setReferencedRateUUID(stock.getUUID());
		
		addStockGroups(stock,baseGroup);
		
		new TimeSeriesGroup(baseGroup,GROUP_TARGET_OUTPUT, true);
		
		return root;
	}
	
	public static void addStockGroups(Stock stock,TimeSeriesGroup baseGroup){
		new TimeSeriesGroup(baseGroup,GROUP_RATE, true);
		new TimeSeriesGroup(baseGroup,GROUP_FINANCIAL, true);
		new TimeSeriesGroup(baseGroup,GROUP_INDICATOR, true);
	}
	
	public static void addRateGroups(ExchangeRate rate,TimeSeriesGroup baseGroup){
		new TimeSeriesGroup(baseGroup,GROUP_INDICATOR, true);
	}
	
	
	public static LinkedList<String> getAvailableSerieNames(TimeSeriesGroup group){
		LinkedList<String> serieNames=new LinkedList<String>();
		
		if(group.getName().equals(GROUP_RATE)){
			serieNames.add(DatePoint.FIELD_Close);
			serieNames.add(DatePoint.FIELD_High);
			serieNames.add(DatePoint.FIELD_Low);
			serieNames.add(DatePoint.FIELD_Open);
			serieNames.add(DatePoint.FIELD_Volume);
			serieNames.add(DatePoint.FIELD_Adj_Close);
		}
		else if(group.getName().equals(GROUP_FINANCIAL)){
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_EarningsPerShare);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_EBIT);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_TotalRevenue);
			serieNames.add(Financials.FIELD_IncomeStatement+":"+IncomeStatementPoint.FIELD_NetIncome);
			
		}
		else if(group.getName().equals(GROUP_TARGET_OUTPUT)){
			serieNames.add("Desired Output");
		}
		
		return serieNames;
	}


}
