package com.munch.exchange.model.core;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.xml.XmlElementIF;


public class ExchangeRate extends ParameterElement implements XmlElementIF {
	
	protected String name;
	static final String NameStr="name";
	
	protected String symbol;
	static final String SymbolStr="symbol";
	
	protected LinkedList<HistoricalPoint> HistoricalData=new LinkedList<HistoricalPoint>();
	static final String HistoricalDataStr="historical_data";
	
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
			this.setSymbol(Root.getAttribute(SymbolStr));
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//Parameter
					if(childElement.getTagName().equals(new Parameter().getTagName())){
						this.setParameter(new Parameter(childElement));
					}
					//Historical Data
					else if(childElement.getTagName().equals(HistoricalDataStr)){
						HistoricalData.clear();
						NodeList Childs=childElement.getChildNodes();
						for(int j=0;j<Childs.getLength();j++){
							Node c = Childs.item(i);
							if(c instanceof Element){
								Element HElement=(Element)c;
								
								//History Point
								HistoricalPoint point=new HistoricalPoint();
								if(HElement.getTagName().equals(point.getTagName())){
									point.init(HElement);
									HistoricalData.add(point);
								}
								
							}
						}
						
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
		e.setAttribute(SymbolStr, this.getSymbol());
		
		/*
		//Material
		for(Material mat:part.getMaterialList()){
			e.appendChild(mat.toDomElement(doc));
		}
		//Representation
		for(Representation rep:part.getRepresentationList()){
			e.appendChild(rep.toDomElement(doc));
		}
		//Translation
		if(part.getTranslation()!=null)
			e.appendChild(part.getTranslation().toDomElement(doc));
		*/
		//Parameter
		e.appendChild(this.getParameter().toDomElement(doc));
		
		Element HDataEle=doc.createElement(HistoricalDataStr);
		
		
		return e;
	  }
	

}
