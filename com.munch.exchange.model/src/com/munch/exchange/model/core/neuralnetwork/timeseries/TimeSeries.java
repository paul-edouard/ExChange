package com.munch.exchange.model.core.neuralnetwork.timeseries;

import java.util.Calendar;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.neuralnetwork.ValuePoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.xml.XmlParameterElement;

public class TimeSeries extends XmlParameterElement{
	
	
	static final String FIELD_Name="Name";
	static final String FIELD_Id="Id";
	static final String FIELD_Category="Category";
	static final String FIELD_TimeRemainingActivated="TimeRemainingActivated";
	public static final String FIELD_NumberOfPastValues="NumberOfPastValues";
	static final String FIELD_InputValues="InputValues";
	static final String FIELD_IsLowFrequency="IsLowFrequency";
	
	private String Name;
	private String id;
	private TimeSeriesCategory category;
	private boolean isLowFrequency=false;
	
	private boolean timeRemainingActivated;
	private int numberOfPastValues;
	private Object parent;
	
	private ValuePointList inputValues=new ValuePointList();
	private ValuePointList lowFrequencyValues=new ValuePointList();
	
	private min,max;
	
	public TimeSeries(){}
	
	public TimeSeries(String Name,TimeSeriesCategory category){
		this.Name=Name;
		this.setCategory(category);
		//this.category=category;
		id="";
		timeRemainingActivated=false;
		numberOfPastValues=6;
	}
	
	public TimeSeries createCopy(){
		TimeSeries copy = new TimeSeries(Name, category.createCopy());
		copy.id=this.id;
		copy.timeRemainingActivated=this.timeRemainingActivated;
		copy.numberOfPastValues=this.numberOfPastValues;

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
	
	
	public LinkedList<double[]> transformSeriesToDoubleArrayList(Calendar lastInputPointDate){
		LinkedList<LinkedList<Double>> doubleListList=new LinkedList<LinkedList<Double>>();
		
		//Initialize the Lists
		if(this.isTimeRemainingActivated()){
			doubleListList.add(new LinkedList<Double>());
		}
		for(int j=0;j<numberOfPastValues;j++){
			doubleListList.add(new LinkedList<Double>());
		}
		
		//Fill the lists
		for(int i=0;i<inputValues.size();i++){
			ValuePoint point=inputValues.get(i);
			if(point.getDate().getTimeInMillis()<lastInputPointDate.getTimeInMillis())continue;
			
			
			
			//Create the past values lists
			for(int j=0;j<numberOfPastValues;j++){
				if(isLowFrequency){
					ValuePoint lowFreqPoint=searchLowFrequencyValuePoint(point.getDate(),j);
					doubleListList.get(j).add(lowFreqPoint.getValue());
				}
				else{
					doubleListList.get(j).add(inputValues.get(i-j).getValue());
				}
			}
			
			//Create the time remaining double list
			if(this.isTimeRemainingActivated()){
				if(isLowFrequency){
					ValuePoint lowFreqPoint=searchLowFrequencyValuePoint(point.getDate(),0);
					double abs=lowFreqPoint.getNextValueDate().getTimeInMillis()-lowFreqPoint.getDate().getTimeInMillis();
					double rate=((double) point.getDate().getTimeInMillis())/abs;
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
	
	
	
	private ValuePoint searchLowFrequencyValuePoint(Calendar date, int pastNb){
		for(int i=1;i<this.lowFrequencyValues.size();i++){
			ValuePoint lowPoint=this.lowFrequencyValues.get(i);
			ValuePoint lastlowPoint=this.lowFrequencyValues.get(i-1);
			
			if(i-1-pastNb<0)continue;
			
			ValuePoint searchlowPoint=this.lowFrequencyValues.get(i-1-pastNb);
			
			if(i-1==0 &&
				date.getTimeInMillis()==lastlowPoint.getDate().getTimeInMillis())
				return searchlowPoint;
			
			if(i==this.lowFrequencyValues.size()-1 &&
					date.getTimeInMillis()>lowPoint.getDate().getTimeInMillis())
				return this.lowFrequencyValues.get(i-pastNb);
			
			if(date.getTimeInMillis()>lastlowPoint.getDate().getTimeInMillis() 
					&& date.getTimeInMillis()<= lowPoint.getDate().getTimeInMillis())
				return searchlowPoint;
			
		}
		return null;
	}
	
	
	public LinkedList<String> getInputNeuronNames(){
		
		LinkedList<String> names=new LinkedList<String>();
		
		for(int j=0;j<numberOfPastValues;j++){
			names.add(this.category.name()+"_"+this.Name+"_"+String.valueOf(j+1));
		}
		
		if(this.isTimeRemainingActivated()){
			names.add(this.category.name()+"_"+this.Name+"_TimeRemaining");
		}
		return names;
		
	}
	
	
	//Use double parameters to save the indicator parameters
	

	public Object getParent() {
		return parent;
	}

	public ValuePointList getInputValues() {
		return inputValues;
	}

	public void setInputValues(ValuePointList inputValues) {
	changes.firePropertyChange(FIELD_InputValues, this.inputValues, this.inputValues = inputValues);}
	

	public void setParent(Object parent) {this.parent = parent;}
	
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
	
	public TimeSeriesCategory getCategory() {
		return category;
	}

	public void setCategory(TimeSeriesCategory category) {
		changes.firePropertyChange(FIELD_Category, this.category, this.category = category);
		if(category.equals(TimeSeriesCategory.FINANCIAL)){
			this.isLowFrequency=true;
		}
	}
	
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
		this.setCategory(TimeSeriesCategory.fromString(rootElement.getAttribute(FIELD_Category)));
		this.setNumberOfPastValues(Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfPastValues)));
		this.setTimeRemainingActivated(Boolean.parseBoolean(rootElement.getAttribute(FIELD_TimeRemainingActivated)));
		this.setLowFrequency(Boolean.parseBoolean(rootElement.getAttribute(FIELD_IsLowFrequency)));
		
	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_Id,this.getId());
		rootElement.setAttribute(FIELD_Category,this.getCategory().getCategoryLabel());
		rootElement.setAttribute(FIELD_NumberOfPastValues,String.valueOf(this.getNumberOfPastValues()));
		rootElement.setAttribute(FIELD_TimeRemainingActivated,String.valueOf(this.isTimeRemainingActivated()));
		rootElement.setAttribute(FIELD_IsLowFrequency,String.valueOf(this.isLowFrequency()));
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {}

	@Override
	public String toString() {
		return "TimeSeries [Name=" + Name + ", id=" + id + ", category="
				+ category + ", timeRemainingActivated="
				+ timeRemainingActivated + ", numberOfPastValues="
				+ numberOfPastValues + ", parent=" + parent + ", inputValues="
				+ inputValues + "]";
	}
	
	
	

}
