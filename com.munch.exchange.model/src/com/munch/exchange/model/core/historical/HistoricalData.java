package com.munch.exchange.model.core.historical;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;

public class HistoricalData extends LinkedList<HistoricalPoint> implements XmlElementIF {
	
	/**
	 *  // hier wird sortiert  
	 *    Comparator<Rechteck> comparator = new QuadratheitComparator(); 
	 *     java.util.Collections.sort( rechtecke, comparator );
	 */
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5430509341617898712L;

	
	public void sort(){
		java.util.Collections.sort(this);
	}
	

	/**
	 * return the TAG Name used in the xml file
	 */
	@Override
	public String getTagName() {
		return this.getClass().getSimpleName();
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
					HistoricalPoint point=new HistoricalPoint();
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
			
		for(HistoricalPoint point : this){
			Element h_p=point.toDomElement(doc);
			e.appendChild(h_p);
		}
		
		return e;
	  }
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
