package com.munch.exchange.model.xml;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XmlParameterElementMap extends HashMap<String, XmlElementIF>
		implements XmlElementIF {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6531987737003142455L;
	static final String FIELD_Key="Key";
	static final String FIELD_Value="Value";
	static final String FIELD_Element="ELement";
	
	public XmlParameterElementMap(String tagName){
		this.tagName=tagName;
	}
	
	private String tagName="";

	@Override
	public Element toDomElement(Document doc) {
		Element e=doc.createElement(this.getTagName());
		
		for(String  k  : this.keySet()){
			XmlElementIF v = this.get(k);
			
			Element k_v_element=doc.createElement(FIELD_Element);
			k_v_element.setAttribute(FIELD_Key,k);
			k_v_element.appendChild(v.toDomElement(doc));
			e.appendChild(k_v_element);
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
					
					//Elements
					//DatePoint point=createPoint();
					if(childElement.getTagName().equals(FIELD_Element)){
						String key=childElement.getAttribute(FIELD_Key);
						this.put(key , getValue((Element)childElement.getFirstChild()));
						
					}
					
				}
			}
			
			
		}

	}

	protected abstract XmlElementIF getValue(Element el);
	
	@Override
	public String getTagName() {
		if(!tagName.isEmpty())
			return tagName;
		return this.getClass().getSimpleName();
	}

}
