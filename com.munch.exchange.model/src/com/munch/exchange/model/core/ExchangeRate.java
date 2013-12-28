package com.munch.exchange.model.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;


public abstract class ExchangeRate extends ParameterElement implements XmlElementIF {
	
	protected String name;
	static final String NameStr="name";
	
	protected String symbol;
	static final String SymbolStr="symbol";
	
	protected HistoricalData historicalData=new HistoricalData();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public HistoricalData getHistoricalData() {
		return historicalData;
	}
	public void setHistoricalData(HistoricalData historicalData) {
		this.historicalData = historicalData;
	}
	
	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	
	/**
	 * return the TAG Name used in the xml file
	 */
	public abstract String getTagName();
	
	protected abstract void initAttribute(Element rootElement);
	protected abstract void initChild(Element childElement);
	
	/**
	 * initializes the users map from a xml element
	 */
	public void init(Element Root){
		
		if(Root.getTagName().equals(this.getTagName())){
			
			
			this.setName(Root.getAttribute(NameStr));
			this.setSymbol(Root.getAttribute(SymbolStr));
			
			this.initAttribute(Root);
			
			NodeList Children=Root.getChildNodes();
			
			historicalData.clear();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Parameter
					if(childElement.getTagName().equals(new Parameter().getTagName())){
						this.setParameter(new Parameter(childElement));
					}
					//Historical Data
					else if(childElement.getTagName().equals(historicalData.getTagName())){
						historicalData.init(childElement);
					}
					
					this.initChild(childElement);
					
				}
			}
			
		}
	}
	
	
	protected abstract void setAttribute(Element rootElement);
	protected abstract void appendChild(Element rootElement);
	
	/**
	 * export the user map in a xml element
	 */
	public Element toDomElement(Document doc){
		Element e=doc.createElement(this.getTagName());
			
		
		e.setAttribute(NameStr, this.getName());
		e.setAttribute(SymbolStr, this.getSymbol());
		
		//Parameter
		e.appendChild(this.getParameter().toDomElement(doc));
		//Historical Data
		e.appendChild(this.getHistoricalData().toDomElement(doc));

		
		
		return e;
	  }
	

}
