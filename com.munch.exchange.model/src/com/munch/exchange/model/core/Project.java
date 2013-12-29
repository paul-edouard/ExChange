package com.munch.exchange.model.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.Parameter;
import com.munch.exchange.model.xml.ParameterElement;
import com.munch.exchange.model.xml.XmlElementIF;

public class Project extends ParameterElement implements XmlElementIF {
	
	
	private String name;
	static final String NameStr="name";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	/**
	 * return the TAG Name used in the xml file
	 */
	public String getTagName(){return "exchange_rate";}
	
	/**
	 * initializes the users map from a xml element
	 */
	public void init(Element Root){
		
		if(Root.getTagName().equals(this.getTagName())){
			
			
			this.setName(Root.getAttribute(NameStr));
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Parameter
					if(childElement.getTagName().equals(new Parameter().getTagName())){
						this.setParameter(new Parameter(childElement));
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
			
		
		e.setAttribute(NameStr, this.getName());
		
		//Parameter
		e.appendChild(this.getParameter().toDomElement(doc));
		
		
		
		
		return e;
	  }

}
