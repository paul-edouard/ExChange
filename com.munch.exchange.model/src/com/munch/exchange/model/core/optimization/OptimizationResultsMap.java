package com.munch.exchange.model.core.optimization;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.xml.XmlElementIF;

public class OptimizationResultsMap extends
		HashMap<OptimizationResults.Type, OptimizationResults> implements XmlElementIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9170584046357748076L;
	
	
	
	
	@Override
	public OptimizationResults get(Object key) {
			if(!this.containsKey(key)){
				OptimizationResults res=new OptimizationResults();
				res.setType((Type)key);
				this.put(res.getType(), res);
			}
	
		
		return super.get(key);
	}

	@Override
	public Element toDomElement(Document doc) {
		Element e=doc.createElement(this.getTagName());
		
		//child
		for(Type key :this.keySet()){
			e.appendChild(this.get(key).toDomElement(doc));
		}
		return e;
	}

	@Override
	public void init(Element Root) {
		this.clear();
		if(Root.getTagName().equals(this.getTagName())){
			
			
			NodeList Children=Root.getChildNodes();
			for(int i=0;i<Children.getLength();i++){
				Node child = Children.item(i);
				if(child instanceof Element){
					Element childElement=(Element)child;
					OptimizationResults res=new OptimizationResults();
					//Parameter
					if(childElement.getTagName().equals(res.getTagName())){
						res.init(childElement);
						this.put(res.getType(), res);
					}
					
					
				}
			}
			
		}

	}

	@Override
	public String getTagName() {
		return this.getClass().getSimpleName();
	}

}
