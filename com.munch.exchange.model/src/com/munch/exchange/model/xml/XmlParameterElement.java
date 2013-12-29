package com.munch.exchange.model.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XmlParameterElement extends ParameterElement implements
		XmlElementIF {
	
	/**
	 * initializes the attributes from a xml element
	 * @param rootElement
	 */
	protected abstract void initAttribute(Element rootElement);
	
	/**
	 * initializes the childs from a xml element
	 * @param childElement
	 */
	protected abstract void initChild(Element childElement);
	
	/**
	 * initializes the users map from a xml element
	 */
	public void init(Element Root){
		
		if(Root.getTagName().equals(this.getTagName())){
			
			//Element Attributes
			this.initAttribute(Root);
			
			NodeList Children=Root.getChildNodes();
			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Parameter
					if(childElement.getTagName().equals(new Parameter().getTagName())){
						this.setParameter(new Parameter(childElement));
					}
					//Element child
					this.initChild(childElement);
					
				}
			}
			
		}
	}
	
	/**
	 * save the attributes data in a xml element
	 * 
	 * @param rootElement
	 */
	protected abstract void setAttribute(Element rootElement);
	
	/**
	 * save the child data in a xml element
	 * @param rootElement
	 */
	protected abstract void appendChild(Element rootElement);
	
	/**
	 * export the user map in a xml element
	 */
	public Element toDomElement(Document doc){
		Element e=doc.createElement(this.getTagName());
		
		//Set Attributes
		this.setAttribute(e);
		
		//Parameter
		e.appendChild(this.getParameter().toDomElement(doc));
		//child
		this.appendChild(e);
		
		return e;
	  }

	@Override
	public String getTagName() {
		return this.getClass().getSimpleName();
	}

}
