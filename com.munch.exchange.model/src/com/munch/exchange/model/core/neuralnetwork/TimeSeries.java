package com.munch.exchange.model.core.neuralnetwork;

import java.util.Calendar;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class TimeSeries extends XmlParameterElement{
	
	
	static final String FIELD_Name="Name";
	static final String FIELD_Id="Id";
	static final String FIELD_Category="Category";
	static final String FIELD_TimeRemainingActivated="TimeRemainingActivated";
	static final String FIELD_NumberOfPastValues="NumberOfPastValues";
	static final String FIELD_InputValues="InputValues";
	
	private String Name;
	private String id;
	private TimeSeriesCategory category;
	
	private boolean timeRemainingActivated;
	private int numberOfPastValues;
	private Object parent;
	
	private ValuePointList inputValues=new ValuePointList();
	
	public TimeSeries(){}
	
	public TimeSeries(String Name,TimeSeriesCategory category){
		this.Name=Name;
		this.category=category;
		id="";
		timeRemainingActivated=false;
		numberOfPastValues=12;
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
		for(int i=0;i<inputValues.size()-1;i++){
			ValuePoint point=inputValues.get(i);
			if(point.getDate().getTimeInMillis()<lastInputPointDate.getTimeInMillis())continue;
			
			for(int j=0;j<numberOfPastValues;j++){
				doubleListList.get(j).add(inputValues.get(i-j).getValue());
			}
			
			if(this.isTimeRemainingActivated()){
				doubleListList.getLast().add((double)point.getNextValueDate().getTimeInMillis()-point.getDate().getTimeInMillis());
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
	changes.firePropertyChange(FIELD_Category, this.category, this.category = category);}
	

	public boolean isTimeRemainingActivated() {
		return timeRemainingActivated;
	}

	public void setTimeRemainingActivated(boolean timeRemainingActivated) {
	changes.firePropertyChange(FIELD_TimeRemainingActivated, this.timeRemainingActivated, this.timeRemainingActivated = timeRemainingActivated);}
	

	public int getNumberOfPastValues() {
		return numberOfPastValues;
	}

	public void setNumberOfPastValues(int numberOfPastValues) {
	changes.firePropertyChange(FIELD_NumberOfPastValues, this.numberOfPastValues, this.numberOfPastValues = numberOfPastValues);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setId(rootElement.getAttribute(FIELD_Id));
		this.setCategory(TimeSeriesCategory.fromString(rootElement.getAttribute(FIELD_Category)));
		this.setNumberOfPastValues(Integer.parseInt(rootElement.getAttribute(FIELD_NumberOfPastValues)));
		this.setTimeRemainingActivated(Boolean.parseBoolean(rootElement.getAttribute(FIELD_TimeRemainingActivated)));
		
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
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {}

}
