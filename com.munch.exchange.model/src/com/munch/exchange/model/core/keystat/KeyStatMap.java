package com.munch.exchange.model.core.keystat;

import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlElementIF;
import com.munch.exchange.model.xml.XmlParameterElementMap;



public class KeyStatMap extends XmlParameterElementMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1919025737293845560L;

	public KeyStatMap(String tagName) {
		super(tagName);
	}
	
	public KeyStatMap(String tagName, Element e) {
		super(tagName);
		this.init(e);
	}

	@Override
	protected XmlElementIF getValue(Element el) {
		ContentAndTerm c_t=new ContentAndTerm(el);
		return c_t;
	}
	
	public ContentAndTerm getContentAndTerm(String key){
		return (ContentAndTerm) this.get(key);
	}

}
