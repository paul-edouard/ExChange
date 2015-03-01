package com.munch.exchange.model.core.neuralnetwork.timeseries;

import java.util.Calendar;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.chart.ChartIndicator;
import com.munch.exchange.model.core.chart.ChartIndicatorFactory;
import com.munch.exchange.model.core.neuralnetwork.ValuePoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class TimeSeries extends XmlParameterElement{
	
	private static Logger logger = Logger.getLogger(TimeSeries.class);
	
	
	static final String FIELD_Name="Name";
	static final String FIELD_Id="Id";
	//static final String FIELD_Category="Category";
	static final String FIELD_TimeRemainingActivated="TimeRemainingActivated";
	public static final String FIELD_NumberOfPastValues="NumberOfPastValues";
	static final String FIELD_InputValues="InputValues";
	static final String FIELD_IsLowFrequency="IsLowFrequency";
	
	static final String FIELD_MinValue="MinValue";
	static final String FIELD_MaxValue="MaxValue";
	static final String FIELD_MinMaxLastRefreshDate="MinMaxLastRefreshDate";
	
	private String Name;
	private String id;
	//private TimeSeriesCategory category;
	private boolean isLowFrequency=false;
	
	private boolean timeRemainingActivated;
	private int numberOfPastValues;
	private Object parent;
	private TimeSeriesGroup parentGroup;
	private ChartIndicator indicator=null;
	
	private ValuePointList inputValues=new ValuePointList();
	private ValuePointList lowFrequencyValues=new ValuePointList();
	
	private double minValue=Double.NaN;
	private double maxValue=Double.NaN;
	private Calendar MinMaxLastRefreshDate=Calendar.getInstance();
	
	public TimeSeries(){
		id="";
		timeRemainingActivated=false;
		numberOfPastValues=4;
	}
	
	public TimeSeries(String Name/*,TimeSeriesCategory category*/){
		this.Name=Name;
		//this.setCategory(category);
		//this.category=category;
		id="";
		timeRemainingActivated=false;
		numberOfPastValues=4;
	}
	
	public TimeSeries createCopy(){
		TimeSeries copy = new TimeSeries(Name/*, category.createCopy()*/);
		copy.id=this.id;
		copy.timeRemainingActivated=this.timeRemainingActivated;
		copy.numberOfPastValues=this.numberOfPastValues;
		copy.isLowFrequency=this.isLowFrequency;
		
		copy.minValue=this.minValue;
		copy.maxValue=this.maxValue;
		copy.MinMaxLastRefreshDate=this.MinMaxLastRefreshDate;
		
		copy.indicator=this.indicator;
		
		return copy;
	}
	
	public void adaptInputValuesToMasterValuePointList(ValuePointList masterValuePointList){
		
		ValuePointList adpatedInputValues=new ValuePointList();
		
		for(int i=0;i<masterValuePointList.size();i++){
			
			ValuePoint masterPoint=masterValuePointList.get(i);
			ValuePoint correspondingInputPoint=null;
			long calculatedTimeToNextValue=0;
			for(ValuePoint point:inputValues){
				
				//Find the corresponding point
				if(masterPoint.getDate().getTimeInMillis()>=point.getDate().getTimeInMillis()){
					if(correspondingInputPoint==null){
						correspondingInputPoint=point;
						
						//Calculate the time remaining to the next value
						for(int j=i;j<masterValuePointList.size();j++){
							ValuePoint nextMasterPoint=masterValuePointList.get(j);
							if(nextMasterPoint.getNextValueDate().getTimeInMillis()<=point.getNextValueDate().getTimeInMillis()){
								calculatedTimeToNextValue+=masterPoint.getNextValueDate().getTimeInMillis()-masterPoint.getDate().getTimeInMillis();
							}
							else{
								break;
							}
						}
						break;
					}
				}
				
			}
			
			//Create and add the new value point
			if(correspondingInputPoint!=null){
				ValuePoint newPoint=new ValuePoint(masterPoint.getDate(), correspondingInputPoint.getValue());
				Calendar nextvalueDate=Calendar.getInstance();
				nextvalueDate.setTimeInMillis(masterPoint.getDate().getTimeInMillis()+calculatedTimeToNextValue);
				
				adpatedInputValues.add(newPoint);
				
			}
			
		}
		
		
		inputValues=adpatedInputValues;
		
	}
	
	
	public LinkedList<Calendar> getAllDatesFrom(Calendar lastInputPointDate){
		
		LinkedList<Calendar> dates=new LinkedList<Calendar>();
		
		for(int i=0;i<this.getInputValues().size();i++){
			ValuePoint point=this.getInputValues().get(i);
			if(point.getDate().getTimeInMillis()<lastInputPointDate.getTimeInMillis())continue;
			
			dates.add(point.getDate());
		}
		
		return dates;
	}
	
	public LinkedList<double[]> transformSeriesToDoubleArrayList(Calendar lastInputPointDate){
		LinkedList<LinkedList<Double>> doubleListList=new LinkedList<LinkedList<Double>>();
		
		//Initialize the Lists
		if(this.isTimeRemainingActivated()){
			doubleListList.add(new LinkedList<Double>());
		}
		for(int j=0;j<numberOfPastValues;j++){
			doubleListList.add(new LinkedList<Double>());
		}
		
		ValuePointList normalizedInputValues=this.getNormalizedInputValues();
		ValuePointList normalizedLowFrequencyValues=this.getNormalizedLowFrequencyValues();
		
		//Fill the lists
		for(int i=0;i<normalizedInputValues.size();i++){
			ValuePoint point=normalizedInputValues.get(i);
			if(point.getDate().getTimeInMillis()<lastInputPointDate.getTimeInMillis())continue;
			
			
			
			//Create the past values lists
			for(int j=0;j<numberOfPastValues;j++){
				if(isLowFrequency){
					ValuePoint lowFreqPoint=searchLowFrequencyValuePoint(point.getDate(),j,normalizedLowFrequencyValues);
					doubleListList.get(j).add(lowFreqPoint.getValue());
				}
				else{
					doubleListList.get(j).add(normalizedInputValues.get(i-j).getValue());
				}
			}
			
			//Create the time remaining double list
			if(this.isTimeRemainingActivated()){
				if(isLowFrequency){
					ValuePoint lowFreqPoint=searchLowFrequencyValuePoint(point.getDate(),0,normalizedLowFrequencyValues);
					//logger.info("lowFreqPoint getDate: "+DateTool.dateToString(lowFreqPoint.getDate()));
					//logger.info("lowFreqPoint getNextValueDate: "+DateTool.dateToString(lowFreqPoint.getNextValueDate()));
					//double abs=lowFreqPoint.getNextValueDate().getTimeInMillis()-lowFreqPoint.getDate().getTimeInMillis();
					//double rate=((double) point.getDate().getTimeInMillis())/abs;
					
					double rate=DateTool.calculateRelativePosition(
							point.getDate(), lowFreqPoint.getDate(), lowFreqPoint.getNextValueDate(), true);
					
					doubleListList.getLast().add(rate);
				}
				else{
					doubleListList.getLast().add(
							(double)point.getNextValueDate().getTimeInMillis()-
							point.getDate().getTimeInMillis());
				}
			}
			
		}
		
		LinkedList<double[]> doubleArrayList=new LinkedList<double[]>();
		for(int j=0;j<doubleListList.size();j++){
			doubleArrayList.add(new double[doubleListList.getFirst().size()]);
		}
		
		for(int j=0;j<doubleListList.size();j++){
			for(int i=0;i<doubleListList.getFirst().size();i++){
				doubleArrayList.get(j)[i]=doubleListList.get(j).get(i);
			}
		}
		
		return doubleArrayList;
		
	}
	
	private ValuePoint searchLowFrequencyValuePoint(Calendar date, int pastNb, ValuePointList lowFrequencyList){
		for(int i=1;i<lowFrequencyList.size();i++){
			ValuePoint lowPoint=lowFrequencyList.get(i);
			ValuePoint lastlowPoint=lowFrequencyList.get(i-1);
			
			if(i-1-pastNb<0)continue;
			
			ValuePoint searchlowPoint=lowFrequencyList.get(i-1-pastNb);
			
			if(i-1==0 &&
				date.getTimeInMillis()==lastlowPoint.getDate().getTimeInMillis())
				return searchlowPoint;
			
			if(i==lowFrequencyList.size()-1 &&
					date.getTimeInMillis()>lowPoint.getDate().getTimeInMillis())
				return lowFrequencyList.get(i-pastNb);
			
			if(date.getTimeInMillis()>lastlowPoint.getDate().getTimeInMillis() 
					&& date.getTimeInMillis()<= lowPoint.getDate().getTimeInMillis())
				return searchlowPoint;
			
		}
		return null;
	}
	
	public LinkedList<String> getInputNeuronNames(){
		
		LinkedList<String> names=new LinkedList<String>();
		
		for(int j=0;j<numberOfPastValues;j++){
			names.add(this.getNamePrefix()+"_"+this.Name+"_"+String.valueOf(j+1));
		}
		
		if(this.isTimeRemainingActivated()){
			names.add(this.getNamePrefix()+"_"+this.Name+"_TimeRemaining");
		}
		return names;
		
	}
	
	private String getNamePrefix(){
		String prefix="";
		
		if(this.parentGroup!=null && this.parentGroup.getParent()!=null)
			prefix=this.parentGroup.getParent().getName()+"_"+this.parentGroup.getName();
		else if(this.parentGroup!=null){
			prefix=this.parentGroup.getName();
		}
		
		return prefix;
	}
	
	
	public void resetMinMaxValues(boolean forceResetting){
		ValuePointList values=inputValues;
		if(isLowFrequency){
			logger.info("Low frequency!!");
			values=lowFrequencyValues;
		}
		
		if(values==null || values.isEmpty()){
			logger.info("Cannot set the min and max values of series:"+this.Name+", Please load the the value points before");
			return;
		}
		
		if( Double.isNaN(minValue) || Double.isNaN(maxValue) ||
				forceResetting){
		
		minValue=Double.POSITIVE_INFINITY;
		maxValue=Double.NEGATIVE_INFINITY;
		
		for(ValuePoint point:values){
			if(point.getValue()>maxValue)
				maxValue=point.getValue();
			
			if(point.getValue()<minValue)
				minValue=point.getValue();
		}
		
		
		MinMaxLastRefreshDate=Calendar.getInstance();
		}
		
	}
	
	
	private ValuePointList normalizeValuePointList(ValuePointList in){
		if(Double.isNaN(minValue) || Double.isNaN(maxValue))return in;
		
		double div_fac=maxValue-minValue;
		
		if(div_fac==0)return in;
		
		ValuePointList normalizedValues=new ValuePointList();
		
		for(ValuePoint point:in){
			double n_val=2*(point.getValue()-minValue)/div_fac -1;
			ValuePoint n_point=new ValuePoint(point.getDate(), n_val);
			n_point.setNextValueDate(point.getNextValueDate());
			normalizedValues.add(n_point);
		}
		
		return normalizedValues;
		
	}
	
	private ValuePointList getNormalizedInputValues(){
		return normalizeValuePointList(inputValues);
	}
	
	private ValuePointList getNormalizedLowFrequencyValues(){
		return normalizeValuePointList(lowFrequencyValues);
	}
	
	
	//****************************************
	//***      GETTER AND SETTER           ****
	//****************************************
	
	public ChartIndicator getIndicator() {
		return indicator;
	}

	public void setIndicator(ChartIndicator indicator) {
		this.indicator = indicator;
	}
	

	public Object getParent() {
		return parent;
	}

	

	public TimeSeriesGroup getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(TimeSeriesGroup parentGroup) {
	this.parentGroup = parentGroup;
	}
	

	public Calendar getMinMaxLastRefreshDate() {
		return MinMaxLastRefreshDate;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
	this.minValue = minValue;}
	

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
	this.maxValue = maxValue;
	}
	

	public ValuePointList getInputValues() {
		return inputValues;
	}

	public void setInputValues(ValuePointList inputValues) {
	changes.firePropertyChange(FIELD_InputValues, this.inputValues, this.inputValues = inputValues);}
	

	public void setParent(Object parent) {
		this.parent = parent;
		if(this.parentGroup.getName().equals(TimeSeriesGroup.GROUP_FINANCIAL)){
			this.isLowFrequency=true;
		}
	}
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		changes.firePropertyChange(FIELD_Name, this.Name, this.Name = name);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
	changes.firePropertyChange(FIELD_Id, this.id, this.id = id);}

	
	public boolean isTimeRemainingActivated() {
		return timeRemainingActivated;
	}

	public void setTimeRemainingActivated(boolean timeRemainingActivated) {
	changes.firePropertyChange(FIELD_TimeRemainingActivated, this.timeRemainingActivated, this.timeRemainingActivated = timeRemainingActivated);}
	
	public boolean isLowFrequency() {
		return isLowFrequency;
	}

	public void setLowFrequency(boolean isLowFrequency) {
		this.isLowFrequency = isLowFrequency;
	}
	
	public ValuePointList getLowFrequencyValues() {
		return lowFrequencyValues;
	}

	public void setLowFrequencyValues(ValuePointList lowFrequencyValues) {
		this.lowFrequencyValues = lowFrequencyValues;
	}
	
	public int getNumberOfPastValues() {
		return numberOfPastValues;
	}

	public void setNumberOfPastValues(int numberOfPastValues) {
	changes.firePropertyChange(FIELD_NumberOfPastValues, this.numberOfPastValues, this.numberOfPastValues = numberOfPastValues);}
	
	
	//****************************************
	//***             XML                 ****
	//****************************************

	@Override
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setId(rootElement.getAttribute(FIELD_Id));
		//this.setCategory(TimeSeriesCategory.fromString(rootElement.getAttribute(FIELD_Category)));
		this.setNumberOfPastValues(Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfPastValues)));
		this.setTimeRemainingActivated(Boolean.parseBoolean(rootElement.getAttribute(FIELD_TimeRemainingActivated)));
		this.setLowFrequency(Boolean.parseBoolean(rootElement.getAttribute(FIELD_IsLowFrequency)));
		
		this.setMaxValue(Double.parseDouble(rootElement.getAttribute(FIELD_MaxValue)));
		this.setMinValue(Double.parseDouble(rootElement.getAttribute(FIELD_MinValue)));
		this.setMinValue(Double.parseDouble(rootElement.getAttribute(FIELD_MinValue)));
		
		
		this.MinMaxLastRefreshDate=(DateTool.StringToDate(rootElement.getAttribute(FIELD_MinMaxLastRefreshDate)));
		
	}

	@Override
	protected void initChild(Element childElement) {
		ChartIndicator ind=ChartIndicatorFactory.createChartIndicator(
				childElement.getTagName(), this);
		
		if(ind!=null){
			this.indicator=ind;
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_Id,this.getId());
		//rootElement.setAttribute(FIELD_Category,this.getCategory().getCategoryLabel());
		rootElement.setAttribute(FIELD_NumberOfPastValues,String.valueOf(this.getNumberOfPastValues()));
		rootElement.setAttribute(FIELD_TimeRemainingActivated,String.valueOf(this.isTimeRemainingActivated()));
		rootElement.setAttribute(FIELD_IsLowFrequency,String.valueOf(this.isLowFrequency()));
		
		rootElement.setAttribute(FIELD_MaxValue,String.valueOf(this.getMaxValue()));
		rootElement.setAttribute(FIELD_MinValue,String.valueOf(this.getMinValue()));
		
		rootElement.setAttribute(FIELD_MinMaxLastRefreshDate,DateTool.dateToString( this.getMinMaxLastRefreshDate()));
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		if(indicator!=null)
			rootElement.appendChild(indicator.toDomElement(doc));
	}

	@Override
	public String toString() {
		return "TimeSeries [Name=" + Name + ", id=" + id + 
				", isLowFrequency=" + isLowFrequency
				+ ", timeRemainingActivated=" + timeRemainingActivated
				+ ", numberOfPastValues=" + numberOfPastValues + ", parent="
				+ parent + ", inputValues=" + inputValues
				+ ", lowFrequencyValues=" + lowFrequencyValues + ", minValue="
				+ minValue + ", maxValue=" + maxValue + "]";
	}

	
	
	
	

}
