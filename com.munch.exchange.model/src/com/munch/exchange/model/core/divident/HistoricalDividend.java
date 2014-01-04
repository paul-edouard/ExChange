package com.munch.exchange.model.core.divident;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;
import com.munch.exchange.model.core.quote.QuotePoint;

public class HistoricalDividend extends DatePointList<Dividend> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1064128402077353719L;
	
	@Override
	protected DatePoint createPoint() {
		return new QuotePoint();
	}
	/*
	
	public void sort(){
		java.util.Collections.sort(this);
	}
	

	
	public String getTagName(){return this.getClass().getSimpleName();}
	
	
	public void init(Element Root){
		
		if(Root.getTagName().equals(this.getTagName())){
			
			
			NodeList Children=Root.getChildNodes();

			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					
					//History Point
					Dividend point=new Dividend();
					if(childElement.getTagName().equals(point.getTagName())){
						point.init(childElement);
						this.add(point);
					}
					
				}
			}
			
			
		}
	}
	
	
	
	public Element toDomElement(Document doc){
		Element e=doc.createElement(this.getTagName());
			
		for(Dividend point : this){
			Element h_p=point.toDomElement(doc);
			e.appendChild(h_p);
			
		}
		
		
		
		return e;
	  }

	*/
}
