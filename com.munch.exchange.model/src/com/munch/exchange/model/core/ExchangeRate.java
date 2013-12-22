package com.munch.exchange.model.core;

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
					/*
					//Weight
					if(childElement.getTagName().equals(Weight.getTagName())){
						Weight w=new Weight();
						w.setDate(childElement.getAttribute(Weight.DateStr));
						w.setEvaluation(childElement.getAttribute(Weight.EvaluationStr));
						w.setWeightTyp(childElement.getAttribute(Weight.WeightTypStr));
						w.setValue(childElement.getAttribute(Weight.ValueStr));
						w.setOriginSystem(childElement.getAttribute(Weight.OriginSystemStr));
						
						part.addWeigth(w);
						
					}
					//Material
					else if(childElement.getTagName().equals(new Material().getTagName())){
						Material mat=new Material();mat.init(childElement);
						part.addMaterial(mat);
					}
					//Representation
					else if(childElement.getTagName().equals(new Representation().getTagName())){
						Representation rep=new Representation();rep.init(childElement);
						rep.setPartId(part.getLockableId());
						part.addRepresentation(rep);
					}
					//Translation
					else if(childElement.getTagName().equals(new Translation().getTagName())){
						Translation trans=new Translation();trans.init(childElement);
						part.setTranslation(trans);
					}
					*/
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
		
		
		
		
		return e;
	  }
	

}
