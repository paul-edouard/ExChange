package com.munch.exchange.model.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;

public abstract class DatePointList<E extends DatePoint> extends LinkedList<DatePoint> implements XmlElementIF {

	
	public static final String FIELD_LastQuoteChanged = "LastQuoteChanged";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7311134818088146079L;
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

	private String tagName="";
	
	public DatePointList(String tagName){
		this.tagName=tagName;
	}
	
	public DatePointList(){
	}
	
	
	/**
	 * return the TAG Name used in the xml file
	 */
	
	@Override
	public String getTagName() {
		if(!tagName.isEmpty())
			return tagName;
		return this.getClass().getSimpleName();
	}
	
	@Override
	public void addLast(DatePoint e) {
		DatePoint oldLast=null;
		if(this.size()>0)oldLast=this.getLast();
		
		changes.firePropertyChange(FIELD_LastQuoteChanged, oldLast, e);
		super.addLast(e);
	}

	@Override
	public boolean add(DatePoint e) {
		DatePoint oldLast=null;
		if(this.size()>0)oldLast=this.getLast();
		
		changes.firePropertyChange(FIELD_LastQuoteChanged, oldLast, e);
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends DatePoint> c) {
		DatePoint oldLast=null;
		if(this.size()>0)oldLast=this.getLast();
		
		if(super.addAll(c)){
			changes.firePropertyChange(FIELD_LastQuoteChanged, oldLast, this.getLast());
			return true;
		}
		
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends DatePoint> c) {
		DatePoint oldLast=null;
		if(this.size()>0)oldLast=this.getLast();
		
		if(super.addAll(index, c)){
			changes.firePropertyChange(FIELD_LastQuoteChanged, oldLast, this.getLast());
			return true;
		}
		
		return false;
	}

	@Override
	public void add(int index, DatePoint element) {
		
		DatePoint oldLast=null;
		if(this.size()>0)oldLast=this.getLast();
		
		super.add(index, element);
		changes.firePropertyChange(FIELD_LastQuoteChanged, oldLast, this.getLast());
	}

	@Override
	public DatePoint pollLast() {
		DatePoint oldLast=super.pollLast();
		changes.firePropertyChange(FIELD_LastQuoteChanged, oldLast, this.getLast());
		return oldLast;
	}
	
	

	protected abstract DatePoint createPoint();
	
	public void sort(){
		java.util.Collections.sort(this);
	}
	
	/**
	 * initializes the users map from a xml element
	 */
	public void init(Element Root){
		
		if(Root.getTagName().equals(this.getTagName())){
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Historical Point
					DatePoint point=createPoint();
					if(childElement.getTagName().equals(point.getTagName())){
						point.init(childElement);
						
						this.add(point);
						
					}
					
				}
			}
			
			
		}
		
		
		
	}
	
	
	/**
	 * export the user map in a xml element
	 */
	public Element toDomElement(Document doc){
		Element e=doc.createElement(this.getTagName());
			
		for(DatePoint point : this){
			Element h_p=point.toDomElement(doc);
			e.appendChild(h_p);
		}
		
		return e;
	  }
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
