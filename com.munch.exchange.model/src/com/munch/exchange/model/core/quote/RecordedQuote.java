package com.munch.exchange.model.core.quote;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.DatePointList;

public class RecordedQuote extends DatePointList<QuotePoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5786399869426289076L;

	@Override
	protected DatePoint createPoint() {
		return new QuotePoint();
	}
	
	
	
	/*
	private static final long serialVersionUID = -7882821093083414667L;
	
	
	public void sort(){
		java.util.Collections.sort(this);
	}
	
	
	@Override
	public Element toDomElement(Document doc) {
		Element e=doc.createElement(this.getTagName());
		
		for(QuotePoint point : this){
			Element h_p=point.toDomElement(doc);
			e.appendChild(h_p);
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
					
					//Quote Point
					QuotePoint point=new QuotePoint();
					if(childElement.getTagName().equals(point.getTagName())){
						point.init(childElement);
						this.add(point);
					}
					
				}
			}
			
			
		}

	}

	@Override
	public String getTagName() {
		return this.getClass().getSimpleName();
	}
	*/

}
