package com.munch.exchange.model.core.neuralnetwork;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.xml.XmlElementIF;

public class ValuePointList extends LinkedList<ValuePoint> implements
		XmlElementIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3034931276048074434L;
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	/*
	public void setNextValueDates(){
		ValuePoint lastPoint=null;
		long mid_diff=0;
		for(ValuePoint point:this){
			if(lastPoint!=null){
				lastPoint.setNextValueDate(point.getDate());
				mid_diff=point.getDate().getTimeInMillis()-lastPoint.getDate().getTimeInMillis();
			}
			lastPoint=point;
		}
		
		if(this.getLast().getNextValueDate()==null && this.size()>1){
			mid_diff=mid_diff/(this.size()-1);
			Calendar expectedNextValue=Calendar.getInstance();
			expectedNextValue.setTimeInMillis(this.getLast().getDate().getTimeInMillis()+mid_diff);
		}
		
	}
	*/
	
	
	
	public double[] toDoubleArray(){
		double[] array=new double[this.size()];
		for(int i=0;i<array.length;i++){
			array[i]=this.get(i).getValue();
		}
		return array;
	}
	
	public String[] toStringArray(){
		String[] array=new String[this.size()];
		for(int i=0;i<array.length;i++){
			array[i]=this.get(i).getMetaData();
		}
		return array;
	}
	
	public void sort(){
		java.util.Collections.sort(this);
	}
	
	
	@Override
	public Element toDomElement(Document doc) {
		Element e=doc.createElement(this.getTagName());
		
		for(ValuePoint point : this){
			Element h_p=point.toDomElement(doc);
			e.appendChild(h_p);
		}
		
		return e;
	}

	@Override
	public void init(Element Root) {
		if(Root.getTagName().equals(this.getTagName())){
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Output Point
					ValuePoint point=new ValuePoint();
					if(childElement.getTagName().equals(point.getTagName())){
						point.init(childElement);
						
						this.add(point);
						
					}		
				}
			}	
		}

	}

	@Override
	public String getTagName() {
		return this.getClass().getSimpleName();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
