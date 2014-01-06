package com.munch.exchange.model.core;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;

public abstract class DatePointList<E extends DatePoint> extends LinkedList<DatePoint> implements XmlElementIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7311134818088146079L;

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

}
