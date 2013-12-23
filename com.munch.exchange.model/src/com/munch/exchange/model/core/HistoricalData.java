package com.munch.exchange.model.core;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;

public class HistoricalData extends LinkedList<HistoricalPoint> implements XmlElementIF {
	static final String HistoricalDataStr="historical_data";
	
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	/**
	 * return the TAG Name used in the xml file
	 */
	public String getTagName(){return "historical_data";}
	
	
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
					
					//History Point
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
